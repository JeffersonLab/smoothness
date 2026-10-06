package org.jlab.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.http.HttpResponse;
import java.util.Map;
import java.util.UUID;
import org.jlab.demo.Demo.Session;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/** The weblib's settings: editing one on the Setup tab changes every page. */
class SettingsIT {

  /** As in container/oracle/initdb.d/04_settings.sql */
  static final String DEFAULT_MESSAGE = "Development Environment";

  @BeforeAll
  static void awaitReady() throws Exception {
    Demo.awaitReady();
  }

  static HttpResponse<String> edit(Session session, String key, String value) throws Exception {
    return session.post("/setup/ajax/edit-setting", Map.of("key", key, "value", value));
  }

  @Test
  void adminChangesTheNotificationBanner() throws Exception {
    Session admin = Session.loggedIn(Demo.ADMIN);
    String message = "IT notification " + UUID.randomUUID();

    try {
      assertEquals("{\"stat\":\"ok\"}", edit(admin, "NOTIFICATION_MESSAGE", message).body());

      String overview = Session.anonymous().get("/overview").body();
      assertTrue(overview.contains(message), "banner doesn't show the new message");
    } finally {
      edit(admin, "NOTIFICATION_MESSAGE", DEFAULT_MESSAGE);
    }

    assertTrue(Session.anonymous().get("/overview").body().contains(DEFAULT_MESSAGE));
  }

  @Test
  void booleanSettingMustBeYOrN() throws Exception {
    HttpResponse<String> response = edit(Session.loggedIn(Demo.ADMIN), "NOTIFICATION_ENABLED", "x");

    assertEquals(
        "{\"stat\":\"fail\",\"error\":\"Unable to edit Setting: Boolean Setting value must be 'Y' or"
            + " 'N'\"}",
        response.body());
  }

  @Test
  void unknownSettingFails() throws Exception {
    HttpResponse<String> response = edit(Session.loggedIn(Demo.ADMIN), "NO_SUCH_SETTING", "x");

    assertEquals(
        "{\"stat\":\"fail\",\"error\":\"Unable to edit Setting: Setting not found with key:"
            + " NO_SUCH_SETTING\"}",
        response.body());
  }

  @Test
  void userCannotEditSettings() throws Exception {
    HttpResponse<String> response =
        edit(Session.loggedIn(Demo.USER), "NOTIFICATION_MESSAGE", "not allowed");

    assertEquals(403, response.statusCode());
  }
}
