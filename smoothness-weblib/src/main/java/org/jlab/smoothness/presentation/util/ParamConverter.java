package org.jlab.smoothness.presentation.util;

import java.math.BigInteger;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author ryans
 */
public final class ParamConverter {

    private ParamConverter() {
        // No one can instantiate due to private visibility
    }

    public static Character convertCharacter(HttpServletRequest request, String name) {
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

    public static Float convertFloat(HttpServletRequest request, String name) {
        String valueStr = request.getParameter(name);
        Float value = null;

        if (valueStr != null && !valueStr.isEmpty()) {
            value = Float.valueOf(valueStr);
        }

        return value;
    }

    public static Integer convertInteger(HttpServletRequest request, String name) {
        String valueStr = request.getParameter(name);
        Integer value = null;

        if (valueStr != null && !valueStr.isEmpty()) {
            value = Integer.valueOf(valueStr);
        }

        return value;
    }

    public static Integer convertPercent(HttpServletRequest request, String name) {
        String valueStr = request.getParameter(name);
        Integer value = null;

        if (valueStr != null && !valueStr.isEmpty()) {
            value = Integer.parseInt(valueStr);

            if (value < 0) {
                throw new IllegalArgumentException(name + " must not be negative");
            }

            if (value > 100) {
                throw new IllegalArgumentException(name + " must not be more than 100");
            }
        }

        return value;
    }

    public static Boolean convertYNBoolean(HttpServletRequest request, String name) {
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

    public static boolean convertYNBoolean(HttpServletRequest request, String name, boolean defaultValue) {
        String valueStr = request.getParameter(name);
        boolean value;

        if (valueStr != null && !valueStr.isEmpty()) {
            if ("N".equals(valueStr)) {
                value = false;
            } else if ("Y".equals(valueStr)) {
                value = true;
            } else {
                throw new IllegalArgumentException("Value must be one of 'Y' or 'N'");
            }
        } else {
            value = defaultValue;
        }

        return value;
    }

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

    public static BigInteger convertBigInteger(HttpServletRequest request,
            String name) {
        String valueStr = request.getParameter(name);
        BigInteger value = null;

        if (valueStr != null && !valueStr.isEmpty()) {
            value = new BigInteger(valueStr);
        }

        return value;
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

    public static int convertNonNegativeInt(HttpServletRequest request, String name, int defaultValue) {
        String valueStr = request.getParameter(name);
        int value;

        if (valueStr == null || valueStr.isEmpty()) {
            value = defaultValue;
        } else {
            value = Integer.parseInt(valueStr);
        }

        if (value < 0) {
            throw new IllegalArgumentException(name + " must not be negative");
        }

        return value;
    }
}
