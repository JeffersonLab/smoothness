package org.jlab.smoothness.presentation.util;

import org.jlab.smoothness.business.util.TimeUtil;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;

/**
 * Utility methods for converting parameters.  Built-in unchecked exceptions such as NumberFormatException and
 * IllegalArgumentException are thrown if conversion fails.  In some cases the built-in checked ParseException is
 * thrown, as declared.
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
     * @throws IllegalArgumentException If the parameter contains more than one character
     */
    public static Character convertCharacter(HttpServletRequest request, String name) throws IllegalArgumentException {
        String valueStr = request.getParameter(name);
        Character value = null;

        if(valueStr != null && !valueStr.isEmpty()) {
            if(valueStr.length() > 1) {
                throw new IllegalArgumentException("String contains more than one Character");
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
     * @throws IllegalArgumentException If parameter does not contain one of "Y" or "N"
     */
    public static Boolean convertYNBoolean(HttpServletRequest request, String name) throws IllegalArgumentException {
        String valueStr = request.getParameter(name);
        Boolean value = null;

        if (valueStr != null && !valueStr.isEmpty()) {
            if ("N".equals(valueStr)) {
                value = false;
            } else if ("Y".equals(valueStr)) {
                value = true;
            } else {
                throw new IllegalArgumentException("Value must be one of 'Y' or 'N'");
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
     * @throws ParseException If the parameter format does not match expected format
     */
    public static Date convertISO8601Date(HttpServletRequest request, String name) throws ParseException {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");        

        Date value = null;

        String valueStr = request.getParameter(name);

        if (valueStr != null && !valueStr.isEmpty()) {
            final LocalDate ld = formatter.parse(valueStr, LocalDate::from);
            final ZonedDateTime zdt = ld.atStartOfDay(ZoneId.of("America/New_York"));
            value = Date.from(zdt.toInstant());
        }

        return value;
    }

    /**
     * Convert a parameter to a Date (in New_York timezone) with expectation of "yyyy-MM-dd HH:mm" format.
     *
     * @param request The HttpServletRequest
     * @param name The parameter name
     * @return A Date or null
     * @throws ParseException If the parameter format does not match expected format
     */
    public static Date convertISO8601DateTime(HttpServletRequest request, String name) throws ParseException {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"); 

        Date value = null;

        String valueStr = request.getParameter(name);

        if (valueStr != null && !valueStr.isEmpty()) {
            final LocalDateTime ldt = formatter.parse(valueStr, LocalDateTime::from);
            final ZonedDateTime zdt = ldt.atZone(ZoneId.of("America/New_York"));
            value = Date.from(zdt.toInstant());
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
    public static BigInteger convertBigInteger(HttpServletRequest request,
            String name) {
        String valueStr = request.getParameter(name);
        BigInteger value = null;

        if (valueStr != null && !valueStr.isEmpty()) {
            value = new BigInteger(valueStr);
        }

        return value;
    }

    public static Short[] convertShortArray(HttpServletRequest request, String name,
                                            Short defaultValue) {
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

    public static Date convertFriendlyDateTime(HttpServletRequest request, String name) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(TimeUtil.getFriendlyDateTimePattern());

        Date value = null;

        String valueStr = request.getParameter(name);

        if (valueStr != null && !valueStr.isEmpty()) {
            value = format.parse(valueStr);
        }

        return value;
    }

    public static Date convertFriendlyDate(HttpServletRequest request, String name) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(TimeUtil.getFriendlyDatePattern());

        Date value = null;

        String valueStr = request.getParameter(name);

        if (valueStr != null && !valueStr.isEmpty()) {
            value = format.parse(valueStr);
        }

        return value;
    }
}
