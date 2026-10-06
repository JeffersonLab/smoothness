package org.jlab.demo;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.KeyboardModifier;
import com.microsoft.playwright.options.LoadState;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** The weblib's tags with their JavaScript, in Chromium, on the demo's Features > Tags page. */
class TagsBrowserIT {

  static final DateTimeFormatter FRIENDLY_DATE =
      DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.US);

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

  static Page tagsPage() {
    Page page = browser.loggedIn(Demo.USER);
    page.navigate(Demo.URL + "/features/tags");
    page.waitForLoadState(LoadState.NETWORKIDLE);
    return page;
  }

  @Test
  void tagsPageHasNoJavaScriptErrors() {
    tagsPage();

    assertEquals(List.of(), browser.errors);
  }

  @Test
  void dateRangesFillInTheDatesOfANamedRange() {
    Page page = tagsPage();
    LocalDate today = LocalDate.now(ZoneId.of("America/New_York"));
    String todayText = today.format(FRIENDLY_DATE);
    String tomorrowText = today.plusDays(1).format(FRIENDLY_DATE);

    page.locator("#date-flyout .filter-flyout-link").click();
    assertThat(page.locator("#date-flyout .filter-flyout-panel")).isVisible();

    page.locator("#dates-date-range").selectOption("0day");
    assertThat(page.locator("#dates-start")).hasValue(todayText);
    assertThat(page.locator("#dates-end")).hasValue(tomorrowText);

    page.locator("#datetimes-date-range").selectOption("0day");
    assertThat(page.locator("#datetimes-start")).hasValue(todayText + " 07:00");
    assertThat(page.locator("#datetimes-end")).hasValue(tomorrowText + " 07:00");

    assertEquals(List.of(), browser.errors);
  }

  @Test
  void dateRangeWithoutDatesStartsAsCustom() {
    Page page = tagsPage();
    Locator select = page.locator("#dates-date-range");
    Locator inputs =
        page.locator("#date-flyout .date-range-widget").first().locator(".custom-date-range-list");

    page.locator("#date-flyout .filter-flyout-link").click();
    // With no start and end, smoothness.js selects Custom and shows the inputs
    assertThat(select).hasValue("custom");
    assertThat(inputs).isVisible();

    select.selectOption("1day");
    assertThat(inputs).isHidden();

    select.selectOption("custom");
    assertThat(inputs).isVisible();
    assertEquals(List.of(), browser.errors);
  }

  @Test
  void multiselectControlsCountTheSelectedRows() {
    Page page = tagsPage();
    Locator rows = page.locator("#tag-table tbody tr");
    Locator count = page.locator("#selected-count");
    Locator add = page.locator("#open-add-row-dialog-button");
    Locator remove = page.locator("#remove-row-button");
    Locator unselect = page.locator("#unselect-all-button");

    assertThat(count).hasText("0");
    assertThat(remove).isDisabled();

    rows.nth(0).click();
    rows.nth(2).click(new Locator.ClickOptions().setModifiers(List.of(KeyboardModifier.CONTROL)));
    assertThat(count).hasText("2");
    assertThat(remove).isEnabled();
    assertThat(unselect).isEnabled();
    assertThat(add).isDisabled();

    unselect.click();
    assertThat(count).hasText("0");
    assertThat(remove).isDisabled();
    assertThat(add).isEnabled();
    assertEquals(List.of(), browser.errors);
  }

  @Test
  void chartWidgetDrawsTheChart() {
    Page page = browser.newPage();
    page.navigate(Demo.URL + "/reports/report-two");
    page.waitForLoadState(LoadState.NETWORKIDLE);

    // Flot draws in canvases inside the placeholder
    assertThat(page.locator("#report2 canvas").first()).isVisible();
    assertEquals(List.of(), browser.errors);
  }

  @Test
  void loosePageWithoutSmoothnessHasNoJquery() {
    Page page = browser.loggedIn(Demo.USER);
    page.navigate(Demo.URL + "/features/loose-page-without-smoothness");

    assertEquals("undefined", page.evaluate("typeof window.jQuery"));
    assertEquals(List.of(), browser.errors);
  }
}
