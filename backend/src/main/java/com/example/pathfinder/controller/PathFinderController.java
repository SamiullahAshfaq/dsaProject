package com.example.pathfinder.controller;

import com.example.pathfinder.model.GridResponse;
import com.example.pathfinder.model.Tile;
import com.example.pathfinder.service.MazeService;
import com.example.pathfinder.service.PathfindingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Simple Position class definition (add this if not already present elsewhere)
class Position {
    public int row;
    public int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }
}

@RestController
@RequestMapping("/api")
public class PathFinderController {

    private static final Logger logger = LoggerFactory.getLogger(PathFinderController.class);

    @Autowired
    private PathfindingService pathfindingService;

    @Autowired
    private MazeService mazeService;

    // Pathfinding Algorithm Endpoint
    @PostMapping("/pathfinding")
    public GridResponse getPath(@RequestParam String algorithm,
                                @RequestParam int rows,
                                @RequestParam int cols,
                                @RequestParam int startX,
                                @RequestParam int startY,
                                @RequestParam int endX,
                                @RequestParam int endY) {

        logger.info("Pathfinding request: algorithm={}, rows={}, cols={}, start=({}, {}), end=({}, {})",
                algorithm, rows, cols, startX, startY, endX, endY);

        validateGridParameters(rows, cols, startX, startY, endX, endY);
        Tile[][] grid = createGrid(rows, cols);
        Tile start = grid[startY][startX];
        Tile end = grid[endY][endX];

        Algorithm algo;
        try {
            algo = Algorithm.valueOf(algorithm.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid algorithm: " + algorithm);
        }

        return switch (algo) {
            case DIJKSTRA -> pathfindingService.runDijkstra(grid, start, end);
            case A_STAR -> pathfindingService.runAStar(grid, start, end);
            case BFS -> pathfindingService.runBFS(grid, start, end);
            case DFS -> pathfindingService.runDFS(grid, start, end);
        };
    }

    // Maze Generation Unified Endpoint
    @PostMapping("/maze/{type}")
    public Tile[][] generateMaze(@PathVariable String type,
                                 @RequestParam int rows,
                                 @RequestParam int cols) {

        logger.info("Maze generation request: type={}, rows={}, cols={}", type, rows, cols);

        Tile[][] grid = createGrid(rows, cols);
        MazeType mazeType;

        // Define start and end positions for the maze (e.g., top-left and bottom-right corners)
        Position start = new Position(0, 0);
        Position end = new Position(rows - 1, cols - 1);

        try {
            mazeType = MazeType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid maze type: " + type);
        }

        switch (mazeType) {
            case RECURSIVE_DIVISION -> mazeService.generateRecursiveDivisionMaze(grid, start, end);
            case BINARY_TREE -> mazeService.generateBinaryTreeMaze(grid);
        }

        return grid;
    }

    // Helper: Grid Creation
    private Tile[][] createGrid(int rows, int cols) {
        Tile[][] grid = new Tile[rows][cols];
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                grid[y][x] = new Tile();
            }
        }
        return grid;
    }

    // Helper: Validate Grid Inputs
    private void validateGridParameters(int rows, int cols, int startX, int startY, int endX, int endY) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Rows and columns must be greater than 0.");
        }
        if (startX < 0 || startX >= cols || startY < 0 || startY >= rows) {
            throw new IllegalArgumentException("Start coordinates out of bounds.");
        }
        if (endX < 0 || endX >= cols || endY < 0 || endY >= rows) {
            throw new IllegalArgumentException("End coordinates out of bounds.");
        }
    }
}
