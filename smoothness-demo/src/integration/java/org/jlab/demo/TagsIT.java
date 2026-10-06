package org.jlab.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.http.HttpResponse;
import java.util.regex.Pattern;
import org.jlab.demo.Demo.Session;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * The markup the weblib's tags render for each option, mostly on the demo's Features > Tags page,
 * which uses the options the other pages don't.
 */
class TagsIT {

  static String tags;

  @BeforeAll
  static void load() throws Exception {
    Demo.awaitReady();
    HttpResponse<String> page = Session.loggedIn(Demo.USER).get("/features/tags");
    assertEquals(200, page.statusCode());
    tags = page.body();
  }

  /** The part of the page from one marker to the next. */
  static String between(String body, String start, String end) {
    int i = body.indexOf(start);
    int j = body.indexOf(end, i + start.length());
    assertTrue(i >= 0 && j >= 0, "no " + start + " ... " + end);
    return body.substring(i, j);
  }

  @Test
  void filterFlyoutWithoutOptions() {
    String flyout = between(tags, "id=\"plain-flyout\"", "id=\"ribbon-flyout\"");

    assertTrue(flyout.contains("<div class=\"filter-flyout-widget\">"), flyout);
    assertTrue(flyout.contains("<a class=\"filter-flyout-link\" href=\"#\">Choose...</a>"));
    assertTrue(flyout.contains("<div class=\"filter-flyout-title\">Choose Parameters</div>"));
    assertFalse(flyout.contains("reset-clear-panel"));
  }

  @Test
  void filterFlyoutRibbon() {
    String flyout = between(tags, "id=\"ribbon-flyout\"", "id=\"date-flyout\"");

    assertTrue(
        flyout.contains("<div class=\"filter-flyout-widget filter-flyout-ribbon\">"), flyout);
  }

  @Test
  void filterFlyoutRequiredMessageAndResetAndClear() {
    String flyout = between(tags, "id=\"date-flyout\"", "<fieldset>");

    assertTrue(
        flyout.contains(
            "(<span class=\"default-reset-panel\"><a href=\"#\">Reset</a></span> | <span"
                + " class=\"default-clear-panel\"><a href=\"#\">Clear</a></span>)"),
        flyout);
    assertTrue(
        flyout.contains("Choose Parameters (<span class=\"required-field\"></span> required)"));
  }

  @Test
  void dateRangeOfDatesFromMidnight() {
    String range = between(tags, "<legend>Date Range: dates", "</fieldset>");

    assertTrue(
        tags.contains(
            "<select id=\"dates-date-range\" class=\"date-range-select date-range midnight-offset\">"),
        "select's classes");
    assertTrue(range.contains("Previous Week (from Wed)</option>"), range);
    assertTrue(range.contains("Today</option>"));
    assertFalse(range.contains("ccshift"), "shift options without datetime");
    assertTrue(range.contains("class=\"start-input date-input\" id=\"dates-start\""));
    assertTrue(range.contains("placeholder=\"DD-MMM-YYYY \""));
    assertFalse(range.contains("required-field"));
  }

  @Test
  void dateRangeOfRequiredDateTimesFrom7() {
    String range = between(tags, "<legend>Date Range: required", "</fieldset>");

    assertTrue(
        tags.contains(
            "<label class=\"required-field\" for=\"datetimes-date-range\">Date Range</label>"));
    assertTrue(
        tags.contains(
            "<select id=\"datetimes-date-range\" class=\"date-range-select datetime-range "
                + "seven-am-offset\">"),
        "select's classes");
    assertTrue(range.contains("Previous Week (from Wed 7:00)</option>"), range);
    assertTrue(range.contains("Today (from 7:00)</option>"));
    assertTrue(range.contains("<option value=\"0ccshift\">Current CC Shift</option>"));
    assertTrue(range.contains("<option value=\"1ccshift\">Previous CC Shift</option>"));
    assertTrue(range.contains("class=\"start-input datetime-input\" id=\"datetimes-start\""));
    assertTrue(range.contains("placeholder=\"DD-MMM-YYYY hh:mm\""));
    assertTrue(range.contains("<label class=\"required-field\" for=\"datetimes-end\""));
  }

  @Test
  void editableRowTableControlsForMultiselectWithoutEdit() {
    String controls =
        between(tags, "<div id=\"editable-row-table-control-panel\">", "<table id=\"tag-table\"");

    assertTrue(controls.contains("id=\"open-add-row-dialog-button\""), controls);
    assertTrue(controls.contains("id=\"remove-row-button\""));
    assertFalse(controls.contains("id=\"open-edit-row-dialog-button\""), "Edit not excluded");
    assertTrue(controls.contains(">Unselect All</button>"));
    assertTrue(controls.contains("(<span id=\"selected-count\">0</span> Selected)"));
  }

  @Test
  void partialPageIsJustTheContent() throws Exception {
    String partial = Session.loggedIn(Demo.USER).get("/features/tags?partial=Y").body();

    assertTrue(partial.startsWith("<div id=\"partial\" data-title=\"Tags\">"), partial);
    assertFalse(partial.contains("<html"));
    assertTrue(partial.contains("id=\"date-flyout\""));
  }

  @Test
  void loosePageWithoutSmoothnessHasNoWeblibResources() throws Exception {
    String page = Session.loggedIn(Demo.USER).get("/features/loose-page-without-smoothness").body();

    assertEquals("Demo - Features - Loose Page Without Smoothness", Demo.title(page));
    assertFalse(page.contains("smoothness.css"), "smoothness.css included");
    assertFalse(page.contains("smoothness.js"), "smoothness.js included");
    assertFalse(page.contains("jquery"), "jQuery included");
  }

  @Test
  void loosePageExcludesResourcesEvenWhenExcludeIsFalse() throws Exception {
    // loose-page excludes them for any value of excludeSmoothResources; only leaving it out keeps
    // them, as /hello does
    String excluded =
        Session.loggedIn(Demo.USER)
            .get("/features/loose-page-without-smoothness?exclude=false")
            .body();
    String hello = Session.anonymous().get("/hello").body();

    assertFalse(excluded.contains("smoothness.js"), "excludeSmoothResources=false included them");
    assertTrue(hello.contains("smoothness.js"), "/hello without the attribute excluded them");
  }

  @Test
  void cdnSettingLoadsTheWeblibFromTheCdn() throws Exception {
    Session admin = Session.loggedIn(Demo.ADMIN);
    Pattern cdnCss =
        Pattern.compile(
            "href=\"//ace\\.jlab\\.org/cdn/jlab-theme/smoothness/[^/\"]+/css/smoothness\\.min\\.css\"");

    try {
      assertEquals(
          "{\"stat\":\"ok\"}", SettingsIT.edit(admin, "SMOOTHNESS_CDN_ENABLED", "Y").body());

      String overview = Session.anonymous().get("/overview").body();
      assertTrue(cdnCss.matcher(overview).find(), "no CDN stylesheet");
      assertTrue(overview.contains("<script src=\"//ace.jlab.org/cdn/jquery/3.7.1.min.js\">"));
      assertFalse(
          overview.contains("/resources/jquery-ui-1.14.1/jquery-ui.min.js"), "local jQuery UI");
    } finally {
      SettingsIT.edit(admin, "SMOOTHNESS_CDN_ENABLED", "N");
    }

    String overview = Session.anonymous().get("/overview").body();
    assertFalse(cdnCss.matcher(overview).find(), "CDN still on");
    assertTrue(overview.contains("/smoothness-demo/resources/jquery-ui-1.14.1/jquery-ui.min.js"));
  }

  @Test
  void chartWidgetPlaceholders() throws Exception {
    Session session = Session.anonymous();
    String report = session.get("/reports/report-two").body();

    assertTrue(
        report.contains("<div id=\"report2\" class=\"chart-placeholder\"></div>"),
        Demo.title(report));
  }
}
