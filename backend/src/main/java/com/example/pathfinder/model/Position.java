package com.example.pathfinder.model;

public class Position {

    
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    private int row;
    private int col;

    public int getRow() {
        return row;
    }
    
    public void setRow(int row) {
        this.row = row;
    }
    
    public int getCol() {
        return col;
    }
    
    public void setCol(int col) {
        this.col = col;
    }
}

