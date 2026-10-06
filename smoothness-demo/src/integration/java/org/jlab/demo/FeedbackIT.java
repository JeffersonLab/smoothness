package org.jlab.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jlab.demo.Demo.Session;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * The weblib's Help page feedback form, which emails the app's admins: MailHog catches the email,
 * and the admins' addresses come from Keycloak's user directory.
 */
class FeedbackIT {

  @BeforeAll
  static void awaitReady() throws Exception {
    Demo.awaitReady();
  }

  @Test
  void feedbackEmailsTheAdmins() throws Exception {
    String subject = "IT feedback " + UUID.randomUUID();

    HttpResponse<String> response =
        Session.loggedIn(Demo.USER)
            .post("/feedback", Map.of("subject", subject, "body", "Sent by FeedbackIT"));

    assertEquals(200, response.statusCode(), response.body());
    assertTrue(response.body().contains("Success"), response.body());

    // MailHog's API: messages containing the subject
    String search =
        Session.anonymous()
            .get(
                Demo.MAILHOG_URL
                    + "/api/v2/search?kind=containing&query="
                    + URLEncoder.encode(subject, StandardCharsets.UTF_8))
            .body();
    Matcher total = Pattern.compile("\"total\":(\\d+)").matcher(search);

    assertTrue(total.find(), search);
    assertEquals("1", total.group(1), "emails with the subject");
    assertTrue(search.contains("jdoe@example.com"), "not sent to jdoe: " + search);
    assertTrue(search.contains("tbrown@example.com"), "not sent to tbrown: " + search);
  }
}
