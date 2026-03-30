package org.github.oleksandrkukotin.core;

import org.github.oleksandrkukotin.core.annotation.Qualifier;
import org.github.oleksandrkukotin.core.annotation.Snowflake;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

            Parameter[] parameters = constructor.getParameters();
            Object[] arguments = new Object[parameters.length];

            for (int i = 0; i < parameters.length; i++) {
                arguments[i] = resolveByType(parameters[i].getType(), parameters[i]);
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
        Qualifier qualifier = annotatedElement.getAnnotation(Qualifier.class);
        if (qualifier != null) {
            return getSnowflake(qualifier.value(), requiredType);
        }

        List<SnowflakeDefinition> candidates = definitions.values().stream()
                .filter(def -> requiredType.isAssignableFrom(def.getSnowflakeClass()))
                .toList();

        if (candidates.size() == 1) {
            return getSnowflake(candidates.getFirst().getSnowflakeName(), requiredType);
        } else if (candidates.isEmpty()) {
            throw new IllegalArgumentException("No snowflake found for type: " + requiredType.getName());
        } else {
            String names = candidates.stream()
                    .map(SnowflakeDefinition::getSnowflakeName)
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException(
                    "Ambiguous snowflakes for type " + requiredType.getSimpleName() +
                    ": [" + names + "]. Use @Qualifier to specify which one to inject.");
        }
    }

    private boolean isInSingletonCache(String snowflakeName) {
        return singletonCache.containsKey(snowflakeName);
    }
}