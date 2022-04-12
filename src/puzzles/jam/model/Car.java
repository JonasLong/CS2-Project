package puzzles.jam.model;

public class Car {
    private boolean movesHorizontal;
    private int startRow;
    private int startCol;
    private int endRow;
    private int endCol;

    public Car(int startCol, int startRow, int endCol, int endRow){
        this.startCol = startCol;
        this.startRow = startRow;
        this.endRow = endRow;
        this.endCol = endCol;
        if (this.startRow == this.endRow){
            this.movesHorizontal = true;
        } else {
            this.movesHorizontal = false;
        }
    }

    public boolean isMovesHorizontal() {
        return movesHorizontal;
    }

    public int getEndCol() {
        return endCol;
    }

    public int getEndRow() {
        return endRow;
    }

    public int getStartCol() {
        return startCol;
    }

    public int getStartRow() {
        return startRow;
    }
}
