package org.jlab.smoothness.business.exception;

/**
 * An Exception with a message that should not be shown to the end user.  It should be used when there is nothing a user
 * can do and the internal details should not be leaked (a bug / assertion issue vs invalid user input).
 *
 * @author ryans
 */
public class InternalException extends WebApplicationException {
    public InternalException(String msg) {
        super(msg);
    }
    
    public InternalException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
