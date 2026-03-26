package org.github.oleksandrkukotin.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field or setter method as an injection point.
 * The container should resolve and inject the dependency automatically.
 *
 * Supports two injection modes:
 *   - Field injection:  applied directly on a field
 *   - Setter injection: applied on a single-argument method (setXxx)
 *
 * In SimpleSnowflakeFactory, after constructing an instance you'll need to:
 *   1. Scan declared fields / methods for this annotation via reflection
 *   2. Resolve the dependency by type (and optionally by @Qualifier name)
 *   3. Make the field/method accessible and inject the value
 *
 * Note: field injection requires calling field.setAccessible(true) because
 * fields are usually private.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Melt {
}
