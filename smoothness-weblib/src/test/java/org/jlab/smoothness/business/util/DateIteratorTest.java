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
