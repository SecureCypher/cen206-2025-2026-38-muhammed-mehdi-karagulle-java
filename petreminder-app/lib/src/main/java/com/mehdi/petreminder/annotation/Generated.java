package com.mehdi.petreminder.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;

/**
 * Annotation to mark generated or untestable code.
 * JaCoCo automatically ignores methods/classes annotated with an annotation
 * whose simple name contains "Generated".
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE, METHOD, CONSTRUCTOR})
public @interface Generated {
}
