package org.jlab.demo.presentation.controller.ajax;

import org.jlab.demo.business.session.MovieFacade;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.presentation.util.ParamConverter;

import javax.ejb.EJB;
import javax.ejb.EJBAccessException;
import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ryans
 */
@WebServlet(name = "EditMovie", urlPatterns = {"/ajax/edit-movie"})
public class EditMovie extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(
            EditMovie.class.getName());

    @EJB
    MovieFacade movieFacade;

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String errorReason = null;

        try {
            BigInteger id = ParamConverter.convertBigInteger(request, "id");
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String rating = request.getParameter("rating");
            Integer duration = ParamConverter.convertInteger(request, "duration");
            Date release = ParamConverter.convertFriendlyDate(request, "release");

            movieFacade.editMovie(id, title, description, rating, duration, release);
        } catch (EJBAccessException e) {
            LOGGER.log(Level.WARNING, "Not authorized", e);
            errorReason = "Not authorized";
        } catch (UserFriendlyException e) {
            LOGGER.log(Level.WARNING, "Unable to edit movie", e);
            errorReason = e.getMessage();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unable to edit movie", e);
            errorReason = "Something unexpected happened";
        }

        String stat = "ok";

        if (errorReason != null) {
            stat = "fail";
        }

        response.setContentType("application/json");

        OutputStream out = response.getOutputStream();

        try (JsonGenerator gen = Json.createGenerator(out)) {
            gen.writeStartObject()
                    .write("stat", stat); // This is unnecessary - if 200 OK then it worked
            if (errorReason != null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                gen.write("error", errorReason);
            }
            gen.writeEnd();
        }
    }
}
