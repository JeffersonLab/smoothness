package org.jlab.smoothness.presentation.controller;

import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.business.service.EmailService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Forward feedback to interested email addresses.
 */
@WebServlet(name = "Feedback", urlPatterns = {"/feedback"})
public class Feedback extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(
            Feedback.class.getName());

    /**
     * The email service
     */
    private EmailService emailService;

    /**
     * Handles the HTTP
     * <code>POST</code> method.
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
            String subject = request.getParameter("subject");
            String body = request.getParameter("body");

            // Environment variable is used to override email address on test environments
            String toCsv = System.getenv("FEEDBACK_TO_ADDRESS_CSV");

            // Set in your web.xml file
            String sender = getServletContext().getInitParameter("feedbackFromAddress");

            String from = null;
            String username = request.getRemoteUser();

            if(username != null && username.contains(":")) {
                username = username.split(":")[2];

                from = username + "@jlab.org";
            }

            if(toCsv == null || toCsv.isEmpty()) {
                toCsv = getServletContext().getInitParameter("feedbackToAddressCsv");
            }

            if(sender == null || sender.isEmpty()) {
                sender = "wildfly@jlab.org";
            }

            if(from == null || from.isEmpty()) {
                from = sender;
            }

            if(toCsv == null || toCsv.isEmpty()) {
                throw new UserFriendlyException("No recipients defined!");
            }

            emailService = new EmailService();
            emailService.sendEmail(sender, from, toCsv, subject, body, false);
        } catch(UserFriendlyException e) {
            errorReason = e.getMessage();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unable to send email", e);
            errorReason = e.getMessage();
        }

        // Using actual XML with AJAX is so outdated.  Maybe we update to JSON like the cool kids?
        response.setContentType("text/xml");

        PrintWriter pw = response.getWriter();

        String xml;

        if (errorReason == null) {
            xml = "<response><span class=\"status\">Success</span></response>";
        } else {
            xml = "<response><span class=\"status\">Error</span><span "
                    + "class=\"reason\">" + errorReason + "</span></response>";
        }

        pw.write(xml);

        pw.flush();

        boolean error = pw.checkError();

        if (error) {
            LOGGER.log(Level.SEVERE, "PrintWriter Error");
        }
    }
}
