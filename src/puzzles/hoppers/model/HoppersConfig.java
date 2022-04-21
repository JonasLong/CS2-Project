package puzzles.hoppers.model;

import puzzles.common.solver.Configuration;
import puzzles.hoppers.solver.Hoppers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;


public class HoppersConfig implements Configuration {

    private cellContents[][] grid;
    //Table of random ints for Zobrist hashing
    private static int[][][] zobristTable;

    public static int ROWS = 0;
    public static int COLS = 0;

    public static final char EMPTY_CHAR = '.';
    public static final char GREEN_CHAR = 'G';
    public static final char RED_CHAR = 'R';
    public static final char INVALID_CHAR = '*';
    public static final String SEPARATOR = " ";
    public static final String NEWLINE = "\n";

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
                        generateConfig(row, col, 2, 0, neighbors, false);
                        generateConfig(row, col, -2, 0, neighbors, false);
                        generateConfig(row, col, 0, 2, neighbors, false);
                        generateConfig(row, col, 0, -2, neighbors, false);
                    }
                    //generate diagonals here
                    generateConfig(row, col, 1, 1, neighbors, false);
                    generateConfig(row, col, 1, -1, neighbors, false);
                    generateConfig(row, col, -1, 1, neighbors, false);
                    generateConfig(row, col, -1, -1, neighbors, false);
                }

            }
        }

        return neighbors;
    }

    /**
     * @rit.pre curRow and curCol point to a cell within bounds containing a green or red frog
     */
    private void generateConfig(int curRow, int curCol, int rowOffset, int colOffset, ArrayList<Configuration> configurations, boolean canLand) {
        //target destination
        int row = curRow + rowOffset;
        int col = curCol + colOffset;

        //ignore if out of bounds
        if (0 <= row && row < ROWS && 0 <= col && col < COLS) {

            cellContents targetCell = grid[row][col];
            switch (targetCell) {
                case GREEN:
                    //this space is occupied, so it cannot be jumped on, but it can be jumped over
                    if (!canLand) {
                        generateConfig(curRow, curCol, rowOffset * 2, colOffset * 2, configurations, true);
                    }
                    break;

                case EMPTY:
                    if (canLand) {
                        //add config because this space can be jumped on but not over
                        cellContents[][] newGrid = getEmptyGrid();
                        for (int rowNum = 0; rowNum < ROWS; rowNum++) {
                            //this line was suggested by the IDE, not my fault if it breaks
                            if (COLS >= 0) System.arraycopy(grid[rowNum], 0, newGrid[rowNum], 0, COLS);
                        }

                        //do this first to move the current frog (green or red) to the new space
                        newGrid[row][col] = newGrid[curRow][curCol];
                        //do this second to empty the space the frog has just jumped from
                        newGrid[curRow][curCol] = cellContents.EMPTY;
                        //empty the space the frog jumped over
                        newGrid[row - rowOffset / 2][col - colOffset / 2] = cellContents.EMPTY;
                        configurations.add(new HoppersConfig(newGrid));
                    }
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
            initZobrist();

        } catch (IOException e) {
            e.printStackTrace();
            grid = getEmptyGrid();
        }
    }

    private cellContents[][] getEmptyGrid() {
        return new cellContents[ROWS][COLS];
    }

    private HoppersConfig(cellContents[][] grid) {
        this.grid = grid;
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
    public String toString() {
        StringBuilder out = new StringBuilder();
        for (int rowNum = 0; rowNum < ROWS; rowNum++) {
            for (int colNum = 0; colNum < COLS; colNum++) {
                cellContents cell = grid[rowNum][colNum];
                switch (cell) {
                    case EMPTY -> out.append(EMPTY_CHAR);
                    case GREEN -> out.append(GREEN_CHAR);
                    case RED -> out.append(RED_CHAR);
                    case INVALID -> out.append(INVALID_CHAR);
                }
                if (colNum != COLS - 1) {
                    out.append(SEPARATOR);
                }
            }
            if (rowNum != ROWS - 1) {
                out.append(NEWLINE);
            }
        }
        return out.toString();
    }

    public boolean equals(Object other) {
        HoppersConfig otherConf = (HoppersConfig) other;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (grid[row][col] != otherConf.grid[row][col]) {
                    return false;
                }
            }
        }
        return true;
    }


    private void initZobrist() {
        /*
        Zobrist hashing because I'm a nerd
        This is based on the pseudocode implementation for chess in https://en.wikipedia.org/wiki/Zobrist_hashing
        Yes I could have used java.util.Arrays.deepHashCode( grid ); but I don't want to
        This part initializes the hashing algorithm
        */
        Random rand = new Random(24); //allowed to be predictably random, just can't be procedural based on row/column
        //fill a table of random numbers/bitstrings
        //this isn't exactly how the example does it, they use a 2d array of [COLS*ROWS][pieces] for some reason
        zobristTable = new int[ROWS][COLS][cellContents.values().length]; //no -1 because the example doesn't include EMPTY but is 1 indexed
        for (int row = 0; row < ROWS; row++) {
            //loop over the board, represented as a linear array
            for (int col = 0; col < COLS; col++) {
                //loop over the pieces
                for (int piece = 0; piece < cellContents.values().length; piece++) {
                    zobristTable[row][col][piece] = rand.nextInt();
                }
            }
        }
    }

    public int hashCode() {
        /*TODO if i have the time implement methods for rehashing to speed this up
        a benefit of Zohurst is you can XOR cells in and out when you make changes without having to recompute the entire hash*/
        int hash = 0;
        //loop over the board positions
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                cellContents cell = grid[row][col];
                if (cell != cellContents.EMPTY) {
                    //EMPTY serves as the #0 ordinal, so no need to add +1 when getting value to change by
                    //the ^ means XOR
                    hash=hash ^ zobristTable[row][col][cell.ordinal()];
                }
            }
        }
        //System.out.println("hash for "+this.toString().replace('\n','|')+ " is "+hash);
        return hash;
    }
}
