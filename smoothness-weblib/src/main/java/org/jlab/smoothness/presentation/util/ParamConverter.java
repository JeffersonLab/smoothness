package org.jlab.smoothness.presentation.util;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.business.util.TimeUtil;

/**
 * Utility methods for converting parameters. Built-in unchecked exceptions such as
 * NumberFormatException and IllegalArgumentException are wrapped in UserFriendlyException if
 * conversion fails. In some cases DateTimeParseException or ParseException is also wrapped in a
 * UserFriendlyException.
 *
 * <p>The UserFriendlyException.getUserMessage() can be used to provide a user-friendly message to
 * the user without tripping the CodeQL Scanner.
 *
 * @author ryans
 */
public final class ParamConverter {

  private ParamConverter() {
    // No one can instantiate due to private visibility
  }

  /**
   * Convert a parameter to a Character.
   *
   * @param request The HttpServletRequest
   * @param name The parameter name
   * @return A Character or null
   * @throws UserFriendlyException If the parameter contains more than one character
   */
  public static Character convertCharacter(HttpServletRequest request, String name)
      throws UserFriendlyException {
    String valueStr = request.getParameter(name);
    Character value = null;

    if (valueStr != null && !valueStr.isEmpty()) {
      if (valueStr.length() > 1) {
        throw new UserFriendlyException("String contains more than one Character");
      } else {
        value = valueStr.charAt(0);
      }
    }

    return value;
  }

  /**
   * Convert a parameter to a Float.
   *
   * @param request The HttpServletRequest
   * @param name The parameter name
   * @return A Float or null
   */
  public static Float convertFloat(HttpServletRequest request, String name) {
    String valueStr = request.getParameter(name);
    Float value = null;

    if (valueStr != null && !valueStr.isEmpty()) {
      value = Float.valueOf(valueStr);
    }

    return value;
  }

  /**
   * Convert a parameter to an Integer.
   *
   * @param request The HttpServletRequest
   * @param name The parameter name
   * @return A Integer or null
   */
  public static Integer convertInteger(HttpServletRequest request, String name) {
    String valueStr = request.getParameter(name);
    Integer value = null;

    if (valueStr != null && !valueStr.isEmpty()) {
      value = Integer.valueOf(valueStr);
    }

    return value;
  }

  /**
   * Convert a parameter to a Boolean.
   *
   * @param request The HttpServletRequest
   * @param name The parameter name
   * @return A Boolean or null
   * @throws UserFriendlyException If parameter does not contain one of "Y" or "N"
   */
  public static Boolean convertYNBoolean(HttpServletRequest request, String name)
      throws UserFriendlyException {
    String valueStr = request.getParameter(name);
    Boolean value = null;

    if (valueStr != null && !valueStr.isEmpty()) {
      if ("N".equals(valueStr)) {
        value = false;
      } else if ("Y".equals(valueStr)) {
        value = true;
      } else {
        throw new UserFriendlyException("Value must be one of 'Y' or 'N'");
      }
    }

    return value;
  }

  /**
   * Convert a parameter to a Date (in New_York timezone) with expectation of "yyyy-MM-dd" format.
   *
   * @param request The HttpServletRequest
   * @param name The parameter name
   * @return A Date or null
   * @throws UserFriendlyException If the parameter format does not match expected format
   */
  public static Date convertISO8601Date(HttpServletRequest request, String name)
      throws UserFriendlyException {
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    Date value = null;

    String valueStr = request.getParameter(name);

    if (valueStr != null && !valueStr.isEmpty()) {
      try {
        final LocalDate ld = formatter.parse(valueStr, LocalDate::from);
        final ZonedDateTime zdt = ld.atStartOfDay(ZoneId.of("America/New_York"));
        value = Date.from(zdt.toInstant());
      } catch (DateTimeParseException e) {
        throw new UserFriendlyException("Date format error", e);
      }
    }

    return value;
  }

  /**
   * Convert a parameter to a Date (in New_York timezone) with expectation of "yyyy-MM-dd HH:mm"
   * format.
   *
   * @param request The HttpServletRequest
   * @param name The parameter name
   * @return A Date or null
   * @throws UserFriendlyException If the parameter format does not match expected format
   */
  public static Date convertISO8601DateTime(HttpServletRequest request, String name)
      throws UserFriendlyException {
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    Date value = null;

    String valueStr = request.getParameter(name);

    if (valueStr != null && !valueStr.isEmpty()) {
      try {
        final LocalDateTime ldt = formatter.parse(valueStr, LocalDateTime::from);
        final ZonedDateTime zdt = ldt.atZone(ZoneId.of("America/New_York"));
        value = Date.from(zdt.toInstant());
      } catch (DateTimeParseException e) {
        throw new UserFriendlyException("Date format error", e);
      }
    }

    return value;
  }

  /**
   * Convert a parameter to a BigInteger.
   *
   * @param request The HttpServletRequest
   * @param name The parameter name
   * @return A BigInteger or null
   */
  public static BigInteger convertBigInteger(HttpServletRequest request, String name) {
    String valueStr = request.getParameter(name);
    BigInteger value = null;

    if (valueStr != null && !valueStr.isEmpty()) {
      value = new BigInteger(valueStr);
    }

    return value;
  }

  /**
   * Convert a parameter to an array of Short values.
   *
   * @param request The HttpServletRequest
   * @param name The parameter name
   * @param defaultValue Value to use for any indices with a null or empty value
   * @return An array of Short values or null
   */
  public static Short[] convertShortArray(
      HttpServletRequest request, String name, Short defaultValue) {
    String[] valueStrArray = request.getParameterValues(name);
    Short[] valueArray = null;

    if (valueStrArray != null && valueStrArray.length > 0) {
      valueArray = new Short[valueStrArray.length];

      for (int i = 0; i < valueStrArray.length; i++) {
        Short value;

        if (valueStrArray[i] != null && !valueStrArray[i].isEmpty()) {
          value = Short.valueOf(valueStrArray[i]);
        } else {
          value = defaultValue;
        }

        valueArray[i] = value;
      }
    }

    return valueArray;
  }

  /**
   * Convert a parameter to an array of Long values.
   *
   * @param request The HttpServletRequest
   * @param name The parameter name
   * @return An array of Long values or null
   */
  public static Long[] convertLongArray(HttpServletRequest request, String name) {
    String[] valueStrArray = request.getParameterValues(name);
    Long[] valueArray = null;

    if (valueStrArray != null && valueStrArray.length > 0) {
      valueArray = new Long[valueStrArray.length];

      for (int i = 0; i < valueStrArray.length; i++) {
        Long value = null;

        if (valueStrArray[i] != null && !valueStrArray[i].isEmpty()) {
          value = Long.valueOf(valueStrArray[i]);
        }

        valueArray[i] = value;
      }
    }

    return valueArray;
  }

  /**
   * Convert a parameter to an array of Float values.
   *
   * @param request The HttpServletRequest
   * @param name The parameter name
   * @return An array of Float values or null
   */
  public static Float[] convertFloatArray(HttpServletRequest request, String name) {
    String[] valueStrArray = request.getParameterValues(name);
    Float[] valueArray = null;

    if (valueStrArray != null && valueStrArray.length > 0) {
      valueArray = new Float[valueStrArray.length];

      for (int i = 0; i < valueStrArray.length; i++) {
        Float value = null;

        if (valueStrArray[i] != null && !valueStrArray[i].isEmpty()) {
          value = Float.valueOf(valueStrArray[i]);
        }

        valueArray[i] = value;
      }
    }

    return valueArray;
  }

  /**
   * Convert a parameter to an array of BigInteger values.
   *
   * @param request The HttpServletRequest
   * @param name The parameter name
   * @return An array of BigInteger values or null
   */
  public static BigInteger[] convertBigIntegerArray(HttpServletRequest request, String name) {
    String[] valueStrArray = request.getParameterValues(name);
    BigInteger[] valueArray = null;

    if (valueStrArray != null && valueStrArray.length > 0) {
      valueArray = new BigInteger[valueStrArray.length];

      for (int i = 0; i < valueStrArray.length; i++) {
        BigInteger value = null;

        if (valueStrArray[i] != null && !valueStrArray[i].isEmpty()) {
          value = new BigInteger(valueStrArray[i]);
        }

        valueArray[i] = value;
      }
    }

    return valueArray;
  }

  /**
   * Convert a parameter to a Date (in New_York timezone) with expectation of "dd-MMM-yyyy HH:mm"
   * format.
   *
   * @param request The HttpServletRequest
   * @param name The parameter name
   * @return A Date or null
   * @throws UserFriendlyException If the parameter format does not match expected format
   */
  public static Date convertFriendlyDateTime(HttpServletRequest request, String name)
      throws UserFriendlyException {
    SimpleDateFormat format = new SimpleDateFormat(TimeUtil.getFriendlyDateTimePattern());

    Date value = null;

    String valueStr = request.getParameter(name);

    if (valueStr != null && !valueStr.isEmpty()) {
      try {
        value = format.parse(valueStr);
      } catch (ParseException e) {
        throw new UserFriendlyException("format error", e);
      }
    }

    return value;
  }

  /**
   * Convert a parameter to a Date (in New_York timezone) with expectation of "dd-MMM-yyyy" format.
   *
   * @param request The HttpServletRequest
   * @param name The parameter name
   * @return A Date or null
   * @throws UserFriendlyException If the parameter format does not match expected format
   */
  public static Date convertFriendlyDate(HttpServletRequest request, String name)
      throws UserFriendlyException {
    SimpleDateFormat format = new SimpleDateFormat(TimeUtil.getFriendlyDatePattern());

    Date value = null;

    String valueStr = request.getParameter(name);

    if (valueStr != null && !valueStr.isEmpty()) {
      try {
        value = format.parse(valueStr);
      } catch (ParseException e) {
        throw new UserFriendlyException("format error", e);
      }
    }

    return value;
  }
}
