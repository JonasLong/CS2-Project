package puzzles.hoppers.model;

import puzzles.common.solver.Configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;


public class HoppersConfig implements Configuration {

    private cellContents[][] grid;
    private static int ROWS=0;
    private static int COLS=0;

    public static final char EMPTY_CHAR='.';
    public static final char GREEN_CHAR='G';
    public static final char RED_CHAR='R';
    public static final char INVALID_CHAR='*';
    public static final String SEPARATOR=" ";
    public static final String NEWLINE="\n";

    public enum cellContents {
        EMPTY, GREEN, RED, INVALID
    }

    @Override
    public Collection<Configuration> getNeighbors() {
        ArrayList<Configuration> neighbors = new ArrayList<>();

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                cellContents cell = grid[row][col];
                if (cell == cellContents.GREEN || cell == cellContents.RED) {
                    //make sure this cell has a frog on it, or chaos will ensue
                    if (isEvenCell(row, col)) {
                        //generate vertical and horizontal neighbors
                        //technically this if statement isn't be necessary, but it also would be slower without it
                        generateConfig(row, col, 0, 0, neighbors);
                        generateConfig(row, col, 0, 0, neighbors);
                        generateConfig(row, col, 0, 0, neighbors);
                        generateConfig(row, col, 0, 0, neighbors);
                    }
                    //generate diagonals here
                    generateConfig(row, col, 1, 1, neighbors);
                    generateConfig(row, col, 1, -1, neighbors);
                    generateConfig(row, col, -1, 1, neighbors);
                    generateConfig(row, col, -1, -1, neighbors);
                }

            }
        }

        return neighbors;
    }

    /**
     *
     * @rit.pre curRow and curCol point to a cell within bounds containing a green or red frog
     */
    private void generateConfig(int curRow, int curCol, int rowOffset, int colOffset, ArrayList<Configuration> configurations) {
        //target destination
        int row = curRow + rowOffset;
        int col = curCol + colOffset;

        //ignore if out of bounds
        if (0 <= row && row < ROWS && 0 <= col && col < COLS) {

            cellContents targetCell = grid[row][col];
            switch (targetCell) {
                case GREEN:
                    //this space is occupied, so it cannot be jumped on, but it can be jumped over
                    if (grid[row+rowOffset][col+colOffset]==cellContents.EMPTY){
                        //to prevent jumping over multiple frogs at a time, check that the new target is empty
                        //generate a new configuration that jumps this one
                        generateConfig(curRow,curCol,rowOffset*2,colOffset*2,configurations);
                        //if you don't understand this, it's ok. I am very big brain, you shouldn't feel bad
                    }
                    break;

                case EMPTY:
                    //add config because this space can be jumped on but not over
                    cellContents[][] newGrid=getEmptyGrid();
                    for (int rowNum = 0; rowNum < ROWS; rowNum++) {
                        System.arraycopy(grid,0,newGrid,0,COLS);
                    }

                    //do this first to move whatever the current frog is (green or red) to the new space
                    newGrid[row][col]=newGrid[curRow][curCol];
                    //do this second to move empty the space the frog jumped from
                    newGrid[curRow][curCol]=cellContents.EMPTY;
                    configurations.add(new HoppersConfig(newGrid));
                case RED:
                    //ignore config because the red frog cannot be jumped on or over

                case INVALID:
                    //ignore config because this space cannot be jumped on or over

                default:
                    //should not be reached
                    break;
            }
        }
        //ignore this space because it is out of bounds, cannot be jumped on or over
    }

    private boolean isEvenCell(int row, int col) {
        return row % 2 == 0;
    }

    public HoppersConfig(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));

            String[] size = reader.readLine().split(SEPARATOR);
            ROWS = Integer.parseInt(size[0]);
            COLS = Integer.parseInt(size[1]);
            grid = getEmptyGrid();

            for (int rowNum = 0; rowNum < ROWS; rowNum++) {
                String[] colStr = reader.readLine().split(SEPARATOR);
                for (int colNum = 0; colNum < COLS; colNum++) {
                    char cellChar = colStr[colNum].charAt(0);
                    cellContents curCell;
                    switch (cellChar) {
                        case EMPTY_CHAR -> curCell = cellContents.EMPTY;
                        case RED_CHAR -> curCell = cellContents.RED;
                        case GREEN_CHAR -> curCell = cellContents.GREEN;
                        case INVALID_CHAR -> curCell = cellContents.INVALID;
                        default -> curCell = null;
                    }
                    grid[rowNum][colNum] = curCell;
                }
            }
        } catch (IOException e){
            e.printStackTrace();
            grid=getEmptyGrid();
        }
    }

    private cellContents[][] getEmptyGrid(){
        return new cellContents[ROWS][COLS];
    }

    private HoppersConfig(cellContents[][] grid){
        this.grid=grid;
    }

    @Override
    public boolean isSolution() {
        for (cellContents[] row : grid) {
            for (cellContents cell : row) {
                if (cell == cellContents.GREEN) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString(){
        StringBuilder out= new StringBuilder();
        for (int rowNum = 0; rowNum < ROWS; rowNum++) {
            for (int colNum = 0; colNum < COLS; colNum++) {
                cellContents cell=grid[rowNum][colNum];
                switch (cell){
                    case EMPTY -> out.append(EMPTY_CHAR);
                    case GREEN -> out.append(GREEN_CHAR);
                    case RED -> out.append(RED_CHAR);
                    case INVALID -> out.append(INVALID_CHAR);
                }
                if(colNum!=COLS-1){
                    out.append(SEPARATOR);
                }
            }
            if(rowNum!=ROWS-1){
                out.append(NEWLINE);
            }
        }
        return out.toString();
    }

}
