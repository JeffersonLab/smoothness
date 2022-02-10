package org.jlab.smoothness.business.exception;

import javax.ejb.ApplicationException;

/**
 * A WebApplicationException is designed as a generic Exception, and it is an EJB @ApplicationException so
 * that it is not wrapped when thrown from an EJB.  This class is abstract as you must throw one of it's subclasses.
 *
 * @author ryans
 */
@ApplicationException(inherited = true, rollback = true)
public abstract class WebApplicationException extends Exception {
    /**
     * Create a new WebApplicationException with the provided message.
     *
     * @param msg The message
     */
    public WebApplicationException(String msg) {
        super(msg);
    }

    /**
     * Create a new WebApplicationException with the provided message and cause.
     *
     * @param msg The message
     * @param cause The cause
     */
    public WebApplicationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
