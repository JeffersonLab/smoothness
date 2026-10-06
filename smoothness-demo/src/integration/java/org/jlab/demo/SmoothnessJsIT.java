package org.jlab.demo;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.microsoft.playwright.Download;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
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
    List<String> alerts = new ArrayList<>();
    page.onDialog(
        dialog -> {
          alerts.add(dialog.message());
          dialog.dismiss();
        });

    page.locator("#open-add-row-dialog-button").click();
    page.locator("#table-row-save-button").click();

    page.waitForCondition(() -> !alerts.isEmpty());
    assertEquals(List.of("Unable to perform request: title must not be empty"), alerts);
    // The dialog stays open with its buttons back, for the user to fix the form
    assertThat(page.locator("#table-row-dialog")).isVisible();
    assertThat(page.locator("#table-row-save-button")).hasText("Save");
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
