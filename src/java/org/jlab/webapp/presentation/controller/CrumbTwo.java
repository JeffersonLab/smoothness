package org.jlab.webapp.presentation.controller;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.webapp.business.session.StaffFacade;
import org.jlab.webapp.persistence.entity.Staff;
import org.jlab.webapp.presentation.util.FilterSelectionMessage;
import org.jlab.webapp.presentation.util.Paginator;
import org.jlab.webapp.presentation.util.ParamConverter;

/**
 *
 * @author ryans
 */
@WebServlet(name = "CrumbTwo", urlPatterns = {"/breadcrumbs/crumb-two"})
public class CrumbTwo extends HttpServlet {

    @EJB
    StaffFacade staffFacade;

    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String lastname = request.getParameter("lastname");

        int offset = ParamConverter.convertNonNegativeInt(request, "offset", 0);
        int max = 10;

        List<Staff> staffList = staffFacade.filterList(lastname, offset, max);
        long totalRecords = staffFacade.countList(lastname, offset, max);

        Paginator paginator = new Paginator(totalRecords, offset, max);

        DecimalFormat formatter = new DecimalFormat("###,###");

        String selectionMessage;

        if (paginator.getTotalRecords() == 0) {
            selectionMessage = "Found 0 Staff";
        } else {
            selectionMessage = "Showing Staff " + formatter.format(paginator.getStartNumber())
                    + " - " + formatter.format(paginator.getEndNumber())
                    + " of " + formatter.format(paginator.getTotalRecords());
        }

        String filters = FilterSelectionMessage.getMessage(lastname);

        if (filters.length() > 0) {
            selectionMessage = selectionMessage + " with " + filters;
        }

        request.setAttribute("selectionMessage", selectionMessage);
        request.setAttribute("staffList", staffList);
        request.setAttribute("paginator", paginator);

        request.getRequestDispatcher("/WEB-INF/views/breadcrumbs/crumb-two.jsp").forward(request, response);
    }
}
