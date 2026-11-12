package org.jlab.smoothness.presentation.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.jlab.smoothness.presentation.util.ClientAddress;

/**
 * WebFilter for setting up an AuditContext.
 *
 * <p>Obtain the AuditContext from anywhere using: <code>
 * AuditContext context = AuditContext.getCurrentInstance();
 * </code>
 *
 * <p>The context's IP address is the client's as {@link ClientAddress} works it out, which behind a
 * proxy depends on the TRUSTED_PROXY_COUNT environment variable.
 *
 * @author ryans
 */
@WebFilter(
    filterName = "AuditFilter",
    urlPatterns = {"/*"},
    dispatcherTypes = {DispatcherType.REQUEST, DispatcherType.FORWARD},
    asyncSupported = true)
public class AuditFilter implements Filter {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) request;

    AuditContext context = new AuditContext();

    String ip = ClientAddress.of(httpRequest);

    String username = httpRequest.getRemoteUser();

    // Keycloak's IDs for federated users, such as from LDAP, are f:<provider id>:<username>
    if (username != null) {
      String[] parts = username.split(":");
      if (parts.length >= 3) {
        username = parts[2];
      }
    }

    context.setIp(ip);
    context.setUsername(username);

    AuditContext.setCurrentInstance(context);

    try {
      chain.doFilter(request, response);
    } finally {
      AuditContext.setCurrentInstance(null);
    }
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {}

  @Override
  public void destroy() {}
}
