package org.jlab.smoothness.persistence.util;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Query;

/**
 * JPA Utilities.
 *
 * @author ryans
 */
public class JPAUtil {

  private static final Logger LOGGER = Logger.getLogger(JPAUtil.class.getName());

  private JPAUtil() {
    // Hidden constructor
  }

  /**
   * Attempt to initialize a lazily loaded JPA collection by iterating over it.
   *
   * @param c The Collection
   */
  public static void initialize(Collection c) {
    if (c != null) {
      for (Object o : c) {
        o.getClass(); // tickle proxy object
      }
    }
  }

  /**
   * Given a JPA Query, convert the result list into a list of entities with the given type.
   *
   * @param q The JPA Query
   * @param clazz The entity class
   * @param <T> The entity type
   * @return A List of entities of the given type
   */
  public static <T> List<T> getResultList(Query q, Class<T> clazz) {
    return getResultList(q, clazz, false);
  }

  /**
   * Given a JPA Query, convert the result list into a list of entities with the given type.
   *
   * @param q The JPA Query
   * @param clazz The entity class
   * @param debug true to log class name
   * @param <T> The entity type
   * @return A List of entities of the given type
   * @throws IllegalArgumentException If something goes wrong!
   */
  @SuppressWarnings("unchecked")
  public static <T> List<T> getResultList(Query q, Class<T> clazz, boolean debug)
      throws IllegalArgumentException {

    Constructor<?> ctor = clazz.getDeclaredConstructors()[0];
    List<T> result = new ArrayList<>();

    List<Object[]> list = q.getResultList();

    for (Object obj : list) {
      if (ctor.getParameterTypes().length == 1) {
        obj = new Object[] {obj};
      }
      if (debug) {
        logObjectTypes((Object[]) obj);
      }
      createAndAddBean(ctor, (Object[]) obj, result);
    }

    return result;
  }

  /**
   * Create a new object given a Constructor and arguments, and add it to the provided List.
   *
   * @param ctor The Constructor
   * @param args The arguments
   * @param result The List to add to
   * @param <T> The object type
   */
  @SuppressWarnings("unchecked")
  private static <T> void createAndAddBean(Constructor<?> ctor, Object[] args, List<T> result) {
    try {
      T obj = (T) ctor.newInstance(args);
      result.add(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Log name of each object in the provided array.
   *
   * @param objArray The array of objects
   */
  public static void logObjectTypes(Object[] objArray) {
    for (Object obj : objArray) {
      if (obj == null) {
        LOGGER.log(Level.INFO, "null");
      } else {
        LOGGER.log(Level.INFO, obj.getClass().getName());
      }
    }
  }
}
