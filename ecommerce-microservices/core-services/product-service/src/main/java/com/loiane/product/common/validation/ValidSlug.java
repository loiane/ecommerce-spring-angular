package com.loiane.product.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation annotation for slug format validation.
 * Validates that slug follows the pattern: lowercase letters, numbers, and hyphens only.
 */
@Documented
@Constraint(validatedBy = ValidSlugValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSlug {

    String message() default "Slug must contain only lowercase letters, numbers, and hyphens (e.g., iphone-15-pro)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Minimum length for slug (default: 2)
     */
    int minLength() default 2;

    /**
     * Maximum length for slug (default: 180)
     */
    int maxLength() default 180;

    /**
     * Allow null values (default: false)
     */
    boolean nullable() default false;
}
