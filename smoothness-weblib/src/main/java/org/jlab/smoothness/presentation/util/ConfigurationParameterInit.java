package org.jlab.smoothness.presentation.util;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * A ServletContextListener that provide all environment variables to the ServletContext to allow
 * easy configuration.
 */
@WebListener
public class ConfigurationParameterInit implements ServletContextListener {

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    ServletContext context = sce.getServletContext();

    // We will just expose all environment variables!
    context.setAttribute("env", System.getenv());
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    // Nothing to do
  }
}
