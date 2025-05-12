package com.example.pathfinder.model;

import java.util.List;

public class GridResponse {
    public List<Tile> traversedTiles;
    public List<Tile> path;

    public GridResponse(List<Tile> traversedTiles, List<Tile> path) {
        this.traversedTiles = traversedTiles;
        this.path = path;
    }
}

