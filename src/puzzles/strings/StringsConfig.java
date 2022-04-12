package puzzles.strings;

import puzzles.common.solver.Configuration;

import java.util.*;

public class StringsConfig implements Configuration {
    /**
     * String arrays to store the goal string and current config as arrays of single-letter strings
     */
    private final String[] solution;
    private String[] current;

    /**
     * hash map to store neighbors of each letter, used later for getNeighbors
     */
    private static final HashMap<String, String> neighborMap = new HashMap<>() {{
        put("A","ZB");
        put("B","AC");
        put("C","BD");
        put("D","CE");
        put("E","DF");
        put("F","EG");
        put("G","FH");
        put("H","GI");
        put("I","HJ");
        put("J","IK"); // I only realized after being halfway through manually making this that i could have just written
        put("K","JL"); // code to do it instead. I didnt want the work to go to waste, so it stays.
        put("L","KM");
        put("M","LN");
        put("N","MO");
        put("O","NP");
        put("P","OQ");
        put("Q","PR");
        put("R","QS");
        put("S","RT");
        put("T","SU");
        put("U","TV");
        put("V","UW");
        put("W","VX");
        put("X","WY");
        put("Y","XZ");
        put("Z","YA");
    }};

    /**
     * Constructor makes a config
     * @param solution the goal string
     * @param start the start string
     */
    public StringsConfig(String solution, String start){
        this.solution = new String[solution.length()];
        for (int i = 0; i< solution.length(); ++i){
            this.solution[i] = String.valueOf(solution.charAt(i));
        }
        this.current = new String[start.length()];
        for (int i = 0; i< start.length(); ++i){
            this.current[i] = String.valueOf(start.charAt(i));
        }
    }

    /**
     * copy constructor copies a config except for one letter at a specified index
     * @param other the config to copy
     * @param newLetter the letter that should be inserted
     * @param index the index to replace
     */
    private StringsConfig(StringsConfig other, String newLetter, int index){
        this.solution = other.solution.clone();
        this.current = other.current.clone();
        this.current[index] = newLetter;
    }

    @Override
    public String toString() {
        String output = "";
        for (int i = 0; i < current.length; ++i){
            output += current[i];
        }
        return output;
    }

    @Override
    public boolean isSolution() {
        return Arrays.equals(this.current, this.solution);
    }

    @Override
    public Collection<Configuration> getNeighbors() {
        ArrayList<Configuration> list = new ArrayList<>();
        for (int i = 0; i < this.current.length; ++i){
            list.add(new StringsConfig(this, String.valueOf(neighborMap.get(this.current[i]).charAt(0)), i));
            list.add(new StringsConfig(this, String.valueOf(neighborMap.get(this.current[i]).charAt(1)), i));
        }
        return list;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringsConfig that = (StringsConfig) o;
        return Arrays.equals(current, that.current);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(current);
    }
}
