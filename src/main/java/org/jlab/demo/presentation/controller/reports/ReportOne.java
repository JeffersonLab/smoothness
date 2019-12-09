package org.jlab.demo.presentation.controller.reports;

import org.jlab.demo.business.params.ReportOneParams;
import org.jlab.demo.presentation.params.ReportOneParamHandler;
import org.jlab.smoothness.business.util.TimeUtil;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author ryans
 */
@WebServlet(name = "ReportOne", urlPatterns = {"/reports/report-one"})
public class ReportOne extends HttpServlet {

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

        ReportOneParamHandler paramHandler = new ReportOneParamHandler(request);

        ReportOneParams params;

        if (paramHandler.qualified()) {
            params = paramHandler.convert();
            paramHandler.validate(params);
            paramHandler.store(params);
        } else {
            params = paramHandler.materialize();
            paramHandler.redirect(response, params);
            return;
        }

        String range = TimeUtil.encodeRange(params.getStart(), params.getEnd(), true, null, null);

        request.setAttribute("message", paramHandler.message(params));
        request.setAttribute("range", range);

        request.getRequestDispatcher("/WEB-INF/views/reports/report-one.jsp").forward(request, response);
    }
}
