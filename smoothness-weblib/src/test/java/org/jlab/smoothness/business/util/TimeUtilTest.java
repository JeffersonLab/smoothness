package org.jlab.smoothness.business.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import org.jlab.smoothness.persistence.enumeration.Shift;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Characterization tests: they record what TimeUtil does now, including its quirks. The build runs
 * them in America/New_York (see build.gradle), as TimeUtil uses the JVM's default time zone.
 */
class TimeUtilTest {

  /** A local date and time, such as "2026-03-10 07:00", in the JVM's default time zone. */
  static Date date(String dateTime) {
    return Date.from(
        LocalDateTime.parse(dateTime.replace(' ', 'T')).atZone(ZoneId.systemDefault()).toInstant());
  }

  @Test
  void runsInNewYork() {
    assertEquals("America/New_York", ZoneId.systemDefault().getId());
  }

  // Crew chief shifts: owl 23:00-07:00 (starting the evening before its day), day 07:00-15:00,
  // swing 15:00-23:00.

  @Test
  void getCcShiftStart() {
    assertEquals(date("2026-03-10 23:00"), TimeUtil.getCcShiftStart(date("2026-03-10 23:30")));
    assertEquals(date("2026-03-09 23:00"), TimeUtil.getCcShiftStart(date("2026-03-10 00:00")));
    assertEquals(date("2026-03-09 23:00"), TimeUtil.getCcShiftStart(date("2026-03-10 06:59")));
    assertEquals(date("2026-03-10 07:00"), TimeUtil.getCcShiftStart(date("2026-03-10 07:00")));
    assertEquals(date("2026-03-10 07:00"), TimeUtil.getCcShiftStart(date("2026-03-10 14:59")));
    assertEquals(date("2026-03-10 15:00"), TimeUtil.getCcShiftStart(date("2026-03-10 15:00")));
    assertEquals(date("2026-03-10 15:00"), TimeUtil.getCcShiftStart(date("2026-03-10 22:59")));
  }

  @Test
  void getCcShiftEndIsTheNextShiftsStart() {
    assertEquals(date("2026-03-11 07:00"), TimeUtil.getCcShiftEnd(date("2026-03-10 23:30")));
    assertEquals(date("2026-03-10 07:00"), TimeUtil.getCcShiftEnd(date("2026-03-10 03:15")));
    assertEquals(date("2026-03-10 15:00"), TimeUtil.getCcShiftEnd(date("2026-03-10 07:00")));
    assertEquals(date("2026-03-10 23:00"), TimeUtil.getCcShiftEnd(date("2026-03-10 15:00")));
  }

  @ParameterizedTest
  @ValueSource(
      ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23})
  void shiftOfEachHour(int hour) {
    Shift expected = (hour <= 6 || hour == 23) ? Shift.OWL : (hour <= 14 ? Shift.DAY : Shift.SWING);
    Date dayAndHour = date(String.format("2026-03-10 %02d:30", hour));

    assertEquals(expected, TimeUtil.calculateCrewChiefShiftType(dayAndHour));
    assertEquals(expected, TimeUtil.calculateCrewChiefShift(dayAndHour));
    assertEquals(expected, Shift.getCcShiftFromDate(dayAndHour));
    assertEquals(hour == 23 || hour == 7 || hour == 15, TimeUtil.isCrewChiefShiftStart(dayAndHour));
    assertEquals(
        hour == 23 || hour == 7 || hour == 15, TimeUtil.isFirstHourOfCrewChiefShift(dayAndHour));
    assertEquals(
        hour == 0 || hour == 8 || hour == 16, TimeUtil.isExperimenterShiftStart(dayAndHour));
  }

  @Test
  void getCrewChiefStartDayAndHour() {
    Date day = date("2026-03-10 00:00");

    assertEquals(date("2026-03-09 23:00"), TimeUtil.getCrewChiefStartDayAndHour(day, Shift.OWL));
    assertEquals(date("2026-03-10 07:00"), TimeUtil.getCrewChiefStartDayAndHour(day, Shift.DAY));
    assertEquals(date("2026-03-10 15:00"), TimeUtil.getCrewChiefStartDayAndHour(day, Shift.SWING));
  }

  @Test
  void getCrewChiefDayAndHourIsAWholeHour() {
    Date dayWithMinutes = date("2026-03-10 00:45");

    assertEquals(
        date("2026-03-10 07:00"), TimeUtil.getCrewChiefStartDayAndHour(dayWithMinutes, Shift.DAY));
    assertEquals(
        date("2026-03-10 14:00"), TimeUtil.getCrewChiefEndDayAndHour(dayWithMinutes, Shift.DAY));
  }

  @Test
  void getCrewChiefEndDayAndHourIsTheLastHourOnTheShiftsDay() {
    Date day = date("2026-03-10 00:00");

    assertEquals(date("2026-03-10 06:00"), TimeUtil.getCrewChiefEndDayAndHour(day, Shift.OWL));
    assertEquals(date("2026-03-10 14:00"), TimeUtil.getCrewChiefEndDayAndHour(day, Shift.DAY));
    assertEquals(date("2026-03-10 22:00"), TimeUtil.getCrewChiefEndDayAndHour(day, Shift.SWING));
  }

  @Test
  void previousAndNextCrewChiefShiftStart() {
    Date owl = date("2026-03-09 23:00");
    Date day = date("2026-03-10 07:00");
    Date swing = date("2026-03-10 15:00");
    Date nextOwl = date("2026-03-10 23:00");

    assertEquals(day, TimeUtil.nextCrewChiefShiftStart(owl));
    assertEquals(swing, TimeUtil.nextCrewChiefShiftStart(day));
    assertEquals(nextOwl, TimeUtil.nextCrewChiefShiftStart(swing));

    assertEquals(swing, TimeUtil.previousCrewChiefShiftStart(nextOwl));
    assertEquals(day, TimeUtil.previousCrewChiefShiftStart(swing));
    assertEquals(owl, TimeUtil.previousCrewChiefShiftStart(day));
  }

  @Test
  void calculateCrewChiefShiftEndDayAndHourIsTheLastHour() {
    assertEquals(
        date("2026-03-10 06:00"),
        TimeUtil.calculateCrewChiefShiftEndDayAndHour(date("2026-03-09 23:00")));
    assertEquals(
        date("2026-03-10 14:00"),
        TimeUtil.calculateCrewChiefShiftEndDayAndHour(date("2026-03-10 07:00")));
    assertEquals(
        date("2026-03-10 22:00"),
        TimeUtil.calculateCrewChiefShiftEndDayAndHour(date("2026-03-10 15:00")));
  }

  @Test
  void calculateExperimenterShiftEndDayAndHour() {
    assertEquals(
        date("2026-03-10 07:00"),
        TimeUtil.calculateExperimenterShiftEndDayAndHour(date("2026-03-10 00:00")));
    assertEquals(
        date("2026-03-10 15:00"),
        TimeUtil.calculateExperimenterShiftEndDayAndHour(date("2026-03-10 08:00")));
    assertEquals(
        date("2026-03-10 23:00"),
        TimeUtil.calculateExperimenterShiftEndDayAndHour(date("2026-03-10 16:00")));
  }

  @Test
  void getCurrentCrewChiefShiftDayMovesTo23hToTheNextDay() {
    assertEquals(
        date("2026-03-09 00:00"), TimeUtil.getCurrentCrewChiefShiftDay(date("2026-03-09 22:59")));
    assertEquals(
        date("2026-03-10 00:00"), TimeUtil.getCurrentCrewChiefShiftDay(date("2026-03-09 23:00")));
  }

  @Test
  void owlShiftOverTheSpringDaylightSavingChangeIsSevenHoursLong() {
    // Clocks went from 02:00 to 03:00 on 2026-03-08
    Date start = TimeUtil.getCcShiftStart(date("2026-03-08 03:30"));

    assertEquals(date("2026-03-07 23:00"), start);
    assertEquals(date("2026-03-08 07:00"), TimeUtil.getCcShiftEnd(start));
    assertEquals(7, TimeUtil.differenceInHours(start, TimeUtil.getCcShiftEnd(start)));
  }

  // Date arithmetic

  @Test
  void addKeepsTheWallClockTimeAcrossDaylightSaving() {
    Date noon = date("2026-03-07 12:00");

    assertEquals(date("2026-03-08 12:00"), TimeUtil.addDays(noon, 1));
    assertEquals(23, TimeUtil.differenceInHours(noon, TimeUtil.addDays(noon, 1)));
    assertEquals(date("2026-03-07 15:00"), TimeUtil.addHours(noon, 3));
    assertEquals(date("2026-03-06 12:00"), TimeUtil.addDays(noon, -1));
  }

  @Test
  void addMonthsAndYearsClampToTheEndOfShorterMonths() {
    assertEquals(date("2026-02-28 00:00"), TimeUtil.addMonths(date("2026-01-31 00:00"), 1));
    assertEquals(date("2027-02-28 00:00"), TimeUtil.addYears(date("2028-02-29 00:00"), -1));
    assertEquals(date("2026-03-14 00:00"), TimeUtil.calculateWeekEndDate(date("2026-03-07 00:00")));
    assertEquals(date("2027-03-07 00:00"), TimeUtil.calculateYearEndDate(date("2026-03-07 00:00")));
  }

  @Test
  void differenceInHoursIsAbsoluteAndTruncated() {
    Date start = date("2026-03-10 07:00");

    assertEquals(1, TimeUtil.differenceInHours(start, date("2026-03-10 08:59")));
    assertEquals(1, TimeUtil.differenceInHours(date("2026-03-10 08:59"), start));
    assertEquals(0, TimeUtil.differenceInHours(start, start));
  }

  @Test
  void countMonthsInclusive() {
    assertEquals(
        3, TimeUtil.countMonthsInclusive(date("2026-01-15 00:00"), date("2026-03-15 00:00")));
    assertEquals(
        1, TimeUtil.countMonthsInclusive(date("2026-01-01 00:00"), date("2026-01-01 00:00")));
  }

  @Test
  void startOfPeriods() {
    Date date = date("2026-03-10 13:45");

    assertEquals(date("2026-01-01 00:00"), TimeUtil.startOfYear(date, Calendar.getInstance()));
    assertEquals(date("2027-01-01 00:00"), TimeUtil.startOfNextYear(date, Calendar.getInstance()));
    assertEquals(date("2026-03-01 00:00"), TimeUtil.startOfMonth(date, Calendar.getInstance()));
    assertEquals(date("2026-04-01 00:00"), TimeUtil.startOfNextMonth(date, Calendar.getInstance()));
    assertEquals(date("2026-03-31 00:00"), TimeUtil.endOfMonth(date, Calendar.getInstance()));
    assertEquals(date("2026-03-10 00:00"), TimeUtil.startOfDay(date, Calendar.getInstance()));
    assertEquals(date("2026-03-11 00:00"), TimeUtil.startOfNextDay(date, Calendar.getInstance()));
    assertEquals(date("2026-03-10 13:00"), TimeUtil.startOfHour(date, Calendar.getInstance()));
    assertEquals(date("2026-03-10 13:00"), TimeUtil.truncateToHour(date));
  }

  @Test
  void endOfMonthIsTheStartOfItsLastDay() {
    assertEquals(
        date("2026-02-28 00:00"),
        TimeUtil.endOfMonth(date("2026-02-15 13:00"), Calendar.getInstance()));
    assertEquals(
        date("2028-02-29 00:00"),
        TimeUtil.endOfMonth(date("2028-02-01 00:00"), Calendar.getInstance()));
  }

  @Test
  void startOfFiscalYearIsTheLatestOctoberFirst() {
    assertEquals(
        date("2025-10-01 00:00"),
        TimeUtil.startOfFiscalYear(date("2026-09-30 23:59"), Calendar.getInstance()));
    assertEquals(
        date("2026-10-01 00:00"),
        TimeUtil.startOfFiscalYear(date("2026-10-01 00:00"), Calendar.getInstance()));
    assertEquals(
        date("2026-10-01 00:00"),
        TimeUtil.startOfFiscalYear(date("2026-12-31 12:00"), Calendar.getInstance()));
  }

  @Test
  void startOfPeriodsLeaveTheCalendarPassedInAlone() {
    Calendar tz = TimeUtil.getUtcCalendar();
    Date before = tz.getTime();

    TimeUtil.startOfMonth(date("2026-03-10 13:45"), tz);
    TimeUtil.startOfYear(date("2026-03-10 13:45"), tz);
    TimeUtil.endOfMonth(date("2026-03-10 13:45"), tz);

    assertEquals(before, tz.getTime());
  }

  @Test
  void startOfPeriodsUseTheCalendarsTimeZone() {
    assertEquals(
        date("2026-02-28 19:00"),
        TimeUtil.startOfMonth(date("2026-03-10 13:45"), TimeUtil.getUtcCalendar()));
  }

  @Test
  void startOfWeek() {
    // 2026-10-07 is a Wednesday
    assertEquals(
        date("2026-10-07 10:00"),
        TimeUtil.startOfWeek(date("2026-10-08 10:00"), Calendar.WEDNESDAY));
    assertEquals(
        date("2026-09-30 10:00"),
        TimeUtil.startOfWeek(date("2026-10-06 10:00"), Calendar.WEDNESDAY));
  }

  @Test
  void startOfWeekOnTheStartDayIsThatDay() {
    assertEquals(
        date("2026-10-07 10:00"),
        TimeUtil.startOfWeek(date("2026-10-07 10:00"), Calendar.WEDNESDAY));
    assertEquals(
        date("2026-10-04 00:00"), TimeUtil.startOfWeek(date("2026-10-04 00:00"), Calendar.SUNDAY));
  }

  @Test
  void roundToNearestHour() {
    assertEquals(date("2026-03-10 10:00"), TimeUtil.roundToNearestHour(date("2026-03-10 10:29")));
    assertEquals(date("2026-03-10 11:00"), TimeUtil.roundToNearestHour(date("2026-03-10 10:30")));
    assertEquals(date("2026-03-11 00:00"), TimeUtil.roundToNearestHour(date("2026-03-10 23:45")));
  }

  @Test
  void convertUnixTimestampToDate() {
    assertEquals(new Date(1_000), TimeUtil.convertUNIXTimestampToDate(1));
  }

  @Test
  void withinLastWeek() {
    assertTrue(TimeUtil.withinLastWeek(new Date()));
    assertTrue(TimeUtil.withinLastWeek(TimeUtil.addDays(new Date(), -6)));
    assertFalse(TimeUtil.withinLastWeek(TimeUtil.addDays(new Date(), -8)));
  }

  @Test
  void isSameMonth() {
    assertTrue(TimeUtil.isSameMonth(date("2026-03-01 00:00"), date("2026-03-31 23:59")));
    assertFalse(TimeUtil.isSameMonth(date("2026-03-31 23:59"), date("2026-04-01 00:00")));
    assertFalse(TimeUtil.isSameMonth(date("2025-03-10 00:00"), date("2026-03-10 00:00")));
  }

  @Test
  void isFirstOfMonth() {
    assertTrue(TimeUtil.isFirstOfMonth(date("2026-03-01 00:00"), Calendar.getInstance()));
    assertFalse(TimeUtil.isFirstOfMonth(date("2026-03-01 07:00"), Calendar.getInstance()));
    assertFalse(TimeUtil.isFirstOfMonth(date("2026-03-02 00:00"), Calendar.getInstance()));
  }

  @Test
  void getUtcCalendar() {
    assertEquals("GMT", TimeUtil.getUtcCalendar().getTimeZone().getID());
  }

  // Formatting

  @Test
  void friendlyPatternsAndPlaceholders() {
    assertEquals("dd-MMM-yyyy HH:mm", TimeUtil.getFriendlyDateTimePattern());
    assertEquals("DD-MMM-YYYY hh:mm", TimeUtil.getFriendlyDateTimePlaceholder());
    assertEquals("dd-MMM-yyyy", TimeUtil.getFriendlyDatePattern());
    assertEquals("DD-MMM-YYYY", TimeUtil.getFriendlyDatePlaceholder());
  }

  @Test
  void formatDatabaseDateTimeTz() {
    assertEquals("2026-03-10 07 EDT", TimeUtil.formatDatabaseDateTimeTZ(date("2026-03-10 07:30")));
    assertEquals("2026-01-10 07 EST", TimeUtil.formatDatabaseDateTimeTZ(date("2026-01-10 07:30")));
  }

  @Test
  void formatMonthInterval() {
    assertEquals(
        "January 2026 - March 2026",
        TimeUtil.formatMonthInterval(date("2026-01-15 00:00"), date("2026-03-01 00:00")));
  }

  @Test
  void formatSmartSingleTimeLeavesOutMidnight() {
    assertEquals("March 10, 2026", TimeUtil.formatSmartSingleTime(date("2026-03-10 00:00")));
    assertEquals("March 10, 2026 07:05", TimeUtil.formatSmartSingleTime(date("2026-03-10 07:05")));
  }

  @Test
  void formatSmartRangeSeparateTimeNamesWholePeriods() {
    assertEquals(
        "March 10, 2026",
        TimeUtil.formatSmartRangeSeparateTime(date("2026-03-10 00:00"), date("2026-03-11 00:00")));
    assertEquals(
        "March 2026",
        TimeUtil.formatSmartRangeSeparateTime(date("2026-03-01 00:00"), date("2026-04-01 00:00")));
    assertEquals(
        "2026",
        TimeUtil.formatSmartRangeSeparateTime(date("2026-01-01 00:00"), date("2027-01-01 00:00")));
    assertEquals(
        "Fiscal Year 2026",
        TimeUtil.formatSmartRangeSeparateTime(date("2025-10-01 00:00"), date("2026-10-01 00:00")));
  }

  @Test
  void formatSmartRangeSeparateTimeLeavesOutRepeatedMonthsAndYears() {
    assertEquals(
        "March 2 - 5, 2026",
        TimeUtil.formatSmartRangeSeparateTime(date("2026-03-02 00:00"), date("2026-03-05 00:00")));
    assertEquals(
        "March 2 - April 5, 2026",
        TimeUtil.formatSmartRangeSeparateTime(date("2026-03-02 00:00"), date("2026-04-05 00:00")));
    assertEquals(
        "December 30, 2025 - January 2, 2026",
        TimeUtil.formatSmartRangeSeparateTime(date("2025-12-30 00:00"), date("2026-01-02 00:00")));
    assertEquals(
        "April 1, 2026 - April 1, 2027",
        TimeUtil.formatSmartRangeSeparateTime(date("2026-04-01 00:00"), date("2027-04-01 00:00")));
  }

  @Test
  void formatSmartRangeSeparateTimeAddsTimesUnlessBothAreMidnight() {
    assertEquals(
        "March 2, 2026 (07:00 - 15:00)",
        TimeUtil.formatSmartRangeSeparateTime(date("2026-03-02 07:00"), date("2026-03-02 15:00")));
    assertEquals(
        "March 2 - 3, 2026 (00:00 - 07:00)",
        TimeUtil.formatSmartRangeSeparateTime(date("2026-03-02 00:00"), date("2026-03-03 07:00")));
  }

  // Named ranges

  @Test
  void encodeRangeOfAnOrdinaryRangeIsCustom() {
    assertEquals(
        "custom",
        TimeUtil.encodeRange(
            date("2001-03-02 00:00"), date("2001-03-05 00:00"), false, null, null));
  }

  @Test
  void encodeRangeMatchesTheRunsLookedUp() {
    DateRange current = new DateRange(date("2001-03-02 00:00"), date("2001-03-05 00:00"));
    DateRange previous = new DateRange(date("2001-01-02 00:00"), date("2001-01-05 00:00"));

    assertEquals(
        "0run",
        TimeUtil.encodeRange(current.getStart(), current.getEnd(), false, current, previous));
    assertEquals(
        "1run",
        TimeUtil.encodeRange(previous.getStart(), previous.getEnd(), false, current, previous));
  }

  @Test
  void encodeRangeMatchesRangesRelativeToToday() {
    Date today = TimeUtil.startOfDay(new Date(), Calendar.getInstance());
    Date monthStart = TimeUtil.startOfMonth(today, Calendar.getInstance());
    Date yearStart = TimeUtil.startOfYear(today, Calendar.getInstance());

    assertEquals(
        "0day", TimeUtil.encodeRange(today, TimeUtil.addDays(today, 1), false, null, null));
    assertEquals(
        "1day", TimeUtil.encodeRange(TimeUtil.addDays(today, -1), today, false, null, null));
    assertEquals(
        "0month",
        TimeUtil.encodeRange(monthStart, TimeUtil.addMonths(monthStart, 1), false, null, null));
    assertEquals(
        "1year",
        TimeUtil.encodeRange(TimeUtil.addYears(yearStart, -1), yearStart, false, null, null));
    assertEquals(
        "past3days", TimeUtil.encodeRange(TimeUtil.addDays(today, -3), today, false, null, null));
    assertEquals(
        "past10days", TimeUtil.encodeRange(TimeUtil.addDays(today, -10), today, false, null, null));
  }

  @Test
  void encodeRangeOfThePastSevenDaysIsLastWeekOnWednesdays() {
    Date today = TimeUtil.startOfDay(new Date(), Calendar.getInstance());
    boolean wednesday = TimeUtil.startOfWeek(today, Calendar.WEDNESDAY).equals(today);

    // Weeks start on Wednesday, and last week is checked before the past seven days.
    assertEquals(
        wednesday ? "1week" : "past7days",
        TimeUtil.encodeRange(TimeUtil.addDays(today, -7), today, false, null, null));
  }

  @Test
  void encodeRangeMatchesTheWeeksFromWednesday() {
    Date today = TimeUtil.startOfDay(new Date(), Calendar.getInstance());
    Date weekStart = TimeUtil.startOfWeek(today, Calendar.WEDNESDAY);

    assertEquals(
        "0week",
        TimeUtil.encodeRange(weekStart, TimeUtil.addDays(weekStart, 7), false, null, null));
    assertEquals(
        "1week",
        TimeUtil.encodeRange(TimeUtil.addDays(weekStart, -7), weekStart, false, null, null));
  }

  @Test
  void encodeRangeWithSevenAmDaysStartsDaysAt7() {
    Date today = TimeUtil.addHours(TimeUtil.startOfDay(new Date(), Calendar.getInstance()), 7);

    assertEquals("0day", TimeUtil.encodeRange(today, TimeUtil.addDays(today, 1), true, null, null));
    assertEquals(
        "custom", TimeUtil.encodeRange(today, TimeUtil.addDays(today, 1), false, null, null));
  }
}
