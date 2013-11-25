package org.jlab.webapp.presentation.util;

/**
 *
 * @author ryans
 */
public class Paginator {
    private int offset;
    private int maxPerPage;
    private int totalRecords;
    
    public Paginator(int totalRecords, int offset, int maxPerPage) {
        this.totalRecords = totalRecords;
        this.offset = offset;
        this.maxPerPage = maxPerPage;
    }
    
    public int getTotalRecords() {
        return totalRecords;
    }
    
    public int getOffset() {
        return offset;
    }
    
    public int getMaxPerPage() {
        return maxPerPage;
    }
    
    public int getStartNumber() {
        int startNumber = offset + 1;
        
        if(startNumber > totalRecords) {
            startNumber = totalRecords;
        }
        
        return startNumber;
    }
    
    public int getEndNumber() {
        int endNumber = offset + maxPerPage;
        
        if(endNumber > totalRecords) {
            endNumber = totalRecords;
        }
        
        return endNumber;
    }
    
    public boolean isPrevious() {
        boolean previous = false;
        
        if(offset > 0) {
            previous = true;
        }
        
        return previous;
    }
    
    public boolean isNext() {
        boolean next = false;
        
        if(totalRecords > offset + maxPerPage) {
            next = true;
        }    
        
        return next;
    }
    
    public int getPreviousOffset() {
        int previousOffset = offset - maxPerPage;
        
        if(previousOffset < 0) {
            previousOffset = 0;
        }
        
        return previousOffset;
    }
    
    public int getNextOffset() {        
        int nextOffset = offset + maxPerPage;
        
        if(nextOffset > (totalRecords - 1)) {
            nextOffset = totalRecords - 1;
        }        
        
        return nextOffset;
    }
}
