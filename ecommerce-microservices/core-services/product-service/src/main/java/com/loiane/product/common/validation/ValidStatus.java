package com.loiane.product.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation annotation for status values validation.
 * Validates that status is one of the allowed values: ACTIVE, INACTIVE, DRAFT.
 */
@Documented
@Constraint(validatedBy = ValidStatusValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidStatus {

    String message() default "Status must be one of: ACTIVE, INACTIVE, DRAFT";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Allowed status values (default: ACTIVE, INACTIVE, DRAFT)
     */
    String[] allowedValues() default {"ACTIVE", "INACTIVE", "DRAFT"};

    /**
     * Allow null values (default: false)
     */
    boolean nullable() default false;
}
