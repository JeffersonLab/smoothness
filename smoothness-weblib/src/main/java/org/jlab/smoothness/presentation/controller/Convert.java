package org.jlab.smoothness.presentation.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

/**
 * Servlet controller for proxying requests to Puppet Show to allow HTML conversion even for
 * external / public users.
 *
 * <p>This controller only allows relative URLs (relative to hosting server).
 *
 * @author ryans
 */
@WebServlet(
    name = "Convert",
    urlPatterns = {"/convert"})
public class Convert extends HttpServlet {

  private static final Logger LOGGER = Logger.getLogger(Convert.class.getName());

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

    String type = request.getParameter("type");
    String filename = request.getParameter("filename");
    String urlString = request.getParameter("url");
    String waitForSelector = request.getParameter("waitForSelector");

    String puppetServer = System.getenv("PUPPET_SHOW_SERVER_URL");
    String internalServer = System.getenv("BACKEND_SERVER_URL");

    if (puppetServer == null) {
      puppetServer = "http://localhost";
    }

    if (internalServer == null) {
      internalServer = "http://localhost";
    }

    if (urlString == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "url parameter must not be empty");
      return;
    }

    if (urlString.indexOf("://") > 0 || urlString.indexOf("//") == 0) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "url parameter must not be absolute");
      return;
    }

    if (!urlString.startsWith("/")) {
      urlString = "/" + urlString;
    }

    internalServer = internalServer + urlString;

    internalServer = URLEncoder.encode(internalServer, StandardCharsets.UTF_8);

    if (waitForSelector == null) {
      waitForSelector = "";
    } else {
      waitForSelector = URLEncoder.encode(waitForSelector, StandardCharsets.UTF_8);
    }

    if ("pdf".equals(type)) {
      response.setHeader("content-type", "application/pdf");
      puppetServer = puppetServer + "/puppet-show/pdf";

    } else {
      response.setHeader("content-type", "application/png");
      puppetServer = puppetServer + "/puppet-show/screenshot";
    }

    puppetServer =
        puppetServer
            + "?ignoreHTTPSErrors=true&fullPage=true&omitBackground=false&viewportWidth=1024&viewportHeight=768&waitForSelector="
            + waitForSelector
            + "&url="
            + internalServer;

    if (filename != null && !filename.isEmpty()) {
      filename = removeSpecial(filename);

      response.setHeader("content-disposition", "attachment; filename=\"" + filename + "\"");
    }

    // LOGGER.log(Level.INFO, "Puppet URL Request: " + puppetServer);

    URL url = new URL(puppetServer);

    URLConnection con = url.openConnection();

    /*HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

    con.setHostnameVerifier(IOUtil.getTrustyHostnameVerifier());

    try {
        con.setSSLSocketFactory(IOUtil.getTrustySSLSocketFactory());
    } catch (NoSuchAlgorithmException | KeyManagementException e) {
        throw new ServletException("Unable to disable SSL Certificate Verification", e);
    }*/

    InputStream in = con.getInputStream();
    OutputStream out = response.getOutputStream();

    in.transferTo(out);

    in.close();
  }

  private static String removeSpecial(String str) {
    return str.replaceAll("[^a-zA-Z.]", "");
  }
}
