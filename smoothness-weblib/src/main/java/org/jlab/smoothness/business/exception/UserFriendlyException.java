package org.jlab.smoothness.business.exception;

/**
 * A user friendly exception is one in which the message is phrased for users to view directly.  A
 * more developer focused cause may be linked.
 *
 * @author ryans
 */
public class UserFriendlyException extends WebApplicationException {
    /**
     * Create a new UserFriendlyException with the provided message.
     *
     * @param msg The message
     */
    public UserFriendlyException(String msg) {
        super(msg);
    }

    /**
     * Create a new UserFriendlyException with the provided message and cause.
     *
     * @param msg The message
     * @param cause The cause
     */
    public UserFriendlyException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
