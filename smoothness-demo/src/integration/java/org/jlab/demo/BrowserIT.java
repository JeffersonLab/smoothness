package org.jlab.demo;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.KeyboardModifier;
import com.microsoft.playwright.options.LoadState;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import org.jlab.demo.Demo.Session;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** The pages in Chromium, with their JavaScript: smoothness.js, jQuery UI, and the demo's own. */
class BrowserIT {

  static DemoBrowser browser;

  @BeforeAll
  static void start() throws Exception {
    Demo.awaitReady();
    browser = new DemoBrowser();
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

  @ParameterizedTest
  @ValueSource(
      strings = {"/overview", "/help", "/hello", "/reports/report-one", "/breadcrumbs/crumb-one"})
  void publicPageHasNoJavaScriptErrors(String path) {
    open(browser.newPage(), path);

    assertEquals(List.of(), browser.errors);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "/features/multiselect-datatable",
        "/features/single-select-datatable",
        "/setup/settings"
      })
  void adminPageHasNoJavaScriptErrors(String path) {
    open(browser.loggedIn(Demo.ADMIN), path);

    assertEquals(List.of(), browser.errors);
  }

  @Test
  void loginLinkReturnsToThePageWithItsParameters() {
    Page page = browser.newPage();
    open(page, LoginIT.REPORT_WITH_DATES);

    page.locator("#login-link").click();
    page.locator("#username").fill(Demo.USER);
    page.locator("#password").fill(Demo.PASSWORD);
    page.locator("#kc-login").click();
    page.waitForURL(Demo.URL + LoginIT.REPORT_WITH_DATES);

    assertThat(page.locator("#report1-start")).hasValue("29-Sep-2026 07:00");
    assertThat(page.locator("#report1-end")).hasValue("06-Oct-2026 07:00");
    assertThat(page.locator("body")).containsText("Welcome " + Demo.USER);
    assertEquals(List.of(), browser.errors);
  }

  @Test
  void filterFlyoutOpensAndCloses() {
    Page page = browser.newPage();
    open(page, "/reports/report-one");
    Locator panel = page.locator(".filter-flyout-panel");

    assertThat(panel).isHidden();
    page.locator(".filter-flyout-link").click();
    assertThat(panel).isVisible();
    page.locator(".filter-flyout-close-button").click();
    assertThat(panel).isHidden();
    assertEquals(List.of(), browser.errors);
  }

  @Test
  void editTheRatingOfTwoSelectedMovies() throws Exception {
    Session http = Session.loggedIn(Demo.USER);
    String first = "IT browser movie " + UUID.randomUUID();
    String second = "IT browser movie " + UUID.randomUUID();
    for (String title : List.of(first, second)) {
      http.post(
          "/ajax/add-movie", Map.of("title", title, "rating", "PG", "release", "10-Mar-2026"));
    }

    try {
      Page page = browser.loggedIn(Demo.USER);
      open(page, MoviesIT.TABLE);
      Locator firstRow = page.locator("tr", new Page.LocatorOptions().setHasText(first));
      Locator secondRow = page.locator("tr", new Page.LocatorOptions().setHasText(second));
      Locator editRating = page.locator("#open-edit-rating-dialog-button");

      assertThat(editRating).isDisabled();
      firstRow.locator("td").first().click();
      secondRow
          .locator("td")
          .first()
          .click(new Locator.ClickOptions().setModifiers(List.of(KeyboardModifier.CONTROL)));
      assertThat(firstRow).hasClass(Pattern.compile("selected-row"));
      assertThat(secondRow).hasClass(Pattern.compile("selected-row"));
      assertThat(editRating).isEnabled();

      editRating.click();
      Locator dialog = page.locator("#rating-dialog");
      assertThat(dialog).isVisible();
      assertThat(dialog.locator("#movie-selected-row-list li"))
          .hasText(new String[] {first, second});

      page.locator("#edit-rating").selectOption("G");
      // Saving reloads the page; the assertions below wait for it
      page.locator("#rating-save-button").click();

      assertThat(firstRow.locator("td").nth(2)).hasText("G");
      assertThat(secondRow.locator("td").nth(2)).hasText("G");
      assertEquals(List.of(), browser.errors);
    } finally {
      for (String title : List.of(first, second)) {
        var row = MoviesIT.row(http, title);
        if (row != null) {
          http.post("/ajax/remove-movie", Map.of("id[]", row.group(1)));
        }
      }
    }
  }

  @Test
  void editACsvSettingOneItemPerLine() throws Exception {
    Session http = Session.loggedIn(Demo.ADMIN);
    String label = "IT doc " + UUID.randomUUID();

    try {
      Page page = browser.loggedIn(Demo.ADMIN);
      open(page, "/setup/settings?key=" + SettingsIT.HELP_DOC_CSV);
      Locator row = page.locator("#settings-table tbody tr");
      Locator dialog = page.locator("#table-row-dialog");
      Locator items = dialog.locator("#row-csv-value");

      row.locator("td").first().click();
      page.locator("#open-edit-row-dialog-button").click();
      assertThat(items).isVisible();
      assertThat(dialog.locator("#row-value")).isHidden();
      assertThat(items).hasValue(SettingsIT.DEFAULT_HELP_DOC);

      // A line with a comma is refused, not split into two items
      items.fill("https://example.com/one|One\nhttps://example.com/two|Two, Three");
      page.locator("#table-row-save-button").click();
      assertThat(page.locator(".error-dialog"))
          .containsText("Line 2 contains a comma, which would split it into two items");
      page.locator(".error-dialog")
          .locator("..")
          .locator("button", new Locator.LocatorOptions().setHasText("OK"))
          .click();

      items.fill("  https://example.com/one|" + label + "  \n\n https://example.com/two|Two\n\n");
      // Saving reloads the page; the assertions below wait for it
      page.locator("#table-row-save-button").click();

      assertThat(row.locator("td").nth(2).locator("li"))
          .hasText(
              new String[] {"https://example.com/one|" + label, "https://example.com/two|Two"});
      assertThat(row)
          .hasAttribute(
              "data-value", "https://example.com/one|" + label + ",https://example.com/two|Two");
      assertEquals(List.of(), browser.errors);
    } finally {
      SettingsIT.edit(http, SettingsIT.HELP_DOC_CSV, SettingsIT.DEFAULT_HELP_DOC);
    }
  }

  @Test
  void editAStringSettingInOneLine() {
    Page page = browser.loggedIn(Demo.ADMIN);
    open(page, "/setup/settings?key=NOTIFICATION_MESSAGE");

    page.locator("#settings-table tbody tr td").first().click();
    page.locator("#open-edit-row-dialog-button").click();

    Locator dialog = page.locator("#table-row-dialog");
    assertThat(dialog.locator("#row-value")).hasValue(SettingsIT.DEFAULT_MESSAGE);
    assertThat(dialog.locator("#row-csv-value")).isHidden();
    assertEquals(List.of(), browser.errors);
  }
}
