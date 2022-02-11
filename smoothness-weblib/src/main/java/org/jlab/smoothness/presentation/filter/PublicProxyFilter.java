package org.jlab.smoothness.presentation.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * WebFilter for automatically adjusting the CDN Context Path.
 *
 * If an application is hosted internally, but also is proxied externally as well
 * (two paths to access), then the external proxy can set the X-Public-Proxy header
 * and this filter will adjust the CDN Context Path to an externally accessible location (relative to
 * hosting server as if using LOCAL RESOURCE_LOCATION).
 *
 * @author ryans
 */
@WebFilter(filterName = "PublicProxyFilter", urlPatterns = {"/*"}, dispatcherTypes = {
    DispatcherType.REQUEST, DispatcherType.FORWARD})
public class PublicProxyFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(
            PublicProxyFilter.class.getName());

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp,
            FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        boolean publicProxy = request.getHeader("X-Public-Proxy") != null || request.getParameter("public-proxy")
                != null;

        // You don't have a choice if proxy server sets this

        request.setAttribute("publicProxy", publicProxy);

        if (publicProxy) {
            request.setAttribute("cdnContextPath", "");
        } else {
            ServletContext context = request.getServletContext();
            request.setAttribute("cdnContextPath", "//" + System.getenv("CDN_SERVER"));
        }

        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
