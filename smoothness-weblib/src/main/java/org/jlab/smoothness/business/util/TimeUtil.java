package org.jlab.smoothness.business.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * @author ryans
 */
public final class TimeUtil {

    private TimeUtil() {
        // private constructor
    }

    public static int countMonthsInclusive(Date start, Date end) {
        DateIterator iterator = new DateIterator(start, end, Calendar.MONTH);

        int count = 0;

        while (iterator.hasNext()) {
            iterator.next();
            count = count + 1;
        }

        return count;
    }

    public static Date getCcShiftStart(Date dateInShift) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateInShift);

        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        int hour = cal.get(Calendar.HOUR_OF_DAY);

        if (hour == 23) {
            //cal.set(Calendar.HOUR_OF_DAY, 23);
        } else if (hour <= 6) {
            cal.add(Calendar.DATE, -1);
            cal.set(Calendar.HOUR_OF_DAY, 23);
        } else if (hour <= 14) {
            cal.set(Calendar.HOUR_OF_DAY, 7);
        } else {
            cal.set(Calendar.HOUR_OF_DAY, 15);
        }

        return cal.getTime();
    }

    public static Date getCcShiftEnd(Date dateInShift) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateInShift);

        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        int hour = cal.get(Calendar.HOUR_OF_DAY);

        if (hour == 23) {
            cal.add(Calendar.DATE, 1);
            cal.set(Calendar.HOUR_OF_DAY, 7);
        } else if (hour <= 6) {
            cal.set(Calendar.HOUR_OF_DAY, 7);
        } else if (hour <= 14) {
            cal.set(Calendar.HOUR_OF_DAY, 15);
        } else {
            cal.set(Calendar.HOUR_OF_DAY, 23);
        }

        return cal.getTime();
    }

    public static Date addHours(Date date, int hours) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, hours);

        return cal.getTime();
    }

    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);
        cal.add(Calendar.DATE, days);

        return cal.getTime();
    }

    public static Date addMonths(Date date, int months) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);
        cal.add(Calendar.MONTH, months);

        return cal.getTime();
    }

    public static Date calculateWeekEndDate(Date start) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(start);
        cal.add(Calendar.DATE, 7);

        return cal.getTime();
    }

    public static Date calculateYearEndDate(Date start) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(start);
        cal.add(Calendar.YEAR, 1);

        return cal.getTime();
    }

    public static Date startOfYear(Date date, Calendar tz) {
        Calendar cal = tz;

        cal.setTime(date);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    public static Date startOfNextYear(Date date, Calendar tz) {
        return addYears(startOfYear(date, tz), 1);
    }

    public static Date addYears(Date date, int years) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);
        cal.add(Calendar.YEAR, years);

        return cal.getTime();
    }    
    
    public static Date startOfMonth(Date date, Calendar tz) {
        Calendar cal = tz;

        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    public static Date startOfNextMonth(Date date, Calendar tz) {
        return addMonths(startOfMonth(date, tz), 1);
    }

    // this is a bad idea due to half-open / half-closed interval used everywhere
    /*public static Date endOfMonth(Date date, Calendar tz) {
        Calendar cal = tz;

        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.DATE, -1);

        return cal.getTime();
    }*/
    public static Date startOfDay(Date day, Calendar tz) {
        Calendar cal = tz;

        cal.setTime(day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    public static Date startOfNextDay(Date date, Calendar tz) {
        return addDays(startOfDay(date, tz), 1);
    }

    public static Date startOfHour(Date date, Calendar tz) {
        Calendar cal = tz;

        cal.setTime(date);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    public static String formatMonthInterval(Date start, Date end) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMMM yyyy");

        return formatter.format(start) + " - " + formatter.format(end);
    }

    public static String formatSmartSingleTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        String format = "MMMM d, yyyy";

        if (cal.get(Calendar.HOUR_OF_DAY) != 0 || cal.get(Calendar.MINUTE) != 0) {
            format = "MMMM d, yyyy HH:mm";
        }

        SimpleDateFormat formatter = new SimpleDateFormat(format);

        return formatter.format(date);
    }

    public static String formatSmartRangeSeparateTime(Date start, Date end) {
        SimpleDateFormat sFormat;
        SimpleDateFormat eFormat;
        boolean sameYear;
        boolean sameMonth;
        boolean sameDay;
        boolean oneDaySpecialCase = false;
        boolean oneMonthSpecialCase = false;
        boolean oneYearSpecialCase = false;
        String sHourFormat = "";
        String eHourFormat = "";
        String result;
        String timeFormat = "";

        Calendar sCal = Calendar.getInstance();
        sCal.setTime(start);

        Calendar eCal = Calendar.getInstance();
        eCal.setTime(end);

        if (sCal.get(Calendar.HOUR_OF_DAY) != 0 || sCal.get(Calendar.MINUTE) != 0) {
            sHourFormat = "yes";
        }

        if (eCal.get(Calendar.HOUR_OF_DAY) != 0 || eCal.get(Calendar.MINUTE) != 0) {
            eHourFormat = "yes";
        }

        if (sHourFormat.length() > 0 || eHourFormat.length() > 0) {
            sHourFormat = "HH:mm";
            eHourFormat = "HH:mm";

            SimpleDateFormat shFormat = new SimpleDateFormat(sHourFormat);
            SimpleDateFormat ehFormat = new SimpleDateFormat(eHourFormat);

            timeFormat = " (" + shFormat.format(start) + " - " + ehFormat.format(end) + ")";
        } else { // No hours or minutes so check special cases
            Calendar special = Calendar.getInstance();
            special.setTime(start);
            special.add(Calendar.DATE, 1);
            oneDaySpecialCase = special.getTime().equals(end);

            if (!oneDaySpecialCase) {
                special.setTime(start);
                special.add(Calendar.MONTH, 1);
                oneMonthSpecialCase = special.getTime().equals(end);

                if (!oneMonthSpecialCase) {
                    special.setTime(start);
                    special.add(Calendar.YEAR, 1);
                    oneYearSpecialCase = special.getTime().equals(end);
                }
            }
        }

        if (oneDaySpecialCase) {
            String format = "MMMM d, yyyy";

            SimpleDateFormat formatter = new SimpleDateFormat(format);

            result = formatter.format(start);
        } else if (oneMonthSpecialCase) {
            String format = "MMMM yyyy";

            SimpleDateFormat formatter = new SimpleDateFormat(format);

            result = formatter.format(start);
        } else if (oneYearSpecialCase) {
            String format = "yyyy";

            SimpleDateFormat formatter = new SimpleDateFormat(format);

            result = formatter.format(start);
        } else {
            sameYear = sCal.get(Calendar.YEAR) == eCal.get(Calendar.YEAR);

            if (sameYear) {
                sameMonth = sCal.get(Calendar.MONTH) == eCal.get(Calendar.MONTH);

                if (sameMonth) {
                    sameDay = sCal.get(Calendar.DATE) == eCal.get(Calendar.DATE);

                    if (sameDay) {
                        sFormat = new SimpleDateFormat("MMMM d");
                        eFormat = new SimpleDateFormat(", yyyy");
                    } else {
                        sFormat = new SimpleDateFormat("MMMM d");
                        eFormat = new SimpleDateFormat(" - d, yyyy");
                    }
                } else {
                    sFormat = new SimpleDateFormat("MMMM d");
                    eFormat = new SimpleDateFormat(" - MMMM d, yyyy");
                }
            } else {
                sFormat = new SimpleDateFormat("MMMM d, yyyy");
                eFormat = new SimpleDateFormat(" - MMMM d, yyyy");
            }

            result = sFormat.format(start) + eFormat.format(end) + timeFormat;
        }

        return result;
    }

    /**
     * This is what is given to SimpleDateFormat
     *
     * @return format pattern
     */
    public static String getGlobalDateTimeFormatPattern() {
        return "dd-MMM-yyyy HH:mm";
    }

    /**
     * This is what users see
     *
     * @return placeholder text
     */
    public static String getGlobalDateTimeFormatPlaceholder() {
        return "DD-MMM-YYYY hh:mm";
    }

    public static boolean isMonday() {
        Calendar cal = Calendar.getInstance();

        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        return Calendar.MONDAY == dayOfWeek;
    }


    public static Calendar getUtcCalendar() {
        return Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    }
}
