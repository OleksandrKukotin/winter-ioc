package org.github.oleksandrkukotin.core;

import org.github.oleksandrkukotin.core.annotation.Qualifier;
import org.github.oleksandrkukotin.core.annotation.Snowflake;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleSnowflakeFactory {

    private final Map<String, SnowflakeDefinition> definitions = new HashMap<>();
    private final Map<String, Object> singletonCache = new HashMap<>();

    public void registerDefinition(SnowflakeDefinition definition) {
        definitions.put(definition.getSnowflakeName(), definition);
    }

    public void scan(String packageName) {
        SnowflakeScanner scanner = new SnowflakeScanner();
        for (Class<?> clazz : scanner.getClasses(packageName)) {
            Snowflake annotation = clazz.getAnnotation(Snowflake.class);
            String name = annotation.name().isBlank() ? clazz.getSimpleName() : annotation.name();
            registerDefinition(new SnowflakeDefinition(clazz, name, annotation.scope()));
        }
    }

    public <T> T getSnowflake(String name, Class<T> type) {
        SnowflakeDefinition foundDefinition = definitions.get(name);
        if  (foundDefinition == null) {
            throw new  IllegalArgumentException(String.format("No definition with name '%s' was found", name));
        }
        if (foundDefinition.getSnowflakeScope().equals(Scope.SINGLETON) && isInSingletonCache(name)) {
            return type.cast(singletonCache.get(name));
        } else {
            T instance = createInstance(foundDefinition, type);

            if (foundDefinition.getSnowflakeScope().equals(Scope.SINGLETON)) {
                singletonCache.put(name, instance);
            }

            return instance;
        }
    }

    private <T> T createInstance(SnowflakeDefinition definition, Class<T> type) {
        try {
            Constructor<?>[] constructors = definition.getSnowflakeClass().getDeclaredConstructors();
            Constructor<?> constructor = Arrays.stream(constructors)
                            .max(Comparator.comparingInt(Constructor::getParameterCount))
                            .orElseThrow(() -> new RuntimeException("No constructor found"));
            constructor.setAccessible(true);

            Class<?> [] parameterTypes = constructor.getParameterTypes();
            Object[] arguments = new Object[parameterTypes.length];

            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> parameterType =  parameterTypes[i];
                String dependencyName = parameterType.getSimpleName();
                arguments[i] = getSnowflake(dependencyName, parameterType);
            }

            Object instance = constructor.newInstance(arguments);
            return type.cast(instance);
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate " + definition.getSnowflakeClass(), e);
        }
    }

    /**
     * Resolves a dependency by type, taking @Qualifier into account.
     *
     * @param requiredType the type of the dependency to resolve
     * @param annotatedElement the field or constructor parameter that may carry @Qualifier
     * @return the resolved dependency instance
     */
    private Object resolveByType(Class<?> requiredType, AnnotatedElement annotatedElement) {
        // TODO: check if annotatedElement has @Qualifier annotation
        //   if yes — extract its value() and delegate to getSnowflake(qualifierName, requiredType)

        // TODO: find all entries in definitions whose snowflakeClass is assignable to requiredType
        //   hint: use requiredType.isAssignableFrom(definition.getSnowflakeClass())

        // TODO: if exactly one candidate found — delegate to getSnowflake(candidate.getSnowflakeName(), requiredType)

        // TODO: if zero candidates found — throw IllegalArgumentException (no bean of required type)

        // TODO: if more than one candidate found — throw IllegalArgumentException (ambiguous, suggest @Qualifier)

        throw new UnsupportedOperationException("resolveByType not implemented yet");
    }

    private boolean isInSingletonCache(String snowflakeName) {
        return singletonCache.containsKey(snowflakeName);
    }
}