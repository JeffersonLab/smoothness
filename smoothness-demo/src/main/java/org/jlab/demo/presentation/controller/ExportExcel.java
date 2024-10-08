package org.jlab.demo.presentation.controller;

import java.io.IOException;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.demo.business.service.ExcelExporter;
import org.jlab.demo.business.service.MovieService;
import org.jlab.demo.persistence.entity.Movie;

/**
 * @author ryans
 */
@WebServlet(
    name = "ExportExcel",
    urlPatterns = {"/features/export.xlsx"})
public class ExportExcel extends HttpServlet {

  @EJB MovieService movieService;

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

    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    response.setHeader("content-disposition", "attachment;filename=\"export.xlsx\"");

    List<Movie> movieList = movieService.findAllDefaultOrder();

    ExcelExporter exporter = new ExcelExporter();
    exporter.export(response.getOutputStream(), movieList);
  }
}
