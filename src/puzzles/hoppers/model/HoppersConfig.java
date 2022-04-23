package puzzles.hoppers.model;

import puzzles.common.solver.Configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;


public class HoppersConfig implements Configuration {

    /**
     * Table of random ints for Zobrist hashing
     */
    private static int[][][] zobristTable;
    /**
     * Whether the zobrist table has been initalized
     */
    private static boolean zobristInitalized=false;
    /**
     * Grid of cellContents for this config
     */
    private cellContents[][] grid;

    /**
     * Number of rows
     */
    public static int ROWS = 0;
    /**
     * Number of columns
     */
    public static int COLS = 0;

    /**
     * Characters in the provided file that correspond with cellContents
     */
    public static final char EMPTY_CHAR = '.';
    public static final char GREEN_CHAR = 'G';
    public static final char RED_CHAR = 'R';
    public static final char INVALID_CHAR = '*';

    public static final String SEPARATOR = " ";
    public static final String NEWLINE = "\n";
    /**
     * Number of cells to jump at a time. Not sure why you would want to change this
     */
    public static final int JUMP_SIZE = 1;
    /**
     * Number to multiply by when double jumping offsets. Not sure why you would want to change this
     */
    public static final int JUMP_MULTIPLIER = 2;

    /**
     * Enum of possible contents of each cell
     */
    public enum cellContents {
        EMPTY, GREEN, RED, INVALID
    }

    /**
     * Creates a new instance of HoppersConfig, reading in data for the configuration from the given file
     * Whenever a new configuration is read in from file, all existing HoppersConfig instances should be thrown out,
     * since their sizes may vary, which could lead to out of bounds errors
     *
     * @param filename filename
     * @throws IOException errors reading from the given file
     */
    public HoppersConfig(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));

        String[] size = reader.readLine().split(SEPARATOR);
        ROWS = Integer.parseInt(size[0]);
        COLS = Integer.parseInt(size[1]);
        zobristInitalized=false; //grid has changed, so Zobrist will need to be re-initalized
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
    }

    /**
     * Creates a new instance of HoppersInstance from an existing instance
     * Reads from the provided grid using <b>shallow copy</b>
     *
     * @param grid provided grid to set
     */
    private HoppersConfig(cellContents[][] grid) {
        this.grid = grid;
    }

    @Override
    public Collection<Configuration> getNeighbors() {
        ArrayList<Configuration> neighbors = new ArrayList<>();

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                cellContents cell = grid[row][col];
                //make sure this cell has a frog on it, or chaos will ensue
                if (cell == cellContents.GREEN || cell == cellContents.RED) {
                    //generate vertical and horizontal neighbors
                    if (isEvenCell(row, col)) {
                        //technically this if statement isn't be necessary, but it also would be slower without it
                        int orthJump = JUMP_SIZE * JUMP_MULTIPLIER;
                        //generate orthagonals here
                        generateConfig(row, col, orthJump, 0, neighbors, false);
                        generateConfig(row, col, -orthJump, 0, neighbors, false);
                        generateConfig(row, col, 0, orthJump, neighbors, false);
                        generateConfig(row, col, 0, -orthJump, neighbors, false);
                    }
                    int diagJump = JUMP_SIZE;
                    //generate diagonals here
                    generateConfig(row, col, diagJump, diagJump, neighbors, false);
                    generateConfig(row, col, diagJump, -diagJump, neighbors, false);
                    generateConfig(row, col, -diagJump, diagJump, neighbors, false);
                    generateConfig(row, col, -diagJump, -diagJump, neighbors, false);
                }

            }
        }

        return neighbors;
    }

    /**
     * Attempt to move a frog from the starting location to the ending location
     * Checks that the start location contains a frog, the destination is empty, a frog is being jumped over, and the spacing is correct
     * Removes any frog that is jumped over
     *
     * @param startRow starting row number (y1)
     * @param startCol starting column number (x1)
     * @param endRow ending row number (y2)
     * @param endCol ending column number (x2)
     * @return new HoppersConfig instance with the frog moved
     */
    public HoppersConfig moveFrog(int startRow, int startCol, int endRow, int endCol) {
        ArrayList<Configuration> config = new ArrayList<>();
        int rowOffset = endRow - startRow;
        int colOffset = endCol - startCol;
        generateConfig(startRow, startCol, rowOffset / JUMP_MULTIPLIER, colOffset / JUMP_MULTIPLIER, config, false);
        if (!config.isEmpty()) {
            return (HoppersConfig) config.get(0);
        }
        return null;
    }

    /**
     * Adds new configurations to the given ArrayList by moving the frog on the given space by the given offset
     * The offset should first be set to land on a frog, then it will check the cell beyond that in the direction of the offsets
     *
     * @param curRow starting row number (y1)
     * @param curCol starting column number (x1)
     * @param rowOffset amount to change the row by (delta y)
     * @param colOffset amount to change the column by (delta x)
     * @param configurations configuration list to add to
     * @param canLand used for recursive calls, should be set to false when called by an outside function
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
                        generateConfig(curRow,
                                curCol,
                                rowOffset * JUMP_MULTIPLIER,
                                colOffset * JUMP_MULTIPLIER,
                                configurations,
                                true
                        );
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
                        newGrid[row - rowOffset / JUMP_MULTIPLIER][col - colOffset / JUMP_MULTIPLIER] = cellContents.EMPTY;
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

    /**
     * Checks if a given cell is even, and therefore has orthagonal neighbors
     * @param row row number
     * @param col column number
     * @return whether the cell is even
     */
    private boolean isEvenCell(int row, int col) {
        return row % 2 == 0;
    }

    /**
     * returns an empty grid with the size specified by ROWS and COLS
     * @return empty grid
     */
    private cellContents[][] getEmptyGrid() {
        return new cellContents[ROWS][COLS];
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

    /**
     * Zobrist Hashing must be initialized with random numbers before it is used
     * This method creates a grid of random integers
     */
    private static void initZobrist() {
        /*
        Zobrist hashing because I'm a nerd
        This is based on the pseudocode implementation for chess in https://en.wikipedia.org/wiki/Zobrist_hashing
        Yes I could have used java.util.Arrays.deepHashCode( grid ); but I don't want to
        This part initializes the hashing algorithm
        */
        zobristInitalized=true;
        Random rand = new Random(24); //allowed to be predictably random, just can't be procedural based on row/column
        //the unchanging seed also means this method can be run multiple times without changing hash values of existing config instance
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

    /**
     * Returns an integer representation of the current configuration. This uses Zohurst Hashing because it's cool
     *
     * @return hash representation
     */
    public int hashCode() {
        //initalize if not already done
        if (!zobristInitalized){
            initZobrist();
        }
        /*TODO if i have the time: implement methods for rehashing to speed this up
        a benefit of Zohurst is you can XOR cells in and out when you make changes without having to recompute the entire hash*/
        int hash = 0;
        //loop over the board positions
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                cellContents cell = grid[row][col];
                if (cell != cellContents.EMPTY) {
                    //EMPTY serves as the #0 ordinal, so no need to add +1 when getting value to change by
                    //the ^ means XOR
                    hash = hash ^ zobristTable[row][col][cell.ordinal()];
                }
            }
        }
        //System.out.println("hash for "+this.toString().replace('\n','|')+ " is "+hash);
        return hash;
    }

    /**
     * Get the contents of a cell at the given coordinates
     *
     * @param row row numbers
     * @param col column numbers
     * @return contents of the given cell
     */
    public cellContents get(int row, int col) {
        return grid[row][col];
    }
}
