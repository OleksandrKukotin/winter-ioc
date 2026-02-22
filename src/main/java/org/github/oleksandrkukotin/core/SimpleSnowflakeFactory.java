package org.github.oleksandrkukotin.core;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class SimpleSnowflakeFactory {

    private final Map<String, SnowflakeDefinition> definitions = new HashMap<>();
    private final Map<String, Object> singletonCache = new HashMap<>();

    public void registerDefinition(SnowflakeDefinition definition) {
        definitions.put(definition.getSnowflakeName(), definition);
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

    private boolean isInSingletonCache(String snowflakeName) {
        return singletonCache.containsKey(snowflakeName);
    }
}
