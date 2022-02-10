package org.jlab.smoothness.business.exception;

/**
 * An Exception with a message that should not be shown to the end user.  It should be used when there is nothing a user
 * can do and the internal details should not be leaked (a bug / assertion issue vs invalid user input).
 *
 * @author ryans
 */
public class InternalException extends WebApplicationException {
    /**
     * Create a new InternalException with a message.
     *
     * @param msg The message
     */
    public InternalException(String msg) {
        super(msg);
    }

    /**
     * Create a new InternalException with a message and cause.
     *
     * @param msg The message
     * @param cause The cause
     */
    public InternalException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
