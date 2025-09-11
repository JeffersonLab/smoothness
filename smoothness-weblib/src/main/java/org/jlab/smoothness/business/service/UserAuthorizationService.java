package org.jlab.smoothness.business.service;

import jakarta.ws.rs.NotFoundException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.smoothness.persistence.view.User;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;

/**
 * User Authorization Service. This class queries Keycloak for user data and caches it in memory to
 * avoid costly lookups. The cache can be cleared by either re-deploying the app, or via the clear
 * cache HTTP endpoint.
 */
public class UserAuthorizationService {
  private static final Logger LOGGER = Logger.getLogger(UserAuthorizationService.class.getName());

  private static UserAuthorizationService instance = null;
  private final ConcurrentHashMap<String, List<User>> usersInRole = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, User> userFromUsername = new ConcurrentHashMap<>();
  private final Keycloak keycloak;
  private final String realm;
  private final String resource;
  private final String secret;

  private UserAuthorizationService() {
    // Private constructor; must use factory method

    String keycloakServerUrl = System.getenv("KEYCLOAK_BACKEND_SERVER_URL");

    if (keycloakServerUrl == null) {
      throw new RuntimeException("KEYCLOAK_BACKEND_SERVER_URL env required");
    }

    realm = System.getenv("KEYCLOAK_REALM");

    if (realm == null) {
      throw new RuntimeException("KEYCLOAK_REALM env required");
    }

    resource = System.getenv("KEYCLOAK_RESOURCE");

    if (resource == null) {
      throw new RuntimeException("KEYCLOAK_RESOURCE env required");
    }

    secret = System.getenv("KEYCLOAK_SECRET");

    if (secret == null) {
      throw new RuntimeException("KEYCLOAK_SECRET env required");
    }

    keycloak =
        KeycloakBuilder.builder()
            .serverUrl(keycloakServerUrl)
            .realm(realm)
            .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
            .clientId(resource)
            .clientSecret(secret)
            .build();
  }

  /**
   * Get the singleton instance of this service.
   *
   * @return The service instance
   */
  public static synchronized UserAuthorizationService getInstance() {

    if (instance == null) {
      instance = new UserAuthorizationService();
    }

    return instance;
  }

  /** Clear the user data cache. */
  public void clearCache() {
    usersInRole.clear();
    userFromUsername.clear();
  }

  /**
   * Get the users that contain the given string in one of their attributes (username, firstname,
   * lastname, email). Keycloak is queried.
   *
   * @param search The search string
   * @param firstResult The index of the first result to return (pagination)
   * @param maxResults The maximum number of results to return (pagination)
   * @return The list of users matching the search string
   */
  public List<User> getUsersLike(String search, Integer firstResult, Integer maxResults) {

    List<User> users = new ArrayList<>();

    UsersResource usersResource = keycloak.realm(realm).users();

    List<UserRepresentation> reps = usersResource.search(search, firstResult, maxResults);

    for (UserRepresentation rep : reps) {
      User user =
          new User(rep.getUsername(), rep.getFirstName(), rep.getLastName(), rep.getEmail());

      users.add(user);
    }

    users.sort(Comparator.comparing(User::getLastname));

    return Collections.unmodifiableList(users);
  }

  /**
   * Count the users that contain the given string in one of their attributes (username, firstname,
   * lastname, email). Keycloak is queried.
   *
   * @param search The search string
   * @return The count of users matching the search string
   */
  public Integer countUsersLike(String search) {

    List<User> users = new ArrayList<>();

    UsersResource usersResource = keycloak.realm(realm).users();

    Integer count;

    if (search == null || search.isEmpty()) {
      count = usersResource.count();
    } else {
      count = usersResource.count(search);
    }

    return count;
  }

  /**
   * Get the users in a given role. Keycloak is queried if the local in-memory cache is empty.
   *
   * @param role The role to lookup
   * @return The list of users in the given role
   */
  public List<User> getUsersInRole(String role) {

    List<User> users = usersInRole.get(role);

    if (users == null) {
      users = lookupUsersInRole(role);

      usersInRole.put(role, users);
    }

    return users;
  }

  /**
   * Get the user data associated with the given username. Keycloak is queried if the local
   * in-memory cache is empty.
   *
   * <p>If the lookup fails, the User data other than username will be empty (but not null).
   *
   * @param username The username to lookup
   * @return The user data for the given username
   */
  public User getUserFromUsername(String username) {

    if (username == null) {
      return new User("", "", "", "");
    }

    User user = userFromUsername.get(username);

    if (user == null) {
      user = lookupUserFromUsername(username);

      userFromUsername.put(username, user);
    }

    return user;
  }

  private List<User> lookupUsersInRole(String role) {

    List<User> users = new ArrayList<>();

    RolesResource roles = keycloak.realm(realm).roles();

    try {
      RoleResource roleResource = roles.get(role);

      List<UserRepresentation> members = roleResource.getUserMembers(0, Integer.MAX_VALUE);

      for (UserRepresentation rep : members) {
        User user =
            new User(rep.getUsername(), rep.getFirstName(), rep.getLastName(), rep.getEmail());

        users.add(user);
      }

      users.sort(Comparator.comparing(User::getLastname));
    } catch (NotFoundException e) {
      LOGGER.log(Level.INFO, "Role not found in Keycloak: " + role);
    }

    return Collections.unmodifiableList(users);
  }

  private User lookupUserFromUsername(String username) {
    User user = new User(username, "", "", "");

    if (username != null) {
      List<UserRepresentation> users = keycloak.realm(realm).users().search(username, true);

      if (users != null && !users.isEmpty()) {
        UserRepresentation rep = users.get(0);
        if (username.equals(rep.getUsername())) {
          user = new User(rep.getUsername(), rep.getFirstName(), rep.getLastName(), rep.getEmail());
        }
      }
    }

    return user;
  }

  /**
   * Return an immutable view of the user cache.
   *
   * @return The user cache
   */
  public Map<String, User> getUserCache() {
    return Collections.unmodifiableMap(userFromUsername);
  }

  /**
   * Return an immutable view of the role cache.
   *
   * @return The role cache
   */
  public Map<String, List<User>> getRoleCache() {
    return Collections.unmodifiableMap(usersInRole);
  }
}
