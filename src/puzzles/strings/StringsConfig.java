package puzzles.strings;

import puzzles.common.solver.Configuration;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a node in the String problem's graph
 */
public class StringsConfig implements Configuration {

    /** Current string of this object */
    private String current;
    /** Goal string */
    private static String goal;

    /**
     * Creates an initial StringConfig instance
     *
     * @param start starting string
     * @param end ending string
     */
    public StringsConfig(String end, String start) {
        this.current = start;
        goal = end;
    }

    /**
     * Creates neighbors of a given StringConfig instance
     *
     * @param current the string for this object
     * @param other the instance of StringsConfig that called this method
     */
    private StringsConfig(String current, StringsConfig other) {
        this.current = current;
        this.goal = other.goal;
    }

    @Override
    public boolean isSolution() {
        return current.equals(goal);
    }

    @Override
    public Collection<Configuration> getNeighbors() {
        ArrayList<Configuration> neighbors = new ArrayList<>();
        char[] a = current.toCharArray();

        for (int i = 0; i < current.length(); i++) {
            char[] b = a.clone();
            char[] c = a.clone();

            b[i] = changeLetter(a[i], true);
            c[i] = changeLetter(a[i], false);
            neighbors.add(new StringsConfig(String.valueOf(b), this));
            neighbors.add(new StringsConfig(String.valueOf(c), this));
        }

        return neighbors;
    }

    private char changeLetter(char c, boolean isPositive) {
        if (isPositive) {
            c += 1;
        } else {
            c -= 1;
        }

        if (c > 'Z') {
            c = 'A';
        } else if (c < 'A') {
            c = 'Z';
        }

        return c;
    }

    @Override
    public String toString() {
        return current;
    }

    @Override
    public int hashCode() {
        return current.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        StringsConfig otherConf = (StringsConfig) other;
        return current.equals(otherConf.current);
    }
}
