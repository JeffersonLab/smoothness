package org.jlab.smoothness.presentation.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ryans
 */
public class ParamBuilder {

    private final Map<String, List<String>> params = new LinkedHashMap<>();

    public void add(String key, String value) {
        List<String> list = new ArrayList<>();
        list.add(value);
        params.put(key, list);
    }

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

    public Map<String, List<String>> getParams() {
        return params;
    }
}
