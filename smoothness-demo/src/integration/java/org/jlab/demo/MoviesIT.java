package org.jlab.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.jlab.demo.Demo.Session;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/** The demo's editable movie table, through its ajax controllers and the database. */
class MoviesIT {

  static final String TABLE = "/features/multiselect-datatable";

  @BeforeAll
  static void awaitReady() throws Exception {
    Demo.awaitReady();
  }

  /** The table row of the movie with the title, or null. */
  static Matcher row(Session session, String title) throws Exception {
    HttpResponse<String> page = session.get(TABLE);
    assertEquals(200, page.statusCode());

    Matcher m =
        Pattern.compile(
                "<tr data-id=\"(\\d+)\">\\s*<td>"
                    + Pattern.quote(title)
                    + "</td>\\s*<td>(.*?)</td>\\s*<td>(.*?)</td>\\s*<td>(.*?)</td>\\s*<td>(.*?)</td>",
                Pattern.DOTALL)
            .matcher(page.body());
    return m.find() ? m : null;
  }

  @Test
  void addEditAndRemoveAMovie() throws Exception {
    Session session = Session.loggedIn(Demo.USER);
    String title = "IT movie " + UUID.randomUUID();

    HttpResponse<String> added =
        session.post(
            "/ajax/add-movie",
            Map.of(
                "title", title,
                "description", "Added by MoviesIT",
                "rating", "PG",
                "duration", "95",
                "release", "10-Mar-2026"));
    assertEquals(200, added.statusCode(), added.body());
    assertEquals("{\"stat\":\"ok\"}", added.body());

    Matcher row = row(session, title);
    assertTrue(row != null, "added movie not listed");
    String id = row.group(1);
    assertEquals("Added by MoviesIT", row.group(2));
    assertEquals("PG", row.group(3));
    assertEquals("95", row.group(4));
    assertEquals("10-Mar-2026", row.group(5).trim());

    HttpResponse<String> edited =
        session.post(
            "/ajax/edit-movie",
            Map.of(
                "id", id,
                "title", title,
                "description", "Edited",
                "rating", "R",
                "duration", "100",
                "release", "11-Mar-2026"));
    assertEquals("{\"stat\":\"ok\"}", edited.body());
    row = row(session, title);
    assertEquals("Edited", row.group(2));
    assertEquals("R", row.group(3));
    assertEquals("100", row.group(4));
    assertEquals("11-Mar-2026", row.group(5).trim());

    HttpResponse<String> rated =
        session.post("/ajax/edit-movie-rating", Map.of("id[]", id, "rating", "G"));
    assertEquals("{\"stat\":\"ok\"}", rated.body());
    assertEquals("G", row(session, title).group(3));

    HttpResponse<String> removed = session.post("/ajax/remove-movie", Map.of("id[]", id));
    assertEquals("{\"stat\":\"ok\"}", removed.body());
    assertEquals(null, row(session, title), "removed movie still listed");
  }

  @Test
  void excelExportAllowsAnEmptyDurationAndKeepsKnownDurationsNumeric() throws Exception {
    Session session = Session.loggedIn(Demo.USER);
    String title = "IT Excel movie " + UUID.randomUUID();
    HttpResponse<String> added =
        session.post("/ajax/add-movie", Map.of("title", title, "duration", ""));
    assertEquals(200, added.statusCode(), added.body());
    assertEquals("{\"stat\":\"ok\"}", added.body());

    Matcher movie = row(session, title);
    assertTrue(movie != null, "added movie not listed");
    String id = movie.group(1);
    try {
      assertExportedDuration(session, title, CellType.BLANK, 0);

      HttpResponse<String> edited =
          session.post("/ajax/edit-movie", Map.of("id", id, "title", title, "duration", "95"));
      assertEquals(200, edited.statusCode(), edited.body());
      assertEquals("{\"stat\":\"ok\"}", edited.body());
      assertExportedDuration(session, title, CellType.NUMERIC, 95);
    } finally {
      HttpResponse<String> removed = session.post("/ajax/remove-movie", Map.of("id[]", id));
      assertEquals(200, removed.statusCode(), removed.body());
      assertEquals("{\"stat\":\"ok\"}", removed.body());
    }
  }

  private static void assertExportedDuration(
      Session session, String title, CellType expectedType, double expectedDuration)
      throws Exception {
    HttpResponse<byte[]> response = session.getBytes("/features/export.xlsx");
    assertEquals(200, response.statusCode());
    assertEquals(
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        response.headers().firstValue("Content-Type").orElse("").split(";", 2)[0].trim());
    assertEquals(
        "attachment;filename=\"export.xlsx\"",
        response.headers().firstValue("Content-Disposition").orElse(null));

    try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(response.body()))) {
      for (Row row : workbook.getSheetAt(0)) {
        if (title.equals(row.getCell(3).getStringCellValue())) {
          assertEquals(expectedType, row.getCell(5).getCellType());
          if (expectedType == CellType.NUMERIC) {
            assertEquals(expectedDuration, row.getCell(5).getNumericCellValue());
          }
          return;
        }
      }
      fail("movie missing from Excel export: " + title);
    }
  }

  @Test
  void addWithoutTitleFails() throws Exception {
    HttpResponse<String> response =
        Session.loggedIn(Demo.USER).post("/ajax/add-movie", Map.of("title", "", "rating", "PG"));

    assertEquals(400, response.statusCode());
    assertEquals("{\"stat\":\"fail\",\"error\":\"title must not be empty\"}", response.body());
  }

  @Test
  void removeWithoutSelectionFails() throws Exception {
    HttpResponse<String> response =
        Session.loggedIn(Demo.USER).post("/ajax/remove-movie", Map.of());

    assertEquals(400, response.statusCode());
    assertEquals(
        "{\"stat\":\"fail\",\"error\":\"Please select at least one movie to remove\"}",
        response.body());
  }
}
