package org.jlab.smoothness.business.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator over an inclusive date range.
 *
 * @author ryans
 */
public class DateIterator implements Iterator<Date>, Iterable<Date> {

  private final Calendar end = Calendar.getInstance();
  private final Calendar current = Calendar.getInstance();
  private final int field;

  /**
   * Constructs a new DateIterator with the specified start and end date and the default iteration
   * resolution of {@link Calendar#DATE}.
   *
   * @param start the start date.
   * @param end the end date.
   */
  public DateIterator(Date start, Date end) {
    this(start, end, Calendar.DATE);
  }

  /**
   * Constructs a new DateIterator with the specified start date, end date, and iteration
   * resolution.
   *
   * <p>The iteration resolution is specified using calendar field as defined in {@link Calendar}.
   * The first element is the supplied start date, unless it is after the end date. Each subsequent
   * element is computed by adding one field to the previous element using {@link Calendar#add(int,
   * int)} in the default time zone; only elements at or before the supplied end date are returned.
   * Month and year steps may clip the day to fit the target month, and later steps use that clipped
   * day. For example, monthly iteration from March 31 yields April 30, then May 30; yearly
   * iteration from February 29 yields February 28 in subsequent years, including the next leap
   * year.
   *
   * @param start the start date.
   * @param end the end date.
   * @param field the calendar field.
   */
  public DateIterator(Date start, Date end, int field) {
    this.field = field;
    this.end.setTime(end);
    this.current.setTime(start);
  }

  /**
   * Get the {@link Calendar} field used as the iterator resolution.
   *
   * @return the calendar field.
   */
  public int getField() {
    return field;
  }

  /**
   * Returns true if the iteration has more elements.
   *
   * @return true if the iterator has more elements.
   */
  @Override
  public boolean hasNext() {
    return !current.after(end);
  }

  /**
   * Returns the next element in the iteration.
   *
   * @return the next element in the iteration.
   * @throws NoSuchElementException if iteration has no more elements.
   */
  @Override
  public Date next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }

    Date result = current.getTime();
    current.add(field, 1);
    return result;
  }

  /**
   * Removes from the underlying collection the last element returned by the iterator (optional
   * operation, which is NOT supported).
   *
   * @throws UnsupportedOperationException if the operation isn't supported, which is the always
   *     true in DateIterator.
   */
  @Override
  public void remove() {
    throw new UnsupportedOperationException("Cannot Remove");
  }

  /**
   * Returns an iterator over a set of elements of type Date.
   *
   * @return an iterator.
   */
  @Override
  public Iterator<Date> iterator() {
    return this;
  }
}
