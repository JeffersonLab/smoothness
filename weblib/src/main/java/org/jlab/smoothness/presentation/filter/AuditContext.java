package org.jlab.smoothness.presentation.filter;

/**
 *
 * @author ryans
 */
public class AuditContext {

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
