package org.jlab.demo;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Chromium, through Playwright, for tests and screenshots that need the pages' JavaScript to run.
 * Playwright downloads Chromium to ~/.cache/ms-playwright the first time.
 */
final class DemoBrowser implements AutoCloseable {

  private final Playwright playwright;
  private final Browser browser;

  /** JavaScript errors, and console errors such as failed loads, in this browser's pages */
  final List<String> errors = new CopyOnWriteArrayList<>();

  DemoBrowser() {
    playwright = Playwright.create();
    browser =
        playwright
            .chromium()
            .launch(
                new BrowserType.LaunchOptions()
                    // Trust the demo's self-signed certificate, and only that one
                    .setArgs(List.of("--ignore-certificate-errors-spki-list=" + demoSpkiHash())));
  }

  /** A new page with its own cookies, recording its errors. */
  Page newPage() {
    Page page =
        browser.newContext(new Browser.NewContextOptions().setViewportSize(1280, 800)).newPage();

    page.onPageError(error -> errors.add(page.url() + ": " + error));
    page.onConsoleMessage(
        message -> {
          if ("error".equals(message.type())) {
            errors.add(page.url() + ": " + message.text());
          }
        });

    return page;
  }

  /** A new page, logged in through Keycloak's login form, showing the overview. */
  Page loggedIn(String username) {
    Page page = newPage();

    page.navigate(Demo.URL + "/sso?returnUrl=" + URI.create(Demo.URL).getPath() + "/overview");
    page.locator("#username").fill(username);
    page.locator("#password").fill(Demo.PASSWORD);
    page.locator("#kc-login").click();
    page.waitForURL(Demo.URL + "/overview");

    return page;
  }

  @Override
  public void close() {
    playwright.close();
  }

  /** The SHA-256 of the demo certificate's public key, base64, as Chromium's flag expects */
  private static String demoSpkiHash() {
    try {
      byte[] spki = Demo.certificate().getPublicKey().getEncoded();
      return Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-256").digest(spki));
    } catch (IOException | GeneralSecurityException e) {
      throw new IllegalStateException(e);
    }
  }
}
