package org.jlab.smoothness.persistence.enumeration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class EnumerationTest {

  @Test
  void shiftsCycleOwlDaySwing() {
    assertSame(Shift.DAY, Shift.OWL.getNext());
    assertSame(Shift.SWING, Shift.DAY.getNext());
    assertSame(Shift.OWL, Shift.SWING.getNext());

    assertSame(Shift.SWING, Shift.OWL.getPrevious());
    assertSame(Shift.OWL, Shift.DAY.getPrevious());
    assertSame(Shift.DAY, Shift.SWING.getPrevious());
  }

  @Test
  void shiftLabels() {
    assertEquals("Owl", Shift.OWL.getLabel());
    assertEquals("Day", Shift.DAY.getLabel());
    assertEquals("Swing", Shift.SWING.getLabel());
  }

  @Test
  void hallLetters() {
    for (Hall hall : Hall.values()) {
      assertEquals(hall.name(), hall.getLabel());
      assertEquals(hall.name().charAt(0), hall.getLetter());
    }
    assertEquals(4, Hall.values().length);
  }
}
