package org.jlab.smoothness.presentation.filter;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.util.Arrays;

/**
 * WebFilter for setting response cache directives. By default, will set maximum cache (1 year) for
 * all responses with a mime type in the CACHEABLE_CONTENT_TYPES array. In a given servlet (request
 * handler) cast response to CacheControlResponse to override the default behavior and either
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

  public static final long MAX_EXPIRE_MILLIS = 31536000000L; // 365 days is max expires per spec

  public enum CachableResponse {
    MAX, // Cache for spec max of 1 year
    OFF, // Don't cache in filter, so maybe you can do your own cache logic
    AUTO // Based on content type, will determine either OFF or MAX.  This is default.
  }

  /**
   * Only mime types of files that ALWAYS should be cached are included. Notably excluded are types
   * text/html and application/json. These rarely should be cached, though sometimes they should and
   * in those cases the Servlet needs to manually cast response to CacheControlResponse and use
   * setContentType(type, cachable) with value CachableResponse.MAX.
   */
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

    chain.doFilter(request, new CacheControlResponse(httpResponse));
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {}

  @Override
  public void destroy() {}

  /**
   * We use a Wrapper because some application servers don't allow you to set Headers in
   * post-processing (filter chain), and in pre-processing the content type often isn't known yet.
   * Wrapper allows us to run filter code during servlet processing at the specific moment we learn
   * of content type.
   *
   * <p>https://stackoverflow.com/questions/2563344/how-to-add-response-headers-based-on-content-type-getting-content-type-before-t
   */
  class CacheControlResponse extends HttpServletResponseWrapper {

    private CachableResponse cachable = CachableResponse.AUTO;

    CacheControlResponse(HttpServletResponse response) {
      super(response);
    }

    public void setContentType(String type, CachableResponse cachable) {
      this.cachable = cachable;
      this.setContentType(type);
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
      super.setDateHeader("Expires", System.currentTimeMillis() + MAX_EXPIRE_MILLIS);
      super.setHeader(
          "Cache-Control", null); // Remove header automatically added by SSL/TLS container module
      super.setHeader(
          "Pragma", null); // Remove header automatically added by SSL/TLS container module
    }
  }
}
