package org.jlab.smoothness.persistence.filter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author ryans
 * @param <T>
 */
public abstract class RequestFilter<T> {
    protected final T params;
    
    public RequestFilter(T params) {
        this.params = params;
    }
    
    public T getParams() {
        return params;
    }
    
    public abstract String getSqlWhereClause();
    
    public abstract void assignParameterValues(PreparedStatement stmt) throws SQLException;
}
