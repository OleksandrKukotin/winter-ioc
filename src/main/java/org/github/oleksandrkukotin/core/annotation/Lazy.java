package org.github.oleksandrkukotin.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a @Snowflake bean for lazy initialization.
 * By default the container may eagerly create all singleton beans at scan time.
 * A bean annotated with @Lazy should only be instantiated on the first call
 * to getSnowflake() that requests it.
 *
 * Implementation hint:
 *   The simplest approach is to do nothing during scan() for @Lazy beans
 *   and let getSnowflake() trigger creation as usual — since the singleton
 *   cache already defers until first access, you mainly need to make sure
 *   eager pre-instantiation (if you add it later) skips @Lazy beans.
 *
 *   Check for this annotation in scan() or in a future preInstantiateSingletons()
 *   method and exclude marked beans from early creation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Lazy {
}
