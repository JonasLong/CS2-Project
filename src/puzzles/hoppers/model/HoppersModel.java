package puzzles.hoppers.model;

import puzzles.common.Observer;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class HoppersModel {
    /**
     * the collection of observers of this model
     */
    private final List<Observer<HoppersModel, String>> observers = new LinkedList<>();

    /**
     * the current configuration
     */
    private HoppersConfig currentConfig;
    /**
     * Instance of Solver to use
     */
    private Solver solver = new Solver();
    /**
     * LinkedList to the solution of this configuration
     */
    public List<Configuration> path;
    /**
     * Current filename that this model last read from
     */
    private String curFname;
    /**
     * Row selected. Used by select()
     */
    private int selectedRow;
    /**
     * Column selected. Used by select()
     */
    private int selectedCol;
    /**
     * Whether there is a row and column selected
     */
    private boolean isSelected = false;

    /**
     * The view calls this to add itself as an observer.
     *
     * @param observer the view
     */
    public void addObserver(Observer<HoppersModel, String> observer) {
        this.observers.add(observer);
    }

    /**
     * The model's state has changed (the counter), so inform the view via
     * the update method
     */
    private void alertObservers(String msg) {
        for (var observer : observers) {
            observer.update(this, msg);
        }
    }

    /**
     * Loads a config from a given file
     *
     * @param fname filename
     */
    public void load(String fname) {
        try {
            currentConfig = new HoppersConfig(fname);
            curFname = fname;//this is redundant when called by reset() but idc
            isSelected = false;
            alertObservers("Loaded: " + fname);
        } catch (IOException e){
            alertObservers("Failed to load: "+fname);
        }
    }

    /**
     * Returns the current config
     *
     * @return config
     */
    public HoppersConfig getConfig() {
        return currentConfig;
    }

    /**
     * Select a space. If a space is already selected, attempt to move the already selected frog to the given location.
     *
     * @param row row number
     * @param col column number
     */
    public void select(int row, int col) {
        HoppersConfig.cellContents cell = currentConfig.get(row, col);
        if (isSelected) {
            isSelected = false;
            moveFrog(selectedRow, selectedCol, row, col);
        } else {
            if (cell == HoppersConfig.cellContents.GREEN || cell == HoppersConfig.cellContents.RED) {
                selectedRow = row;
                selectedCol = col;
                isSelected=true;
                alertObservers("Selected " + printCoords(row, col));
            } else if (cell== HoppersConfig.cellContents.INVALID) {
                alertObservers("Invalid selection "+printCoords(row,col));
            } else {
                alertObservers("No frog at " + printCoords(row, col));
            }
        }
    }

    /**
     * Attempts to move a frog from one space to another
     *
     * @param startRow starting row number (y1)
     * @param startCol starting column number (x1)
     * @param endRow ending row number (y2)
     * @param endCol ending column number (x2)
     */
    private void moveFrog(int startRow, int startCol, int endRow, int endCol) {
        HoppersConfig c = currentConfig.moveFrog(startRow, startCol, endRow, endCol);
        if (c == null) {
            alertObservers("Can't jump from " + printCoords(startRow, startCol) + " to " + printCoords(endRow, endCol));
        } else {
            currentConfig = c;
            alertObservers("Jumped from " + printCoords(startRow, startCol) + " to " + printCoords(endRow, endCol));
        }
    }

    /**
     * Solves the current configuration, then gets the next configuration after this one
     */
    public void getHint() {
        refreshPath();
        boolean hintFound = false;
        if (currentConfig.isSolution()) {
            alertObservers("Already solved!");
        } else {
            for (int i = 0; i < path.size(); i++) {
                if (currentConfig.equals(path.get(i))) {
                    currentConfig = (HoppersConfig) path.get(i + 1);
                    alertObservers("Next step!");
                    hintFound = true;
                    break;
                }
            }
            if (!hintFound) {
                alertObservers("No solution!");
            }
        }
    }

    /**
     * Resets the puzzle to the starting configuration
     */
    public void reset(){
        load(curFname);
        alertObservers("Puzzle reset!");
    }

    /**
     * Solves the current configuration
     */
    private void refreshPath() {
        //todo only run when user has moved a frog (or reset) to save computation time
        //this method call is the most computationally intensive part of hints and the model as a whole
        path = solver.findPath(currentConfig);
    }

    /**
     * Prints a row,column pair of coordinates in (row,col) format
     *
     * @param row row number
     * @param col column number
     * @return string representation
     */
    public String printCoords(int row, int col) {
        return "(" + row + ", " + col + ")";
    }


}
