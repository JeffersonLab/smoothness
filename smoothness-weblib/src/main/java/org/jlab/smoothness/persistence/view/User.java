package org.jlab.smoothness.persistence.view;

/** A simple immutable view of a User entity. */
public final class User implements Comparable<User> {
  private final String username;
  private final String firstname;
  private final String lastname;
  private final String email;

  /**
   * Create a new User
   *
   * @param username The username
   * @param firstname The firstname
   * @param lastname The lastname
   * @param email The email address
   */
  public User(String username, String firstname, String lastname, String email) {
    this.username = username;
    this.firstname = firstname;
    this.lastname = lastname;
    this.email = email;
  }

  /**
   * Get the username.
   *
   * @return The username
   */
  public String getUsername() {
    return username;
  }

  /**
   * Get the firstname.
   *
   * @return The firstname
   */
  public String getFirstname() {
    return firstname;
  }

  /**
   * Get the lastname.
   *
   * @return The lastname
   */
  public String getLastname() {
    return lastname;
  }

  /**
   * Get the email.
   *
   * @return The email
   */
  public String getEmail() {
    return email;
  }

  /**
   * Get a String representation.
   *
   * @return The String representation
   */
  public String toString() {
    return firstname + " " + lastname + "(" + username + ") [" + email + "]";
  }

  /**
   * Compare this User to another by username asc.
   *
   * @param other The other User
   * @return 0 if equal, less than 0 if less than other, more than 0 if greater than other
   */
  @Override
  public int compareTo(User other) {
    return username.compareTo(other.username);
  }
}
