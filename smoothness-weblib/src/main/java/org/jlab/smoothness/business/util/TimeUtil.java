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

    public static boolean isCrewChiefShiftStart(Date startDayAndHour) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(startDayAndHour);

        int startHourOfDay = cal.get(Calendar.HOUR_OF_DAY);

        return (startHourOfDay == 23 || startHourOfDay == 7 || startHourOfDay == 15);
    }

    public static boolean isExperimenterShiftStart(Date startDayAndHour) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(startDayAndHour);

        int startHourOfDay = cal.get(Calendar.HOUR_OF_DAY);

        return (startHourOfDay == 0 || startHourOfDay == 8 || startHourOfDay == 16);
    }

    public static Date previousCrewChiefShiftStart(Date currentStartDayAndHour) {
        Date previousDay = currentStartDayAndHour;

        Shift previousShift = TimeUtil.calculateCrewChiefShiftType(previousDay).getPrevious();

        return TimeUtil.getCrewChiefStartDayAndHour(previousDay, previousShift);
    }

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

    public static boolean isMonday() {
        Calendar cal = Calendar.getInstance();

        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        return Calendar.MONDAY == dayOfWeek;
    }

    public static boolean isSameMonth(Date first, Date second) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(first);
        int firstMonth = cal.get(Calendar.MONTH);
        cal.setTime(second);
        int secondMonth = cal.get(Calendar.MONTH);
        return firstMonth == secondMonth;
    }

    public static boolean isFirstOfMonth(Date dayMonthYear, Calendar tz) {
        return dayMonthYear.equals(TimeUtil.startOfMonth(dayMonthYear, tz));
    }

    public static Calendar getUtcCalendar() {
        return Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    }
}
