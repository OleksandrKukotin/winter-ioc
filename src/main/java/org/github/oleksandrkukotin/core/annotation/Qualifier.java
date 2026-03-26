package org.github.oleksandrkukotin.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies which bean to inject when multiple implementations of the same
 * type are registered in the container.
 *
 * Example scenario:
 *   Two classes implement Notifier: EmailService and SmsService.
 *   Without @Qualifier the container doesn't know which one to pick —
 *   it should throw an exception (NoUniqueBeanException or similar).
 *   With @Qualifier("EmailService") it knows exactly which bean to use.
 *
 * Can be placed:
 *   - On a constructor parameter   — narrows injection for that argument
 *   - On a @Melt field or setter   — narrows injection at that injection point
 *
 * In SimpleSnowflakeFactory, during dependency resolution you'll need to:
 *   1. Check whether the parameter/field carries this annotation
 *   2. If present, look up by the qualifier name instead of the type name
 *   3. If absent and multiple candidates exist, throw a descriptive exception
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
public @interface Qualifier {

    String value() default "";
}
