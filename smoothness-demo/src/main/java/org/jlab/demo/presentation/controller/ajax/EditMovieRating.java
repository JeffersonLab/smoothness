package org.jlab.demo.presentation.controller.ajax;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBAccessException;
import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.demo.business.service.MovieService;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.presentation.util.ParamConverter;

/**
 * @author ryans
 */
@WebServlet(
    name = "EditMovieRating",
    urlPatterns = {"/ajax/edit-movie-rating"})
public class EditMovieRating extends HttpServlet {

  private static final Logger LOGGER = Logger.getLogger(EditMovieRating.class.getName());

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

    try {
      BigInteger[] idArray = ParamConverter.convertBigIntegerArray(request, "id[]");
      String rating = request.getParameter("rating");

      movieService.editMovieRating(idArray, rating);
    } catch (EJBAccessException e) {
      // LOGGER.log(Level.WARNING, "Not authorized", e);
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      errorReason = "Not authorized";
    } catch (UserFriendlyException e) {
      // LOGGER.log(Level.WARNING, "Unable to edit movie rating", e);
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      errorReason = e.getMessage();
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Unable to edit movie rating", e);
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      errorReason = "Something unexpected happened";
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
