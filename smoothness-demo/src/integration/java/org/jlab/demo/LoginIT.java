package org.jlab.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.http.HttpResponse;
import org.jlab.demo.Demo.Session;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/** Logging in through Keycloak, and the pages each role may see (web.xml's constraints). */
class LoginIT {

  @BeforeAll
  static void awaitReady() throws Exception {
    Demo.awaitReady();
  }

  @Test
  void protectedPageSendsAnonymousUsersToKeycloak() throws Exception {
    HttpResponse<String> page =
        Session.notFollowingRedirects().get("/features/single-select-datatable");

    assertEquals(302, page.statusCode());
    assertTrue(
        page.headers()
            .firstValue("Location")
            .orElse("")
            .startsWith(Demo.KEYCLOAK_URL + "/realms/test-realm/protocol/openid-connect/auth"),
        page.headers().toString());
  }

  @Test
  void adminSeesSetup() throws Exception {
    Session admin = Session.loggedIn(Demo.ADMIN);

    HttpResponse<String> settings = admin.get("/setup/settings");

    assertEquals(200, settings.statusCode());
    assertEquals("Demo - Setup - Settings", Demo.title(settings));
    assertTrue(admin.get("/overview").body().contains("/setup/settings"), "no Setup tab");
  }

  @Test
  void userCannotSeeSetup() throws Exception {
    Session user = Session.loggedIn(Demo.USER);

    HttpResponse<String> settings = user.get("/setup/settings");

    assertEquals(403, settings.statusCode());
    assertEquals("Demo - Error", Demo.title(settings));
    assertFalse(user.get("/overview").body().contains("/setup/settings"), "Setup tab shown");
  }

  @Test
  void userSeesFeatures() throws Exception {
    HttpResponse<String> page = Session.loggedIn(Demo.USER).get("/features/multiselect-datatable");

    assertEquals(200, page.statusCode());
    assertTrue(page.body().contains("movie-table"), Demo.title(page));
  }

  @Test
  void wrongPasswordStaysOnKeycloaksForm() throws Exception {
    HttpResponse<String> page = Session.anonymous().login(Demo.USER, "wrong");

    assertTrue(page.uri().toString().startsWith(Demo.KEYCLOAK_URL), page.uri().toString());
    assertTrue(page.body().contains("Invalid username or password."), Demo.title(page));
  }
}
