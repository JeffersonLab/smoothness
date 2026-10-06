package org.jlab.smoothness.presentation.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jlab.smoothness.persistence.view.User;
import org.junit.jupiter.api.Test;

/** The EL functions declared in smoothness.tld that don't look up users in Keycloak. */
class FunctionsTest {

  @Test
  void inArray() {
    assertTrue(Functions.inArray(new Object[] {"a", "b"}, "b"));
    assertTrue(Functions.inArray(new Object[] {1, null}, 1));
    assertFalse(Functions.inArray(new Object[] {"a"}, "A"));
    assertFalse(Functions.inArray(new Object[] {"1"}, 1));
    assertFalse(Functions.inArray(new Object[] {null}, null));
    assertFalse(Functions.inArray(null, "a"));
  }

  @Test
  void formatUser() {
    assertEquals("Doe, John (jdoe)", Functions.formatUser(new User("jdoe", "John", "Doe", null)));
    assertEquals("(jdoe)", Functions.formatUser(new User("jdoe", "John", "", null)));
    assertEquals("(jdoe)", Functions.formatUser(new User("jdoe", null, "Doe", null)));
    assertEquals("", Functions.formatUser(new User("", "John", "Doe", null)));
    assertEquals("", Functions.formatUser(null));
  }

  @Test
  void friendlyPatternsComeFromTimeUtil() {
    assertEquals("dd-MMM-yyyy HH:mm", Functions.getFriendlyDateTimePattern());
    assertEquals("DD-MMM-YYYY hh:mm", Functions.getFriendlyDateTimePlaceholder());
    assertEquals("dd-MMM-yyyy", Functions.getFriendlyDatePattern());
    assertEquals("DD-MMM-YYYY", Functions.getFriendlyDatePlaceholder());
  }
}
