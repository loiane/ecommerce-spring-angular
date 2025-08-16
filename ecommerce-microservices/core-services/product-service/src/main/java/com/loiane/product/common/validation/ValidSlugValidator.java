package com.loiane.product.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Validator for slug format validation.
 * Validates that slug contains only lowercase letters, numbers, and hyphens.
 */
public class ValidSlugValidator implements ConstraintValidator<ValidSlug, String> {

    private static final Pattern SLUG_PATTERN = Pattern.compile("^[a-z0-9-]+$");
    private static final String HYPHEN = "-";

    private int minLength;
    private int maxLength;
    private boolean nullable;

    @Override
    public void initialize(ValidSlug constraintAnnotation) {
        this.minLength = constraintAnnotation.minLength();
        this.maxLength = constraintAnnotation.maxLength();
        this.nullable = constraintAnnotation.nullable();
    }

    @Override
    public boolean isValid(String slug, ConstraintValidatorContext context) {
        // Handle null values
        if (slug == null) {
            return nullable;
        }

        // Validate length first
        if (slug.length() < minLength) {
            addCustomMessage(context, String.format("Slug must be at least %d characters long", minLength));
            return false;
        }
        if (slug.length() > maxLength) {
            addCustomMessage(context, String.format("Slug cannot exceed %d characters", maxLength));
            return false;
        }

        // Validate format - slug must be exactly in correct format (no sanitization)
        if (!SLUG_PATTERN.matcher(slug).matches()) {
            addCustomMessage(context, "Slug must contain only lowercase letters, numbers, and hyphens");
            return false;
        }

        // Additional business rules
        if (slug.startsWith(HYPHEN) || slug.endsWith(HYPHEN)) {
            addCustomMessage(context, "Slug cannot start or end with a hyphen");
            return false;
        }

        if (slug.contains(HYPHEN + HYPHEN)) {
            addCustomMessage(context, "Slug cannot contain consecutive hyphens");
            return false;
        }

        return true;
    }

    private void addCustomMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
