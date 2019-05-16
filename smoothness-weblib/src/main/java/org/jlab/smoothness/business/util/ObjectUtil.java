package org.jlab.smoothness.business.util;

/**
 *
 * @author ryans
 */
public class ObjectUtil {

    private ObjectUtil() {
        // private constructor
    }
    
    @SafeVarargs
    public static <T> T coalesce(T... items) {
        for (T i : items) {
            if (i != null) {
                return i;
            }
        }
        return null;
    }

}
