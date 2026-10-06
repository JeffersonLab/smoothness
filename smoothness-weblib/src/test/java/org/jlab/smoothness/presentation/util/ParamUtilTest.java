package org.jlab.smoothness.presentation.util;

import static org.jlab.smoothness.presentation.util.FakeRequest.withParams;
import static org.jlab.smoothness.presentation.util.ParamConverterTest.newYork;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

/** ParamUtil and ParamValidator, whose messages users see. */
class ParamUtilTest {

  static void assertRejected(String message, Executable executable) {
    assertEquals(message, assertThrows(IllegalArgumentException.class, executable).getMessage());
  }

  @Test
  void nonNegativeInt() {
    assertEquals(0, ParamUtil.convertAndValidateNonNegativeInt(withParams("n", "0"), "n"));
    assertRejected(
        "n must not be empty", () -> ParamUtil.convertAndValidateNonNegativeInt(withParams(), "n"));
    assertRejected(
        "n must not be negative",
        () -> ParamUtil.convertAndValidateNonNegativeInt(withParams("n", "-1"), "n"));
  }

  @Test
  void nonNegativeIntWithDefault() {
    assertEquals(5, ParamUtil.convertAndValidateNonNegativeInt(withParams(), "n", 5));
    assertEquals(2, ParamUtil.convertAndValidateNonNegativeInt(withParams("n", "2"), "n", 5));
    assertEquals(-5, ParamUtil.convertAndValidateNonNegativeInt(withParams(), "n", -5));
    assertRejected(
        "n must not be negative",
        () -> ParamUtil.convertAndValidateNonNegativeInt(withParams("n", "-1"), "n", 5));
  }

  @Test
  void nonNegativeInteger() {
    assertNull(ParamUtil.convertAndValidateNonNegativeInteger(withParams(), "n"));
    assertEquals(3, ParamUtil.convertAndValidateNonNegativeInteger(withParams("n", "3"), "n"));
    assertRejected(
        "n must not be negative",
        () -> ParamUtil.convertAndValidateNonNegativeInteger(withParams("n", "-1"), "n"));
  }

  @Test
  void ynBoolean() throws UserFriendlyException {
    assertTrue(ParamUtil.convertAndValidateYNBoolean(withParams("b", "Y"), "b"));
    assertRejected(
        "b must not be empty", () -> ParamUtil.convertAndValidateYNBoolean(withParams(), "b"));
    assertTrue(ParamUtil.convertAndValidateYNBoolean(withParams(), "b", true));
    assertFalse(ParamUtil.convertAndValidateYNBoolean(withParams("b", "N"), "b", true));
    assertThrows(
        UserFriendlyException.class,
        () -> ParamUtil.convertAndValidateYNBoolean(withParams("b", "x"), "b", true));
  }

  @Test
  void validatePercent() {
    ParamValidator.validatePercent("p", 0);
    ParamValidator.validatePercent("p", 100);
    assertRejected("p must not be empty", () -> ParamValidator.validatePercent("p", null));
    assertRejected("p must not be negative", () -> ParamValidator.validatePercent("p", -1));
    assertRejected("p must not be more than 100", () -> ParamValidator.validatePercent("p", 101));
  }

  @Test
  void validateNonNull() {
    ParamValidator.validateNonNull("x", "");
    assertRejected("x must not be empty", () -> ParamValidator.validateNonNull("x", null));
  }

  @Test
  void validateDateRange() {
    ParamValidator.validateDateRange(
        "start", "end", newYork("2026-03-10 00:00"), newYork("2026-03-10 00:00"));
    assertRejected(
        "start must not be empty",
        () -> ParamValidator.validateDateRange("start", "end", null, newYork("2026-03-10 00:00")));
    assertRejected(
        "end must not be empty",
        () -> ParamValidator.validateDateRange("start", "end", newYork("2026-03-10 00:00"), null));
    assertRejected(
        "end must not come before start",
        () ->
            ParamValidator.validateDateRange(
                "start", "end", newYork("2026-03-10 00:00"), newYork("2026-03-09 00:00")));
  }
}
