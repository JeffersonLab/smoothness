package org.jlab.smoothness.presentation.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet controller for Single Sign-On (SSO).
 *
 * <p>When users click the login link it directs them here such that the auth constraints in the
 * web.xml for this path trigger a login. The actual login is transferred to Keycloak, and upon
 * return this servlet forwards to the originally provided returnUrl.
 *
 * <p>This allows us to have both an authenticated and unauthenticated view of pages. If we were to
 * simply apply auth constraints to all pages then we could never have an unauthenticated view.
 *
 * @author ryans
 */
@WebServlet(
    name = "SSORedirect",
    urlPatterns = {"/sso"})
public class SSORedirect extends HttpServlet {

  /**
   * Handles the HTTP <code>GET</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String returnUrl = request.getParameter("returnUrl");
    if (returnUrl == null || returnUrl.isEmpty()) {
      returnUrl = request.getContextPath();
    }

    response.sendRedirect(response.encodeRedirectURL(returnUrl));
  }
}
