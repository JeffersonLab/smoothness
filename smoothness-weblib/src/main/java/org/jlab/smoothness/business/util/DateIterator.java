package org.jlab.smoothness.business.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator over a date range.
 * 
 * @author ryans
 */
public class DateIterator implements Iterator<Date>, Iterable<Date> {

    private final Calendar end = Calendar.getInstance();
    private final Calendar current = Calendar.getInstance();
    private int field;

    /**
     * Constructs a new DateIterator with the specified start and end date and
     * the default iteration resolution of {@link Calendar#DATE}.
     *
     * @param start the start date.
     * @param end the end date.
     */
    public DateIterator(Date start, Date end) {
        this(start, end, Calendar.DATE);
    }

    /**
     * Constructs a new DateIterator with the specified start date, end date,
     * and iteration resolution.
     *
     * The iteration resolution is specified using calendar field as defined in
     * {@link Calendar}.
     *
     * @param start the start date.
     * @param end the end date.
     * @param field the calendar field.
     */
    public DateIterator(Date start, Date end, int field) {
        this.field = field;
        this.end.setTime(end);
        this.end.add(field, -1);
        this.current.setTime(start);
        this.current.add(field, -1);
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
        if( !hasNext() ) {
            throw new NoSuchElementException();
        }

        current.add(field, 1);
        return current.getTime();
    }

    /**
     * Removes from the underlying collection the last element returned by the 
     * iterator (optional operation, which is NOT supported).
     * 
     * @throws UnsupportedOperationException if the operation isn't supported,
     * which is the always true in DateIterator.
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
