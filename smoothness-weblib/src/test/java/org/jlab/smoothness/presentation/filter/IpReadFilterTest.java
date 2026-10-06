package org.jlab.smoothness.presentation.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.jlab.smoothness.persistence.view.TestSettings;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * IpReadFilter lets anyone logged in read, and others only from an IP address the
 * IP_READ_ALLOWLIST_PATTERN setting matches; the rest it sends to log in. The build sets
 * FRONTEND_SERVER_URL for the redirects.
 */
class IpReadFilterTest {

  static final String FRONTEND = System.getenv("FRONTEND_SERVER_URL");

  @BeforeAll
  static void allowlistFirstSubnet() {
    // IpReadFilter reads the cached settings as it loads
    allowlist("192\\.168\\.1\\..*");
  }

  static void allowlist(String pattern) {
    TestSettings.cache(pattern == null ? Map.of() : Map.of("IP_READ_ALLOWLIST_PATTERN", pattern));
    IpReadFilter.reconfigureAllowlist();
  }

  final FakeHttp.Request request = new FakeHttp.Request();
  final FakeHttp.Response response = new FakeHttp.Response();
  final FakeHttp.Chain chain = new FakeHttp.Chain();

  void filter() throws Exception {
    new IpReadFilter().doFilter(request.proxy(), response.proxy(), chain);
  }

  void assertSentToLogin() {
    assertFalse(chain.called());
    assertEquals(
        FRONTEND
            + "/app/sso?returnUrl="
            + URLEncoder.encode(FRONTEND + request.requestUri, StandardCharsets.UTF_8),
        response.redirect);
  }

  @Test
  void runsWithTheBuildsFrontendUrl() {
    assertEquals("https://frontend.example", FRONTEND);
  }

  @Test
  void allowlistedAddressReads() throws Exception {
    request.remoteAddr = "192.168.1.5";

    filter();

    assertTrue(chain.called());
    assertNull(response.redirect);
  }

  @Test
  void otherAddressIsSentToLogin() throws Exception {
    request.remoteAddr = "192.168.2.5";
    request.requestUri = "/app/reports/one";

    filter();

    assertSentToLogin();
    assertEquals(
        "https://frontend.example/app/sso?returnUrl=https%3A%2F%2Ffrontend.example%2Fapp%2Freports%2Fone",
        response.redirect);
  }

  @Test
  void loggedInUserReadsFromAnyAddress() throws Exception {
    request.remoteAddr = "203.0.113.9";
    request.remoteUser = "jdoe";

    filter();

    assertTrue(chain.called());
  }

  @Test
  void blankUserIsNotLoggedIn() throws Exception {
    request.remoteAddr = "203.0.113.9";
    request.remoteUser = " ";

    filter();

    assertSentToLogin();
  }

  @Test
  void usesTheFirstForwardedAddress() throws Exception {
    request.remoteAddr = "10.0.0.1"; // the proxy
    request.headers.put("X-Forwarded-For", "192.168.1.5, 10.0.0.2");

    filter();

    assertTrue(chain.called());
  }

  @Test
  void forwardedAddressOutsideTheAllowlistIsSentToLogin() throws Exception {
    request.remoteAddr = "192.168.1.5"; // a proxy on the allowlist doesn't count
    request.headers.put("X-Forwarded-For", "203.0.113.9");

    filter();

    assertSentToLogin();
  }

  @Test
  void unknownForwardedAddressFallsBackToTheRemoteAddress() throws Exception {
    request.remoteAddr = "192.168.1.5";
    request.headers.put("X-Forwarded-For", "unknown");

    filter();

    assertTrue(chain.called());
  }

  @Test
  void patternMustMatchTheWholeAddress() throws Exception {
    try {
      allowlist("192\\.168");
      request.remoteAddr = "192.168.1.5";

      filter();

      assertSentToLogin();
    } finally {
      allowlistFirstSubnet();
    }
  }

  @Test
  void noPatternAllowsNoAddress() throws Exception {
    try {
      allowlist(null);
      request.remoteAddr = "192.168.1.5";

      filter();

      assertSentToLogin();
    } finally {
      allowlistFirstSubnet();
    }
  }

  @Test
  void redirectLeavesOutTheQueryString() throws Exception {
    // getRequestURI has no query string, so the user returns to the page without its parameters
    request.remoteAddr = "203.0.113.9";
    request.requestUri = "/app/reports/one";

    filter();

    assertTrue(response.redirect.endsWith("%2Fapp%2Freports%2Fone"), response.redirect);
  }
}
