package org.jlab.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jlab.demo.Demo.Session;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Public pages, the weblib's page template and resources, and its error page. */
class PagesIT {

  @BeforeAll
  static void awaitReady() throws Exception {
    Demo.awaitReady();
  }

  @ParameterizedTest
  @CsvSource({
    "/overview, Demo - Overview, true",
    "/help, Demo - Help, true",
    "/reports/report-one, Demo - Reports - Report One, true",
    "/breadcrumbs/crumb-one, Demo - Crumb One, true",
    // s:loose-page, without the tabs and banner
    "/hello, Demo - Hello, false"
  })
  void publicPage(String path, String title, boolean banner) throws Exception {
    HttpResponse<String> page = Session.anonymous().get(path);

    assertEquals(200, page.statusCode(), path);
    assertEquals(title, Demo.title(page));
    // The weblib's page template shows the notification banner from the settings
    assertEquals(banner, page.body().contains(SettingsIT.DEFAULT_MESSAGE), "banner on " + path);
  }

  @Test
  void rootRedirectsToOverview() throws Exception {
    HttpResponse<String> page = Session.anonymous().get("/");

    assertEquals(Demo.URL + "/overview", page.uri().toString());
  }

  @Test
  void reportAddsItsDefaultDateRange() throws Exception {
    HttpResponse<String> response = Session.notFollowingRedirects().get("/reports/report-one");

    assertEquals(302, response.statusCode());
    assertTrue(
        response.headers().firstValue("Location").orElse("").matches(".*\\?start=.+&end=.+"),
        response.headers().toString());
  }

  @Test
  void versionedResourcesAreServed() throws Exception {
    String overview = Session.anonymous().get("/overview").body();
    Matcher links =
        Pattern.compile(
                "/smoothness-demo(/resources/v[^/\"]+/(css/smoothness\\.css|js/smoothness\\.js))")
            .matcher(overview);

    int found = 0;
    while (links.find()) {
      HttpResponse<String> resource = Session.anonymous().get(links.group(1));
      assertEquals(200, resource.statusCode(), links.group(1));
      assertTrue(resource.body().length() > 1000, links.group(1) + " is nearly empty");
      found++;
    }
    assertEquals(2, found, "links to smoothness.css and smoothness.js");
  }

  @Test
  void helpPageListsTheAdminsFromKeycloaksDirectory() throws Exception {
    String help = Session.loggedIn(Demo.USER).get("/help").body();

    // The members of ADMIN_ROLE_NAME's role, which the weblib looks up with Keycloak's admin API
    assertTrue(
        help.contains("<div class=\"li-value\">jdoe,tbrown</div>"), "admins not listed on /help");
  }

  @Test
  void unknownPageShowsTheErrorPage() throws Exception {
    HttpResponse<String> page = Session.anonymous().get("/no-such-page");

    assertEquals(404, page.statusCode());
    assertEquals("Demo - Error", Demo.title(page));
  }
}
