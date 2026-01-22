package org.jlab.smoothness.presentation.filter;

import java.io.IOException;
import java.util.Arrays;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * WebFilter for setting response cache directives. By default, will set maximum cache (1 year) for
 * all responses with a mime type in the CACHEABLE_CONTENT_TYPES array. In a given servlet (request
 * handler) the CACHEABLE_RESPONSE attribute can be used to override the default behavior and either
 * forcibly set to OFF (no cache headers set) or MAX (set max cache headers of 1 year).
 *
 * @author ryans
 */
@WebFilter(
    filterName = "CacheFilter",
    urlPatterns = {"/*"},
    dispatcherTypes = {DispatcherType.REQUEST, DispatcherType.FORWARD},
    asyncSupported = true)
public class CacheFilter implements Filter {

  private static final long EXPIRE_MILLIS = 31536000000L; // 365 days is max expires per spec

  // Name of attribute to set on request processor to override default CachableResponse Auto
  // behavior.
  // Value should be CachableResponse enum
  public final String CACHEABLE_RESPONSE = "CACHEABLE_RESPONSE";

  public enum CachableResponse {
    MAX, // Cache for spec max of 1 year
    OFF, // Don't cache in filter, so maybe you can do your own cache logic
    AUTO // Based on content type, will determine either OFF or MAX.  This is default.
  }

  private static final String[] CACHEABLE_CONTENT_TYPES =
      new String[] {
        "text/css",
        "text/javascript",
        "application/javascript",
        "image/png",
        "image/jpeg",
        "image/jpg",
        "image/gif",
        "image/icon",
        "image/x-icon",
        "image/vnd.microsoft.icon",
        "image/svg+xml"
      };

  static {
    Arrays.sort(CACHEABLE_CONTENT_TYPES);
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    CachableResponse cachable = (CachableResponse) request.getAttribute(CACHEABLE_RESPONSE);

    chain.doFilter(request, new CacheControlResponse(httpResponse, cachable));
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {}

  @Override
  public void destroy() {}

  class CacheControlResponse extends HttpServletResponseWrapper {

    private CachableResponse cachable;

    CacheControlResponse(HttpServletResponse response, CachableResponse cachable) {
      super(response);
      this.cachable = cachable;
    }

    @Override
    public void setContentType(String type) {
      super.setContentType(type);

      if (cachable == null || cachable == CachableResponse.AUTO) {

        if (type != null && Arrays.binarySearch(CACHEABLE_CONTENT_TYPES, type) > -1) {
          setMaxCache();
        } else {
          setNoCache();
        }
      } else if (cachable == CachableResponse.MAX) {
        setMaxCache();
      } else { // OFF
        setNoCache();
      }
    }

    private void setNoCache() {
      super.setHeader("Cache-Control", "no-store, no-cache, must-revalidate"); // HTTP 1.1
      super.setHeader("Pragma", "no-cache"); // HTTP 1.0
      super.setDateHeader("Expires", 0); // Proxies
    }

    private void setMaxCache() {
      super.setDateHeader("Expires", System.currentTimeMillis() + EXPIRE_MILLIS);
      super.setHeader(
          "Cache-Control", null); // Remove header automatically added by SSL/TLS container module
      super.setHeader(
          "Pragma", null); // Remove header automatically added by SSL/TLS container module
    }
  }
}
