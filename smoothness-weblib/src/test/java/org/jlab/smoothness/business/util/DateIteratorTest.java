package org.jlab.smoothness.business.util;

import static org.jlab.smoothness.business.util.TimeUtilTest.date;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class DateIteratorTest {

  static List<Date> toList(DateIterator iterator) {
    List<Date> dates = new ArrayList<>();
    for (Date d : iterator) {
      dates.add(d);
    }
    return dates;
  }

  @Test
  void iteratesDaysIncludingTheEnd() {
    DateIterator iterator = new DateIterator(date("2026-01-01 00:00"), date("2026-01-03 00:00"));

    assertEquals(Calendar.DATE, iterator.getField());
    assertEquals(
        List.of(date("2026-01-01 00:00"), date("2026-01-02 00:00"), date("2026-01-03 00:00")),
        toList(iterator));
  }

  @Test
  void iteratesMonths() {
    assertEquals(
        List.of(date("2026-01-15 00:00"), date("2026-02-15 00:00"), date("2026-03-15 00:00")),
        toList(
            new DateIterator(date("2026-01-15 00:00"), date("2026-03-15 00:00"), Calendar.MONTH)));
  }

  @ParameterizedTest
  @CsvSource({"2026-03-31 12:00, 2", "2024-02-29 12:00, 1"})
  void sameMonthEndOrLeapDayEndpointsGiveExactlyTheSuppliedDate(String endpoint, int field) {
    Date start = date(endpoint);
    DateIterator iterator = new DateIterator(start, start, field);

    assertEquals(List.of(start), toList(iterator));
    assertFalse(iterator.hasNext());
    assertThrows(NoSuchElementException.class, iterator::next);
  }

  @Test
  void monthEndStepsClipFromThePreviousElement() {
    assertEquals(
        List.of(
            date("2026-03-31 12:00"),
            date("2026-04-30 12:00"),
            date("2026-05-30 12:00"),
            date("2026-06-30 12:00")),
        toList(
            new DateIterator(date("2026-03-31 12:00"), date("2026-06-30 12:00"), Calendar.MONTH)));
  }

  @Test
  void monthEndStepsStopBeforeAnUnalignedUpperBound() {
    assertEquals(
        List.of(date("2026-03-31 12:00")),
        toList(
            new DateIterator(date("2026-03-31 12:00"), date("2026-04-29 12:00"), Calendar.MONTH)));
  }

  @Test
  void leapDayYearlyStepsClipFromThePreviousElement() {
    assertEquals(
        List.of(
            date("2024-02-29 12:00"),
            date("2025-02-28 12:00"),
            date("2026-02-28 12:00"),
            date("2027-02-28 12:00"),
            date("2028-02-28 12:00")),
        toList(
            new DateIterator(date("2024-02-29 12:00"), date("2028-02-29 12:00"), Calendar.YEAR)));
  }

  @Test
  void yearlyStepsStopBeforeAnUnalignedUpperBound() {
    assertEquals(
        List.of(date("2024-02-29 12:00")),
        toList(
            new DateIterator(date("2024-02-29 12:00"), date("2025-02-27 12:00"), Calendar.YEAR)));
  }

  @ParameterizedTest
  @CsvSource({"2026-03-31 12:00, 2026-03-30 12:00, 2", "2024-02-29 12:00, 2024-02-28 12:00, 1"})
  void reversedMonthEndAndLeapDayRangesAreEmpty(String start, String end, int field) {
    DateIterator iterator = new DateIterator(date(start), date(end), field);

    assertFalse(iterator.hasNext());
    assertThrows(NoSuchElementException.class, iterator::next);
  }

  @ParameterizedTest
  @CsvSource({
    "2026-03-07 12:00, 2026-03-08 12:00, 2026-03-09 12:00",
    "2026-10-31 12:00, 2026-11-01 12:00, 2026-11-02 12:00"
  })
  void dailyStepsKeepLocalTimeAcrossDaylightSavingChanges(String start, String middle, String end) {
    assertEquals(
        List.of(date(start), date(middle), date(end)),
        toList(new DateIterator(date(start), date(end))));
  }

  @Test
  void sameStartAndEndGivesOneDate() {
    assertEquals(
        List.of(date("2026-01-01 00:00")),
        toList(new DateIterator(date("2026-01-01 00:00"), date("2026-01-01 00:00"))));
  }

  @Test
  void startAfterEndGivesNone() {
    assertEquals(
        List.of(), toList(new DateIterator(date("2026-01-05 00:00"), date("2026-01-01 00:00"))));
  }

  @Test
  void nextAfterTheEndThrows() {
    DateIterator iterator = new DateIterator(date("2026-01-01 00:00"), date("2026-01-01 00:00"));
    iterator.next();

    assertFalse(iterator.hasNext());
    assertThrows(NoSuchElementException.class, iterator::next);
  }

  @Test
  void removeIsUnsupported() {
    DateIterator iterator = new DateIterator(date("2026-01-01 00:00"), date("2026-01-02 00:00"));

    assertSame(iterator, iterator.iterator());
    assertThrows(UnsupportedOperationException.class, iterator::remove);
  }
}
