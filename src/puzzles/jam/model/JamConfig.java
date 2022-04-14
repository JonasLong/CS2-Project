package puzzles.jam.model;

import puzzles.common.solver.Configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// TODO: implement your JamConfig for the common solver

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
                int startRow = Integer.parseInt(line[1]);
                int startCol = Integer.parseInt(line[2]);
                int endRow = Integer.parseInt(line[3]);
                int endCol = Integer.parseInt(line[4]);
                Car car = new Car(startRow, startCol, endRow, endCol);
                Character name = line[0].charAt(0);
                carList.put(name, car);
                if (car.movesHorizontal()){
                    for (int j = startCol; j < endCol; j++) {
                        mainGrid[startRow][j] = name;
                    }
                } else {
                    for (int j = startRow; j < endRow; j++) {
                        mainGrid[j][startCol] = name;
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
        Car car = carList.get(name);
        if (forward){
            if (car.movesHorizontal()){

            } else {

            }
        } else {
            if (car.movesHorizontal()){

            } else {

            }
        }
    }

    @Override
    public boolean isSolution() {
        return carList.get('X').getEndCol() == (numCols - 1);
    }

    @Override
    public Collection<Configuration> getNeighbors() {
        return null;
    }
}
