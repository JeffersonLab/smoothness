package org.jlab.smoothness.presentation.filter;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ryans
 */
public class AuditContext {

    private Map<String, Object> extra = new HashMap<>();
    private String username;
    private String ip;

    public String getIp() {
        return ip;
    }

    protected void setIp(String ip) {
        this.ip = ip;
    }

    public String getUsername() {
        return username;
    }

    public Object getExtra(String key) {
        return extra.get(key);
    }

    public Object putExtra(String key, Object value) {
        return extra.put(key, value);
    }

    protected void setUsername(String username) {
        this.username = username;
    }
    private static ThreadLocal<AuditContext> instance = new ThreadLocal<AuditContext>() {

        @Override
        protected AuditContext initialValue() {
            return null;
        }
    };

    public static AuditContext getCurrentInstance() {
        return instance.get();
    }

    protected static void setCurrentInstance(AuditContext context) {
        if (context == null) {
            instance.remove();
        } else {
            instance.set(context);
        }
    }
}
