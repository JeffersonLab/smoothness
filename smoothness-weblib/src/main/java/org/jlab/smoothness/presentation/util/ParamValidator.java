package org.jlab.smoothness.presentation.util;

import java.util.Date;

/**
 * Utility methods for validating parameters and throwing an IllegalArgumentException if validation fails.
 *
 * @author ryans
 */
public final class ParamValidator {
    private ParamValidator() {
        // No one can instantiate due to private visibility
    }

    /**
     * Validate that an Object is not null.
     *
     * @param name The parameter name
     * @param value The parameter value
     */
    public static void validateNonNull(String name, Object value) {
        if(value == null) {
            throw new IllegalArgumentException(name + " must not be empty");
        }
    }

    /**
     * Validate that an Integer is not null and between 0 and 100 inclusive.
     *
     * @param name The parameter name
     * @param value The parameter value
     */
    public static void validatePercent(String name, Integer value) {

        if (value == null) {
            throw new IllegalArgumentException(name + " must not be empty");
        }

        int v = value;

        if (v < 0) {
            throw new IllegalArgumentException(name + " must not be negative");
        }

        if (v > 100) {
            throw new IllegalArgumentException(name + " must not be more than 100");
        }
    }

    /**
     * Validate that an Integer is not null and non negative.
     *
     * @param name The parameter name
     * @param value The parameter value
     */
    public static void validateNonNegative(String name, Integer value) {
        if (value == null) {
            throw new IllegalArgumentException(name + " must not be empty");
        }


        if (value < 0) {
            throw new IllegalArgumentException(name + " must not be negative");
        }
    }

    /**
     * Validate that a start and end date range is not null and end does not come before start.
     *
     * @param startName The start parameter name
     * @param endName The end parameter name
     * @param start The start value
     * @param end The end value
     */
    public static void validateDateRange(String startName, String endName, Date start, Date end) {
        if(start == null) {
            throw new IllegalArgumentException(startName + " must not be empty");
        }

        if(end == null) {
            throw new IllegalArgumentException(endName + " must not be empty");
        }

        if(end.before(start)) {
            throw new IllegalArgumentException(endName + " must not come before " + startName);
        }
    }
}
