package com.loiane.product.common.validation;

/**
 * Validation group marker interfaces for different operations.
 * These interfaces are used to group validation constraints and apply them
 * conditionally based on the operation being performed.
 */
public final class ValidationGroups {

    /**
     * Validation group for create operations.
     * Used when creating new resources where all required fields must be present.
     */
    public interface Create {}

    /**
     * Validation group for update operations.
     * Used when updating existing resources where some fields may be optional.
     */
    public interface Update {}

    /**
     * Validation group for partial update operations (PATCH).
     * Used for operations that modify only specific fields of a resource.
     */
    public interface PartialUpdate {}

    /**
     * Validation group for search operations.
     * Used when validating search parameters and filters.
     */
    public interface Search {}

    /**
     * Validation group for admin operations.
     * Used for operations that require elevated privileges.
     */
    public interface Admin {}

    private ValidationGroups() {
        // Utility class - prevent instantiation
    }
}
