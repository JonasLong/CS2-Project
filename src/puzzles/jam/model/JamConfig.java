package puzzles.jam.model;

import puzzles.common.solver.Configuration;

import java.util.Collection;
import java.util.HashMap;

// TODO: implement your JamConfig for the common solver

public class JamConfig implements Configuration{

    private String[][] mainGrid;
    private HashMap<Character, Car> carList;

    public JamConfig(){

    }

    @Override
    public boolean isSolution() {
        return false;
    }

    @Override
    public Collection<Configuration> getNeighbors() {
        return null;
    }
}
