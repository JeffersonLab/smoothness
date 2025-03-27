package org.jlab.smoothness.presentation.util;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.jlab.smoothness.business.service.SettingsService;
import org.jlab.smoothness.persistence.view.ImmutableSettings;

/**
 * A ServletContextListener that provide the settings to the ServletContext to allow easy
 * configuration. The settings are application-scoped (cached) so either require an app restart, or
 * manual refresh from Setup tab in order to requery from database.
 */
@WebListener
public class SettingsCacheInit implements ServletContextListener {

  @EJB SettingsService settingsService;

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    ServletContext context = sce.getServletContext();

    ImmutableSettings immutable = settingsService.getImmutableSettings();

    SettingsService.cachedSettings = immutable;

    context.setAttribute("settings", immutable);
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    // Nothing to do
  }
}
