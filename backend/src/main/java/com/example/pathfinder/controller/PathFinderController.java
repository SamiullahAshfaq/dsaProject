package com.example.pathfinder.controller;

import com.example.pathfinder.model.GridResponse;
import com.example.pathfinder.model.Position;
import com.example.pathfinder.model.Tile;
import com.example.pathfinder.service.MazeService;
import com.example.pathfinder.service.MazeService.MazeGenerationResult;
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
    @PostMapping("/maze/{mazeType}")
    public MazeGenerationResult generateMaze(
        @PathVariable String mazeType,
        @RequestParam int rows,
        @RequestParam int cols,
        @RequestParam int startRow,
        @RequestParam int startCol,
        @RequestParam int endRow,
        @RequestParam int endCol
    ) {
        logger.info("Maze generation request: type={}, rows={}, cols={}, start=({}, {}), end=({}, {})",
                mazeType, rows, cols, startRow, startCol, endRow, endCol);

        Position start = new Position(startRow, startCol);
        Position end = new Position(endRow, endCol);
        
        if ("BINARY_TREE".equals(mazeType)) {
            return mazeService.generateBinaryTreeMaze(rows, cols, start, end);
        } else if ("RECURSIVE_DIVISION".equals(mazeType)) {
            return mazeService.generateRecursiveDivisionMaze(rows, cols, start, end);
        } else {
            throw new IllegalArgumentException("Invalid maze type: " + mazeType);
        }
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