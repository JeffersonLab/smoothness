package org.jlab.smoothness.presentation.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.smoothness.business.service.UserAuthorizationService;
import org.jlab.smoothness.persistence.view.User;

/**
 * Servlet controller for Clearing the user authorization cache.
 *
 * @author ryans
 */
@WebServlet(
    name = "DirectoryCache",
    urlPatterns = {"/setup/directory-cache"})
public class DirectoryCache extends HttpServlet {

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
    UserAuthorizationService auth = UserAuthorizationService.getInstance();

    Map<String, User> userCache = auth.getUserCache();
    Map<String, List<User>> roleCache = auth.getRoleCache();

    request.setAttribute("userCache", userCache);
    request.setAttribute("roleCache", roleCache);

    request.getRequestDispatcher("/WEB-INF/views/directory-cache.jsp").forward(request, response);
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

    String action = request.getParameter("action");

    UserAuthorizationService auth = UserAuthorizationService.getInstance();

    switch (action) {
      case "user":
        String username = request.getParameter("username");
        User user = auth.getUserFromUsername(username);
        break;
      case "role":
        String role = request.getParameter("role");
        auth.getUsersInRole(role);
        break;
      case "clear":
        auth.clearCache();
        break;
      default:
        throw new IllegalArgumentException("Unexpected action: " + action);
    }

    response.sendRedirect(request.getContextPath() + "/setup/directory-cache");
  }
}
