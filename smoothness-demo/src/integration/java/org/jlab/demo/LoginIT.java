package org.jlab.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

  /** Report one with a week's dates: encoded characters and several parameters */
  static final String REPORT_WITH_DATES =
      "/reports/report-one?start=29-Sep-2026+07%3A00&end=06-Oct-2026+07%3A00&qualified=";

  @Test
  void loginLinkReturnsToThePageWithItsParameters() throws Exception {
    // Wildfly's OIDC client failed this with "Incorrect redirect_uri", or dropped all parameters
    // but the first, until the jeffersonlab/wildfly image's Elytron patch (3.1.1)
    Session session = Session.anonymous();
    HttpResponse<String> report = session.get(REPORT_WITH_DATES);
    Matcher link = Pattern.compile("<a id=\"login-link\" href=\"([^\"]+)\"").matcher(report.body());
    assertTrue(link.find(), "no login link on " + REPORT_WITH_DATES);

    HttpResponse<String> page =
        session.submitLoginForm(
            session.get(link.group(1).replace("&amp;", "&")), Demo.USER, Demo.PASSWORD);

    assertEquals(200, page.statusCode(), Demo.title(page));
    assertEquals(Demo.URL + REPORT_WITH_DATES, page.uri().toString());
    assertTrue(page.body().contains(Demo.USER), "not logged in");
    assertTrue(page.body().contains("value=\"29-Sep-2026 07:00\""), "start date lost");
    assertTrue(page.body().contains("value=\"06-Oct-2026 07:00\""), "end date lost");
  }

  @Test
  void sessionStatusSaysWhoIsLoggedIn() throws Exception {
    HttpResponse<String> anonymous = Session.anonymous().get("/session-status");
    HttpResponse<String> user = Session.loggedIn(Demo.USER).get("/session-status");

    assertEquals(200, anonymous.statusCode());
    assertEquals("{\"user\":null}", anonymous.body());
    assertEquals("no-store", anonymous.headers().firstValue("Cache-Control").orElse(""));
    assertEquals("{\"user\":\"" + Demo.USER + "\"}", user.body());
  }

  /** A page's login URL for logging in again from a new tab ends on /logged-in */
  @Test
  void loginAgainUrlEndsOnTheLoggedInPage() throws Exception {
    Session session = Session.loggedIn(Demo.USER);
    HttpResponse<String> overview = session.get("/overview");
    Matcher url = Pattern.compile("data-login-url=\"([^\"]+)\"").matcher(overview.body());
    assertTrue(url.find(), "no login URL on /overview");

    Session anonymous = Session.anonymous();
    HttpResponse<String> page =
        anonymous.submitLoginForm(
            anonymous.get(url.group(1).replace("&amp;", "&")), Demo.USER, Demo.PASSWORD);

    assertEquals(Demo.URL + "/logged-in", page.uri().toString());
    assertEquals("Demo - Logged In", Demo.title(page));
    assertTrue(page.body().contains("You're logged in to Smoothness Demo as"), page.body());
    assertTrue(page.body().contains("<b>" + Demo.USER + "</b>"), page.body());
  }

  @Test
  void loggedInPageSaysWhenNoOneIsLoggedIn() throws Exception {
    HttpResponse<String> page = Session.anonymous().get("/logged-in");

    assertEquals(200, page.statusCode());
    assertEquals("Demo - Not Logged In", Demo.title(page));
    assertTrue(page.body().contains("You're not logged in"), page.body());
  }

  @Test
  void wrongPasswordStaysOnKeycloaksForm() throws Exception {
    HttpResponse<String> page = Session.anonymous().login(Demo.USER, "wrong");

    assertTrue(page.uri().toString().startsWith(Demo.KEYCLOAK_URL), page.uri().toString());
    assertTrue(page.body().contains("Invalid username or password."), Demo.title(page));
  }
}
