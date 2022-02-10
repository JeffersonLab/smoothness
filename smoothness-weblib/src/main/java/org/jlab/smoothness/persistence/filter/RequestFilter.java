package org.jlab.smoothness.persistence.filter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * SQL filter for a given set of request parameters.
 *
 * @param <T> The request parameters type
 */
public abstract class RequestFilter<T> {
    /**
     * The parameters
     */
    protected final T params;

    /**
     * Create a new RequestFilter with the given parameters.
     *
     * @param params The parameters
     */
    public RequestFilter(T params) {
        this.params = params;
    }

    /**
     * Return the parameters
     *
     * @return The parameters
     */
    public T getParams() {
        return params;
    }

    /**
     * Return the constructed SQL where clause.
     *
     * @return The String representation of the where clause
     */
    public abstract String getSqlWhereClause();

    /**
     * Assign parameter values to a PreparedStatement.
     *
     * @param stmt The PreparedStatement
     * @throws SQLException If unable to assign values
     */
    public abstract void assignParameterValues(PreparedStatement stmt) throws SQLException;
}
