package org.jlab.smoothness.presentation.util;

import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ConfigurationParameterInit implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(
            ConfigurationParameterInit.class.getName());

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
