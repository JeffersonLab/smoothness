package org.jlab.demo.presentation.controller.features;

import org.jlab.demo.business.service.MovieService;
import org.jlab.demo.persistence.entity.Movie;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author ryans
 */
@WebServlet(name = "MultiselectDatatable", urlPatterns = {"/features/multiselect-datatable"})
public class MultiselectDatatable extends HttpServlet {

    /**
     * Movie Service
     */
    @EJB
    MovieService movieService;

    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Movie> movieList = movieService.findAllDefaultOrder();

        request.setAttribute("movieList", movieList);

        request.getRequestDispatcher("/WEB-INF/views/features/multiselect-datatable.jsp").forward(request, response);
    }
}
