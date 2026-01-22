package org.jlab.smoothness.presentation.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * WebFilter for setting up an AuditContext.
 *
 * <p>Obtain the AuditContext from anywhere using: <code>
 * AuditContext context = AuditContext.getCurrentInstance();
 * </code>
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

    String ip = request.getRemoteAddr();

    String xForwardedFor = httpRequest.getHeader("X-Forwarded-For");

    if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
      String[] ipArray = xForwardedFor.split(",");
      ip = ipArray[0].trim(); // first one, if more than one
    }

    String username = httpRequest.getRemoteUser();

    if (username != null && username.contains(":")) {
      username = username.split(":")[2];
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
