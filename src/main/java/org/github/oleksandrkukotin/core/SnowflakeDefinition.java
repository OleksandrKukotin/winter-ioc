package org.github.oleksandrkukotin.core;

public class SnowflakeDefinition {

    private final Class<?> snowflakeClass;
    private final String snowflakeName;
    private final Scope snowflakeScope;

    public SnowflakeDefinition(Class<?> snowflakeClass, String snowflakeName) {
        this(snowflakeClass, snowflakeName, Scope.SINGLETON);
    }

    public SnowflakeDefinition(Class<?> snowflakeClass, String snowflakeName, Scope snowflakeScope) {
        if (snowflakeClass == null) {
            throw new IllegalArgumentException("Snowflake class cannot be null");
        }
        if (snowflakeName == null || snowflakeName.isBlank()) {
            throw new IllegalArgumentException("Snowflake name cannot be null or empty");
        }
        this.snowflakeClass = snowflakeClass;
        this.snowflakeName = snowflakeName;
        this.snowflakeScope = snowflakeScope;
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
}
