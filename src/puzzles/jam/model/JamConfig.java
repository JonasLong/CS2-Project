package puzzles.jam.model;

import puzzles.common.solver.Configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class JamConfig implements Configuration{

    private Character[][] mainGrid;
    private HashMap<Character, Car> carList;
    private int numRows;
    private int numCols;

    /**
     * create a new config using a file
     * @param filename the file to be used
     */
    public JamConfig(File filename){
        try (BufferedReader br = new BufferedReader(new FileReader(filename))){
            carList = new HashMap<>();
            String[] line = br.readLine().split(" ");
            this.numRows = Integer.parseInt(line[0]);
            this.numCols = Integer.parseInt(line[1]);
            mainGrid = new Character[numRows][numCols];
            for (int i = 0; i < numRows; i++) {
                for (int j = 0; j < numCols; j++) {
                    mainGrid[i][j] = '.';
                }
            }
            int numCars = Integer.parseInt(br.readLine());
            for (int i = 0; i < numCars; i++) {
                line = br.readLine().split(" ");
                Character name = line[0].charAt(0);
                int startRow = Integer.parseInt(line[1]);
                int startCol = Integer.parseInt(line[2]);
                int endRow = Integer.parseInt(line[3]);
                int endCol = Integer.parseInt(line[4]);
                Car car = new Car(startRow, startCol, endRow, endCol, name);
                carList.put(name, car);
                if (car.movesHorizontal()){
                    for (int j = startCol; j < (endCol + 1); j++) {
                        mainGrid[startRow][j] = name;
                    }
                } else {
                    for (int j = startRow; j < (endRow + 1); j++) {
                        mainGrid[j][startCol] = name;
                    }
                }
            }
        } catch (IOException e){
            System.out.println(e);
        }
    }

    /**
     * create a copy of another config, but with one car moved
     * @param other the config to copy
     * @param name the name of the car to move
     * @param forward boolean for car direction, true = forward
     */
    public JamConfig(JamConfig other, Character name, boolean forward){
        this.numCols = other.numCols;
        this.numRows = other.numRows;
        this.mainGrid = new Character[this.numRows][this.numCols];
        this.carList = new HashMap<>();
        for (int i = 0; i < numRows; i++) {
            if (numCols >= 0) System.arraycopy(other.mainGrid[i], 0, this.mainGrid[i], 0, numCols);
        }
        for (Car car: other.carList.values()) {
            this.carList.put(car.getName(), new Car(car));
        }
        Car car = this.carList.get(name);
        moveCar(car, forward);
    }

    /**
     * move one car one space
     * @param car the car to move
     * @param forward boolean for direction
     */
    public void moveCar(Car car, boolean forward){
        if (forward){
            mainGrid[car.getStartRow()][car.getStartCol()] = '.';
            if (car.movesHorizontal()){
                mainGrid[car.getEndRow()][(car.getEndCol() + 1)] = car.getName();
                car.setStartCol((car.getStartCol() + 1));
                car.setEndCol((car.getEndCol() + 1));
            } else {
                mainGrid[(car.getEndRow() + 1)][car.getEndCol()] = car.getName();
                car.setStartRow((car.getStartRow() + 1));
                car.setEndRow((car.getEndRow() + 1));
            }
        } else {
            mainGrid[car.getEndRow()][car.getEndCol()] = '.';
            if (car.movesHorizontal()){
                mainGrid[car.getStartRow()][(car.getStartCol() - 1)] = car.getName();
                car.setStartCol((car.getStartCol() - 1));
                car.setEndCol((car.getEndCol() - 1));
            } else {
                mainGrid[(car.getStartRow() - 1)][car.getEndCol()] = car.getName();
                car.setStartRow((car.getStartRow() - 1));
                car.setEndRow((car.getEndRow() - 1));
            }
        }
    }

    /**
     * check iof config is the solution
     * @return boolean for if the thing is the right thing
     */
    @Override
    public boolean isSolution() {
        return carList.get('X').getEndCol() == (numCols - 1);
    }

    /**
     * get the list of possible neighbors for the current config
     * @return the list of neighbor configs
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        ArrayList<Configuration> nbr = new ArrayList<>();
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                Character cur = mainGrid[i][j];
                if (cur.equals('.')){
                    if (i > 0){
                        addConfig(nbr, (i - 1), j, (true), false);
                    }
                    if (i < (numRows - 1)){
                        addConfig(nbr, (i + 1), j, (false), false);
                    }
                    if (j > 0){
                        addConfig(nbr, i, (j - 1), (true), true);
                    }
                    if (j < (numCols - 1)){
                        addConfig(nbr, i, (j + 1), (false), true);
                    }
                }
            }
        }
        return nbr;
    }

    /**
     * get the value of teh grid at a specified row or column
     * @param row the row coordinate to check
     * @param col the column to check
     * @return the character from the main grid
     */
    public Character getAt(int row, int col){
        return mainGrid[row][col];
    }

    /**
     * get the Car object of a car with a given name
     * @param name the name of teh car to get
     * @return the Car object
     */
    public Car getCar(Character name){
        return carList.get(name);
    }

    /**
     * add a config to a list, having checked for a move's validity and moved a car if valid
     * @param list the list to add to
     * @param row the row coord of the car to move
     * @param col the column coord of the car to be moved
     * @param forward boolean for direction
     * @param horiz the expected value of teh car's movesHorizontal variable. the move is valid if this matches the actual value
     */
    public void addConfig(ArrayList<Configuration> list, int row, int col, boolean forward, boolean horiz) {
        if (!mainGrid[row][col].equals('.') && (carList.get(mainGrid[row][col]).movesHorizontal() == horiz)){
            list.add(new JamConfig((this), mainGrid[row][col], forward));
        }
    }

    @Override
    public String toString(){
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                string.append(mainGrid[i][j]).append(" ");
            }
            string.append("\n");
        }
        return string.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JamConfig jamConfig = (JamConfig) o;
        return Arrays.deepEquals(mainGrid, jamConfig.mainGrid);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(mainGrid);
    }

    public int getNumCols() {
        return numCols;
    }

    public int getNumRows() {
        return numRows;
    }

    public Character[][] getMainGrid() {
        return mainGrid;
    }
}


