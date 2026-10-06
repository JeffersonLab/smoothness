package org.jlab.demo;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.options.LoadState;
import java.nio.file.Path;

/**
 * Saves a full-page screenshot of a page of the running demo, to see a UI change: {@code ./gradlew
 * screenshot -Ppage=/features/multiselect-datatable -Puser=jdoe}. It prints the file's path, and
 * any JavaScript errors on the page.
 */
public final class Screenshot {

  private Screenshot() {}

  /**
   * @param args the page's path in the app; the user to log in as, or empty; the directory to save
   *     the screenshot in
   */
  public static void main(String[] args) {
    String path = args[0];
    String user = args[1];
    Path dir = Path.of(args[2]);

    try (DemoBrowser browser = new DemoBrowser()) {
      Page page = user.isEmpty() ? browser.newPage() : browser.loggedIn(user);
      Response response = page.navigate(Demo.URL + path);
      page.waitForLoadState(LoadState.NETWORKIDLE);

      String name = (path + (user.isEmpty() ? "" : "-" + user)).replaceAll("[^A-Za-z0-9]+", "-");
      Path file = dir.resolve(name.replaceAll("^-|-$", "") + ".png");
      page.screenshot(new Page.ScreenshotOptions().setPath(file).setFullPage(true));

      System.out.println(
          "Wrote " + file + " of " + page.url() + " (HTTP " + response.status() + ")");
      browser.errors.forEach(error -> System.out.println("JavaScript error: " + error));
    }
  }
}
