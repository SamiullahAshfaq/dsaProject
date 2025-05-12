package com.example.pathfinder.controller;

import com.example.pathfinder.model.GridResponse;
import com.example.pathfinder.model.Tile;
import com.example.pathfinder.service.MazeService;
import com.example.pathfinder.service.PathfindingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PathFinderController {

    @Autowired
    private PathfindingService pathfindingService;

    @Autowired
    private MazeService mazeService;

    // Pathfinding Algorithm Endpoints
    @PostMapping("/pathfinding")
    public GridResponse getPath(@RequestParam String algorithm,
                                @RequestParam int rows,
                                @RequestParam int cols,
                                @RequestParam int startX,
                                @RequestParam int startY,
                                @RequestParam int endX,
                                @RequestParam int endY) {
        Tile[][] grid = createGrid(rows, cols);
        Tile start = grid[startY][startX];
        Tile end = grid[endY][endX];

        switch (algorithm.toUpperCase()) {
            case "DIJKSTRA":
                return pathfindingService.runDijkstra(grid, start, end);
            case "A_STAR":
                return pathfindingService.runAStar(grid, start, end);
            case "BFS":
                return pathfindingService.runBFS(grid, start, end);
            case "DFS":
                return pathfindingService.runDFS(grid, start, end);
            default:
                throw new IllegalArgumentException("Invalid algorithm: " + algorithm);
        }
    }

    // Maze Generation Algorithm Endpoints
    @PostMapping("/maze/recursive-division")
    public Tile[][] getRecursiveDivisionMaze(@RequestParam int rows, @RequestParam int cols) {
        Tile[][] grid = createGrid(rows, cols);
        mazeService.generateRecursiveDivisionMaze(grid);
        return grid;
    }

    @PostMapping("/maze/binary-tree")
    public Tile[][] getBinaryTreeMaze(@RequestParam int rows, @RequestParam int cols) {
        Tile[][] grid = createGrid(rows, cols);
        mazeService.generateBinaryTreeMaze(grid);
        return grid;
    }

    // Helper method to create the grid
    private Tile[][] createGrid(int rows, int cols) {
        Tile[][] grid = new Tile[rows][cols];
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                grid[y][x] = new Tile();  // Initializing each tile
            }
        }
        return grid;
    }
}
