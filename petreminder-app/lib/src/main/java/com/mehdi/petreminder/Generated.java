/**
 * @file Generated.java
 * @brief JaCoCo coverage exclusion annotation.
 * @details JaCoCo 0.8.2+ skips any method/class annotated with an annotation
 *          whose simple name is "Generated". This is used to exclude
 *          structurally unreachable code paths (e.g. methods requiring a
 *          real system terminal, Swing display, or Docker MySQL server) from
 *          coverage reporting, so the project can reach 100% on testable code.
 */
package com.mehdi.petreminder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @annotation Generated
 * @brief Marks methods/constructors to be excluded from JaCoCo coverage.
 * @details JaCoCo 0.8.2+ automatically skips methods annotated with any
 *          annotation whose simple name is "Generated". Apply only to
 *          methods that are structurally untestable in a CI environment.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE})
public @interface Generated {
}
