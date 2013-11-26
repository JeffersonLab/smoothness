package org.jlab.webapp.presentation.util;

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

        List<String> filters = new ArrayList<String>();

        if (lastname != null && !lastname.isEmpty()) {
            filters.add("Lastname \"" + lastname + "\"");
        }

        String message = "";

        if (!filters.isEmpty()) {
            for (String filter : filters) {
                message += " " + filter + " and";
            }

            // Remove trailing " and"
            message = message.substring(0, message.length() - 4);
        }

        return message;
    }
}
