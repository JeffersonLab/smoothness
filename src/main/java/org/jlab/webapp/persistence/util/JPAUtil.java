package org.jlab.webapp.persistence.util;

import java.util.Collection;

/**
 *
 * @author ryans
 */
public class JPAUtil {

    private JPAUtil() {
        // Hidden constructor
    }

    public static void initialize(Collection c) {
        if (c != null) {
            for (Object o : c) {
                JPAUtil.initialize(o);
            }
        }
    }

    /**
     * This method simply documents that a user is attempting to force a proxy
     * object to load (callers could easily invoke an arbitrary method)
     */
    public static void initialize(Object o) {
        if (o != null) {
            o.getClass(); // tickle proxy object
        }
    }
}
