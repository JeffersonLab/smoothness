package org.jlab.smoothness.presentation.controller;

import java.io.IOException;
import javax.ejb.EJBAccessException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet controller for request errors.
 *
 * @author ryans
 */
@WebServlet(
    name = "Error",
    urlPatterns = {"/error"})
public class Error extends HttpServlet {

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
    Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
    Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
    String servletName = (String) request.getAttribute("javax.servlet.error.servlet_name");
    String requestUri = (String) request.getAttribute("javax.servlet.error.request_uri");

    String message = "An unknown error has occurred.";

    if (400 == statusCode) {
      message = "400: Bad Request";
    } else if (401 == statusCode) {
      message = "401: Unauthorized";
    } else if (404 == statusCode) {
      message = "404: Not Found";
    } else if (throwable != null) {
      System.err.println("Throwable Class: " + throwable.getClass());

      if (throwable instanceof EJBAccessException) {
        message = "You are not authorized to perform the requested action.";
      } else {
        message = throwable.getMessage();
      }
    }

    request.setAttribute("message", message);
    request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
  }

  /**
   * Handles the HTTP <code>POST</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doGet(request, response);
  }
}
