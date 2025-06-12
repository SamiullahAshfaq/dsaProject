package com.example.pathfinder.model;

public class Tile {
    public int row;
    public int col;
    public boolean isWall;
    public boolean isPath;
    public boolean isTraversed;
    public int distance = Integer.MAX_VALUE;
    public Tile parent;

    public Tile() {} // Required for Jackson

    public Tile(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public Tile(Tile other) {
    this.row = other.row;
    this.col = other.col;
    this.isWall = other.isWall;
    this.isPath = other.isPath;
    this.isTraversed = other.isTraversed;
    this.distance = other.distance;
    this.parent = other.parent; // optional: you may skip parent if unused in copy
}


    public boolean isWall() {
        return isWall;
    }

    public void setWall(boolean wall) {
        isWall = wall;
    }
}
