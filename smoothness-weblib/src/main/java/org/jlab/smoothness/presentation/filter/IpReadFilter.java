package org.jlab.smoothness.presentation.filter;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.smoothness.business.service.SettingsService;

/**
 * This filter restricts who can view (read) pages (URL paths) to a whitelist of IP addresses but
 * also allows all authenticated users to also view.
 *
 * <p>More specifically, this filter allows users on-site read access without authentication,
 * whereas offsite users can only view if authenticated.
 *
 * <p>Wildfly Undertow provides an IP Access Control filter, but configuring it to only apply when
 * not authenticated doesn't appear to be supported.
 *
 * <p>This filter is proxy-aware, meaning it checks for the client IP in X-Forwarded-For header, not
 * the immediate client IP, which is the proxy server.
 *
 * <p>This filter uses the DB Setting IP_READ_WHITELIST_PATTERN to set the Java REGEX pattern to
 * match client IPs against. For example, to match any client IPs in 192.168.x you'd use pattern
 * "192\.168.*". Notice you must escape the first period literal. The second period means match any
 * character and the asterisk means any number of times. Also note, that if you were to specify this
 * regex in a Java String literal in the code, you'd need to escape the escape.
 *
 * <p>This filter uses the DB Setting IP_READ_URL_PATTERN to set which pages (URLs) this filter
 * applies to. The filter is enabled via the DB Setting IP_READ_FILTER_ENABLED. The
 * IP_READ_WHITELIST_PATTERN is runtime configurable, however changes to IP_READ_FILTER_ENABLED and
 * IP_READ_URL_PATTERN both require app redeploy because Java app servers do not generally allow
 * reconfiguring filter url patterns nor removing existing filters at runtime.
 */
public class IpReadFilter implements Filter {

  static String WHITELIST_PATTERN;
  static Pattern PATTERN;

  public static void reconfigureWhitelist() {
    WHITELIST_PATTERN = SettingsService.cachedSettings.get("IP_READ_WHITELIST_PATTERN");

    if (WHITELIST_PATTERN == null || WHITELIST_PATTERN.isBlank()) {
      PATTERN = null;
    } else {
      PATTERN = Pattern.compile(WHITELIST_PATTERN);
    }
  }

  static {
    reconfigureWhitelist();
  }

  @Override
  public void doFilter(
      ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
      throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
    String ipAddress = httpRequest.getHeader("X-Forwarded-For");

    if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = httpRequest.getRemoteAddr();
    } else {
      String[] ipAddresses = ipAddress.split(",");
      ipAddress = ipAddresses[0].trim();
    }

    String username = httpRequest.getRemoteUser();

    boolean authenticated = username != null && !username.isBlank();

    /*System.out.println("context path: " + httpRequest.getContextPath());
    System.out.println("request path: " + httpRequest.getRequestURI());
    System.out.println("WHITELIST_PATTERN: " + WHITELIST_PATTERN);
    System.out.println("Authenticated: " + authenticated);
    System.out.println("Client Username: " + username);
    System.out.println("Client IP Address: " + ipAddress);*/

    if (authenticated || inWhitelist(ipAddress)) {
      filterChain.doFilter(servletRequest, servletResponse);
    } else {
      HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

      String redirectUrl = getRedirectUrl(httpRequest);

      // System.out.println("REDIRECT_URL: " + redirectUrl);

      httpResponse.sendRedirect(redirectUrl);
    }
  }

  private String getRedirectUrl(HttpServletRequest httpRequest) {
    String contextPath = httpRequest.getContextPath();

    String requestUri = httpRequest.getRequestURI();

    String serverUrl = System.getenv("FRONTEND_SERVER_URL");

    String returnUrl = serverUrl + requestUri;

    returnUrl = URLEncoder.encode(returnUrl, StandardCharsets.UTF_8);

    return serverUrl + contextPath + "/sso?returnUrl=" + returnUrl;
  }

  private boolean inWhitelist(String ipAddress) {
    return PATTERN != null && PATTERN.matcher(ipAddress).matches();
  }
}
