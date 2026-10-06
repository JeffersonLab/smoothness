package org.jlab.smoothness.presentation.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import org.jlab.smoothness.persistence.view.TestSettings;
import org.junit.jupiter.api.Test;

/** IpReadEnabledListener adds IpReadFilter at startup when IP_READ_FILTER_ENABLED is Y. */
class IpReadEnabledListenerTest {

  /** What the listener did to the servlet context */
  final List<String> calls = new ArrayList<>();

  /** A servlet context that records added filters and their mappings. */
  ServletContext context(boolean alreadyRegistered) {
    FilterRegistration.Dynamic registration =
        (FilterRegistration.Dynamic)
            Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class<?>[] {FilterRegistration.Dynamic.class},
                (p, method, args) -> {
                  switch (method.getName()) {
                    case "addMappingForUrlPatterns":
                      calls.add(
                          "map "
                              + args[0]
                              + " "
                              + args[1]
                              + " "
                              + Arrays.toString((String[]) args[2]));
                      return null;
                    case "getName":
                      return "IpReadFilter";
                    default:
                      throw new UnsupportedOperationException(method.getName());
                  }
                });

    return (ServletContext)
        Proxy.newProxyInstance(
            getClass().getClassLoader(),
            new Class<?>[] {ServletContext.class},
            (p, method, args) -> {
              switch (method.getName()) {
                case "getFilterRegistration":
                  return alreadyRegistered ? registration : null;
                case "addFilter":
                  calls.add("add " + args[0] + " " + args[1]);
                  return registration;
                default:
                  throw new UnsupportedOperationException(method.getName());
              }
            });
  }

  void start(String enabled, boolean alreadyRegistered) {
    TestSettings.cache(
        Map.of(
            "IP_READ_FILTER_ENABLED", enabled,
            "IP_READ_URL_PATTERN", "/reports/*",
            "IP_READ_ALLOWLIST_PATTERN", "192\\.168\\..*"));
    new IpReadEnabledListener()
        .contextInitialized(new ServletContextEvent(context(alreadyRegistered)));
  }

  @Test
  void enabledAddsTheFilterForTheUrlPattern() {
    start("Y", false);

    assertEquals(
        List.of(
            "add IpReadFilter org.jlab.smoothness.presentation.filter.IpReadFilter",
            "map " + EnumSet.of(DispatcherType.REQUEST) + " true [/reports/*]"),
        calls);
  }

  @Test
  void disabledAddsNothing() {
    start("N", false);

    assertEquals(List.of(), calls);
  }

  @Test
  void existingRegistrationIsLeftAlone() {
    start("Y", true);

    assertEquals(List.of(), calls);
  }
}
