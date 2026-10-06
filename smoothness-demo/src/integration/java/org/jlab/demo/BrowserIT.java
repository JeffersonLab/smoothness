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
}
