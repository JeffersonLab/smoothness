package org.jlab.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import javax.imageio.ImageIO;
import org.jlab.demo.Demo.Session;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * The weblib's /convert, which has Puppet Show render a page of the demo as a PDF or an image. The
 * export menu's Image item uses it (see SmoothnessJsIT).
 */
class ConvertIT {

  @BeforeAll
  static void awaitReady() throws Exception {
    Demo.awaitReady();
  }

  /** /convert's url: a path from the server's root, as jlab.getPrintUrl() gives */
  static String convert(String type, String filename, String path) {
    return "/convert?type="
        + type
        + "&filename="
        + filename
        + "&url="
        + URLEncoder.encode(URI.create(Demo.URL).getPath() + path, StandardCharsets.UTF_8);
  }

  @Test
  void pdfExportRendersThePage() throws Exception {
    HttpResponse<byte[]> response =
        Session.anonymous().getBytes(convert("pdf", "help.pdf", "/help"));

    assertEquals(200, response.statusCode());
    assertTrue(
        response.headers().firstValue("Content-Type").orElse("").startsWith("application/pdf"),
        response.headers().toString());
    assertEquals(
        "attachment; filename=\"help.pdf\"",
        response.headers().firstValue("Content-Disposition").orElse(null));

    String pdf = new String(response.body(), StandardCharsets.ISO_8859_1);
    assertTrue(pdf.startsWith("%PDF-"), "not a PDF: " + pdf.substring(0, 100));
    // Chromium keeps the page's links in the PDF: the login link returns to the page rendered
    assertTrue(
        pdf.contains("%2fsmoothness-demo%2fhelp)"), "no login link back to /help in the PDF");
  }

  @Test
  void imageExportRendersThePage() throws Exception {
    HttpResponse<byte[]> response =
        Session.anonymous().getBytes(convert("image", "help.png", "/help?print=Y"));

    assertEquals(200, response.statusCode());
    // Convert sends application/png, not the standard image/png
    assertTrue(
        response.headers().firstValue("Content-Type").orElse("").startsWith("application/png"),
        response.headers().toString());
    assertRendersAPage(response.body());
  }

  @Test
  void absoluteUrlIsRefused() throws Exception {
    HttpResponse<String> response =
        Session.anonymous()
            .get(
                "/convert?type=pdf&url="
                    + URLEncoder.encode("http://example.com/", StandardCharsets.UTF_8));

    assertEquals(400, response.statusCode());
  }

  /**
   * Checks that a PNG is a page of the demo, at least /convert's viewport width (a full-page image
   * is as wide as the page), and not an error page or a blank one: those are white, and the demo's
   * pages have a gray background.
   */
  static void assertRendersAPage(byte[] png) throws IOException {
    BufferedImage image = ImageIO.read(new ByteArrayInputStream(png));

    assertNotNull(image, "not an image");
    assertTrue(image.getWidth() >= 1024, "width " + image.getWidth());

    long white = 0;
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        if ((image.getRGB(x, y) & 0xFFFFFF) == 0xFFFFFF) {
          white++;
        }
      }
    }
    double whiteFraction = (double) white / (image.getWidth() * image.getHeight());
    assertTrue(whiteFraction < 0.5, "mostly white, as an error page: " + whiteFraction);
  }
}
