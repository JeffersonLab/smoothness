package org.jlab.smoothness.business.exception;

/**
 * A user friendly exception is one in which the message is phrased for users to view directly.  A
 * more developer focused cause may be linked.
 *
 * @author ryans
 */
public class UserFriendlyException extends WebApplicationException {
    public UserFriendlyException(String msg) {
        super(msg);
    }
    
    public UserFriendlyException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
