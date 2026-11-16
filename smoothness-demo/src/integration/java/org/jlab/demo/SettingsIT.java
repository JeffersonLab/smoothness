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

  /** A CSV setting, as in container/oracle/initdb.d/04_settings.sql */
  static final String HELP_DOC_CSV = "HELP_DOC_CSV";

  static final String DEFAULT_HELP_DOC = "https://github.com/JeffersonLab/smoothness|Home";

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
  void csvSettingIsSavedWithItemsTrimmedAndBlankItemsDropped() throws Exception {
    Session admin = Session.loggedIn(Demo.ADMIN);
    String label = "IT doc " + UUID.randomUUID();

    try {
      assertEquals(
          "{\"stat\":\"ok\"}",
          edit(
                  admin,
                  HELP_DOC_CSV,
                  " https://example.com/one|" + label + " ,\n, https://example.com/two|Two \n")
              .body());

      String settings = admin.get("/setup/settings?key=" + HELP_DOC_CSV).body();
      assertTrue(
          settings.contains(
              "data-value=\"https://example.com/one|" + label + ",https://example.com/two|Two\""),
          "stored value isn't cleaned");
      assertTrue(settings.contains("<li>https://example.com/two|Two</li>"), "items aren't listed");

      // The help panel splits the value on commas, as ImmutableSettings.csv() does
      String help = Session.anonymous().get("/help").body();
      assertTrue(help.contains("<li><a href=\"https://example.com/one\">" + label + "</a></li>"));
      assertTrue(help.contains("<li><a href=\"https://example.com/two\">Two</a></li>"));
    } finally {
      edit(admin, HELP_DOC_CSV, DEFAULT_HELP_DOC);
    }
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
