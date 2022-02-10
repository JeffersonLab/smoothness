package org.jlab.smoothness.presentation.util;

/**
 * A paginator utility.
 *
 * @author ryans
 */
public class Paginator {
    private final long offset;
    private final long maxPerPage;
    private final long totalRecords;

    /**
     * Create a new Paginator.
     *
     * @param totalRecords The total number of records
     * @param offset The current offset
     * @param maxPerPage The max number of records per page
     */
    public Paginator(long totalRecords, long offset, long maxPerPage) {
        this.totalRecords = totalRecords;
        this.offset = offset;
        this.maxPerPage = maxPerPage;
    }

    /**
     * Return the total number of records.
     *
     * @return The total number of records
     */
    public long getTotalRecords() {
        return totalRecords;
    }

    /**
     * Return the current offset.
     *
     * @return The offset
     */
    public long getOffset() {
        return offset;
    }

    /**
     * Return the max number of records per page.
     *
     * @return The max number of records per page
     */
    public long getMaxPerPage() {
        return maxPerPage;
    }

    /**
     * Return the start number (counting starts at one and offset starts at zero).
     *
     * @return The start number
     */
    public long getStartNumber() {
        long startNumber = offset + 1;
        
        if(startNumber > totalRecords) {
            startNumber = totalRecords;
        }
        
        return startNumber;
    }

    /**
     * Return the end number (counting starts at one and offset starts at zero).
     *
     * @return The end number
     */
    public long getEndNumber() {
        long endNumber = offset + maxPerPage;
        
        if(endNumber > totalRecords) {
            endNumber = totalRecords;
        }
        
        return endNumber;
    }

    /**
     * Check if there is a previous page.
     *
     * @return true if a previous page exists
     */
    public boolean isPrevious() {
        boolean previous = offset > 0;

        return previous;
    }

    /**
     * Check if there is a next page.
     *
     * @return true if a next page exists
     */
    public boolean isNext() {
        boolean next = totalRecords > offset + maxPerPage;

        return next;
    }

    /**
     * Return the previous offset.
     *
     * @return The previous offset
     */
    public long getPreviousOffset() {
        long previousOffset = offset - maxPerPage;
        
        if(previousOffset < 0) {
            previousOffset = 0;
        }
        
        return previousOffset;
    }

    /**
     * Return the next offset.
     *
     * @return The next offset
     */
    public long getNextOffset() {        
        long nextOffset = offset + maxPerPage;
        
        if(nextOffset > (totalRecords - 1)) {
            nextOffset = totalRecords - 1;
        }        
        
        return nextOffset;
    }
}
