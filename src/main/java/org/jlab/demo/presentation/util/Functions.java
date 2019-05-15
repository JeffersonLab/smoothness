package org.jlab.demo.presentation.util;

import org.jlab.demo.persistence.entity.Staff;

/**
 *
 * @author ryans
 */
public final class Functions {

    private Functions() {
        // cannot instantiate publicly
    }

    public static String formatStaff(Staff staff) {
        StringBuilder builder = new StringBuilder();

        if (staff != null) {
            builder.append(staff.getLastname());
            builder.append(", ");
            builder.append(staff.getFirstname());
            builder.append(" (");
            builder.append(staff.getUsername());
            builder.append(")");
        }

        return builder.toString();
    }

    public static String formatBoolean(Boolean value) {
        if (value == null) {
            return "";
        } else if (value) {
            return "Yes"; // true; Y; '✔' 
        } else {
            return "No"; // false; N; 'X'
        }
    }

    public static boolean inArray(String[] haystack, String needle) {
        boolean inArray = false;

        if (needle != null && haystack != null) {
            for (String s : haystack) {
                if (needle.equals(s)) {
                    inArray = true;
                    break;
                }
            }
        }

        return inArray;
    }
}
