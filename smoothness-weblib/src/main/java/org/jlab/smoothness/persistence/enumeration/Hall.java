package org.jlab.smoothness.persistence.enumeration;

/**
 * Represents one of the four experimenter halls at Jefferson Lab.
 * 
 * @author ryans
 */
public enum Hall {
    A("A"),
    B("B"),
    C("C"),
    D("D");

    private final String label;

    /**
     * Create a new Hall with the given label.
     *
     * @param label The label
     */
    Hall(String label) {
        this.label = label;
    }

    /**
     * Return the label.
     *
     * @return The label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Return the hall abbreviation (first letter).
     *
     * @return The first letter
     */
    public char getLetter() {
        return label.charAt(0);
    }
}
