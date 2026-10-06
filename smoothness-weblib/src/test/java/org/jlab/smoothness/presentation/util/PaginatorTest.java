package org.jlab.smoothness.presentation.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PaginatorTest {

  @Test
  void firstPage() {
    Paginator p = new Paginator(95, 0, 10);

    assertEquals(1, p.getStartNumber());
    assertEquals(10, p.getEndNumber());
    assertFalse(p.isPrevious());
    assertTrue(p.isNext());
    assertEquals(0, p.getPreviousOffset());
    assertEquals(10, p.getNextOffset());
    assertEquals(95, p.getTotalRecords());
    assertEquals(0, p.getOffset());
    assertEquals(10, p.getMaxPerPage());
  }

  @Test
  void middlePage() {
    Paginator p = new Paginator(95, 40, 10);

    assertEquals(41, p.getStartNumber());
    assertEquals(50, p.getEndNumber());
    assertTrue(p.isPrevious());
    assertTrue(p.isNext());
    assertEquals(30, p.getPreviousOffset());
    assertEquals(50, p.getNextOffset());
  }

  @Test
  void lastPage() {
    Paginator p = new Paginator(95, 90, 10);

    assertEquals(91, p.getStartNumber());
    assertEquals(95, p.getEndNumber());
    assertTrue(p.isPrevious());
    assertFalse(p.isNext());
    assertEquals(80, p.getPreviousOffset());
    // The next offset stops at the last record
    assertEquals(94, p.getNextOffset());
  }

  @Test
  void exactlyOnePage() {
    Paginator p = new Paginator(10, 0, 10);

    assertEquals(10, p.getEndNumber());
    assertFalse(p.isNext());
    assertEquals(9, p.getNextOffset());
  }

  @Test
  void offsetNotOnAPageBoundary() {
    Paginator p = new Paginator(95, 5, 10);

    assertEquals(6, p.getStartNumber());
    assertEquals(15, p.getEndNumber());
    assertEquals(0, p.getPreviousOffset());
  }

  @Test
  void noRecords() {
    Paginator p = new Paginator(0, 0, 10);

    assertEquals(0, p.getStartNumber());
    assertEquals(0, p.getEndNumber());
    assertFalse(p.isPrevious());
    assertFalse(p.isNext());
    assertEquals(-1, p.getNextOffset());
  }
}
