package org.jlab.smoothness.presentation.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jlab.smoothness.business.service.UserAuthorizationService;
import org.jlab.smoothness.business.util.TimeUtil;
import org.jlab.smoothness.persistence.view.User;

/**
 * Utility functions.
 *
 * @author ryans
 */
public final class Functions {

  private Functions() {
    // cannot instantiate publicly
  }

  /**
   * Check if an object is in an array.
   *
   * @param haystack The array to search
   * @param needle The object to search for
   * @return true if in array, false otherwise
   */
  public static boolean inArray(Object[] haystack, Object needle) {
    boolean inArray = false;

    if (needle != null && haystack != null) {
      for (Object s : haystack) {
        if (needle.equals(s)) {
          inArray = true;
          break;
        }
      }
    }

    return inArray;
  }

  /**
   * Return a friendly date placeholder.
   *
   * @return The date placeholder
   */
  public static String getFriendlyDatePlaceholder() {
    return TimeUtil.getFriendlyDatePlaceholder();
  }

  /**
   * Return a friendly date-time placeholder.
   *
   * @return The date-time placeholder
   */
  public static String getFriendlyDateTimePlaceholder() {
    return TimeUtil.getFriendlyDateTimePlaceholder();
  }

  /**
   * Return a friendly date pattern.
   *
   * @return The date pattern
   */
  public static String getFriendlyDatePattern() {
    return TimeUtil.getFriendlyDatePattern();
  }

  /**
   * Return a friendly date-time pattern.
   *
   * @return The date-time pattern
   */
  public static String getFriendlyDateTimePattern() {
    return TimeUtil.getFriendlyDateTimePattern();
  }

  /**
   * Obtain a User from a username.
   *
   * @param username The username
   * @return The User
   */
  public static User lookupUserByUsername(String username) {
    UserAuthorizationService auth = UserAuthorizationService.getInstance();

    return auth.getUserFromUsername(username);
  }

  /**
   * Format a user by username. A Smoothness standard format.
   *
   * @param username The username
   * @return The formatted string
   */
  public static String formatUsername(String username) {
    User user = lookupUserByUsername(username);

    if (user != null) {
      return formatUser(user);
    } else {
      return username;
    }
  }

  /**
   * Return a CSV of all usernames that are members of a given role, sorted by username asc.
   *
   * @param role The role to query
   * @return A String CSV formatted of usernames, empty if none
   */
  public static String getMemberUsernameCsv(String role) {
    UserAuthorizationService auth = UserAuthorizationService.getInstance();

    String usernameCsv = "";

    if (role != null) {
      List<User> userList = new ArrayList<>(auth.getUsersInRole(role));

      Collections.sort(userList);

      if (userList != null) {
        for (int i = 0; i < userList.size(); i++) {
          User user = userList.get(i);
          usernameCsv += user.getUsername();
          if (i < userList.size() - 1) {
            usernameCsv += ",";
          }
        }
      }
    }

    return usernameCsv;
  }

  /**
   * Format a user by User object. A Smoothness standard format.
   *
   * @param user The User
   * @return The formatted string
   */
  public static String formatUser(User user) {
    StringBuilder builder = new StringBuilder();

    if (user != null && user.getUsername() != null && !user.getUsername().isEmpty()) {
      if (user.getFirstname() == null
          || user.getLastname() == null
          || user.getFirstname().isEmpty()
          || user.getLastname().isEmpty()) {
        builder.append("(");
        builder.append(user.getUsername());
        builder.append(")");
      } else {
        builder.append(user.getLastname());
        builder.append(", ");
        builder.append(user.getFirstname());
        builder.append(" (");
        builder.append(user.getUsername());
        builder.append(")");
      }
    }

    return builder.toString();
  }
}
