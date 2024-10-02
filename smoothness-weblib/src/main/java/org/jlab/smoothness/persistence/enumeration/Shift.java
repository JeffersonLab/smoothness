package org.jlab.smoothness.persistence.enumeration;

import java.util.Calendar;
import java.util.Date;

/**
 * Represents one of the three shifts used at Jefferson Lab by experimenters and operators.
 *
 * @author ryans
 */
public enum Shift {
  /** OWL */
  OWL("Owl"),
  /** DAY */
  DAY("Day"),
  /** SWING */
  SWING("Swing");

  private final String label;

  /**
   * Create a new shift with the given label.
   *
   * @param label The label
   */
  Shift(String label) {
    this.label = label;
  }

  /**
   * Return the shift label.
   *
   * @return The label
   */
  public String getLabel() {
    return label;
  }

  /**
   * Return the next shift.
   *
   * @return The next shift
   */
  public Shift getNext() {
    return this.ordinal() < Shift.values().length - 1
        ? Shift.values()[this.ordinal() + 1]
        : Shift.values()[0];
  }

  /**
   * Return the previous shift.
   *
   * @return The previous shift
   */
  public Shift getPrevious() {
    return this.ordinal() > 0
        ? Shift.values()[this.ordinal() - 1]
        : Shift.values()[Shift.values().length - 1];
  }

  /**
   * Return the shift that encompasses the provided Date.
   *
   * @param dateInShift The date
   * @return The Shift
   */
  public static Shift getCcShiftFromDate(Date dateInShift) {
    Shift shift;

    Calendar cal = Calendar.getInstance();

    cal.setTime(dateInShift);

    int hour = cal.get(Calendar.HOUR_OF_DAY);

    if (hour == 23) {
      shift = Shift.OWL;
    } else if (hour <= 6) {
      shift = Shift.OWL;
    } else if (hour <= 14) {
      shift = Shift.DAY;
    } else {
      shift = Shift.SWING;
    }

    return shift;
  }
}
