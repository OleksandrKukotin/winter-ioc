package org.github.oleksandrkukotin.core.annotation;

import org.github.oleksandrkukotin.core.Scope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Snowflake {
    String name() default "";
    Scope scope() default Scope.SINGLETON;
}
