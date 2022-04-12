package puzzles.hoppers.model;

import puzzles.common.solver.Configuration;

import java.util.ArrayList;
import java.util.Collection;


public class HoppersConfig implements Configuration {


    private ArrayList<ArrayList<cellContents>> grid;
    private static int ROWS;
    private static int COLS;

    public enum cellContents{
        EMPTY, GREEN, RED, INVALID
    }

    @Override
    public Collection<Configuration> getNeighbors() {
        ArrayList<Configuration> neighbors = new ArrayList<>();

        for (int row=0; row<ROWS; row++) {
            for (int col=0; col<COLS; col++) {
                if(isEven(col)){

                }
                //neighbors.add(new HoppersConfig());
            }
        }

        return null;
    }

    private boolean isEven(int num){
        return num%2==0;
    }

    public HoppersConfig(ArrayList<ArrayList<cellContents>> grid){
        this.grid=grid;
        ROWS=grid.size();
        COLS=grid.get(0).size();
    }

    @Override
    public boolean isSolution() {
        for (ArrayList<cellContents> row: grid){
            for (cellContents cell: row){
                if(cell== cellContents.GREEN){
                    return false;
                }
            }
        }
        return true;
    }
}
