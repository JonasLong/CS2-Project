package puzzles.hoppers.model;

import puzzles.common.Observer;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class HoppersModel {
    /** the collection of observers of this model */
    private final List<Observer<HoppersModel, String>> observers = new LinkedList<>();

    /** the current configuration */
    private HoppersConfig currentConfig;

    private HoppersConfig startingConfig;

    private Solver solver=new Solver();
    //private ArrayList<Configuration> path=new ArrayList<>();

    private int selectedRow;
    private int selectedCol;
    private boolean isSelected=false;

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

    public void updateConfig(HoppersConfig config){
        currentConfig=config;
        alertObservers("");
    }

    public void load(String fname){
        currentConfig=new HoppersConfig(fname);
        startingConfig=currentConfig;
        isSelected=false;
        alertObservers("Loaded: "+fname);
    }

    public HoppersConfig getConfig(){
        return currentConfig;
    }

    public void select(int row, int col){
        HoppersConfig.cellContents cell=currentConfig.get(row,col);
        if (isSelected){
            isSelected=false;
            //if(cell== HoppersConfig.cellContents.EMPTY){
            moveFrog(selectedRow,selectedCol,row,col);
            /*} else {
                alertObservers("");
            }*/
            //TODO
        } else {
            if (cell== HoppersConfig.cellContents.GREEN || cell== HoppersConfig.cellContents.RED) {
                selectedRow = row;
                selectedCol = col;
                System.out.println("Selected "+printCoords(row,col));
            } else {
                alertObservers("No frog at "+printCoords(row,col));
            }
        }
    }

    private void moveFrog(int startRow, int startCol, int endRow, int endCol){
        HoppersConfig c=currentConfig.moveFrog(startRow,startCol,endRow,endCol);
        if(c==null){
            alertObservers("Can't jump from "+printCoords(startRow,startCol)+"  to "+printCoords(endRow,endCol));
        } else {
            alertObservers("Jumped from "+printCoords(startRow,startCol)+" to "+printCoords(endRow,endCol));
            currentConfig=c;
        }
    }

    public void getHint(){

        alertObservers("Next step!");
    }

    private void refreshPath(){

        List<Configuration> path= solver.findPath(getConfig());



    }

    public String printCoords(int row, int col){
        return "("+row+", "+col+")";
    }


}
