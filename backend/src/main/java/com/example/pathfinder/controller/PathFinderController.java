// Fixed Controller with proper CORS and error handling
package com.example.pathfinder.controller;

import com.example.pathfinder.model.GridResponse;
import com.example.pathfinder.model.Position;
import com.example.pathfinder.model.Tile;
import com.example.pathfinder.service.MazeService;
import com.example.pathfinder.service.MazeService.MazeGenerationResult;
import com.example.pathfinder.service.PathfindingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
public class PathFinderController {
    
    private Tile[][] latestMazeGrid; // Store last generated maze
    private static final Logger logger = LoggerFactory.getLogger(PathFinderController.class);

    @Autowired
    private PathfindingService pathfindingService;

    @Autowired
    private MazeService mazeService;

    // Pathfinding Algorithm Endpoint
    @GetMapping("/pathfinding")
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
        if (latestMazeGrid == null) {
            throw new IllegalStateException("No maze has been generated yet.");
        }
        Tile[][] grid = deepCopyGrid(latestMazeGrid);
        Tile start = grid[startY][startX];
        Tile end = grid[endY][endX];
        start.setWall(false);
        end.setWall(false);

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

    // Maze Generation Unified Endpoint with better error handling
    @PostMapping("/maze/{mazeType}")
    public ResponseEntity<?> generateMaze(
        @PathVariable String mazeType,
        @RequestParam int rows,
        @RequestParam int cols,
        @RequestParam int startRow,
        @RequestParam int startCol,
        @RequestParam int endRow,
        @RequestParam int endCol
    ) {
        try {
            logger.info("Maze generation request: type={}, rows={}, cols={}, start=({}, {}), end=({}, {})",
                    mazeType, rows, cols, startRow, startCol, endRow, endCol);

            // Validate parameters
            if (rows <= 0 || cols <= 0) {
                return ResponseEntity.badRequest().body("Rows and columns must be greater than 0");
            }
            
            if (startRow < 0 || startRow >= rows || startCol < 0 || startCol >= cols) {
                return ResponseEntity.badRequest().body("Start coordinates out of bounds");
            }
            
            if (endRow < 0 || endRow >= rows || endCol < 0 || endCol >= cols) {
                return ResponseEntity.badRequest().body("End coordinates out of bounds");
            }

            Position start = new Position(startRow, startCol);
            Position end = new Position(endRow, endCol);
            
            MazeGenerationResult result;
            
            if (null == mazeType) {
                return ResponseEntity.badRequest().body("Invalid maze type: " + mazeType);
            } else switch (mazeType) {
                case "BINARY_TREE" ->{ result = mazeService.generateBinaryTreeMaze(rows, cols, start, end);
                                      latestMazeGrid = result.getFinalMaze(); // Store the latest maze grid
                                    }
                default -> {
                    return ResponseEntity.badRequest().body("Invalid maze type: " + mazeType);
                }
            }
            
            logger.info("Maze generation completed successfully. Steps: {}", 
                       result.getAnimationSteps().size());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("Error generating maze", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error generating maze: " + e.getMessage());
        }
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

    // Add row and col properties to copied tiles
private Tile[][] deepCopyGrid(Tile[][] original) {
    int rows = original.length;
    int cols = original[0].length;
    Tile[][] copy = new Tile[rows][cols];
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            Tile orig = original[i][j];
            Tile t = new Tile(i, j); // Set correct row/col
            t.setWall(orig.isWall);
            // Initialize pathfinding properties
            t.distance = Integer.MAX_VALUE;
            t.isTraversed = false;
            t.parent = null;
            copy[i][j] = t;
        }
    }
    return copy;
}

}