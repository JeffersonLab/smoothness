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

    Hall(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public char getLetter() {
        return label.charAt(0);
    }
}
