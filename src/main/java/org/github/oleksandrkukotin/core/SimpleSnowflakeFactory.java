package org.github.oleksandrkukotin.core;

import org.github.oleksandrkukotin.core.annotation.Melt;
import org.github.oleksandrkukotin.core.annotation.Qualifier;
import org.github.oleksandrkukotin.core.annotation.Snowflake;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SimpleSnowflakeFactory {

    private static final Logger logger = Logger.getLogger(SimpleSnowflakeFactory.class.getName());

    private final Map<String, SnowflakeDefinition> definitions = new HashMap<>();
    private final Map<String, Object> singletonCache = new HashMap<>();

    public void registerDefinition(SnowflakeDefinition definition) {
        definitions.put(definition.getSnowflakeName(), definition);
        logger.fine(() -> "Registered snowflake definition: " + definition.getSnowflakeName() +
                " [" + definition.getSnowflakeScope() + "]");
    }

    public void scan(String packageName) {
        logger.info("Scanning package: " + packageName);
        SnowflakeScanner scanner = new SnowflakeScanner();
        Class<?>[] classes = scanner.getClasses(packageName);
        for (Class<?> clazz : classes) {
            Snowflake annotation = clazz.getAnnotation(Snowflake.class);
            String name = annotation.name().isBlank() ? clazz.getSimpleName() : annotation.name();
            registerDefinition(new SnowflakeDefinition(clazz, name, annotation.scope()));
        }
        logger.info("Scan complete. Registered " + classes.length + " snowflake(s) from package: " + packageName);
    }

    public <T> T getSnowflake(String name, Class<T> type) {
        SnowflakeDefinition foundDefinition = definitions.get(name);
        if  (foundDefinition == null) {
            throw new  IllegalArgumentException(String.format("No definition with name '%s' was found", name));
        }
        if (foundDefinition.getSnowflakeScope().equals(Scope.SINGLETON) && isInSingletonCache(name)) {
            logger.fine(() -> "Returning cached singleton: " + name);
            return type.cast(singletonCache.get(name));
        } else {
            logger.fine(() -> "Creating new instance: " + name);
            T instance = createInstance(foundDefinition, type);

            if (foundDefinition.getSnowflakeScope().equals(Scope.SINGLETON)) {
                singletonCache.put(name, instance);
                logger.fine(() -> "Cached singleton: " + name);
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
            logger.fine(() -> "Instantiating " + definition.getSnowflakeName() +
                    " using constructor with " + constructor.getParameterCount() + " parameter(s)");

            Parameter[] parameters = constructor.getParameters();
            Object[] arguments = new Object[parameters.length];

            for (int i = 0; i < parameters.length; i++) {
                arguments[i] = resolveByType(parameters[i].getType(), parameters[i]);
            }

            Object instance = constructor.newInstance(arguments);
            // Second pass: fill @Melt fields and setters that constructor injection cannot cover
            injectMeltDependencies(instance);
            return type.cast(instance);
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate " + definition.getSnowflakeClass(), e);
        }
    }

    /**
     * Performs field and setter injection on an already-constructed instance.
     * Called immediately after constructor instantiation in {@link #createInstance}.
     *
     * <p>Two passes are made over the class members:
     * <ol>
     *   <li>Fields annotated with {@code @Melt} — dependency is resolved by the field's
     *       declared type, then written via reflection ({@code setAccessible} is required
     *       because fields are typically private).</li>
     *   <li>Methods annotated with {@code @Melt} — must be single-argument setters;
     *       the dependency is resolved by the parameter type and the method is invoked
     *       via reflection.</li>
     * </ol>
     *
     * <p>In both cases {@link #resolveByType} is called with the member itself as the
     * {@code AnnotatedElement}, so a {@code @Qualifier} placed on the field or method
     * is picked up automatically.
     *
     * @param instance the freshly created object whose injection points should be filled
     */
    private void injectMeltDependencies(Object instance) {
        Class<?> clazz = instance.getClass();

        // --- field injection ---
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Melt.class)) {
                // Pass the field as AnnotatedElement so @Qualifier on the field is respected
                Object dependency = resolveByType(field.getType(), field);
                field.setAccessible(true);
                try {
                    field.set(instance, dependency);
                    logger.fine(() -> "Injected field: " + clazz.getSimpleName() + "." + field.getName());
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to inject field: " + field.getName(), e);
                }
            }
        }

        // --- setter injection ---
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Melt.class)) {
                // Setters must have exactly one parameter; anything else is a misconfiguration
                if (method.getParameterCount() != 1) {
                    throw new IllegalArgumentException(
                            "@Melt setter must have exactly one parameter: " + method.getName());
                }
                // Pass the method as AnnotatedElement so @Qualifier on the setter is respected
                Parameter param = method.getParameters()[0];
                Object dependency = resolveByType(param.getType(), method);
                method.setAccessible(true);
                try {
                    method.invoke(instance, dependency);
                    logger.fine(() -> "Injected via setter: " + clazz.getSimpleName() + "." + method.getName());
                } catch (Exception e) {
                    throw new RuntimeException("Failed to inject via setter: " + method.getName(), e);
                }
            }
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
            logger.fine(() -> "Resolving " + requiredType.getSimpleName() + " via @Qualifier(\"" + qualifier.value() + "\")");
            return getSnowflake(qualifier.value(), requiredType);
        }

        List<SnowflakeDefinition> candidates = definitions.values().stream()
                .filter(def -> requiredType.isAssignableFrom(def.getSnowflakeClass()))
                .toList();

        if (candidates.size() == 1) {
            logger.fine(() -> "Resolved " + requiredType.getSimpleName() + " by type -> " + candidates.getFirst().getSnowflakeName());
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