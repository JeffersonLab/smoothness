package org.jlab.demo.presentation.util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ryans
 */
public final class FilterSelectionMessage {

    private FilterSelectionMessage() {
        // Private constructor
    }

    public static String getMessage(String lastname) {

        List<String> filters = new ArrayList<>();

        if (lastname != null && !lastname.isEmpty()) {
            filters.add("Lastname \"" + lastname + "\"");
        }

        String message = "";

        if (!filters.isEmpty()) {
            message = filters.get(0);

            for (int i = 1; i < filters.size(); i++) {
                String filter = filters.get(i);
                message += " and " + filter;
            }
        }

        return message;
    }
}
