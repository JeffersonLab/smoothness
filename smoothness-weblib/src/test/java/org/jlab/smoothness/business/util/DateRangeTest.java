package org.jlab.smoothness.business.util;

import static org.jlab.smoothness.business.util.TimeUtilTest.date;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;
import org.junit.jupiter.api.Test;

class DateRangeTest {

  final Date march1 = date("2026-03-01 00:00");
  final Date march2 = date("2026-03-02 00:00");
  final Date march3 = date("2026-03-03 00:00");

  @Test
  void holdsStartAndEnd() {
    DateRange range = new DateRange(march1, march2);

    assertEquals(march1, range.getStart());
    assertEquals(march2, range.getEnd());
  }

  @Test
  void startMayEqualEnd() {
    DateRange range = new DateRange(march1, march1);

    assertEquals(range.getStart(), range.getEnd());
  }

  @Test
  void defaultIsNowToNow() {
    DateRange range = new DateRange();

    assertSame(range.getStart(), range.getEnd());
  }

  @Test
  void rejectsStartAfterEnd() {
    IllegalArgumentException e =
        assertThrows(IllegalArgumentException.class, () -> new DateRange(march2, march1));

    assertEquals("start cannot come after end", e.getMessage());
  }

  @Test
  void settersRejectStartAfterEnd() {
    DateRange range = new DateRange(march1, march2);

    assertThrows(IllegalArgumentException.class, () -> range.setStart(march3));
    assertThrows(
        IllegalArgumentException.class, () -> new DateRange(march2, march3).setEnd(march1));

    range.setEnd(march3);
    range.setStart(march2);
    assertEquals(march2, range.getStart());
    assertEquals(march3, range.getEnd());
  }
}
