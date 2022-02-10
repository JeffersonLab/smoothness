package org.jlab.smoothness.presentation.filter;

import java.util.HashMap;
import java.util.Map;

/**
 * Leverages ThreadLocal to share an audit key-value data store across layers of an application for a given
 * HTTP request.
 *
 * Username and request IP address are stored explicitly (outside the generic key-value store) as they are critical
 * audit data.
 *
 * @author ryans
 */
public class AuditContext {

    private final Map<String, Object> extra = new HashMap<>();
    private String username;
    private String ip;

    /**
     * Get the request IP address.
     *
     * @return The IP address
     */
    public String getIp() {
        return ip;
    }

    /**
     * Set the request IP address.
     *
     * @param ip The IP address
     */
    protected void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * Get the username.
     *
     * @return The username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Get an extra audit value by key.
     *
     * @param key The key
     * @return The extra audit value
     */
    public Object getExtra(String key) {
        return extra.get(key);
    }

    /**
     * Put an extra audit value in the context with a given key.
     *
     * @param key The key
     * @param value The value
     * @return The previous value if replacing, otherwise null
     */
    public Object putExtra(String key, Object value) {
        return extra.put(key, value);
    }

    /**
     * Set the username associated with the request thread.
     *
     * @param username The username
     */
    protected void setUsername(String username) {
        this.username = username;
    }

    private static final ThreadLocal<AuditContext> instance = new ThreadLocal<AuditContext>() {

        @Override
        protected AuditContext initialValue() {
            return null;
        }
    };

    /**
     * Return the ThreadLocal AuditContext.
     *
     * @return The AuditContext
     */
    public static AuditContext getCurrentInstance() {
        return instance.get();
    }

    /**
     * Set the current AuditContext.
     *
     * @param context The AuditContext
     */
    protected static void setCurrentInstance(AuditContext context) {
        if (context == null) {
            instance.remove();
        } else {
            instance.set(context);
        }
    }
}
