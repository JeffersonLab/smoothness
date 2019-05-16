package org.jlab.smoothness.business.exception;

import javax.ejb.ApplicationException;

/**
 * A user friendly exception is one in which the message is phrased for users to view directly.  A
 * more developer focused cause may be linked.
 *
 * A UserFriendlyExecption is designed as a generic Web Application Exception, and it is an EJB @ApplicationException so
 * that it is not wrapped when thrown from an EJB.
 *
 * @author ryans
 */
@ApplicationException(inherited = true, rollback = true)
public class UserFriendlyException extends Exception {
    public UserFriendlyException(String msg) {
        super(msg);
    }
    
    public UserFriendlyException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
