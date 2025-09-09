package org.jlab.smoothness.presentation.controller.setup.ajax;

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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.business.service.SettingsService;

/**
 * @author ryans
 */
@WebServlet(
    name = "EditSetting",
    urlPatterns = {"/setup/ajax/edit-setting"})
public class EditSetting extends HttpServlet {

  private static final Logger logger = Logger.getLogger(EditSetting.class.getName());

  /** The SettingService. */
  @EJB SettingsService settingService;

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String stat = "ok";
    String error = null;

    try {
      String key = request.getParameter("key");
      String value = request.getParameter("value");

      settingService.editSetting(key, value, request.getServletContext());
    } catch (UserFriendlyException e) {
      stat = "fail";
      error = "Unable to edit Setting: " + e.getMessage();
    } catch (EJBAccessException e) {
      stat = "fail";
      error = "Unable to edit Setting: Not authenticated / authorized (do you need to re-login?)";
    } catch (RuntimeException e) {
      stat = "fail";
      error = "Unable to edit Setting";
      logger.log(Level.SEVERE, "Unable to edit Setting", e);
    }

    response.setContentType("application/json");

    OutputStream out = response.getOutputStream();

    try (JsonGenerator gen = Json.createGenerator(out)) {
      gen.writeStartObject().write("stat", stat);
      if (error != null) {
        gen.write("error", error);
      }
      gen.writeEnd();
    }
  }
}
