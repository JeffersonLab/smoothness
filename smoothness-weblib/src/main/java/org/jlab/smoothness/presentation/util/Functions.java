package org.jlab.smoothness.presentation.util;

import org.jlab.smoothness.business.util.TimeUtil;

/**
 *
 * @author ryans
 */
public final class Functions {

    private Functions() {
        // cannot instantiate publicly
    }

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

    public static String getFriendlyDatePlaceholder() {
        return TimeUtil.getFriendlyDatePlaceholder();
    }

    public static String getFriendlyDateTimePlaceholder() {
        return TimeUtil.getFriendlyDateTimePlaceholder();
    }

    public static String getFriendlyDatePattern() {
        return TimeUtil.getFriendlyDatePattern();
    }

    public static String getFriendlyDateTimePattern() {
        return TimeUtil.getFriendlyDateTimePattern();
    }
}
