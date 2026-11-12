package org.jlab.smoothness.presentation.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * ClientAddress takes the client's address from the X-Forwarded-For entry that the trusted proxies
 * added, counting from the end, since a client can put entries of its own first. The filter tests
 * cover reading the header from a request.
 */
class ClientAddressTest {

  static final String PROXY = "10.0.0.1";

  static String resolve(int trustedProxies, String... forwardedFor) {
    return ClientAddress.resolve(List.of(forwardedFor), PROXY, trustedProxies);
  }

  @Test
  void oneProxyTakesTheLastEntry() {
    assertEquals("203.0.113.9", resolve(1, "203.0.113.9"));
    assertEquals("203.0.113.9", resolve(1, "192.168.1.5", "203.0.113.9"));
    assertEquals("203.0.113.9", resolve(1, "192.168.1.5", "198.51.100.7", "203.0.113.9"));
  }

  @Test
  void twoProxiesTakeTheSecondEntryFromTheEnd() {
    assertEquals("203.0.113.9", resolve(2, "203.0.113.9", "10.0.0.2"));
    assertEquals("203.0.113.9", resolve(2, "192.168.1.5", "203.0.113.9", "10.0.0.2"));
  }

  @Test
  void fewerEntriesThanProxiesTakesTheFirst() {
    assertEquals("203.0.113.9", resolve(2, "203.0.113.9"));
    assertEquals("203.0.113.9", resolve(3, "203.0.113.9", "10.0.0.2"));
  }

  @Test
  void noProxyIgnoresTheHeader() {
    assertEquals(PROXY, resolve(0, "192.168.1.5"));
    assertEquals(PROXY, resolve(0, "192.168.1.5", "203.0.113.9"));
  }

  @Test
  void noHeaderIsTheConnectionsAddress() {
    assertEquals(PROXY, resolve(1));
    assertEquals(PROXY, resolve(2));
  }

  @Test
  void emptyOrUnknownEntryIsTheConnectionsAddress() {
    assertEquals(PROXY, resolve(1, "203.0.113.9", ""));
    assertEquals(PROXY, resolve(1, "unknown"));
    assertEquals(PROXY, resolve(1, "203.0.113.9", "Unknown"));
    assertEquals("203.0.113.9", resolve(1, "unknown", "203.0.113.9"));
  }

  @Test
  void trustedProxyCountDefaultsToOne() {
    assertEquals(1, ClientAddress.DEFAULT_TRUSTED_PROXIES);
    assertEquals(1, ClientAddress.parseTrustedProxies(null));
    assertEquals(1, ClientAddress.parseTrustedProxies(""));
    assertEquals(1, ClientAddress.parseTrustedProxies("  "));
  }

  @Test
  void trustedProxyCountIsAWholeNumberFromZero() {
    assertEquals(0, ClientAddress.parseTrustedProxies("0"));
    assertEquals(2, ClientAddress.parseTrustedProxies("2"));
    assertEquals(3, ClientAddress.parseTrustedProxies(" 3 "));
  }

  @Test
  void invalidTrustedProxyCountIsTheDefault() {
    assertEquals(1, ClientAddress.parseTrustedProxies("-1"));
    assertEquals(1, ClientAddress.parseTrustedProxies("one"));
    assertEquals(1, ClientAddress.parseTrustedProxies("1.5"));
  }
}
