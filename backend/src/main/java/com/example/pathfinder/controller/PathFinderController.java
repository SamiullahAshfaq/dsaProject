package com.example.pathfinder.controller;

import com.example.pathfinder.model.GridResponse;
import com.example.pathfinder.model.Tile;
import com.example.pathfinder.service.MazeService;
import com.example.pathfinder.service.PathfindingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
public class PathFinderController {

    private static final Logger logger = LoggerFactory.getLogger(PathFinderController.class);

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
        logger.info("Pathfinding request received: algorithm={}, rows={}, cols={}, start=({}, {}), end=({}, {})",
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

        switch (algo) {
            case DIJKSTRA -> {
                return pathfindingService.runDijkstra(grid, start, end);
            }
            case A_STAR -> {
                return pathfindingService.runAStar(grid, start, end);
            }
            case BFS -> {
                return pathfindingService.runBFS(grid, start, end);
            }
            case DFS -> {
                return pathfindingService.runDFS(grid, start, end);
            }
            default -> throw new IllegalArgumentException("Unsupported algorithm: " + algorithm);
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

    private void validateGridParameters(int rows, int cols, int startX, int startY, int endX, int endY) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Rows and columns must be greater than 0.");
        }
        if (startX < 0 || startX >= cols || startY < 0 || startY >= rows) {
            throw new IllegalArgumentException("Start coordinates are out of bounds.");
        }
        if (endX < 0 || endX >= cols || endY < 0 || endY >= rows) {
            throw new IllegalArgumentException("End coordinates are out of bounds.");
        }
    }
}
