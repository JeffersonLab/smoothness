package org.jlab.smoothness.presentation.util;

import javax.servlet.http.HttpServletRequest;

/**
 * Utility methods for combined conversion and validation, with optional default values.
 *
 * @author ryans
 */
public final class ParamUtil {
    private ParamUtil() {
        // No one can instantiate due to private visibility
    }

    /**
     * Convert and validate a non-negative int, and throw an IllegalArgumentException if null.
     *
     * @param request The request
     * @param name The parameter name
     * @return The int
     */
    public static int convertAndValidateNonNegativeInt(HttpServletRequest request, String name) {
        Integer value = ParamConverter.convertInteger(request, name);

        ParamValidator.validateNonNegative(name, value);

        return value;
    }

    /**
     * Convert and validate a non-negative int, and provide a default value if null.
     *
     * @param request The request
     * @param name The parameter name
     * @param defaultValue The default to use if the parameter is null
     * @return The int
     */
    public static int convertAndValidateNonNegativeInt(HttpServletRequest request, String name, int defaultValue) {
        Integer value = ParamConverter.convertInteger(request, name);

        if(value == null) {
            value = defaultValue;
        } else {
            ParamValidator.validateNonNegative(name, value);
        }

        return value;
    }

    /**
     * Convert and validate a non-negative Integer, and allow null values.
     *
     * @param request The request
     * @param name The parameter name
     * @return The Integer
     */
    public static Integer convertAndValidateNonNegativeInteger(HttpServletRequest request, String name) {
        Integer value = ParamConverter.convertInteger(request, name);

        if(value != null) {
            ParamValidator.validateNonNegative(name, value);
        }

        return value;
    }

    /**
     * Convert and validate a Y/N boolean, and throw an IllegalArgumentException if null.
     *
     * @param request The request
     * @param name The parameter name
     * @return The boolean
     */
    public static boolean convertAndValidateYNBoolean(HttpServletRequest request, String name) {
        Boolean value = ParamConverter.convertYNBoolean(request, name);

        ParamValidator.validateNonNull(name, value);

        return value;
    }

    /**
     * Convert and validate a Y/N boolean, and provide a default value if null.
     *
     * @param request The request
     * @param name The parameter name
     * @param defaultValue The default to use if the parameter is null
     * @return The boolean
     */
    public static boolean convertAndValidateYNBoolean(HttpServletRequest request, String name, boolean defaultValue) {
        Boolean value = ParamConverter.convertYNBoolean(request, name);

        if(value == null) {
            value = defaultValue;
        }

        return value;
    }
}
