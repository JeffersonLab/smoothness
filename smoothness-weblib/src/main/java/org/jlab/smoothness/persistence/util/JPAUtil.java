package org.jlab.smoothness.persistence.util;

import javax.persistence.Query;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ryans
 */
public class JPAUtil {

    private static final Logger logger = Logger.getLogger(
            JPAUtil.class.getName());

    private JPAUtil() {
        // Hidden constructor
    }

    public static void initialize(Collection c) {
        if (c != null) {
            for (Object o : c) {
                o.getClass(); // tickle proxy object
            }
        }
    }

    public static <T> List<T> getResultList(Query q, Class<T> clazz) {
        return getResultList(q, clazz, false);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> getResultList(Query q, Class<T> clazz, boolean debug)
            throws IllegalArgumentException {

        Constructor<?> ctor =
                (Constructor<?>) clazz.getDeclaredConstructors()[0];
        List<T> result = new ArrayList<>();

        List<Object[]> list = q.getResultList();

        for (Object obj : list) {
            if (ctor.getParameterTypes().length == 1) {
                obj = new Object[]{obj};
            }
            if(debug) {
                logObjectTypes((Object[]) obj);
            }
            createAndAddBean(ctor, (Object[]) obj, result);
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private static <T> void createAndAddBean(
            Constructor<?> ctor, Object[] args, List<T> result) {
        try {
            T obj = (T) ctor.newInstance(args);
            result.add(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void logObjectTypes(Object[] objArray) {
        for (Object obj : objArray) {
            if(obj == null) {
                logger.log(Level.INFO, "null");
            } else {
                logger.log(Level.INFO, obj.getClass().getName());
            }
        }
    }
}
