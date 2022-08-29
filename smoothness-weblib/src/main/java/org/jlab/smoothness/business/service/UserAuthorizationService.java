package org.jlab.smoothness.business.service;

import org.jlab.smoothness.persistence.view.User;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User Authorization Service.
 *
 * This class queries Keycloak for user data and caches it in memory to avoid costly lookups.  The cache can be
 * cleared by either re-deploying the app, or via the clear cache HTTP endpoint.
 */
public class UserAuthorizationService {
    private static UserAuthorizationService instance = null;
    private ConcurrentHashMap<String, List<User>> usersInRole = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, User> userFromUsername = new ConcurrentHashMap<>();
    private Keycloak keycloak;
    private String realm;
    private String resource;
    private String secret;

    private UserAuthorizationService() {
        // Private constructor; must use factory method

        String keycloakServerUrl = System.getenv("KEYCLOAK_SERVER_URL");

        if(keycloakServerUrl == null) {
            throw new RuntimeException("KEYCLOAK_SERVER_URL env required");
        }

       realm = System.getenv("KEYCLOAK_REALM");

        if(realm == null) {
            throw new RuntimeException("KEYCLOAK_REALM env required");
        }

        resource = System.getenv("KEYCLOAK_RESOURCE");

        if(resource == null) {
            throw new RuntimeException("KEYCLOAK_RESOURCE env required");
        }

        secret = System.getenv("KEYCLOAK_SECRET");

        if(secret == null) {
            throw new RuntimeException("KEYCLOAK_SECRET env required");
        }

        keycloak = KeycloakBuilder.builder()
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
    public synchronized static UserAuthorizationService getInstance() {

        if(instance == null) {
            instance = new UserAuthorizationService();
        }

        return instance;
    }

    /**
     * Clear the user data cache.
     */
    public void clearCache() {
        usersInRole.clear();
        userFromUsername.clear();
    }

    /**
     * Get the users in a given role.  Keycloak is queried if the local in-memory cache is empty.
     *
     * @param role The role to lookup
     * @return The list of users in the given role
     */
    public List<User> getUsersInRole(String role) {

        List<User> users = usersInRole.get(role);

        if(users == null) {
            users = lookupUsersInRole(role);

            usersInRole.put(role, users);
        }

        return users;
    }

    /**
     * Get the user data associated with the given username.  Keycloak is queried if the local in-memory cache is empty.
     *
     * If the lookup fails, the User data other than username will be empty (but not null).
     *
     * @param username The username to lookup
     * @return The user data for the given username
     */
    public User getUserFromUsername(String username) {

        User user = userFromUsername.get(username);

        if(user == null) {
            user = lookupUserFromUsername(username);

            userFromUsername.put(username, user);
        }

        return user;
    }

    private List<User> lookupUsersInRole(String role) {

        System.err.println("Looking up role: " + role);
        System.err.println("Using realm: " + realm);

        List<User> users = new ArrayList<>();

        RolesResource roles = keycloak.realm(realm).roles();

        RoleResource roleResource = roles.get(role);

        Set<UserRepresentation> members = roleResource.getRoleUserMembers();

        for(UserRepresentation rep: members) {
            User user = new User(rep.getUsername(), rep.getFirstName(), rep.getLastName());

            users.add(user);
        }

        users.sort(Comparator.comparing(User::getLastname));

        return Collections.unmodifiableList(users);
    }

    private User lookupUserFromUsername(String username) {
        User user = null;

        List<UserRepresentation> users = keycloak.realm(realm).users().search(username, 0, 1);

        if(users != null && !users.isEmpty()) {
            UserRepresentation rep = users.get(0);
            user = new User(rep.getUsername(), rep.getFirstName(), rep.getLastName());
        } else {
            System.err.println("Could not find username: " + username);
            user = new User(username, "", "");
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
