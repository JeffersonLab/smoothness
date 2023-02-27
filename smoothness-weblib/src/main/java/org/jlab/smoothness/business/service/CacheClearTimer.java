package org.jlab.smoothness.business.service;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class CacheClearTimer {
    private static final Logger LOGGER = Logger.getLogger(
            CacheClearTimer.class.getName());
    @Schedule(hour="0", minute="0", second="0", persistent=false, info="CacheClearTimer")
    public void scheduledCacheClear() {
        LOGGER.log(Level.FINE, "CacheClearTimer");
        UserAuthorizationService service = UserAuthorizationService.getInstance();
        service.clearCache();
    }
}
