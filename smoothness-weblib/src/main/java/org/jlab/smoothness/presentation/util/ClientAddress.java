package org.jlab.smoothness.presentation.util;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The address a request came from, for an app that may sit behind reverse proxies.
 *
 * <p>Each proxy adds the address it received the request from to the end of the X-Forwarded-For
 * header, so only the entries the installation's own proxies added can be believed: a client can
 * send a header of its own, and those entries come first. The environment variable
 * TRUSTED_PROXY_COUNT says how many proxies are in front of the app, and the client's address is
 * the entry that many from the end:
 *
 * <ul>
 *   <li>1, the default: the last entry, which the one proxy added.
 *   <li>2 or more: that many entries from the end, or the first entry if there are fewer.
 *   <li>0: the header is ignored and the address is the connection's, for an app that clients reach
 *       directly.
 * </ul>
 *
 * <p>Without the header, or if the chosen entry is empty or "unknown", the address is the
 * connection's, as {@link HttpServletRequest#getRemoteAddr()} reports it.
 *
 * <p>The count must match what every request passes through. If some requests skip a proxy, such as
 * clients inside a network that an outer proxy does not serve, have the innermost proxy replace the
 * header with the address it trusts and leave the count at 1.
 *
 * @author ryans
 */
public final class ClientAddress {

  private static final Logger LOGGER = Logger.getLogger(ClientAddress.class.getName());

  /** The environment variable that holds the number of trusted proxies. */
  public static final String TRUSTED_PROXY_COUNT = "TRUSTED_PROXY_COUNT";

  /** The number of trusted proxies when the environment variable is not set. */
  public static final int DEFAULT_TRUSTED_PROXIES = 1;

  private static final int TRUSTED_PROXIES =
      parseTrustedProxies(System.getenv(TRUSTED_PROXY_COUNT));

  private ClientAddress() {
    // Can't instantiate publicly
  }

  /**
   * The address the request came from, by the X-Forwarded-For header and the TRUSTED_PROXY_COUNT
   * environment variable.
   *
   * @param request The request
   * @return The client's address
   */
  public static String of(HttpServletRequest request) {
    return resolve(forwardedFor(request), request.getRemoteAddr(), TRUSTED_PROXIES);
  }

  /**
   * The request's X-Forwarded-For entries in order. A proxy may add its entry as a header line of
   * its own, after the client's, so every line counts and not only the first.
   */
  private static List<String> forwardedFor(HttpServletRequest request) {
    List<String> entries = new ArrayList<>();
    Enumeration<String> lines = request.getHeaders("X-Forwarded-For");

    while (lines != null && lines.hasMoreElements()) {
      String line = lines.nextElement();
      if (line != null && !line.isBlank()) {
        for (String entry : line.split(",", -1)) {
          entries.add(entry.trim());
        }
      }
    }

    return entries;
  }

  /**
   * Choose the client's address.
   *
   * @param forwardedFor The X-Forwarded-For entries, in order
   * @param remoteAddr The connection's address
   * @param trustedProxies The number of proxies in front of the app
   * @return The client's address
   */
  static String resolve(List<String> forwardedFor, String remoteAddr, int trustedProxies) {
    if (trustedProxies <= 0 || forwardedFor.isEmpty()) {
      return remoteAddr;
    }

    String entry = forwardedFor.get(Math.max(0, forwardedFor.size() - trustedProxies));

    if (entry.isEmpty() || "unknown".equalsIgnoreCase(entry)) {
      return remoteAddr;
    }

    return entry;
  }

  /**
   * Read the number of trusted proxies. A value that is not a whole number of zero or more is
   * logged and the default is used.
   *
   * @param value The environment variable's value, or null if it is not set
   * @return The number of trusted proxies
   */
  static int parseTrustedProxies(String value) {
    if (value == null || value.isBlank()) {
      return DEFAULT_TRUSTED_PROXIES;
    }

    try {
      int count = Integer.parseInt(value.trim());
      if (count >= 0) {
        return count;
      }
    } catch (NumberFormatException e) {
      // logged below
    }

    LOGGER.log(
        Level.SEVERE,
        "{0} must be a whole number of 0 or more, but is \"{1}\"; using {2}",
        new Object[] {TRUSTED_PROXY_COUNT, value, DEFAULT_TRUSTED_PROXIES});

    return DEFAULT_TRUSTED_PROXIES;
  }
}
