package puzzles.jam.model;

import puzzles.common.Observer;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import puzzles.jam.model.JamConfig;
import puzzles.jam.model.JamModel;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class JamModel {
    /**
     * the collection of observers of this model
     */
    private final List<Observer<JamModel, String>> observers = new LinkedList<>();

    /**
     * the current configuration
     */
    private JamConfig currentConfig;
    private Solver solver = new Solver();
    public List<Configuration> path;
    private String filename;
    private Car selectedCar;
    private int selectedRow;
    private int selectedCol;
    private boolean selection = false;

    /**
     * The view calls this to add itself as an observer.
     *
     * @param observer the view
     */
    public void addObserver(Observer<JamModel, String> observer) {
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

    public void load(File fname) {
        currentConfig = new JamConfig(fname);
        if (currentConfig.getMainGrid() != null){
            filename = fname.getPath();
            selection = false;
            alertObservers("Loaded: " + fname.getName());
        } else {
            System.out.println("Could not find file.");
        }
    }

    public JamConfig getConfig() {
        return currentConfig;
    }

    public void select(int row, int col) {
        if (selection){
            selection = false;
            boolean valid = checkMove(row, col);
            boolean forward = false;
            if (valid){
                if (selectedCar.movesHorizontal() && selectedCar.getEndCol() < col){
                    forward = true;
                } else if (!selectedCar.movesHorizontal() && selectedCar.getEndRow() < row){
                    forward = true;
                }
                currentConfig = new JamConfig(currentConfig, selectedCar.getName(), forward);
                alertObservers(("Moved from (" + selectedRow + ", " + selectedCol + ") to (" + row + ", " + col + ")."));
            } else {
                alertObservers(("Could not move from (" + selectedRow + ", " + selectedCol + ") to (" + row + ", " + col + ")."));
            }
        } else {
            Character val = currentConfig.getAt(row, col);
            if (val.equals('.')){
                alertObservers(("No car at (" + row + ", " + col + ")"));
            } else {
                selection = true;
                selectedCar = currentConfig.getCar(val);
                selectedRow = row;
                selectedCol = col;
                alertObservers(("Selected (" + row + ", " + col + ")"));
            }
        }
    }

    public boolean checkMove(int row, int col){
        if (selectedCar.movesHorizontal() && row != selectedCar.getEndRow()){
            return false;
        }
        if (!selectedCar.movesHorizontal() && col != selectedCar.getEndCol()) {
            return false;
        }
        if (!currentConfig.getAt(row, col).equals('.')){
            return false;
        }
        return true;
    }

    public void getHint() {
        refreshPath();
        boolean hintFound = false;
        if (currentConfig.isSolution()) {
            alertObservers("Already solved!");
        } else {
            for (int i = 0; i < path.size(); i++) {
                if (currentConfig.equals(path.get(i))) {
                    currentConfig = (JamConfig) path.get(i + 1);
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

    public void reset(){
        File file = new File(filename);
        currentConfig = new JamConfig(file);
        selection = false;
        alertObservers("Puzzle reset!");
    }

    private void refreshPath() {
        path = solver.findPath(currentConfig);
    }
}
