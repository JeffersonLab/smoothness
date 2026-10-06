package org.jlab.demo.presentation.controller.features;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** A loose page that leaves out the smoothness styles and scripts. */
@WebServlet(
    name = "LoosePageWithoutSmoothness",
    urlPatterns = {"/features/loose-page-without-smoothness"})
public class LoosePageWithoutSmoothness extends HttpServlet {

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
    request
        .getRequestDispatcher("/WEB-INF/views/features/loose-page-without-smoothness.jsp")
        .forward(request, response);
  }
}
