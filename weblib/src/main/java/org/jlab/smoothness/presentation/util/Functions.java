package org.jlab.smoothness.presentation.util;

/**
 *
 * @author ryans
 */
public final class Functions {

    private Functions() {
        // cannot instantiate publicly
    }

    public static boolean inArray(String[] haystack, String needle) {
        boolean inArray = false;

        if (needle != null && haystack != null) {
            for (String s : haystack) {
                if (needle.equals(s)) {
                    inArray = true;
                    break;
                }
            }
        }

        return inArray;
    }
}
