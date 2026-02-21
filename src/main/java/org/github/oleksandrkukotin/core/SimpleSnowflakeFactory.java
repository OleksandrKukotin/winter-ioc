package org.github.oleksandrkukotin.core;

import java.lang.reflect.Constructor;
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
            Constructor<?> constructor = definition.getSnowflakeClass().getDeclaredConstructor();
            constructor.setAccessible(true);

            Object instance = constructor.newInstance();
            return type.cast(instance);
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate " + definition.getSnowflakeClass(), e);
        }
    }

    public void saveInSingletonCache(SnowflakeDefinition snowflakeDefinition) {
        if (snowflakeDefinition == null || snowflakeDefinition.getSnowflakeScope().equals(Scope.PROTOTYPE)) {
            throw new IllegalArgumentException("Snowflake definition cannot be null or has prototype scope");
        }
        singletonCache.put(snowflakeDefinition.getSnowflakeName(), snowflakeDefinition);
    }

    private boolean isInSingletonCache(String snowflakeName) {
        return singletonCache.containsKey(snowflakeName);
    }
}
