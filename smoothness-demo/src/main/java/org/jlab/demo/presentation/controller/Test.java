package org.jlab.demo.presentation.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.jlab.smoothness.presentation.filter.CacheFilter;

/**
 * @author ryans
 */
@WebServlet(
    name = "test",
    urlPatterns = {"/test"})
public class Test extends HttpServlet {

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

    // Let's test forcibly caching response!
    CacheFilter.CacheControlResponse cachableResponse = (CacheFilter.CacheControlResponse) response;
    cachableResponse.setContentType("application/json", CacheFilter.CachableResponse.MAX);

    response.getWriter().println("{\"name\":\"test\"}");
  }
}
