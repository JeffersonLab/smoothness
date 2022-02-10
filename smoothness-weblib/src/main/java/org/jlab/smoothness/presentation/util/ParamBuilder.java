package org.jlab.smoothness.presentation.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility for building a Map of parameters.
 *
 * @author ryans
 */
public class ParamBuilder {

    private final Map<String, List<String>> params = new LinkedHashMap<>();

    /**
     * Add a parameter.
     *
     * @param key The key/name
     * @param value The value
     */
    public void add(String key, String value) {
        List<String> list = new ArrayList<>();
        list.add(value);
        params.put(key, list);
    }

    /**
     * Add a parameter that has multiple values, calling toString() on each value.
     *
     * @param key The key/name
     * @param array The array of values
     */
    public void add(String key, Object[] array) {
        List<String> list = new ArrayList<>();

        if (array != null && array.length > 0) {
            for (Object o : array) {
                if(o != null) {
                    list.add(o.toString());
                }
            }
        }

        params.put(key, list);
    }

    /**
     * Returns the built parameters.
     *
     * @return The parameters
     */
    public Map<String, List<String>> getParams() {
        return params;
    }
}
