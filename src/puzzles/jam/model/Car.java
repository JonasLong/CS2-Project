package puzzles.jam.model;

public class Car {
    private boolean movesHorizontal;
    private int startRow;
    private int startCol;
    private int endRow;
    private int endCol;
    private Character name;

    public Car(int startRow, int startCol, int endRow, int endCol, Character name){
        this.startCol = startCol;
        this.startRow = startRow;
        this.endRow = endRow;
        this.endCol = endCol;
        this.name = name;
        if (this.startRow == this.endRow){
            this.movesHorizontal = true;
        } else {
            this.movesHorizontal = false;
        }
    }

    public boolean movesHorizontal() {
        return movesHorizontal;
    }

    public void setEndCol(int endCol) {
        this.endCol = endCol;
    }

    public int getEndCol() {
        return endCol;
    }

    public void setEndRow(int endRow) {
        this.endRow = endRow;
    }

    public int getEndRow() {
        return endRow;
    }

    public void setStartCol(int startCol) {
        this.startCol = startCol;
    }

    public int getStartCol() {
        return startCol;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public int getStartRow() {
        return startRow;
    }

    public Character getName() {
        return name;
    }

    public void setName(Character name) {
        this.name = name;
    }
}
