package org.jlab.smoothness.presentation.util;

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
}
