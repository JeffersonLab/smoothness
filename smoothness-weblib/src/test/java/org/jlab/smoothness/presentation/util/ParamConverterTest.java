package org.jlab.smoothness.presentation.util;

import static org.jlab.smoothness.presentation.util.FakeRequest.withParams;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
  void convertNumbersThrowNumberFormatExceptionNotUserFriendly() {
    assertThrows(
        NumberFormatException.class,
        () -> ParamConverter.convertInteger(withParams("p", "x"), "p"));
    assertThrows(
        NumberFormatException.class,
        () -> ParamConverter.convertInteger(withParams("p", " 1"), "p"));
    assertThrows(
        NumberFormatException.class,
        () -> ParamConverter.convertBigInteger(withParams("p", "1.0"), "p"));
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
  void convertIso8601DateMovesAnInvalidDayToTheEndOfTheMonth() throws UserFriendlyException {
    // DateTimeFormatter's default SMART resolver
    assertEquals(
        newYork("2026-02-28 00:00"),
        ParamConverter.convertISO8601Date(withParams("p", "2026-02-30"), "p"));
    assertThrows(
        UserFriendlyException.class,
        () -> ParamConverter.convertISO8601Date(withParams("p", "2026-02-32"), "p"));
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
  void convertFriendlyDateIsLenient() throws UserFriendlyException {
    assertEquals(
        newYork("2026-02-01 00:00"),
        ParamConverter.convertFriendlyDate(withParams("p", "32-Jan-2026"), "p"));
    assertEquals(
        newYork("2026-03-10 00:00"),
        ParamConverter.convertFriendlyDate(withParams("p", "10-Mar-2026 07:30"), "p"));
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
