package org.jlab.smoothness.presentation.util;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Handles Parameters from a ServletRequest.
 *
 * @param <E>
 */
public interface UrlParamHandler<E> {
    /**
     * Convert URL Parameters to an entity.
     *
     * @return The entity
     */
    E convert();

    /**
     * Validate URL Parameters.
     *
     * @param params The parameters
     */
    void validate(E params);

    /**
     * Store URL Parameters in a session.
     *
     * @param params The parameters
     */
    void store(E params);

    /**
     * Return default parameter values.
     *
     * @return The default value
     */
    E defaults();

    /**
     * Load URL Parameters from
     *
     * @return
     */
    E materialize();
    
    boolean qualified();

    /**
     * Return a String representing the provided parameters.
     *
     * @param params The parameters
     * @return A String representation of the parameters
     */
    String message(E params);

    /**
     * Redirects a request that is missing parameters to a new request with missing parameters provided with default
     * values.
     *
     * @param response The HttpServletResponse
     * @param params The parameters
     * @throws IOException If unable to redirect
     */
    void redirect(HttpServletResponse response, E params) throws IOException;
}
