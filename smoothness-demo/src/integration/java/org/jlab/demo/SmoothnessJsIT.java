package org.jlab.demo;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.microsoft.playwright.Download;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.options.LoadState;
import java.nio.file.Files;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.jlab.demo.Demo.Session;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * smoothness.js in Chromium: its functions, called directly on a page that loads it, and the
 * behavior it gives the demo's pages. Characterization tests, like the weblib's unit tests.
 */
class SmoothnessJsIT {

  /** Tuesday 2026-10-06 10:00 in New York, the browser's clock for the date tests */
  static final long TUESDAY_10AM =
      ZonedDateTime.of(2026, 10, 6, 10, 0, 0, 0, ZoneId.of("America/New_York"))
          .toInstant()
          .toEpochMilli();

  static DemoBrowser browser;

  /** /hello with the browser's clock at TUESDAY_10AM, which the function tests share */
  static Page tuesday;

  /** /hello with the browser's clock on October 31, made by onOctober31() */
  static Page october31;

  @BeforeAll
  static void start() throws Exception {
    Demo.awaitReady();
    browser = new DemoBrowser();
    tuesday = browser.newPage();
    tuesday.clock().setFixedTime(TUESDAY_10AM);
    open(tuesday, "/hello");
  }

  @AfterAll
  static void stop() {
    if (browser != null) {
      browser.close();
    }
  }

  @BeforeEach
  void clearErrors() {
    browser.errors.clear();
  }

  static void open(Page page, String path) {
    page.navigate(Demo.URL + path);
    page.waitForLoadState(LoadState.NETWORKIDLE);
  }

  /** A page that loads smoothness.js, with the browser's clock at TUESDAY_10AM; don't change it */
  static Page onTuesday() {
    return tuesday;
  }

  /** /hello with the browser's clock at Saturday 2026-10-31 10:00, the 31st of a month */
  static Page onOctober31() {
    if (october31 == null) {
      october31 = browser.newPage();
      october31
          .clock()
          .setFixedTime(
              ZonedDateTime.of(2026, 10, 31, 10, 0, 0, 0, ZoneId.of("America/New_York"))
                  .toInstant()
                  .toEpochMilli());
      open(october31, "/hello");
    }
    return october31;
  }

  /** Runs JavaScript on the page and returns its result as a string. */
  static String js(Page page, String expression) {
    return String.valueOf(page.evaluate(expression));
  }

  // Functions

  @Test
  void padAndIntegerWithCommas() {
    Page page = onTuesday();

    assertEquals("007", js(page, "jlab.pad(7, 3)"));
    assertEquals("1234", js(page, "jlab.pad(1234, 3)"));
    assertEquals("xx5", js(page, "jlab.pad(5, 3, 'x')"));
    assertEquals("1,234,567", js(page, "jlab.integerWithCommas(1234567)"));
    assertEquals("999", js(page, "jlab.integerWithCommas(999)"));
  }

  @Test
  void dateStrings() {
    Page page = onTuesday();
    String date = "new Date(2026, 2, 8, 7, 5)";

    assertEquals("08-Mar-2026 07:05", js(page, "jlab.toFriendlyDateTimeString(" + date + ")"));
    assertEquals("08-Mar-2026", js(page, "jlab.toFriendlyDateString(" + date + ")"));
    assertEquals("2026-03-08T07:05", js(page, "jlab.toIsoDateTimeString(" + date + ")"));
    assertEquals("2026-03-08", js(page, "jlab.toIsoDateString(" + date + ")"));
    assertEquals(
        "08-Mar-2026 07:05",
        js(
            page,
            "jlab.toFriendlyDateTimeString(jlab.fromFriendlyDateTimeString('08-Mar-2026 07:05'))"));
    assertEquals(
        "08-Mar-2026",
        js(page, "jlab.toFriendlyDateString(jlab.fromFriendlyDateString('08-Mar-2026'))"));
    assertEquals(
        "08-Mar-2026 07:05",
        js(
            page,
            "jlab.toFriendlyDateTimeString(jlab.fromIsoDateTimeString('2026-03-08T07:05:00'))"));
    assertEquals(
        "08-Mar-2026", js(page, "jlab.toFriendlyDateString(jlab.fromIsoDateString('2026-03-08'))"));
  }

  @Test
  void isoDateTimeParsesWithOrWithoutSeconds() {
    Page page = onTuesday();

    // toIsoDateTimeString leaves out the seconds, and fromIsoDateTimeString reads its output
    assertEquals(
        "true",
        js(
            page,
            "jlab.fromIsoDateTimeString(jlab.toIsoDateTimeString(new Date(2026, 2, 8, 7, 5)))"
                + ".getTime() === new Date(2026, 2, 8, 7, 5).getTime()"));
    assertEquals(
        "08-Mar-2026 07:05",
        js(page, "jlab.toFriendlyDateTimeString(jlab.fromIsoDateTimeString('2026-03-08T07:05'))"));
    assertEquals("0", js(page, "jlab.fromIsoDateTimeString('2026-03-08T07:05').getSeconds()"));
    assertEquals("42", js(page, "jlab.fromIsoDateTimeString('2026-03-08T07:05:42').getSeconds()"));
    // Anything else after the minutes is malformed: an Invalid Date
    assertEquals("true", js(page, "isNaN(jlab.fromIsoDateTimeString('2026-03-08T07:05Z'))"));
    assertEquals("true", js(page, "isNaN(jlab.fromIsoDateTimeString('2026-03-08T07:05:'))"));
    assertEquals("true", js(page, "isNaN(jlab.fromIsoDateTimeString('2026-03-08T07'))"));
  }

  @ParameterizedTest
  @CsvSource({
    // hour, shift start, shift end: as TimeUtil.getCcShiftStart and getCcShiftEnd
    "0, 05-Oct-2026 23:00, 06-Oct-2026 07:00",
    "6, 05-Oct-2026 23:00, 06-Oct-2026 07:00",
    "7, 06-Oct-2026 07:00, 06-Oct-2026 15:00",
    "14, 06-Oct-2026 07:00, 06-Oct-2026 15:00",
    "15, 06-Oct-2026 15:00, 06-Oct-2026 23:00",
    "22, 06-Oct-2026 15:00, 06-Oct-2026 23:00",
    "23, 06-Oct-2026 23:00, 07-Oct-2026 07:00"
  })
  void crewChiefShifts(int hour, String start, String end) {
    Page page = onTuesday();
    String date = "new Date(2026, 9, 6, " + hour + ", 30)";

    assertEquals(
        start, js(page, "jlab.toFriendlyDateTimeString(jlab.getCcShiftStart(" + date + "))"));
    assertEquals(end, js(page, "jlab.toFriendlyDateTimeString(jlab.getCcShiftEnd(" + date + "))"));
  }

  @Test
  void startOfFiscalYearIsTheLatestOctoberFirst() {
    Page page = onTuesday();

    assertEquals(
        "01-Oct-2025 00:00",
        js(
            page,
            "jlab.toFriendlyDateTimeString(jlab.getStartOfFiscalYear(new Date(2026, 8, 30)))"));
    assertEquals(
        "01-Oct-2026 00:00",
        js(page, "jlab.toFriendlyDateTimeString(jlab.getStartOfFiscalYear(new Date(2026, 9, 1)))"));
  }

  @ParameterizedTest
  @CsvSource({
    // On Tuesday 2026-10-06 10:00, days from midnight
    "0day, 06-Oct-2026 00:00, 07-Oct-2026 00:00",
    "1day, 05-Oct-2026 00:00, 06-Oct-2026 00:00",
    "past3days, 03-Oct-2026 00:00, 06-Oct-2026 00:00",
    "past7days, 29-Sep-2026 00:00, 06-Oct-2026 00:00",
    "past10days, 26-Sep-2026 00:00, 06-Oct-2026 00:00",
    "0week, 30-Sep-2026 00:00, 07-Oct-2026 00:00",
    "1week, 23-Sep-2026 00:00, 30-Sep-2026 00:00",
    "0month, 01-Oct-2026 00:00, 01-Nov-2026 00:00",
    "1month, 01-Sep-2026 00:00, 01-Oct-2026 00:00",
    "0year, 01-Jan-2026 00:00, 01-Jan-2027 00:00",
    "1year, 01-Jan-2025 00:00, 01-Jan-2026 00:00",
    "0fiscalyear, 01-Oct-2026 00:00, 01-Oct-2027 00:00",
    "0fiscalyearq1, 01-Oct-2026 00:00, 01-Jan-2027 00:00",
    "0fiscalyearq2, 01-Jan-2027 00:00, 01-Apr-2027 00:00",
    "0fiscalyearq3, 01-Apr-2027 00:00, 01-Jul-2027 00:00",
    "0fiscalyearq4, 01-Jul-2027 00:00, 01-Oct-2027 00:00",
    "1fiscalyear, 01-Oct-2025 00:00, 01-Oct-2026 00:00",
    "1fiscalyearq1, 01-Oct-2025 00:00, 01-Jan-2026 00:00",
    "1fiscalyearq2, 01-Jan-2026 00:00, 01-Apr-2026 00:00",
    "1fiscalyearq3, 01-Apr-2026 00:00, 01-Jul-2026 00:00",
    "1fiscalyearq4, 01-Jul-2026 00:00, 01-Oct-2026 00:00",
    "0ccshift, 06-Oct-2026 07:00, 06-Oct-2026 15:00",
    "1ccshift, 05-Oct-2026 23:00, 06-Oct-2026 07:00"
  })
  void decodeRange(String range, String start, String end) {
    Page page = onTuesday();
    String decoded = "jlab.decodeRange('" + range + "', false)";

    assertEquals(start, js(page, "jlab.toFriendlyDateTimeString(" + decoded + ".start)"));
    assertEquals(end, js(page, "jlab.toFriendlyDateTimeString(" + decoded + ".end)"));
  }

  @ParameterizedTest
  @CsvSource({
    // On Saturday 2026-10-31 10:00: setting April, the 30-day month, on the 31st rolls into May
    "0fiscalyear, 01-Oct-2026 00:00, 01-Oct-2027 00:00",
    "0fiscalyearq1, 01-Oct-2026 00:00, 01-Jan-2027 00:00",
    "0fiscalyearq2, 01-Jan-2027 00:00, 01-Apr-2027 00:00",
    "0fiscalyearq3, 01-Apr-2027 00:00, 01-Jul-2027 00:00",
    "0fiscalyearq4, 01-Jul-2027 00:00, 01-Oct-2027 00:00",
    "1fiscalyear, 01-Oct-2025 00:00, 01-Oct-2026 00:00",
    "1fiscalyearq1, 01-Oct-2025 00:00, 01-Jan-2026 00:00",
    "1fiscalyearq2, 01-Jan-2026 00:00, 01-Apr-2026 00:00",
    "1fiscalyearq3, 01-Apr-2026 00:00, 01-Jul-2026 00:00",
    "1fiscalyearq4, 01-Jul-2026 00:00, 01-Oct-2026 00:00"
  })
  void decodeRangeOnTheThirtyFirst(String range, String start, String end) {
    Page page = onOctober31();
    String decoded = "jlab.decodeRange('" + range + "', false)";

    assertEquals(start, js(page, "jlab.toFriendlyDateTimeString(" + decoded + ".start)"));
    assertEquals(end, js(page, "jlab.toFriendlyDateTimeString(" + decoded + ".end)"));
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void encodeRangeNamesEveryRangeDecodeRangeMakes(boolean sevenAmOffset) {
    Page page = onTuesday();
    List<String> mismatches = new ArrayList<>();

    for (String range :
        List.of(
            "1fiscalyear",
            "1fiscalyearq4",
            "1fiscalyearq3",
            "1fiscalyearq2",
            "1fiscalyearq1",
            "1year",
            "1month",
            "1week",
            "1ccshift",
            "0fiscalyear",
            "0fiscalyearq4",
            "0fiscalyearq3",
            "0fiscalyearq2",
            "0fiscalyearq1",
            "0year",
            "0month",
            "0week",
            "0ccshift",
            "past10days",
            "past7days",
            "past3days",
            "1day",
            "0day")) {
      String encoded =
          js(
              page,
              "(() => { const r = jlab.decodeRange('"
                  + range
                  + "', "
                  + sevenAmOffset
                  + "); return jlab.encodeRange(r.start, r.end, "
                  + sevenAmOffset
                  + "); })()");
      if (!range.equals(encoded)) {
        mismatches.add(range + " -> " + encoded);
      }
    }

    assertEquals(List.of(), mismatches);
  }

  @Test
  void printAndFullscreenUrls() {
    Page page = browser.newPage();
    open(page, "/hello?a=1&print=N");

    assertEquals("/smoothness-demo/hello?a=1&print=Y", js(page, "jlab.getPrintUrl()"));
    assertTrue(
        js(page, "jlab.getFullscreenUrl()")
            .endsWith("/smoothness-demo/hello?a=1&print=Y&fullscreen=Y"),
        js(page, "jlab.getFullscreenUrl()"));
    assertTrue(
        js(page, "jlab.getExitFullscreenUrl()")
            .endsWith("/smoothness-demo/hello?a=1&print=N&fullscreen=N"),
        js(page, "jlab.getExitFullscreenUrl()"));
  }

  @Test
  void searchParamsAppendAll() {
    Page page = onTuesday();

    assertEquals(
        "a=1&b=x&b=y",
        js(
            page,
            "(() => { const p = new URLSearchParams(); jlab.searchParamsAppendAll(p, 'a', '1');"
                + " jlab.searchParamsAppendAll(p, 'b', ['x', 'y']); return p.toString(); })()"));
  }

  @Test
  void initParamsRedirectsWithTheDefaults() {
    Page page = browser.newPage();
    open(page, "/hello");

    // initParams redirects, so call it after evaluate returns, and wait for the new URL
    page.evaluate("setTimeout(() => jlab.initParams({color: 'blue', size: ['s', 'm']}))");
    page.waitForURL(url -> url.contains("qualified="));

    assertEquals(Demo.URL + "/hello?color=blue&size=s&size=m&qualified=", page.url().toString());
  }

  @Test
  void initParamsRemembersAnEmptyKeyAsEmpty() {
    // On a qualified page, a missing key means the user chose nothing, such as an empty multiple
    // select; it is remembered as an empty array, so the next unqualified page gets no value
    Page page = browser.newPage();
    open(page, "/hello?color=red&qualified=");

    assertEquals("false", js(page, "jlab.initParams({color: 'blue', size: ['s']})"));
    assertEquals("[]", js(page, "sessionStorage.getItem('size')"));
    assertEquals("[\"red\"]", js(page, "sessionStorage.getItem('color')"));

    open(page, "/hello");
    page.evaluate("setTimeout(() => jlab.initParams({color: 'blue', size: ['s']}))");
    page.waitForURL(url -> url.contains("qualified="));

    assertEquals(Demo.URL + "/hello?color=red&qualified=", page.url().toString());
  }

  // Page behavior

  @Test
  void escapeClosesTheFilterFlyout() {
    Page page = browser.newPage();
    open(page, "/reports/report-one");
    Locator panel = page.locator(".filter-flyout-panel");

    page.locator(".filter-flyout-link").click();
    assertThat(panel).isVisible();
    page.locator(".filter-flyout-widget input").first().press("Escape");
    assertThat(panel).isHidden();
  }

  @Test
  void singleSelectTableSelectsOneRow() {
    Page page = browser.loggedIn(Demo.USER);
    open(page, "/features/single-select-datatable");
    Locator rows = page.locator("#movie-table tbody tr");

    assertThat(page.locator("#open-edit-row-dialog-button")).isDisabled();
    rows.nth(0).click();
    rows.nth(1).click();

    assertThat(rows.nth(0)).not().hasClass(java.util.regex.Pattern.compile("selected-row"));
    assertThat(rows.nth(1)).hasClass(java.util.regex.Pattern.compile("selected-row"));
    assertThat(page.locator("#open-edit-row-dialog-button")).isEnabled();
    assertThat(page.locator("#remove-row-button")).isEnabled();
    assertThat(page.locator("#open-add-row-dialog-button")).isDisabled();
    assertEquals(List.of(), browser.errors);
  }

  @Test
  void addAMovieThroughTheRowDialog() throws Exception {
    String title = "IT dialog movie " + UUID.randomUUID();
    Page page = browser.loggedIn(Demo.USER);
    open(page, "/features/single-select-datatable");

    try {
      page.locator("#open-add-row-dialog-button").click();
      Locator dialog = page.locator("#table-row-dialog");
      assertThat(dialog).isVisible();
      assertThat(
              page.locator(".ui-dialog-title")
                  .filter(new Locator.FilterOptions().setHasText("Add Movie")))
          .isVisible();

      // The release date's date picker covers the Save button until a click elsewhere closes it
      dialog.locator("#row-release").fill("10-Mar-2026");
      dialog.locator("#row-title").click();
      assertThat(page.locator("#ui-datepicker-div")).isHidden();
      dialog.locator("#row-title").fill(title);
      dialog.locator("#row-description").fill("Added through the dialog");
      dialog.locator("#row-rating").selectOption("PG");
      dialog.locator("#row-duration").fill("90");
      // Saving posts with AJAX and reloads the page
      page.locator("#table-row-save-button").click();

      assertThat(page.locator("#movie-table tbody tr", new Page.LocatorOptions().setHasText(title)))
          .isVisible();
      assertEquals(List.of(), browser.errors);
    } finally {
      Session http = Session.loggedIn(Demo.USER);
      var row = MoviesIT.row(http, title);
      if (row != null) {
        http.post("/ajax/remove-movie", Map.of("id[]", row.group(1)));
      }
    }
  }

  @Test
  void failedAjaxRequestShowsTheServersError() {
    Page page = browser.loggedIn(Demo.USER);
    open(page, "/features/single-select-datatable");

    page.locator("#open-add-row-dialog-button").click();
    page.locator("#table-row-save-button").click();

    Locator error = page.locator(".error-dialog");
    assertThat(error).hasText("title must not be empty");
    assertThat(dialogTitle(page, error)).hasText("Unable to perform request");
    // The dialog stays open with its buttons back, for the user to fix the form
    assertThat(page.locator("#table-row-dialog")).isVisible();
    assertThat(page.locator("#table-row-save-button")).hasText("Save");
    assertThat(page.locator("#session-bar")).hasCount(0);

    dialogButton(page, error, "OK").click();
    assertThat(error).hasCount(0);
    assertThat(page.locator("#table-row-dialog")).isVisible();
  }

  @Test
  void failedAjaxRequestSaysWhenTheServerIsUnreachable() {
    Page page = browser.loggedIn(Demo.USER);
    open(page, "/features/single-select-datatable");
    page.route("**/ajax/add-movie", Route::abort);
    page.route("**/session-status", Route::abort);

    page.locator("#open-add-row-dialog-button").click();
    page.locator("#row-title").fill("Unsaved");
    page.locator("#table-row-save-button").click();

    assertThat(page.locator(".error-dialog"))
        .hasText("The server could not be reached. Check your network connection, then try again.");
    assertThat(page.locator("#row-title")).hasValue("Unsaved");
    assertThat(page.locator("#session-bar")).hasCount(0);
  }

  /**
   * After the user's session ends, a save that fails (here as one to a protected URL does: the
   * redirect to Keycloak fails) says they were logged out, keeps what they entered, and logs them
   * in again in a new tab, after which the save works (issue 60).
   */
  @Test
  void failedSaveAfterLogoutLogsInAgainInANewTab() throws Exception {
    String title = "IT logged out movie " + UUID.randomUUID();
    Page page = browser.loggedIn(Demo.USER);
    open(page, "/features/single-select-datatable");

    try {
      page.context().clearCookies();
      page.route("**/ajax/add-movie", Route::abort);
      page.locator("#open-add-row-dialog-button").click();
      page.locator("#row-title").fill(title);
      page.locator("#row-description").fill("Added after logging in again");
      page.locator("#row-rating").selectOption("PG");
      page.locator("#row-duration").fill("90");
      page.locator("#table-row-save-button").click();

      Locator session = page.locator("#session-dialog");
      assertThat(session)
          .containsText("You've been logged out: your session expired, or you logged out of");
      assertThat(dialogTitle(page, session)).hasText("Logged out");
      assertThat(page.locator("#session-bar")).isVisible();
      assertThat(page.locator("#row-title")).hasValue(title);

      Page tab = page.waitForPopup(() -> dialogButton(page, session, "Log in").click());
      DemoBrowser.submitLoginForm(tab, Demo.USER);
      tab.waitForClose(() -> {}); // The logged-in page closes the tab it ends on

      assertThat(session)
          .hasText(
              "You're logged in again as " + Demo.USER + ". Close this message and try again.");
      assertThat(dialogTitle(page, session)).hasText("Logged in");
      assertThat(page.locator("#session-bar")).hasCount(0);

      dialogButton(page, session, "Close").click();
      page.unroute("**/ajax/add-movie");
      page.locator("#table-row-save-button").click();
      assertThat(page.locator("#movie-table tbody tr", new Page.LocatorOptions().setHasText(title)))
          .isVisible();
      assertThat(page.locator("#username-container")).hasText(Demo.USER);
    } finally {
      Session http = Session.loggedIn(Demo.USER);
      var row = MoviesIT.row(http, title);
      if (row != null) {
        http.post("/ajax/remove-movie", Map.of("id[]", row.group(1)));
      }
    }
  }

  /**
   * The settings page's save, to a protected URL, after the session ends: the browser's request
   * follows the redirect to Keycloak and fails, and the page says the user was logged out.
   */
  @Test
  void settingsSaveAfterLogoutSaysTheUserWasLoggedOut() {
    Page page = browser.loggedIn(Demo.ADMIN);
    open(page, "/setup/settings");
    page.locator(".editable-row-table tbody tr").first().click();
    page.locator("#open-edit-row-dialog-button").click();
    page.context().clearCookies();

    page.locator("#table-row-save-button").click();

    assertThat(page.locator("#session-dialog")).containsText("You've been logged out");
    assertThat(page.locator("#table-row-dialog")).isVisible();
    assertThat(page.locator("#table-row-save-button")).hasText("Save");
  }

  /** Returning to a page after a while checks the user is still logged in (issue 60) */
  @Test
  void returningToThePageShowsTheUserWasLoggedOut() {
    Page page = browser.loggedIn(Demo.USER);
    page.context().clearCookies();

    // Not due yet: the page loaded less than checkAfterMinutes ago
    returnToPage(page);
    assertThat(page.locator("#session-bar")).hasCount(0);

    page.evaluate("jlab.session.lastChecked = Date.now() - 10 * 60 * 1000");
    returnToPage(page);
    assertThat(page.locator("#session-bar .session-bar-message"))
        .hasText("You've been logged out: your session expired, or you logged out of another app.");
    assertThat(page.locator("#session-bar .session-login-button")).isVisible();
    assertThat(page.locator("#session-dialog")).hasCount(0);

    // Logged out, it checks on every return, and the bar goes once the user is back
    DemoBrowser.logInAgain(page, Demo.USER);
    returnToPage(page);
    assertThat(page.locator("#session-bar")).hasCount(0);
    assertEquals(List.of(), browser.errors);
  }

  @Test
  void returningToThePageShowsAnotherUserLoggedIn() {
    Page page = browser.loggedIn(Demo.USER);
    page.context().clearCookies();
    DemoBrowser.logInAgain(page, Demo.ADMIN);

    page.evaluate("jlab.session.lastChecked = 0");
    returnToPage(page);

    assertThat(page.locator("#session-bar .session-bar-message"))
        .hasText(
            "This page was opened as "
                + Demo.USER
                + ", but you're now logged in as "
                + Demo.ADMIN
                + ".");
    assertThat(page.locator("#session-bar .session-login-button")).isHidden();
    assertThat(page.locator("#session-bar .session-reload-button")).isVisible();
  }

  @Test
  void returningToAnAnonymousPageDoesNotCheck() {
    Page page = browser.newPage();
    open(page, "/overview");
    List<String> checks = new ArrayList<>();
    page.onRequest(
        request -> {
          if (request.url().contains("/session-status")) {
            checks.add(request.url());
          }
        });

    page.evaluate("jlab.session.lastChecked = 0");
    returnToPage(page);

    assertEquals("", js(page, "jlab.user"));
    assertEquals(List.of(), checks);
    assertThat(page.locator("#session-bar")).hasCount(0);
  }

  /** A dialog that fails to load because the user was logged out says so (issue 60) */
  @Test
  void dialogOpenedAfterLogoutSaysTheUserWasLoggedOut() {
    Page page = browser.loggedIn(Demo.USER);
    open(page, "/breadcrumbs/crumb-one");
    page.context().clearCookies();

    page.evaluate("jlab.openPageInDialog(jlab.contextPath + '/features/tags')");

    assertThat(page.locator("#session-dialog")).containsText("You've been logged out");
    assertThat(page.locator(".page-dialog")).hasCount(0);
    assertEquals(Demo.URL + "/breadcrumbs/crumb-one", page.url());
  }

  /** Fires the events a browser fires when the user returns to the tab, and waits for the check */
  static void returnToPage(Page page) {
    page.evaluate(
        "() => { document.dispatchEvent(new Event('visibilitychange'));"
            + " return jlab.session.checking || Promise.resolve(); }");
  }

  /** The jQuery UI dialog's title, for a dialog's content */
  static Locator dialogTitle(Page page, Locator content) {
    return page.locator(".ui-dialog", new Page.LocatorOptions().setHas(content))
        .locator(".ui-dialog-title");
  }

  static Locator dialogButton(Page page, Locator content, String text) {
    return page.locator(".ui-dialog", new Page.LocatorOptions().setHas(content))
        .locator(".ui-dialog-buttonpane button", new Locator.LocatorOptions().setHasText(text));
  }

  @Test
  void fullscreenButtonsSwitchToAndFromFullscreen() {
    Page page = browser.newPage();
    open(page, "/reports/report-one");

    page.locator("#fullscreen-button").click();
    page.waitForURL(url -> url.contains("print=Y") && url.contains("fullscreen=Y"));
    assertThat(page.locator("body"))
        .hasClass(java.util.regex.Pattern.compile("print .*fullscreen"));

    page.locator("#exit-fullscreen-button").click();
    page.waitForURL(url -> url.contains("print=N") && url.contains("fullscreen=N"));
    assertThat(page.locator("body")).not().hasClass(java.util.regex.Pattern.compile("fullscreen"));
  }

  @Test
  void printMenuItemOpensThePrintVersion() {
    Page page = browser.newPage();
    open(page, "/reports/report-one");

    page.locator("#export-menu-button").click();
    page.locator("#print-menu-item").click();

    page.waitForURL(url -> url.contains("print=Y"));
    assertThat(page.locator("body")).hasClass(java.util.regex.Pattern.compile("print"));
  }

  @Test
  void excelMenuItemDownloadsTheSpreadsheet() {
    Page page = browser.loggedIn(Demo.USER);
    open(page, "/features/single-select-datatable");

    page.locator("#export-menu-button").click();
    Download download = page.waitForDownload(() -> page.locator("#excel-menu-item").click());

    assertTrue(download.suggestedFilename().endsWith(".xlsx"), download.suggestedFilename());
    assertEquals(null, download.failure());
  }

  @Test
  void imageMenuItemDownloadsAnImageOfThePage() throws Exception {
    Page page = browser.newPage();
    open(page, "/reports/report-one");

    page.locator("#export-menu-button").click();
    Download download = page.waitForDownload(() -> page.locator("#image-menu-item").click());

    assertEquals("chart.png", download.suggestedFilename());
    assertEquals(null, download.failure());
    ConvertIT.assertRendersAPage(Files.readAllBytes(download.path()));
  }

  @Test
  void dialogOpenerShowsThePageInADialog() {
    Page page = browser.newPage();
    open(page, "/breadcrumbs/crumb-one");

    page.locator(".dialog-opener").click();
    Locator dialog = page.locator(".page-dialog");
    assertThat(dialog).isVisible();
    assertThat(dialog).containsText("Hello World");
    assertThat(page.locator(".ui-dialog-title")).hasText("Crumb One");

    // Links with partial-support open their page in the same dialog
    dialog.locator("a.partial-support", new Locator.LocatorOptions().setHasText("Next")).click();
    assertThat(page.locator(".ui-dialog-title")).hasText("Crumb Two");
    assertThat(page.locator(".page-dialog")).hasCount(1);

    page.locator(".ui-dialog-titlebar-close").click();
    assertThat(page.locator(".page-dialog")).hasCount(0);
    assertEquals(List.of(), browser.errors);
  }

  /** Reloading a partial, as a successful edit does, keeps the dialog's filter (issue 95) */
  @Test
  void reloadPartialKeepsTheFilterSubmittedInTheDialog() {
    Page page = browser.loggedIn(Demo.USER);
    open(page, "/breadcrumbs/crumb-one");
    page.locator(".dialog-opener").click();
    page.locator(".page-dialog a.partial-support", new Page.LocatorOptions().setHasText("Next"))
        .click();
    assertThat(page.locator(".ui-dialog-title")).hasText("Crumb Two");

    page.locator(".page-dialog .filter-flyout-link").click();
    page.locator(".page-dialog #search").fill(Demo.USER);
    page.locator(".page-dialog .filter-form-submit-button").click();
    assertThat(page.locator(".page-dialog #search")).hasValue(Demo.USER);
    assertThat(page.locator(".page-dialog tbody tr")).hasCount(1);

    page.evaluate("$('.page-dialog').addClass('before-reload'); jlab.reloadPartial();");
    Locator reloaded = page.locator(".page-dialog:not(.before-reload)");
    assertThat(reloaded.locator("#search")).hasValue(Demo.USER);
    assertThat(reloaded.locator("tbody tr")).hasCount(1);
    assertThat(page.locator(".page-dialog")).hasCount(1);
    assertEquals(List.of(), browser.errors);
  }

  /**
   * A partial's scripts run in document order, each external one loaded before the next script, and
   * the dialog opens after them; reopening doesn't run them again (issue 94).
   */
  @Test
  void dialogRunsThePartialsScriptsInOrder() {
    Page page = browser.newPage();
    String base = Demo.URL + "/script-order/";
    String partial =
        """
        <div id="partial" data-title="Script Order">
          <div id="partial-css"></div>
          <div id="partial-js">
            <script src="%1$sslow.js"></script>
            <script src="%1$sfast.js"></script>
            <script id="script-order-init">
              order.push('init:' + typeof slowValue + ',' + typeof fastValue);
              $(document).on('click', '#script-order-button', function () { clicks++; });
            </script>
          </div>
          <div id="partial-html"><button id="script-order-button">Click</button></div>
        </div>
        """
            .formatted(base);
    List<Route> heldSlow = new ArrayList<>();
    List<String> requested = new ArrayList<>();
    page.route(
        url -> url.startsWith(base),
        route -> {
          String url = route.request().url();
          requested.add(url.substring(base.length()));
          if (url.startsWith(base + "slow.js")) {
            heldSlow.add(route); // Loads after fast.js would, if they weren't in order
          } else if (url.startsWith(base + "fast.js")) {
            fulfillScript(route, "order.push('fast:' + typeof slowValue); fastValue = 2;");
          } else {
            route.fulfill(new Route.FulfillOptions().setContentType("text/html").setBody(partial));
          }
        });
    open(page, "/hello");
    page.evaluate(
        "window.order = []; window.clicks = 0;"
            + " $(document).on('partial-page-init', function () { order.push('shown'); });"
            + " jlab.openPageInDialog('"
            + base
            + "page');");

    page.waitForCondition(() -> !heldSlow.isEmpty());
    page.waitForTimeout(500);
    assertEquals("", js(page, "order.join()"));
    assertThat(page.locator(".page-dialog")).hasCount(0);
    fulfillScript(heldSlow.get(0), "order.push('slow'); slowValue = 1;");

    assertThat(page.locator(".page-dialog")).isVisible();
    assertEquals("slow,fast:number,init:number,number,shown", js(page, "order.join()"));

    page.locator(".ui-dialog-titlebar-close").click();
    page.evaluate("jlab.openPageInDialog('" + base + "page')");
    assertThat(page.locator(".page-dialog")).isVisible();
    page.locator("#script-order-button").click();

    assertEquals("slow,fast:number,init:number,number,shown,shown", js(page, "order.join()"));
    assertEquals("1", js(page, "clicks"));
    assertEquals(List.of("page?partial=Y", "slow.js", "fast.js", "page?partial=Y"), requested);
    assertEquals(List.of(), browser.errors);
  }

  static void fulfillScript(Route route, String script) {
    route.fulfill(new Route.FulfillOptions().setContentType("text/javascript").setBody(script));
  }

  /** An app configures jlab.pageDialog before loading smoothness.js; resizable defaults to true */
  @ParameterizedTest
  @CsvSource({"'', true", "'resizable: false', false", "'resizable: true', true"})
  void pageDialogKeepsTheConfiguredResizable(String config, boolean resizable) {
    Page page = browser.newPage();
    page.addInitScript("window.jlab = {pageDialog: {" + config + "}};");
    open(page, "/breadcrumbs/crumb-one");

    assertEquals(String.valueOf(resizable), js(page, "jlab.pageDialog.resizable"));
    page.locator(".dialog-opener").click();
    assertThat(page.locator(".page-dialog")).isVisible();
    assertThat(page.locator(".ui-dialog.ui-resizable")).hasCount(resizable ? 1 : 0);
    assertEquals(List.of(), browser.errors);
  }

  @Test
  void paginationButtonsSubmitTheOffset() {
    Page page = browser.loggedIn(Demo.USER);
    open(page, "/breadcrumbs/crumb-two?max=2");

    assertThat(page.locator(".previous-button")).isDisabled();
    page.locator(".next-button").click();

    page.waitForURL(url -> url.contains("offset=2"));
    assertThat(page.locator(".previous-button")).isEnabled();
    assertEquals(List.of(), browser.errors);
  }
}
