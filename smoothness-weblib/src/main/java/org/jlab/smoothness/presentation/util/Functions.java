package org.jlab.smoothness.presentation.util;

import org.jlab.smoothness.business.util.TimeUtil;

/**
 * Utility functions.
 *
 * @author ryans
 */
public final class Functions {

    private Functions() {
        // cannot instantiate publicly
    }

    /**
     * Check if an object is in an array.
     *
     * @param haystack The array to search
     * @param needle The object to search for
     * @return true if in array, false otherwise
     */
    public static boolean inArray(Object[] haystack, Object needle) {
        boolean inArray = false;

        if (needle != null && haystack != null) {
            for (Object s : haystack) {
                if (needle.equals(s)) {
                    inArray = true;
                    break;
                }
            }
        }

        return inArray;
    }

    /**
     * Return a friendly date placeholder.
     *
     * @return The date placeholder
     */
    public static String getFriendlyDatePlaceholder() {
        return TimeUtil.getFriendlyDatePlaceholder();
    }

    /**
     * Return a friendly date-time placeholder.
     *
     * @return The date-time placeholder
     */
    public static String getFriendlyDateTimePlaceholder() {
        return TimeUtil.getFriendlyDateTimePlaceholder();
    }

    /**
     * Return a friendly date pattern.
     *
     * @return The date pattern
     */
    public static String getFriendlyDatePattern() {
        return TimeUtil.getFriendlyDatePattern();
    }

    /**
     * Return a friendly date-time pattern.
     *
     * @return The date-time pattern
     */
    public static String getFriendlyDateTimePattern() {
        return TimeUtil.getFriendlyDateTimePattern();
    }
}
