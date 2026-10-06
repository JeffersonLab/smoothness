package org.jlab.smoothness.presentation.util;

import static org.jlab.smoothness.presentation.util.FakeRequest.withParams;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

/**
 * Characterization tests. ParamConverter returns null for a missing or empty parameter; the
 * ISO-8601 methods use America/New_York, and the friendly ones the JVM's default time zone (the
 * build sets America/New_York).
 */
class ParamConverterTest {

  static Date newYork(String dateTime) {
    return Date.from(
        LocalDateTime.parse(dateTime.replace(' ', 'T'))
            .atZone(ZoneId.of("America/New_York"))
            .toInstant());
  }

  final HttpServletRequest empty = withParams("p", "");
  final HttpServletRequest missing = withParams();

  @Test
  void missingAndEmptyParametersAreNull() throws UserFriendlyException {
    for (HttpServletRequest request : new HttpServletRequest[] {empty, missing}) {
      assertNull(ParamConverter.convertCharacter(request, "p"));
      assertNull(ParamConverter.convertFloat(request, "p"));
      assertNull(ParamConverter.convertInteger(request, "p"));
      assertNull(ParamConverter.convertYNBoolean(request, "p"));
      assertNull(ParamConverter.convertISO8601Date(request, "p"));
      assertNull(ParamConverter.convertISO8601DateTime(request, "p"));
      assertNull(ParamConverter.convertBigInteger(request, "p"));
      assertNull(ParamConverter.convertFriendlyDate(request, "p"));
      assertNull(ParamConverter.convertFriendlyDateTime(request, "p"));
    }
  }

  @Test
  void convertCharacter() throws UserFriendlyException {
    assertEquals('x', ParamConverter.convertCharacter(withParams("p", "x"), "p"));
    assertThrows(
        UserFriendlyException.class,
        () -> ParamConverter.convertCharacter(withParams("p", "xy"), "p"));
  }

  @Test
  void convertNumbers() {
    assertEquals(42, ParamConverter.convertInteger(withParams("p", "42"), "p"));
    assertEquals(-1.5f, ParamConverter.convertFloat(withParams("p", "-1.5"), "p"));
    assertEquals(
        new BigInteger("12345678901234567890"),
        ParamConverter.convertBigInteger(withParams("p", "12345678901234567890"), "p"));
  }

  @Test
  void numbersThatArentNumbersNameTheParameter() {
    assertNotANumber(
        "p must be a whole number", () -> ParamConverter.convertInteger(withParams("p", "x"), "p"));
    assertNotANumber(
        "p must be a whole number",
        () -> ParamConverter.convertInteger(withParams("p", " 1"), "p"));
    assertNotANumber(
        "p must be a whole number",
        () -> ParamConverter.convertInteger(withParams("p", "1.5"), "p"));
    assertNotANumber(
        "p must be a number", () -> ParamConverter.convertFloat(withParams("p", "x"), "p"));
    assertNotANumber(
        "p must be a whole number",
        () -> ParamConverter.convertBigInteger(withParams("p", "1.0"), "p"));
    assertNotANumber(
        "id[] must be a whole number",
        () -> ParamConverter.convertLongArray(withParams("id[]", "1", "id[]", "x"), "id[]"));
    assertNotANumber(
        "id[] must be a whole number",
        () -> ParamConverter.convertShortArray(withParams("id[]", "70000"), "id[]", (short) 0));
    assertNotANumber(
        "id[] must be a number",
        () -> ParamConverter.convertFloatArray(withParams("id[]", "x"), "id[]"));
    assertNotANumber(
        "id[] must be a whole number",
        () -> ParamConverter.convertBigIntegerArray(withParams("id[]", "x"), "id[]"));
  }

  /** Still a NumberFormatException, so callers' catch blocks work, with the parser's as cause. */
  static void assertNotANumber(String message, Executable executable) {
    NumberFormatException e = assertThrows(NumberFormatException.class, executable);
    assertEquals(message, e.getMessage());
    assertInstanceOf(NumberFormatException.class, e.getCause());
  }

  @Test
  void convertYnBooleanAcceptsOnlyUppercaseYAndN() throws UserFriendlyException {
    assertTrue(ParamConverter.convertYNBoolean(withParams("p", "Y"), "p"));
    assertFalse(ParamConverter.convertYNBoolean(withParams("p", "N"), "p"));

    UserFriendlyException e =
        assertThrows(
            UserFriendlyException.class,
            () -> ParamConverter.convertYNBoolean(withParams("p", "y"), "p"));
    assertEquals("Value must be one of 'Y' or 'N'", e.getMessage());
    assertThrows(
        UserFriendlyException.class,
        () -> ParamConverter.convertYNBoolean(withParams("p", "n"), "p"));
  }

  @Test
  void convertIso8601DateIsMidnightInNewYork() throws UserFriendlyException {
    assertEquals(
        newYork("2026-03-10 00:00"),
        ParamConverter.convertISO8601Date(withParams("p", "2026-03-10"), "p"));
    assertThrows(
        UserFriendlyException.class,
        () -> ParamConverter.convertISO8601Date(withParams("p", "2026-3-10"), "p"));
  }

  @Test
  void convertIso8601DatesAreStrict() throws UserFriendlyException {
    assertEquals(
        newYork("2028-02-29 00:00"),
        ParamConverter.convertISO8601Date(withParams("p", "2028-02-29"), "p"));
    for (String date : new String[] {"2026-02-30", "2026-02-29", "2026-04-31", "2026-13-01"}) {
      assertThrows(
          UserFriendlyException.class,
          () -> ParamConverter.convertISO8601Date(withParams("p", date), "p"),
          date);
    }
    assertThrows(
        UserFriendlyException.class,
        () -> ParamConverter.convertISO8601DateTime(withParams("p", "2026-03-10 24:00"), "p"));
  }

  @Test
  void convertIso8601DateTime() throws UserFriendlyException {
    assertEquals(
        newYork("2026-03-10 07:30"),
        ParamConverter.convertISO8601DateTime(withParams("p", "2026-03-10 07:30"), "p"));
    assertThrows(
        UserFriendlyException.class,
        () -> ParamConverter.convertISO8601DateTime(withParams("p", "2026-03-10T07:30"), "p"));
  }

  @Test
  void convertFriendlyDates() throws UserFriendlyException {
    assertEquals(
        newYork("2026-03-10 00:00"),
        ParamConverter.convertFriendlyDate(withParams("p", "10-Mar-2026"), "p"));
    assertEquals(
        newYork("2026-03-10 07:30"),
        ParamConverter.convertFriendlyDateTime(withParams("p", "10-Mar-2026 07:30"), "p"));
    assertThrows(
        UserFriendlyException.class,
        () -> ParamConverter.convertFriendlyDate(withParams("p", "2026-03-10"), "p"));
  }

  @Test
  void convertFriendlyDatesAreStrict() throws UserFriendlyException {
    for (String date :
        new String[] {"32-Jan-2026", "29-Feb-2026", "10-Mar-2026 07:30", "10-Mar-2026x"}) {
      assertThrows(
          UserFriendlyException.class,
          () -> ParamConverter.convertFriendlyDate(withParams("p", date), "p"),
          date);
    }
    for (String dateTime :
        new String[] {
          "10-Mar-2026 25:00", "10-Mar-2026 07:60", "10-Mar-2026 07:30x", "10-Mar-2026"
        }) {
      assertThrows(
          UserFriendlyException.class,
          () -> ParamConverter.convertFriendlyDateTime(withParams("p", dateTime), "p"),
          dateTime);
    }
  }

  @Test
  void convertFriendlyDatesIgnoreSurroundingWhitespace() throws UserFriendlyException {
    assertEquals(
        newYork("2026-03-10 00:00"),
        ParamConverter.convertFriendlyDate(withParams("p", " 10-Mar-2026 "), "p"));
    assertEquals(
        newYork("2026-03-10 07:30"),
        ParamConverter.convertFriendlyDateTime(withParams("p", "10-Mar-2026 07:30 "), "p"));
    assertEquals(
        newYork("2026-03-10 07:30"),
        ParamConverter.convertFriendlyDateTime(withParams("p", "\t10-Mar-2026 07:30\n"), "p"));
    assertThrows(
        UserFriendlyException.class,
        () -> ParamConverter.convertFriendlyDateTime(withParams("p", "  "), "p"));
  }

  @Test
  void convertFriendlyDateAcceptsOtherFormsOfTheDayAndMonth() throws UserFriendlyException {
    assertEquals(
        newYork("2026-03-10 00:00"),
        ParamConverter.convertFriendlyDate(withParams("p", "10-mar-2026"), "p"));
    assertEquals(
        newYork("2028-02-29 00:00"),
        ParamConverter.convertFriendlyDate(withParams("p", "29-Feb-2028"), "p"));
    assertEquals(
        newYork("2026-03-01 00:00"),
        ParamConverter.convertFriendlyDate(withParams("p", "1-Mar-2026"), "p"));
    assertEquals(
        newYork("2026-03-10 07:30"),
        ParamConverter.convertFriendlyDateTime(withParams("p", "10-March-2026 07:30"), "p"));
  }

  @Test
  void convertArrays() {
    HttpServletRequest request = withParams("p", "1", "p", "", "p", "3");

    assertArrayEquals(
        new Short[] {1, -1, 3}, ParamConverter.convertShortArray(request, "p", (short) -1));
    assertArrayEquals(new Long[] {1L, null, 3L}, ParamConverter.convertLongArray(request, "p"));
    assertArrayEquals(new Float[] {1f, null, 3f}, ParamConverter.convertFloatArray(request, "p"));
    assertArrayEquals(
        new BigInteger[] {BigInteger.ONE, null, BigInteger.valueOf(3)},
        ParamConverter.convertBigIntegerArray(request, "p"));
  }

  @Test
  void missingArraysAreNull() {
    assertNull(ParamConverter.convertShortArray(missing, "p", (short) 0));
    assertNull(ParamConverter.convertLongArray(missing, "p"));
    assertNull(ParamConverter.convertFloatArray(missing, "p"));
    assertNull(ParamConverter.convertBigIntegerArray(missing, "p"));
  }
}
