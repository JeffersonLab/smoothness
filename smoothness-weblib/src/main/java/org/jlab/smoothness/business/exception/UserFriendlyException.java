package org.jlab.smoothness.business.exception;

/**
 * A user friendly exception is one in which the message is phrased for users to view directly. A
 * more developer focused cause may be linked.
 *
 * @author ryans
 */
public class UserFriendlyException extends WebApplicationException {
  /** Message explicitly intended for user consumption. */
  private final String userMessage;

  /**
   * Create a new UserFriendlyException with the provided message.
   *
   * @param msg The message
   */
  public UserFriendlyException(String msg) {
    super(msg);
    userMessage = msg;
  }

  /**
   * Create a new UserFriendlyException with the provided message and cause.
   *
   * @param msg The message
   * @param cause The cause
   */
  public UserFriendlyException(String msg, Throwable cause) {
    super(msg, cause);
    userMessage = msg;
  }

  /**
   * Get message explicitly intended for user consumption.
   *
   * <p>This is a workaround for CodeQL whining about any use of getMessage() being dangerous, even
   * if the intent is to relay a message directly to the user.
   *
   * @return The user message
   */
  public String getUserMessage() {
    return userMessage;
  }
}
