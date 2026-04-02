package org.github.oleksandrkukotin.core;

public class SnowflakeDefinition {

    private final Class<?> snowflakeClass;
    private final String snowflakeName;
    private final Scope snowflakeScope;
    private final boolean isLazy;

    public SnowflakeDefinition(Class<?> snowflakeClass, String snowflakeName) {
        this(snowflakeClass, snowflakeName, Scope.SINGLETON, false);
    }

    public SnowflakeDefinition(Class<?> snowflakeClass, String snowflakeName, Scope snowflakeScope) {
        this(snowflakeClass, snowflakeName, snowflakeScope,false);
    }

    public SnowflakeDefinition(Class<?> snowflakeClass, String snowflakeName, Scope snowflakeScope, boolean isLazy) {
        checkConstructorArguments(snowflakeClass, snowflakeName);
        this.snowflakeClass = snowflakeClass;
        this.snowflakeName = snowflakeName;
        this.snowflakeScope = snowflakeScope;
        this.isLazy = isLazy;
    }

    private static void checkConstructorArguments(Class<?> snowflakeClass, String snowflakeName) {
        if (snowflakeClass == null) {
            throw new IllegalArgumentException("Snowflake class cannot be null");
        }
        if (snowflakeName == null || snowflakeName.isBlank()) {
            throw new IllegalArgumentException("Snowflake name cannot be null or empty");
        }
    }

    public Class<?> getSnowflakeClass() {
        return snowflakeClass;
    }

    public String getSnowflakeName() {
        return snowflakeName;
    }

    public Scope getSnowflakeScope() {
        return snowflakeScope;
    }

    public boolean isLazy() {
        return isLazy;
    }
}
