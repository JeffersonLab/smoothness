package org.jlab.smoothness.business.util;

/**
 * Object Utilities.
 *
 * @author ryans
 */
public class ObjectUtil {

  private ObjectUtil() {
    // private constructor
  }

  /**
   * Return the first non-null item.
   *
   * @param items The items
   * @param <T> The item type
   * @return The first non-null item, or null if none are non-null
   */
  @SafeVarargs
  public static <T> T coalesce(T... items) {
    for (T i : items) {
      if (i != null) {
        return i;
      }
    }
    return null;
  }
}
