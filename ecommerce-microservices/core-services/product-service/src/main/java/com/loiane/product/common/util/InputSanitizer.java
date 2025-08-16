package com.loiane.product.common.util;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.regex.Pattern;

/**
 * Utility class for sanitizing input parameters to prevent XSS attacks
 * and ensure data integrity.
 */
@Schema(description = "Input sanitization utility for security and data integrity")
public final class InputSanitizer {

    // Patterns for dangerous characters and scripts
    private static final Pattern SCRIPT_PATTERN = Pattern.compile(
            "<script[^>]*>.*?</script>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );
    private static final Pattern HTML_PATTERN = Pattern.compile(
            "<[^>]+>", Pattern.CASE_INSENSITIVE
    );
    // Improved SQL injection pattern (covers comments, logical operators, and common payloads)
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
            "(?i)(union(\\s+all)?\\s+select|select\\s+.*\\s+from|insert\\s+into|update\\s+.*\\s+set|delete\\s+from|drop\\s+table|create\\s+table|alter\\s+table|exec\\s+|execute\\s+|--|;|\\bor\\b|\\band\\b|\\bwhere\\b|\\bgroup\\s+by\\b|\\border\\s+by\\b|\\bhaving\\b|\\bwaitfor\\b|\\bsleep\\b|\\bbenchmark\\b)",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );
    // For production, consider using a dedicated SQL injection prevention library such as OWASP ESAPI or parameterized queries.

    // Maximum lengths for different field types
    private static final int MAX_SEARCH_LENGTH = 100;
    private static final int MAX_NAME_LENGTH = 160;
    private static final int MAX_SLUG_LENGTH = 180;
    private static final int MAX_SKU_LENGTH = 64;
    private static final int MAX_BRAND_LENGTH = 120;

    private InputSanitizer() {
        // Utility class - prevent instantiation
    }

    /**
     * Sanitize search query parameters to prevent XSS and limit length
     */
    @Schema(description = "Sanitize search query string")
    public static String sanitizeSearchQuery(String query) {
        if (query == null) {
            return null;
        }

        String sanitized = query.trim();

        // Remove script tags and HTML
        sanitized = SCRIPT_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = HTML_PATTERN.matcher(sanitized).replaceAll("");

        // Remove potential SQL injection patterns
        sanitized = SQL_INJECTION_PATTERN.matcher(sanitized).replaceAll("");

        // Limit length
        if (sanitized.length() > MAX_SEARCH_LENGTH) {
            sanitized = sanitized.substring(0, MAX_SEARCH_LENGTH);
        }

        return sanitized.isEmpty() ? null : sanitized;
    }

    /**
     * Sanitize product name
     */
    @Schema(description = "Sanitize product name field")
    public static String sanitizeName(String name) {
        return sanitizeWithLength(name, MAX_NAME_LENGTH);
    }

    /**
     * Sanitize slug field
     */
    @Schema(description = "Sanitize slug field")
    public static String sanitizeSlug(String slug) {
        if (slug == null) {
            return null;
        }

        String sanitized = slug.trim().toLowerCase();

        // Remove HTML and scripts
        sanitized = SCRIPT_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = HTML_PATTERN.matcher(sanitized).replaceAll("");

        // Keep only valid slug characters: lowercase letters, numbers, hyphens
        sanitized = sanitized.replaceAll("[^a-z0-9-]", "");

        // Remove multiple consecutive hyphens
        sanitized = sanitized.replaceAll("-{2,}", "-");

        // Remove leading/trailing hyphens
        sanitized = sanitized.replaceAll("(^-+)|(-+$)", "");

        // Limit length
        if (sanitized.length() > MAX_SLUG_LENGTH) {
            sanitized = sanitized.substring(0, MAX_SLUG_LENGTH);
            // Ensure we don't end with a hyphen after truncation
            sanitized = sanitized.replaceAll("-+$", "");
        }

        return sanitized.isEmpty() ? null : sanitized;
    }

    /**
     * Sanitize SKU field
     */
    @Schema(description = "Sanitize SKU field")
    public static String sanitizeSku(String sku) {
        if (sku == null) {
            return null;
        }

        String sanitized = sku.trim().toUpperCase();

        // Remove HTML and scripts
        sanitized = SCRIPT_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = HTML_PATTERN.matcher(sanitized).replaceAll("");

        // Keep only valid SKU characters: uppercase letters, numbers, hyphens
        sanitized = sanitized.replaceAll("[^A-Z0-9-]", "");

        // Limit length
        if (sanitized.length() > MAX_SKU_LENGTH) {
            sanitized = sanitized.substring(0, MAX_SKU_LENGTH);
        }

        return sanitized.isEmpty() ? null : sanitized;
    }

    /**
     * Sanitize brand field
     */
    @Schema(description = "Sanitize brand field")
    public static String sanitizeBrand(String brand) {
        return sanitizeWithLength(brand, MAX_BRAND_LENGTH);
    }

    /**
     * Generic sanitization with length limit
     */
    private static String sanitizeWithLength(String input, int maxLength) {
        if (input == null) {
            return null;
        }

        String sanitized = input.trim();

        // Remove script tags and HTML
        sanitized = SCRIPT_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = HTML_PATTERN.matcher(sanitized).replaceAll("");

        // Remove potential SQL injection patterns
        sanitized = SQL_INJECTION_PATTERN.matcher(sanitized).replaceAll("");

        // Limit length
        if (sanitized.length() > maxLength) {
            sanitized = sanitized.substring(0, maxLength);
        }

        return sanitized.isEmpty() ? null : sanitized;
    }

    /**
     * Validate that a string doesn't contain dangerous patterns
     */
    @Schema(description = "Check if string contains dangerous patterns")
    public static boolean containsDangerousPatterns(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }

        return SCRIPT_PATTERN.matcher(input).find() ||
               SQL_INJECTION_PATTERN.matcher(input).find();
    }

    /**
     * Clean string from null, empty, or whitespace-only values
     */
    @Schema(description = "Clean string from null/empty/whitespace values")
    public static String cleanString(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        return input.trim();
    }
}
