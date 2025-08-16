package com.loiane.product.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation annotation for product SKU format validation.
 * Validates that SKU follows the pattern: uppercase letters, numbers, and hyphens only.
 */
@Documented
@Constraint(validatedBy = ValidSkuValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSku {

    String message() default "SKU must contain only uppercase letters, numbers, and hyphens (e.g., IPH-15-PRO-256)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Minimum length for SKU (default: 3)
     */
    int minLength() default 3;

    /**
     * Maximum length for SKU (default: 64)
     */
    int maxLength() default 64;

    /**
     * Allow null values (default: false)
     */
    boolean nullable() default false;
}
