package com.loiane.product.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validator for status values validation.
 * Validates that status is one of the allowed values.
 */
public class ValidStatusValidator implements ConstraintValidator<ValidStatus, String> {

    private Set<String> allowedValues;
    private boolean nullable;

    @Override
    public void initialize(ValidStatus constraintAnnotation) {
        this.allowedValues = Arrays.stream(constraintAnnotation.allowedValues())
                .collect(Collectors.toSet());
        this.nullable = constraintAnnotation.nullable();
    }

    @Override
    public boolean isValid(String status, ConstraintValidatorContext context) {
        // Handle null values
        if (status == null) {
            return nullable;
        }

        // Trim and validate
        String trimmedStatus = status.trim();
        if (trimmedStatus.isEmpty()) {
            addCustomMessage(context, "Status cannot be empty");
            return false;
        }

        // Check if status is in allowed values
        if (!allowedValues.contains(trimmedStatus.toUpperCase())) {
            addCustomMessage(context,
                String.format("Status must be one of: %s", String.join(", ", allowedValues)));
            return false;
        }

        return true;
    }

    private void addCustomMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
