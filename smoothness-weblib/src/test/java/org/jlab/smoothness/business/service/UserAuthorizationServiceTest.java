package org.jlab.smoothness.business.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.jlab.smoothness.persistence.view.User;
import org.junit.jupiter.api.Test;

/**
 * Tests the ordering {@link UserAuthorizationService#getUsersLike} and {@link
 * UserAuthorizationService#getUsersInRole} apply to the users Keycloak returns. A Keycloak profile
 * need not have a last name, so the order must not throw on a null one.
 */
class UserAuthorizationServiceTest {

  private static List<String> usernamesSorted(User... users) {
    List<User> list = new ArrayList<>(Arrays.asList(users));

    list.sort(UserAuthorizationService.BY_LASTNAME_THEN_USERNAME);

    return list.stream().map(User::getUsername).collect(Collectors.toList());
  }

  @Test
  void sortsByLastname() {
    assertEquals(
        Arrays.asList("carol", "alice", "bob"),
        usernamesSorted(
            new User("alice", "Alice", "Baker", "alice@example.com"),
            new User("bob", "Bob", "Clark", "bob@example.com"),
            new User("carol", "Carol", "Able", "carol@example.com")));
  }

  @Test
  void keepsUsersWithoutLastnameAndPutsThemLast() {
    assertEquals(
        Arrays.asList("alice", "bob", "dave", "erin", "frank"),
        usernamesSorted(
            new User("erin", "Erin", "", "erin@example.com"),
            new User("frank", null, null, null),
            new User("alice", "Alice", "Able", "alice@example.com"),
            new User("dave", "Dave", null, "dave@example.com"),
            new User("bob", "Bob", "Baker", "bob@example.com")));
  }

  @Test
  void breaksLastnameTiesByUsername() {
    assertEquals(
        Arrays.asList("asmith", "jsmith", "tsmith"),
        usernamesSorted(
            new User("tsmith", "Tom", "Smith", "tsmith@example.com"),
            new User("asmith", "Ann", "Smith", "asmith@example.com"),
            new User("jsmith", "Jane", "Smith", "jsmith@example.com")));
  }
}
