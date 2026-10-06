package org.jlab.smoothness.presentation.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * A request, response, and filter chain for testing filters, as proxies that record what the filter
 * does; any other call throws UnsupportedOperationException.
 */
final class FakeHttp {

  private FakeHttp() {}

  /** A request's settings, and what the filter set on it. */
  static final class Request {
    String remoteAddr = "10.0.0.1";
    String remoteUser = null;
    String contextPath = "/app";
    String requestUri = "/app/page";
    String characterEncoding = null;
    final Map<String, String> headers = new HashMap<>();
    final Map<String, Object> attributes = new HashMap<>();

    HttpServletRequest proxy() {
      return (HttpServletRequest)
          Proxy.newProxyInstance(
              FakeHttp.class.getClassLoader(),
              new Class<?>[] {HttpServletRequest.class},
              (p, method, args) -> {
                switch (method.getName()) {
                  case "getRemoteAddr":
                    return remoteAddr;
                  case "getRemoteUser":
                    return remoteUser;
                  case "getContextPath":
                    return contextPath;
                  case "getRequestURI":
                    return requestUri;
                  case "getHeader":
                    return headers.get((String) args[0]);
                  case "getAttribute":
                    return attributes.get((String) args[0]);
                  case "setAttribute":
                    attributes.put((String) args[0], args[1]);
                    return null;
                  case "setCharacterEncoding":
                    characterEncoding = (String) args[0];
                    return null;
                  case "toString":
                    return "FakeRequest " + requestUri;
                  default:
                    throw new UnsupportedOperationException(method.getName());
                }
              });
    }
  }

  /** What a filter set on the response. Setting a header to null removes it. */
  static final class Response {
    String contentType = null;
    String characterEncoding = null;
    String redirect = null;
    final Map<String, String> headers = new LinkedHashMap<>();
    final Map<String, Long> dateHeaders = new LinkedHashMap<>();

    HttpServletResponse proxy() {
      return (HttpServletResponse)
          Proxy.newProxyInstance(
              FakeHttp.class.getClassLoader(),
              new Class<?>[] {HttpServletResponse.class},
              (p, method, args) -> {
                switch (method.getName()) {
                  case "setContentType":
                    contentType = (String) args[0];
                    return null;
                  case "setCharacterEncoding":
                    characterEncoding = (String) args[0];
                    return null;
                  case "setHeader":
                    if (args[1] == null) {
                      headers.remove((String) args[0]);
                    } else {
                      headers.put((String) args[0], (String) args[1]);
                    }
                    return null;
                  case "setDateHeader":
                    dateHeaders.put((String) args[0], (Long) args[1]);
                    return null;
                  case "sendRedirect":
                    redirect = (String) args[0];
                    return null;
                  case "toString":
                    return "FakeResponse";
                  default:
                    throw new UnsupportedOperationException(method.getName());
                }
              });
    }
  }

  /** A filter chain that records whether it was called, and with what, running an action. */
  static final class Chain implements FilterChain {
    private final BiConsumer<ServletRequest, ServletResponse> action;
    ServletRequest request;
    ServletResponse response;

    Chain() {
      this((request, response) -> {});
    }

    Chain(BiConsumer<ServletRequest, ServletResponse> action) {
      this.action = action;
    }

    boolean called() {
      return request != null;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response) {
      this.request = request;
      this.response = response;
      action.accept(request, response);
    }
  }
}
