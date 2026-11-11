package org.jlab.smoothness.presentation.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet controller for the page that ends logging in again from a new tab.
 *
 * <p>When a page finds its user has been logged out, smoothness.js offers to log in again in a new
 * tab, so that what the user entered on the page isn't lost. That tab logs in through {@link
 * SSORedirect} and returns here: this page tells the original page the user is logged in again and
 * closes itself, or says the user can close it.
 */
@WebServlet(
    name = "LoggedIn",
    urlPatterns = {"/logged-in"})
public class LoggedIn extends HttpServlet {

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
    request.getRequestDispatcher("/WEB-INF/views/logged-in.jsp").forward(request, response);
  }
}
