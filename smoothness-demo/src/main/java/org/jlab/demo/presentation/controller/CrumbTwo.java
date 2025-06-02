package org.jlab.demo.presentation.controller;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.smoothness.business.service.UserAuthorizationService;
import org.jlab.smoothness.persistence.view.User;
import org.jlab.smoothness.presentation.util.Paginator;
import org.jlab.smoothness.presentation.util.ParamUtil;

/**
 * @author ryans
 */
@WebServlet(
    name = "CrumbTwo",
    urlPatterns = {"/breadcrumbs/crumb-two"})
public class CrumbTwo extends HttpServlet {

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

    String search;
    int offset;
    int max;

    try {
      search = request.getParameter("search");
      offset = ParamUtil.convertAndValidateNonNegativeInt(request, "offset", 0);
      max = ParamUtil.convertAndValidateNonNegativeInt(request, "max", 5);
    } catch(Exception e) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    UserAuthorizationService auth = UserAuthorizationService.getInstance();

    List<User> userList = auth.getUsersLike(search, offset, max);
    long totalRecords = auth.countUsersLike(search);

    Paginator paginator = new Paginator(totalRecords, offset, max);

    DecimalFormat formatter = new DecimalFormat("###,###");

    String selectionMessage;

    if (paginator.getTotalRecords() == 0) {
      selectionMessage = "Found 0 Users";
    } else {
      selectionMessage =
          "Showing User "
              + formatter.format(paginator.getStartNumber())
              + " - "
              + formatter.format(paginator.getEndNumber())
              + " of "
              + formatter.format(paginator.getTotalRecords());
    }

    String filters = getMessage(search);

    if (filters.length() > 0) {
      selectionMessage = selectionMessage + " with " + filters;
    }

    request.setAttribute("selectionMessage", selectionMessage);
    request.setAttribute("userList", userList);
    request.setAttribute("paginator", paginator);

    request
        .getRequestDispatcher("/WEB-INF/views/breadcrumbs/crumb-two.jsp")
        .forward(request, response);
  }

  /**
   * Format selection message.
   *
   * @param search The search string
   * @return Formatted selection message
   */
  public static String getMessage(String search) {

    List<String> filters = new ArrayList<>();

    if (search != null && !search.isEmpty()) {
      filters.add("Name like \"" + search + "\"");
    }

    String message = "";

    if (!filters.isEmpty()) {
      message = filters.get(0);

      for (int i = 1; i < filters.size(); i++) {
        String filter = filters.get(i);
        message += " and " + filter;
      }
    }

    return message;
  }
}
