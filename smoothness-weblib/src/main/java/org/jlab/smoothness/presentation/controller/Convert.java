package org.jlab.smoothness.presentation.controller;

import org.jlab.smoothness.business.util.IOUtil;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * Servlet controller for proxying requests to Puppet Show to allow HTML conversion even for external / public users.
 *
 * This controller only allows relative URLs (relative to hosting server).
 *
 * @author ryans
 */
@WebServlet(name = "Convert", urlPatterns = {"/convert"})
public class Convert extends HttpServlet {

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

        String type = request.getParameter("type");
        String filename = request.getParameter("filename");
        String urlString = request.getParameter("url");
        String waitForSelector = request.getParameter("waitForSelector");

        String server = System.getenv("PUPPET_SHOW_SERVER");

        String path = "https://" + server + "/puppet-show/";

        if (urlString == null) {
            throw new ServletException("url parameter must not be empty");
        }

        if (urlString.indexOf("://") > 0 || urlString.indexOf("//") == 0) {
            throw new ServletException("url parameter must not be absolute");
        }

        urlString = "https://" + server + urlString;

        urlString = URLEncoder.encode(urlString, StandardCharsets.UTF_8);

        if(waitForSelector == null) {
            waitForSelector = "";
        } else {
            waitForSelector = URLEncoder.encode(waitForSelector, StandardCharsets.UTF_8);
        }

        if ("pdf".equals(type)) {
            response.setHeader("content-type", "application/pdf");
            path = path + "pdf";

        } else {
            response.setHeader("content-type", "application/png");
            path = path + "screenshot";
        }

        path = path + "?ignoreHTTPSErrors=true&fullPage=true&omitBackground=false&viewportWidth=1024&viewportHeight=768&waitForSelector=" + waitForSelector + "&url=" + urlString;

        if (filename != null && !filename.isEmpty()) {
            response.setHeader("content-disposition", "attachment; filename=\"" + filename + "\"");
        }

        URL url = new URL(path);

        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

        con.setHostnameVerifier(IOUtil.getTrustyHostnameVerifier());

        try {
            con.setSSLSocketFactory(IOUtil.getTrustySSLSocketFactory());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new ServletException("Unable to disable SSL Certificate Verification", e);
        }

        InputStream in = con.getInputStream();
        OutputStream out = response.getOutputStream();

        in.transferTo(out);

        in.close();
    }
}
