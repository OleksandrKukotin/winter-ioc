package org.github.oleksandrkukotin.core.annotation;

import org.github.oleksandrkukotin.core.Scope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a managed component in the Winter IoC container.
 * Equivalent to Spring's {@code @Component}.
 *
 * Annotated classes are discovered during classpath scanning (via {@code SnowflakeScanner})
 * and registered as {@code SnowflakeDefinition} entries in {@code SimpleSnowflakeFactory}.
 *
 * Attributes:
 *   - name:  the bean name used to look it up via {@code getSnowflake()}.
 *            Defaults to the simple class name if left blank.
 *   - scope: controls the instantiation strategy.
 *            {@code SINGLETON} (default) — one shared instance per container.
 *            {@code PROTOTYPE} — a new instance is created on every {@code getSnowflake()} call.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Snowflake {
    String name() default "";
    Scope scope() default Scope.SINGLETON;
}
