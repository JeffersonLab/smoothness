package org.jlab.smoothness.presentation.util;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles Parameters from a ServletRequest.
 *
 * @param <E> A class representing parameters
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
   * Load URL Parameters from a session.
   *
   * @return The parameters
   */
  E materialize();

  /**
   * Return true if request is fully qualified, else some expected parameters are missing and
   * defaults need to be resolved.
   *
   * @return true if qualified, false otherwise
   */
  boolean qualified();

  /**
   * Return a String representing the provided parameters.
   *
   * @param params The parameters
   * @return A String representation of the parameters
   */
  String message(E params);

  /**
   * Redirects a request that is missing parameters to a new request with missing parameters
   * provided with default values.
   *
   * @param response The HttpServletResponse
   * @param params The parameters
   * @throws IOException If unable to redirect
   */
  void redirect(HttpServletResponse response, E params) throws IOException;
}
