package org.jlab.smoothness.presentation.util;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An HttpServletRequest with only parameters, a context path, and a servlet path; any other call
 * throws UnsupportedOperationException.
 */
final class FakeRequest {

  private FakeRequest() {}

  /** A request with the parameters, given as name and value pairs; a name may repeat. */
  static HttpServletRequest withParams(String... namesAndValues) {
    Map<String, String[]> params = new LinkedHashMap<>();
    for (int i = 0; i < namesAndValues.length; i += 2) {
      String name = namesAndValues[i];
      String[] old = params.getOrDefault(name, new String[0]);
      String[] values = Arrays.copyOf(old, old.length + 1);
      values[old.length] = namesAndValues[i + 1];
      params.put(name, values);
    }
    return create(params, "/app", "/page");
  }

  static HttpServletRequest create(
      Map<String, String[]> params, String contextPath, String servletPath) {
    return (HttpServletRequest)
        Proxy.newProxyInstance(
            FakeRequest.class.getClassLoader(),
            new Class<?>[] {HttpServletRequest.class},
            (proxy, method, args) -> {
              switch (method.getName()) {
                case "getParameter":
                  String[] values = params.get((String) args[0]);
                  return values == null ? null : values[0];
                case "getParameterValues":
                  return params.get((String) args[0]);
                case "getContextPath":
                  return contextPath;
                case "getServletPath":
                  return servletPath;
                case "toString":
                  return "FakeRequest" + params.keySet();
                default:
                  throw new UnsupportedOperationException(method.getName());
              }
            });
  }
}
