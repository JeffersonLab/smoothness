package org.jlab.demo.presentation.controller.ajax;

import jakarta.ejb.EJB;
import jakarta.ejb.EJBAccessException;
import jakarta.json.Json;
import jakarta.json.stream.JsonGenerator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.demo.business.service.MovieService;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.presentation.util.ParamConverter;

/**
 * @author ryans
 */
@WebServlet(
    name = "EditMovie",
    urlPatterns = {"/ajax/edit-movie"})
public class EditMovie extends HttpServlet {

  private static final Logger LOGGER = Logger.getLogger(EditMovie.class.getName());

  /** Movie Service */
  @EJB MovieService movieService;

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
    String errorReason = null;

    BigInteger id = null;
    String title = null;
    String description = null;
    String rating = null;
    Integer duration = null;
    Date release = null;

    try {
      id = ParamConverter.convertBigInteger(request, "id");
      title = request.getParameter("title");
      description = request.getParameter("description");
      rating = request.getParameter("rating");
      duration = ParamConverter.convertInteger(request, "duration");
      release = ParamConverter.convertFriendlyDate(request, "release");
    } catch (Exception e) {
      errorReason = "Bad Request";
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    if (errorReason == null) {
      try {
        movieService.editMovie(id, title, description, rating, duration, release);
      } catch (EJBAccessException e) {
        // LOGGER.log(Level.WARNING, "Not authorized", e);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        errorReason = "Not authorized";
      } catch (UserFriendlyException e) {
        // LOGGER.log(Level.WARNING, "Unable to edit movie", e);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        errorReason = e.getMessage();
      } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Unable to edit movie", e);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        errorReason = "Something unexpected happened";
      }
    }

    String stat = "ok";

    if (errorReason != null) {
      stat = "fail";
    }

    response.setContentType("application/json");

    OutputStream out = response.getOutputStream();

    try (JsonGenerator gen = Json.createGenerator(out)) {
      gen.writeStartObject().write("stat", stat); // This is unnecessary - if 200 OK then it worked
      if (errorReason != null) {
        gen.write("error", errorReason);
      }
      gen.writeEnd();
    }
  }
}
