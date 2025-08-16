package com.loiane.product.common.validation;

import com.loiane.product.common.util.InputSanitizer;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Validator for SKU format validation.
 * Validates that SKU contains only uppercase letters, numbers, and hyphens.
 */
public class ValidSkuValidator implements ConstraintValidator<ValidSku, String> {

    private static final Pattern SKU_PATTERN = Pattern.compile("^[A-Z0-9-]+$");
    private static final String HYPHEN = "-";
    private static final String DOUBLE_HYPHEN = "--";

    private int minLength;
    private int maxLength;
    private boolean nullable;

    @Override
    public void initialize(ValidSku constraintAnnotation) {
        this.minLength = constraintAnnotation.minLength();
        this.maxLength = constraintAnnotation.maxLength();
        this.nullable = constraintAnnotation.nullable();
    }

    @Override
    public boolean isValid(String sku, ConstraintValidatorContext context) {
        // Handle null values
        if (sku == null) {
            return nullable;
        }

        // Sanitize the SKU first
        String sanitizedSku = InputSanitizer.sanitizeSku(sku);
        if (sanitizedSku == null) {
            addCustomMessage(context, "SKU cannot be empty or contain only invalid characters");
            return false;
        }

        // Check if sanitized value differs from original (indicates potentially dangerous input)
        if (!sku.equals(sanitizedSku)) {
            addCustomMessage(context, "SKU contains invalid characters or format");
            return false;
        }

        // Validate length
        if (sanitizedSku.length() < minLength) {
            addCustomMessage(context, String.format("SKU must be at least %d characters long", minLength));
            return false;
        }
        if (sanitizedSku.length() > maxLength) {
            addCustomMessage(context, String.format("SKU cannot exceed %d characters", maxLength));
            return false;
        }

        // Validate format
        if (!SKU_PATTERN.matcher(sanitizedSku).matches()) {
            addCustomMessage(context, "SKU must contain only uppercase letters, numbers, and hyphens");
            return false;
        }

        // Additional business rules
        if (sanitizedSku.startsWith(HYPHEN) || sanitizedSku.endsWith(HYPHEN)) {
            addCustomMessage(context, "SKU cannot start or end with a hyphen");
            return false;
        }

        if (sanitizedSku.contains(DOUBLE_HYPHEN)) {
            addCustomMessage(context, "SKU cannot contain consecutive hyphens");
            return false;
        }

        return true;
    }

    private void addCustomMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
