package org.jlab.smoothness.business.util;

import org.jlab.smoothness.persistence.enumeration.Shift;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Time Utilities.
 *
 * @author ryans
 */
public final class TimeUtil {

    private TimeUtil() {
        // private constructor
    }

    /**
     * Count the number of months in a date range inclusive.
     *
     * @param start The start Date
     * @param end The end Date
     * @return The number of months, inclusive
     */
    public static int countMonthsInclusive(Date start, Date end) {
        DateIterator iterator = new DateIterator(start, end, Calendar.MONTH);

        int count = 0;

        while (iterator.hasNext()) {
            iterator.next();
            count = count + 1;
        }

        return count;
    }

    /**
     * Return the Crew Chief shift start given a Date in the shift.
     *
     * @param dateInShift Date in the shift
     * @return The date representing the start of shift
     */
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

    /**
     * Return the Crew Chief shift end given a Date in the shift.
     *
     * @param dateInShift Date in the shift
     * @return The Date representing the end of shift
     */
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

    /**
     * Determines the crew chief shift type for the specified day and hour.
     *
     * @param dayAndHour the day and hour.
     * @return the shift type.
     */
    public static Shift calculateCrewChiefShiftType(Date dayAndHour) {
        Calendar cal = Calendar.getInstance();
        int hour;
        Shift shift;

        cal.setTime(dayAndHour);
        hour = cal.get(Calendar.HOUR_OF_DAY);

        if (hour <= 6 || hour == 23) {
            shift = Shift.OWL;
        } else if (hour <= 14) {
            shift = Shift.DAY;
        } else {
            shift = Shift.SWING;
        }

        return shift;
    }

    /**
     * Return a Date representing the Crew Chief shift start day and hour given a day and shift.
     *
     * @param day The day
     * @param shift The shift
     * @return The Date representing the shift start
     */
    public static Date getCrewChiefStartDayAndHour(Date day, Shift shift) {
        int hour;
        switch (shift) {
            case DAY:
                hour = 7;
                break;
            case SWING:
                hour = 15;
                break;
            case OWL:
                hour = 23;
                day = TimeUtil.addDays(day, -1);
                break;
            default:
                throw new IllegalArgumentException("Unrecognized shift: " + shift);
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(day);
        cal.set(Calendar.HOUR_OF_DAY, hour);

        return cal.getTime();
    }

    /**
     * WARNING WARNING WARNING : Use calculateCrewChiefShiftEndDayAndHour instead if you are
     * providing the startDayAndHour. This method is only if the "day" parameter is the actual day
     * that pairs with the shift name. The gotcha is with OWL shift where it technically starts the
     * day before!
     *
     * @param day The day
     * @param shift The shift
     * @return The end date
     */
    public static Date getCrewChiefEndDayAndHour(Date day, Shift shift) {
        int hour = 6;
        switch (shift) {
            case DAY:
                hour = 14;
                break;
            case SWING:
                hour = 22;
                break;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(day);
        cal.set(Calendar.HOUR_OF_DAY, hour);

        return cal.getTime();
    }

    /**
     * Check if the provided Date is the start of the Crew Chief shift.
     *
     * @param startDayAndHour Date to check
     * @return true if start of Crew Chief shift, false otherwise
     */
    public static boolean isCrewChiefShiftStart(Date startDayAndHour) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(startDayAndHour);

        int startHourOfDay = cal.get(Calendar.HOUR_OF_DAY);

        return (startHourOfDay == 23 || startHourOfDay == 7 || startHourOfDay == 15);
    }

    /**
     * Check if the provided Date is the start of an Experimenter shift.
     *
     * @param startDayAndHour Date to check
     * @return true if start of Experimenter shift, false otherwise
     */
    public static boolean isExperimenterShiftStart(Date startDayAndHour) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(startDayAndHour);

        int startHourOfDay = cal.get(Calendar.HOUR_OF_DAY);

        return (startHourOfDay == 0 || startHourOfDay == 8 || startHourOfDay == 16);
    }

    /**
     * Calculate the previous Crew Chief shift start Date.
     *
     * @param currentStartDayAndHour The current shift start Date
     * @return The previous start Date
     */
    public static Date previousCrewChiefShiftStart(Date currentStartDayAndHour) {
        Date previousDay = currentStartDayAndHour;

        Shift previousShift = TimeUtil.calculateCrewChiefShiftType(previousDay).getPrevious();

        return TimeUtil.getCrewChiefStartDayAndHour(previousDay, previousShift);
    }

    /**
     * Calculate the next Crew Chief shift start Date.
     *
     * @param currentStartDayAndHour The current shift start Date
     * @return The next start Date
     */
    public static Date nextCrewChiefShiftStart(Date currentStartDayAndHour) {
        Date nextDay = currentStartDayAndHour;

        Shift nextShift = TimeUtil.calculateCrewChiefShiftType(nextDay).getNext();

        if (nextShift == Shift.OWL || nextShift == Shift.DAY) {
            nextDay = TimeUtil.addDays(nextDay, 1);
        }

        return TimeUtil.getCrewChiefStartDayAndHour(nextDay, nextShift);
    }

    /**
     * Determines the crew chief shift type for the specified day and hour.
     *
     * @param dayAndHour the day and hour.
     * @return the shift type.
     */
    public static Shift calculateCrewChiefShift(Date dayAndHour) {
        Calendar cal = Calendar.getInstance();
        int hour;
        Shift shift;

        cal.setTime(dayAndHour);
        hour = cal.get(Calendar.HOUR_OF_DAY);

        if (hour <= 6 || hour == 23) {
            shift = Shift.OWL;
        } else if (hour <= 14) {
            shift = Shift.DAY;
        } else {
            shift = Shift.SWING;
        }

        return shift;
    }

    /**
     * Calculate the Crew Chief shift end day and hour.
     *
     * @param startDayAndHour Date representing the start of the shift
     * @return The end day and hour
     */
    public static Date calculateCrewChiefShiftEndDayAndHour(Date startDayAndHour) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(startDayAndHour);

        int startHourOfDay = cal.get(Calendar.HOUR_OF_DAY);
        int endHourOfDay = 22;

        switch (startHourOfDay) {
            case 23:
                endHourOfDay = 6;
                cal.add(Calendar.DATE, 1); // CC OWL Shift actually starts on previous day...
                break;
            case 7:
                endHourOfDay = 14;
                break;
        }

        cal.set(Calendar.HOUR_OF_DAY, endHourOfDay);

        return cal.getTime();
    }

    /**
     * Calculate the experimenter shift end day and hour.
     *
     * @param startDayAndHour Date representing the start of the shift
     * @return The end day and hour
     */
    public static Date calculateExperimenterShiftEndDayAndHour(Date startDayAndHour) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(startDayAndHour);

        int startHourOfDay = cal.get(Calendar.HOUR_OF_DAY);
        int endHourOfDay = 23;

        switch (startHourOfDay) {
            case 0:
                endHourOfDay = 7;
                break;
            case 8:
                endHourOfDay = 15;
                break;
        }

        cal.set(Calendar.HOUR_OF_DAY, endHourOfDay);

        return cal.getTime();
    }

    /**
     * A really complex way to say give me "now". This is complex due to Crew Chief's days starting
     * at 23 the previous day. This means if the hour is 23 then actually return tomorrow.
     *
     * Wait, there is more complication:
     *
     * We take Date as a parameter so we can avoid race condition when using this method plus
     * TimeUtil.calculateCrewChiefShiftType. If the calls to new Date() are independent in each then
     * we can get into trouble if boundaries are crossed. For example: if
     * getCurrentCrewChiefShiftDay uses 23:59 and new Date() uses 00:00. Another example: 22:59 vs
     * 23:00.
     *
     * @param date The same "now" as used by other methods to avoid race conditions
     * @return The "Crew Chief Shift Day"
     */
    public static Date getCurrentCrewChiefShiftDay(Date date) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        if (cal.get(Calendar.HOUR_OF_DAY) == 23) {
            cal.add(Calendar.DATE, 1);
        }

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    /**
     * Check if Date represents the first hour of a Crew Chief shift.
     *
     * @param now The date
     * @return true if first hour of Crew Chief shift, else false
     */
    public static boolean isFirstHourOfCrewChiefShift(Date now) {
        boolean firstHour = false;

        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        int hour = cal.get(Calendar.HOUR_OF_DAY);

        if (hour == 23 || hour == 7 || hour == 15) {
            firstHour = true;
        }

        return firstHour;
    }

    /**
     *  Add hours to a Date.
     *
     * @param date The date
     * @param hours The number of hours to add (negative allowed)
     * @return A new Date adjusted by hours
     */
    public static Date addHours(Date date, int hours) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, hours);

        return cal.getTime();
    }

    /**
     *  Add days to a Date.
     *
     * @param date The date
     * @param days The number of days to add (negative allowed)
     * @return A new Date adjusted by days
     */
    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);
        cal.add(Calendar.DATE, days);

        return cal.getTime();
    }

    /**
     * Add months to a Date.
     *
     * @param date The date
     * @param months The number of months to add (negative allowed)
     * @return A new Date adjusted by months
     */
    public static Date addMonths(Date date, int months) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);
        cal.add(Calendar.MONTH, months);

        return cal.getTime();
    }

    /**
     * Find last day of week given a Date.
     *
     * @param start The date
     * @return Date representing the last day of week
     */
    public static Date calculateWeekEndDate(Date start) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(start);
        cal.add(Calendar.DATE, 7);

        return cal.getTime();
    }

    /**
     * Find last day of year given a Date.
     *
     * @param start The date
     * @return Date representing the last day of year
     */
    public static Date calculateYearEndDate(Date start) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(start);
        cal.add(Calendar.YEAR, 1);

        return cal.getTime();
    }

    /**
     * Return the start of the year given a Date in the year and timezone (Calendar).
     *
     * @param date The date
     * @param tz The timezone (Calendar)
     * @return A Date representing the start of the year
     */
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

    /**
     * Return the start of the next fiscal year given a Date and timezone (Calendar).
     *
     * Fiscal year starts Oct 1.
     *
     * @param date The date
     * @param tz The timezone
     * @return The Date adjusted to the start of the next fiscal year
     */
    public static Date startOfFiscalYear(Date date, Calendar tz) {
        Calendar cal = tz;

        cal.setTime(date);

        if(cal.get(Calendar.MONTH) < Calendar.OCTOBER) {
            cal.add(Calendar.YEAR, -1);
        }

        cal.set(Calendar.MONTH, Calendar.OCTOBER);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    /**
     * Return the start of next year given a Date and timezone (Calendar).
     *
     * @param date The date
     * @param tz The timezone
     * @return The Date adjusted to start of the next year
     */
    public static Date startOfNextYear(Date date, Calendar tz) {
        return addYears(startOfYear(date, tz), 1);
    }

    /**
     * Return a new Date which is the result of adding the specified number of years to a given Date.
     *
     * @param date The Date to add to
     * @param years The number of years (negative allowed)
     * @return The new Date result from the addition
     */
    public static Date addYears(Date date, int years) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);
        cal.add(Calendar.YEAR, years);

        return cal.getTime();
    }

    /**
     * Return the start of the month given a Date and timezone (Calendar).
     *
     * @param date The date in the month
     * @param tz The timezone
     * @return The Date representing the start of the month
     */
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

    /**
     * Return the start of the next month given a Date and timezone (Calendar).
     *
     * @param date The date
     * @param tz The timezone (Calendar)
     * @return The Date representing the start of the next month
     */
    public static Date startOfNextMonth(Date date, Calendar tz) {
        return addMonths(startOfMonth(date, tz), 1);
    }

    /**
     * Calculate the difference in hours between two Dates.
     *
     * @param first The first Date
     * @param second The second Date
     * @return The difference in hours
     */
    public static long differenceInHours(Date first, Date second) {
        long hoursBetween = 0;
        long milliSecBetween = 0;

        milliSecBetween = Math.abs(second.getTime() - first.getTime());

        hoursBetween = milliSecBetween / (1000 * 60 * 60);

        return hoursBetween;
    }

    /**
     * Get last day of month with other fields set to zero.
     *
     * @param date The date denoting month and year
     * @param tz Calendar denoting timezone
     * @return A date shifted to end of month with other fields zeroed
     */
    public static Date endOfMonth(Date date, Calendar tz) {
        Calendar cal = tz;

        cal.setTime(date);

        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.DATE, 1);
        cal.add(Calendar.DATE, -1);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    /**
     * Rounds the specified time to the nearest whole hour.
     *
     * @param time the time.
     * @return the time, rounded to the nearest whole hour.
     */
    public static Date roundToNearestHour(Date time) {
        Instant instant = time.toInstant();
        ZonedDateTime ldt = instant.atZone(ZoneId.of("America/New_York"));

        if(ldt.getMinute() >= 30) {
            ldt = ldt.plus(1, ChronoUnit.HOURS);
        }

        ldt = ldt.withMinute(0);
        ldt = ldt.withSecond(0);
        ldt = ldt.withNano(0);

        return Date.from(ldt.toInstant());
    }

    /**
     * Format a Date using the timezone in database.
     *
     * @param dayAndHour The Date
     * @return The formatted date String
     */
    public static String formatDatabaseDateTimeTZ(Date dayAndHour) {
        SimpleDateFormat databaseDateTimeTZ = new SimpleDateFormat("yyyy-MM-dd HH z");
        return databaseDateTimeTZ.format(dayAndHour);
    }

    /**
     * Converts a UNIX timestamp value to a {@link java.util.Date}.
     *
     * @param unix the UNIX timestamp value.
     * @return the date.
     */
    public static Date convertUNIXTimestampToDate(long unix) {
        return new Date(unix * 1000);
    }

    /**
     * Truncate a Date to set hour, minute, second, and millisecond to zero.
     *
     * @param date The Date
     * @return The Date adjusted to have no hours, minutes, seconds, and milliseconds.
     */
    public static Date truncateToHour(Date date) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    /**
     * Return true if the specified date is within the last week.
     *
     * @param date the date.
     * @return true if the date is within the last week.
     */
    public static boolean withinLastWeek(Date date) {
        boolean result = false;

        Calendar oneWeekAgo = Calendar.getInstance();
        Calendar inQuestion = Calendar.getInstance();

        oneWeekAgo.setTime(new Date());
        oneWeekAgo.add(Calendar.WEEK_OF_YEAR, -1);

        inQuestion.setTime(date);

        if (inQuestion.equals(oneWeekAgo)) {
            result = true;
        } else if (oneWeekAgo.before(inQuestion)) {
            result = true;
        }

        return result;
    }

    /**
     * Return the start of the week given a Date in the week and the day of the week which represents the start of the
     * week.
     *
     * @param today The day in the week
     * @param dayOfWeek The day representing the start of the week
     * @return The Date adjusted to the start of the week
     */
    public static Date startOfWeek(Date today, int dayOfWeek) {
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        int currentDayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        int distance = dayOfWeek - currentDayOfWeek;
        if(distance < 0) {
            distance = 7 + distance;
        }
        c.set(Calendar.DATE, c.get(Calendar.DATE) + distance - 7);

        return c.getTime();
    }

    /**
     * Return the start of the day indicated in the Date, given a timezone (Calendar).
     *
     * @param day The Date
     * @param tz The timezone (Calendar)
     * @return The Date adjusted to the start of the day
     */
    public static Date startOfDay(Date day, Calendar tz) {
        Calendar cal = tz;

        cal.setTime(day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    /**
     *  Return the start of the next day given a Date and timezone (Calendar).
     *
     * @param date The Date
     * @param tz The timezone (Calendar)
     * @return The Date adjusted to the start of the next day
     */
    public static Date startOfNextDay(Date date, Calendar tz) {
        return addDays(startOfDay(date, tz), 1);
    }

    /**
     * Return the start of the hour given a Date and timezone (in Calendar).
     *
     * @param date The Date
     * @param tz The timezone (Calendar)
     * @return The Date adjusted to the start of the hour
     */
    public static Date startOfHour(Date date, Calendar tz) {
        Calendar cal = tz;

        cal.setTime(date);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    /**
     * Format a Date range as an interval of only month and year.
     *
     * @param start The start Date
     * @param end The end Date
     * @return A formatted String representing the interval
     */
    public static String formatMonthInterval(Date start, Date end) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMMM yyyy");

        return formatter.format(start) + " - " + formatter.format(end);
    }

    /**
     * Format as a human would - if hour and minutes are zero then don't mention them.
     *
     * @param date The Date
     * @return The formatted date String
     */
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

    /**
     * Smoothness template handles a set of human-friendly labeled ranges, and this method checks if a given
     * start and end Date range matches one of the named ranges.  If not, "custom" is returned.  This is useful
     * because instead of always showing users some explicit verbose date range, using familiar named ranges is
     * convenient when possible.  For example, running a report on "Last Year" is concise.
     *
     * @param start The start Date
     * @param end The end Date
     * @param sevenAmAdjusted true if a day starts at 7 AM, not midnight.
     * @param currentRun The current run date range (must be looked up)
     * @param previousRun The previous run date range (must be looked up)
     * @return The human-friendly range label, or 'custom' if not in the set of smoothness-honored ranges.
     */
    public static String encodeRange(Date start, Date end, boolean sevenAmAdjusted, DateRange currentRun, DateRange previousRun) {
        Calendar c = Calendar.getInstance();
        Date now = new Date();
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MINUTE, 0);

        if(sevenAmAdjusted) {
            c.set(Calendar.HOUR_OF_DAY, 7);
        } else {
            c.set(Calendar.HOUR_OF_DAY, 0);
        }

        Date today = c.getTime();
        c.add(Calendar.DATE, -1);
        Date oneDayAgo = c.getTime();
        c.add(Calendar.DATE, -6);
        Date sevenDaysAgo = c.getTime();
        c.add(Calendar.DATE, 8);
        Date tomorrow = c.getTime();
        c.add(Calendar.DATE, 18);

        Date tenDaysAgo = TimeUtil.addDays(sevenDaysAgo, -3);
        Date threeDaysAgo = TimeUtil.addDays(sevenDaysAgo, 4);
        Date currentShiftStart = TimeUtil.getCcShiftStart(now);
        Date currentShiftEnd = TimeUtil.getCcShiftEnd(now);
        Date dateInPreviousShift = TimeUtil.addHours(currentShiftStart, -1);
        Date previousShiftStart = TimeUtil.getCcShiftStart(dateInPreviousShift);
        Date previousShiftEnd = TimeUtil.getCcShiftEnd(dateInPreviousShift);
        Date currentWeekStart = TimeUtil.startOfWeek(today, Calendar.WEDNESDAY);
        Date currentWeekEnd = TimeUtil.addDays(currentWeekStart, 7);
        Date previousWeekStart = TimeUtil.addDays(currentWeekStart, -7);
        Date previousWeekEnd = TimeUtil.addDays(currentWeekEnd, -7);
        Date currentMonthStart = TimeUtil.startOfMonth(today, c);
        Date currentMonthEnd = TimeUtil.startOfNextMonth(today, c);
        Date previousMonthStart = TimeUtil.addMonths(currentMonthStart, -1);
        Date previousMonthEnd = currentMonthStart;
        Date currentYearStart = TimeUtil.startOfYear(today, c);
        Date currentYearEnd = TimeUtil.startOfNextYear(today, c);
        Date previousYearStart = TimeUtil.addYears(currentYearStart, -1);
        Date previousYearEnd = currentYearStart;
        Date currentFiscalYearStart = TimeUtil.startOfFiscalYear(today, c);
        Date currentFiscalYearEnd = TimeUtil.addYears(currentFiscalYearStart, 1);
        Date previousFiscalYearStart = TimeUtil.addYears(currentFiscalYearStart, -1);
        Date previousFiscalYearEnd = currentFiscalYearStart;
        Date currentFiscalYearQ1Start = currentFiscalYearStart;
        Date currentFiscalYearQ1End = TimeUtil.addMonths(currentFiscalYearStart, 3);
        Date currentFiscalYearQ2Start = currentFiscalYearQ1End;
        Date currentFiscalYearQ2End = TimeUtil.addMonths(currentFiscalYearQ1End, 3);
        Date currentFiscalYearQ3Start = currentFiscalYearQ2End;
        Date currentFiscalYearQ3End = TimeUtil.addMonths(currentFiscalYearQ2End, 3);
        Date currentFiscalYearQ4Start = currentFiscalYearQ3End;
        Date currentFiscalYearQ4End = TimeUtil.addMonths(currentFiscalYearQ3End, 3);
        Date previousFiscalYearQ1Start = previousFiscalYearStart;
        Date previousFiscalYearQ1End = TimeUtil.addMonths(previousFiscalYearStart, 3);
        Date previousFiscalYearQ2Start = previousFiscalYearQ1End;
        Date previousFiscalYearQ2End = TimeUtil.addMonths(previousFiscalYearQ1End, 3);
        Date previousFiscalYearQ3Start = previousFiscalYearQ2End;
        Date previousFiscalYearQ3End = TimeUtil.addMonths(previousFiscalYearQ2End, 3);
        Date previousFiscalYearQ4Start = previousFiscalYearQ3End;
        Date previousFiscalYearQ4End = TimeUtil.addMonths(previousFiscalYearQ3End, 3);

        String range = "custom";

        if (end.getTime() == currentShiftEnd.getTime() && start.getTime() == currentShiftStart.getTime()) {
            range = "0ccshift";
        } else if (end.getTime() == previousShiftEnd.getTime() && start.getTime() == previousShiftStart.getTime()) {
            range = "1ccshift";
        } else if (end.getTime() == tomorrow.getTime() && start.getTime() == today.getTime()) {
            range = "0day";
        } else if (end.getTime() == today.getTime() && start.getTime() == oneDayAgo.getTime()) {
            range = "1day";
        } else if (end.getTime() == currentWeekEnd.getTime() && start.getTime() == currentWeekStart.getTime()) {
            range = "0week";
        } else if (end.getTime() == previousWeekEnd.getTime() && start.getTime() == previousWeekStart.getTime()) {
            range = "1week";
        } else if (end.getTime() == currentMonthEnd.getTime() && start.getTime() == currentMonthStart.getTime()) {
            range = "0month";
        } else if (end.getTime() == previousMonthEnd.getTime() && start.getTime() == previousMonthStart.getTime()) {
            range = "1month";
        } else if (end.getTime() == currentYearEnd.getTime() && start.getTime() == currentYearStart.getTime()) {
            range = "0year";
        } else if (end.getTime() == previousYearEnd.getTime() && start.getTime() == previousYearStart.getTime()) {
            range = "1year";
        } else if (end.getTime() == currentFiscalYearEnd.getTime() && start.getTime() == currentFiscalYearStart.getTime()) {
            range = "0fiscalyear";
        } else if (end.getTime() == previousFiscalYearEnd.getTime() && start.getTime() == previousFiscalYearStart.getTime()) {
            range = "1fiscalyear";
        } else if (end.getTime() == previousFiscalYearQ1End.getTime() && start.getTime() == previousFiscalYearQ1Start.getTime()) {
            range = "1fiscalyearq1";
        } else if (end.getTime() == previousFiscalYearQ2End.getTime() && start.getTime() == previousFiscalYearQ2Start.getTime()) {
            range = "1fiscalyearq2";
        } else if (end.getTime() == previousFiscalYearQ3End.getTime() && start.getTime() == previousFiscalYearQ3Start.getTime()) {
            range = "1fiscalyearq3";
        } else if (end.getTime() == previousFiscalYearQ4End.getTime() && start.getTime() == previousFiscalYearQ4Start.getTime()) {
            range = "1fiscalyearq4";
        } else if (end.getTime() == currentFiscalYearQ1End.getTime() && start.getTime() == currentFiscalYearQ1Start.getTime()) {
            range = "0fiscalyearq1";
        } else if (end.getTime() == currentFiscalYearQ2End.getTime() && start.getTime() == currentFiscalYearQ2Start.getTime()) {
            range = "0fiscalyearq2";
        } else if (end.getTime() == currentFiscalYearQ3End.getTime() && start.getTime() == currentFiscalYearQ3Start.getTime()) {
            range = "0fiscalyearq3";
        } else if (end.getTime() == currentFiscalYearQ4End.getTime() && start.getTime() == currentFiscalYearQ4Start.getTime()) {
            range = "0fiscalyearq4";
        } else if (currentRun != null && currentRun.getEnd().getTime() == end.getTime() && currentRun.getStart().getTime() == start.getTime()) {
            range = "0run";
        } else if (previousRun != null && previousRun.getEnd().getTime() == end.getTime() && previousRun.getStart().getTime() == start.getTime()) {
            range = "1run";
        } else if (end.getTime() == today.getTime()) {
            if (start.getTime() == tenDaysAgo.getTime()) {
                range = "past10days";
            } else if (start.getTime() == sevenDaysAgo.getTime()) {
                range = "past7days";
            } else if (start.getTime() == threeDaysAgo.getTime()) {
                range = "past3days";
            }
        }

        return range;
    }

    /**
     * Return a formatted String representation of a date range using "smart human" shortcuts.  Specifically, any
     * time with zero minutes and zero hours are don't show hours and minutes (they're implied).  If the start and
     * end are in the same month or same year, then the month or year respectively is not shown more than once (it's
     * implied).
     *
     * @param start The start Date
     * @param end The end Date
     * @return The formatted date range
     */
    public static String formatSmartRangeSeparateTime(Date start, Date end) {
        SimpleDateFormat sFormat;
        SimpleDateFormat eFormat;
        boolean sameYear;
        boolean sameMonth;
        boolean sameDay;
        boolean firstDayOfMonth;
        boolean oneDaySpecialCase = false;
        boolean oneMonthSpecialCase = false;
        boolean oneYearSpecialCase = false;
        boolean fiscalYearSpecialCase = false;
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
                firstDayOfMonth = special.get(Calendar.DAY_OF_MONTH) == 1;
                oneMonthSpecialCase = (firstDayOfMonth && special.getTime().equals(end));

                if (!oneMonthSpecialCase && firstDayOfMonth) {
                    special.setTime(start);
                    special.add(Calendar.YEAR, 1);
                    oneYearSpecialCase = (special.get(Calendar.MONTH) == Calendar.JANUARY &&
                            special.getTime().equals(end));

                    if(!oneYearSpecialCase) {
                        fiscalYearSpecialCase = (special.get(Calendar.MONTH) == Calendar.OCTOBER &&
                                special.getTime().equals(end));
                    }
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
        } else if(fiscalYearSpecialCase) {
            String format = "'Fiscal Year 'yyyy";

            SimpleDateFormat formatter = new SimpleDateFormat(format);

            result = formatter.format(end);
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
    public static String getFriendlyDateTimePattern() {
        return "dd-MMM-yyyy HH:mm";
    }

    /**
     * This is what users see
     *
     * @return placeholder text
     */
    public static String getFriendlyDateTimePlaceholder() {
        return "DD-MMM-YYYY hh:mm";
    }

    /**
     * This is what is given to SimpleDateFormat
     *
     * @return format pattern
     */
    public static String getFriendlyDatePattern() {
        return "dd-MMM-yyyy";
    }

    /**
     * This is what users see
     *
     * @return placeholder text
     */
    public static String getFriendlyDatePlaceholder() {
        return "DD-MMM-YYYY";
    }

    /**
     * Check if "today/now" is currently Monday.
     *
     * @return true if Monday, false otherwise
     */
    public static boolean isMonday() {
        Calendar cal = Calendar.getInstance();

        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        return Calendar.MONDAY == dayOfWeek;
    }

    /**
     * Check if the first Date is in the same month as the second.
     *
     * @param first The first Date
     * @param second The second Date
     * @return true if in the same month, false otherwise
     */
    public static boolean isSameMonth(Date first, Date second) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(first);
        int firstMonth = cal.get(Calendar.MONTH);
        cal.setTime(second);
        int secondMonth = cal.get(Calendar.MONTH);
        return firstMonth == secondMonth;
    }

    /**
     * Check if a Date and timezone (Calendar) represent the first of a month.
     *
     * @param dayMonthYear The Date
     * @param tz The timezone (Calendar)
     * @return true if represents first of month, false otherwise
     */
    public static boolean isFirstOfMonth(Date dayMonthYear, Calendar tz) {
        return dayMonthYear.equals(TimeUtil.startOfMonth(dayMonthYear, tz));
    }

    /**
     * Return a Calendar in UTC/GMT.
     *
     * @return The Calendar
     */
    public static Calendar getUtcCalendar() {
        return Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    }
}
