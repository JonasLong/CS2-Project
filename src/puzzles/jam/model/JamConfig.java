package puzzles.jam.model;

import puzzles.common.solver.Configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class JamConfig implements Configuration{

    private Character[][] mainGrid;
    private HashMap<Character, Car> carList;
    private static int numRows;
    private static int numCols;

    public JamConfig(String filename){
        try (BufferedReader br = new BufferedReader(new FileReader(filename))){
            carList = new HashMap<>();
            String[] line = br.readLine().split(" ");
            numRows = Integer.parseInt(line[0]);
            numCols = Integer.parseInt(line[1]);
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
                        System.out.println(this);
                    }
                } else {
                    for (int j = startRow; j < (endRow + 1); j++) {
                        mainGrid[j][startCol] = name;
                        System.out.println(this);
                    }
                }
            }
        } catch (IOException e){
            System.out.println(e);
        }
    }

    public JamConfig(JamConfig other, Character name, boolean forward){
        this.mainGrid = new Character[numRows][numCols];
        this.carList = new HashMap<>();
        for (int i = 0; i < numRows; i++) {
            if (numCols >= 0) System.arraycopy(other.mainGrid[i], 0, this.mainGrid[i], 0, numCols);
        }
        this.carList.putAll(other.carList);
        Car car = this.carList.get(name);
        moveCar(car, forward);
    }

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

    @Override
    public boolean isSolution() {
        return carList.get('X').getEndCol() == (numCols - 1);
    }

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

    public void addConfig(ArrayList<Configuration> list, int row, int col, boolean forward, boolean horiz) {
        if (!mainGrid[row][col].equals('.') && (carList.get(mainGrid[row][col]).movesHorizontal() == horiz)){
            list.add(new JamConfig((this), mainGrid[row][col], forward));
        }
    }

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
}


