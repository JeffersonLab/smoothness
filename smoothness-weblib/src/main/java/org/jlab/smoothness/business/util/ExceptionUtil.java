package org.jlab.smoothness.business.util;

/**
 * Exception Utilities.
 *
 * @author ryans
 */
public final class ExceptionUtil {
    
    private ExceptionUtil() {
        // No one can instantiate due to private visibility
    }

    /**
     * Traverse the cause of a Throwable until the root is reached.
     *
     * @param t The Throwable
     * @return The root cause
     */
    public static Throwable getRootCause(Throwable t) {
        while(t != null && t.getCause() != null) {
            t = t.getCause();
        }
        
        return t;
    }
}
