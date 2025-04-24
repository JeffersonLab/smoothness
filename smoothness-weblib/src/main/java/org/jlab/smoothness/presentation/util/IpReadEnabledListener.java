package org.jlab.smoothness.presentation.util;

import java.util.EnumSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.*;
import javax.servlet.annotation.WebListener;
import org.jlab.smoothness.business.service.SettingsService;
import org.jlab.smoothness.persistence.view.ImmutableSettings;

/**
 * This ServletContextListener conditionally enables the IpReadFilter.
 *
 * <p>This simply allows users to opt-in to using the filter via DB Setting instead of having to
 * re-build the app with an updated web.xml. Specifically, the filter can be applied conditionally
 * based on the environment instead of fixed at build time.
 *
 * <p>Note: Users still must re-deploy the app for settings to take effect. Most app servers do not
 * allow you to reconfigure the urlPattern of a Servlet Filter dynamically at runtime, and you also
 * can't remove a filter once it's added.
 */
@WebListener
public class IpReadEnabledListener implements ServletContextListener {

  private static final Logger LOGGER = Logger.getLogger(IpReadEnabledListener.class.getName());

  @Override
  public void contextInitialized(ServletContextEvent event) {
    ImmutableSettings settings = SettingsService.cachedSettings;

    boolean enabled = settings.is("IP_READ_FILTER_ENABLED");
    String urlPattern = settings.get("IP_READ_URL_PATTERN");
    String ipPattern = settings.get("IP_READ_WHITELIST_PATTERN");

    LOGGER.log(
        Level.INFO,
        "IP READ FILTER ENABLED: {0}, URL PATTERN: {1}, IP WHITELIST: {2}",
        new Object[] {enabled, urlPattern, ipPattern});

    if (enabled) {
      ServletContext context = event.getServletContext();

      FilterRegistration registration = context.getFilterRegistration("IpReadFilter");

      if (registration == null) {
        registration =
            context.addFilter("IpReadFilter", "org.jlab.sim.presentation.util.IpReadFilter");

        EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(DispatcherType.REQUEST);

        registration.addMappingForUrlPatterns(dispatcherTypes, true, urlPattern);
      } else {
        LOGGER.log(
            Level.WARNING,
            "Existing Filter registration found with name {0}",
            registration.getName());
      }
    }
  }
}
