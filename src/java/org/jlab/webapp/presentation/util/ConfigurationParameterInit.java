package org.jlab.webapp.presentation.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ConfigurationParameterInit implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(
            ConfigurationParameterInit.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        /*If the web.xml does not contain a parameter then we better set it*/
        // CDN Path
        if (context.getInitParameter("cdnContextPath") == null) {
            // Let's see if the parameter exists as an environment variable
            String cdnContextPath = System.getenv("cdnContextPath");
            if (cdnContextPath == null || cdnContextPath.trim().isEmpty()) {
                // Let's use a sensible default
                cdnContextPath = "//cdn.acc.jlab.org";
            }

            context.setInitParameter("cdnContextPath", cdnContextPath);
            logger.log(Level.FINEST, "Setting CDN Context Path: {0}", cdnContextPath);
        }

        if (context.getInitParameter("notificationMessage") == null) {
            // Let's see if the parameter exists as an environment variable
            String notification = System.getenv("notificationMessage");

            // If not null, but empty set to null
            if (notification != null && notification.trim().isEmpty()) {
                notification = null;
            }

            context.setInitParameter("notification", notification);
            logger.log(Level.FINEST, "Setting notification: {0}", notification);
        }

        /*
         // Deployed environment (prod|test|local)
         if (context.getInitParameter("GLASSFISH_ENV") == null) {
         // Let's see if the parameter exists as an environment variable
         String env = System.getenv("GLASSFISH_ENV");
         if (env == null || env.trim().isEmpty()) {
         // Let's use a sensible default
         env = "prod";
         }

         context.setInitParameter("env", env);
         logger.log(Level.FINEST, "Setting env: {0}", env);
         }
         */
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Nothing to do
    }

}
