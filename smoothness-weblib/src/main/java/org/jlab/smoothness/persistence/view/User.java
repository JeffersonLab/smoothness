package org.jlab.smoothness.persistence.view;

/**
 * A simple immutable view of a User entity.
 */
public final class User {
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
}
