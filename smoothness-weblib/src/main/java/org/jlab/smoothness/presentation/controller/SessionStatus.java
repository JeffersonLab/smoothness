package org.jlab.smoothness.presentation.controller;

import jakarta.json.Json;
import jakarta.json.stream.JsonGenerator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet controller that says who is logged in, as JSON: <code>{"user": "jdoe"}</code>, or <code>
 * {"user": null}</code> when no one is.
 *
 * <p>smoothness.js asks when a request fails, and when the user returns to a page that has been
 * open a while, to tell whether the user the page was loaded for has been logged out: their session
 * expired, or they logged out of another app that uses the same single sign-on. It isn't behind a
 * security constraint, so asking never redirects to the login page.
 *
 * <p>Each request counts as activity in the user's session, so the page asks only on those events,
 * never on a timer, which would keep the session from expiring.
 */
@WebServlet(
    name = "SessionStatus",
    urlPatterns = {"/session-status"})
public class SessionStatus extends HttpServlet {

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
    String user = request.getRemoteUser();

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.setHeader("Cache-Control", "no-store");

    try (JsonGenerator gen = Json.createGenerator(response.getOutputStream())) {
      gen.writeStartObject();
      if (user == null) {
        gen.writeNull("user");
      } else {
        gen.write("user", user);
      }
      gen.writeEnd();
    }
  }
}
