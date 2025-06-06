package com.example.pathfinder.service;

import com.example.pathfinder.model.Tile;
import com.example.pathfinder.model.Position;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MazeService {
    private final Random random = new Random();
    
    // Step class to represent animation steps
    public static class MazeStep {
        private int row;
        private int col;
        private boolean isWall;
        private String stepType; // "wall", "passage", "border"
        
        public MazeStep(int row, int col, boolean isWall, String stepType) {
            this.row = row;
            this.col = col;
            this.isWall = isWall;
            this.stepType = stepType;
        }
        
        // Getters
        public int getRow() { return row; }
        public int getCol() { return col; }
        public boolean isWall() { return isWall; }
        public String getStepType() { return stepType; }
    }
    
    // Response class for maze generation
    public static class MazeGenerationResult {
        private Tile[][] finalMaze;
        private List<MazeStep> animationSteps;
        
        public MazeGenerationResult(Tile[][] finalMaze, List<MazeStep> animationSteps) {
            this.finalMaze = finalMaze;
            this.animationSteps = animationSteps;
        }
        
        public Tile[][] getFinalMaze() { return finalMaze; }
        public List<MazeStep> getAnimationSteps() { return animationSteps; }
    }

    public MazeGenerationResult generateRecursiveDivisionMaze(int rows, int cols, Position start, Position end) {
        Tile[][] grid = new Tile[rows][cols];
        List<MazeStep> steps = new ArrayList<>();
        
        // Initialize grid
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = new Tile();
                grid[i][j].setWall(false);
            }
        }
        
        // Create border walls and record steps
        for (int i = 0; i < rows; i++) {
            grid[i][0].setWall(true);
            grid[i][cols - 1].setWall(true);
            steps.add(new MazeStep(i, 0, true, "border"));
            steps.add(new MazeStep(i, cols - 1, true, "border"));
        }
        for (int j = 1; j < cols - 1; j++) {
            grid[0][j].setWall(true);
            grid[rows - 1][j].setWall(true);
            steps.add(new MazeStep(0, j, true, "border"));
            steps.add(new MazeStep(rows - 1, j, true, "border"));
        }
        
        // Ensure start and end are passages
        grid[start.getRow()][start.getCol()].setWall(false);
        grid[end.getRow()][end.getCol()].setWall(false);
        
        // Generate maze with recorded steps
        divide(grid, 1, 1, cols - 2, rows - 2, start, end, steps);
        
        return new MazeGenerationResult(grid, steps);
    }

    private void divide(Tile[][] grid, int row, int col, int height, int width,
                    Position start, Position end, List<MazeStep> steps) {
    if (height <= 1 || width <= 1) return;

    if (height > width) {
        // Horizontal division
        int makeWallAt = row + random.nextInt(height / 2) * 2 + 1;
        int makePassageAt = col + random.nextInt((width + 1) / 2) * 2;

        for (int i = 0; i < 2 * width - 1; i++) {
            int currentCol = col + i;
            if (makePassageAt == currentCol ||
                (makeWallAt == start.getRow() && currentCol == start.getCol()) ||
                (makeWallAt == end.getRow() && currentCol == end.getCol())) {
                continue;
            }

            if (isInBounds(grid, makeWallAt, currentCol)) {
                grid[makeWallAt][currentCol].setWall(true);
                steps.add(new MazeStep(makeWallAt, currentCol, true, "wall"));
            }
        }

        // Recursively divide above and below
        divide(grid, row, col, (makeWallAt - row + 1) / 2, width, start, end, steps);
        divide(grid, makeWallAt + 1, col, height - (makeWallAt - row + 1) / 2, width, start, end, steps);

    } else {
        // Vertical division
        int makeWallAt = col + random.nextInt(width / 2) * 2 + 1;
        int makePassageAt = row + random.nextInt((height + 1) / 2) * 2;

        for (int i = 0; i < 2 * height - 1; i++) {
            int currentRow = row + i;
            if (makePassageAt == currentRow ||
                (currentRow == start.getRow() && makeWallAt == start.getCol()) ||
                (currentRow == end.getRow() && makeWallAt == end.getCol())) {
                continue;
            }

            if (isInBounds(grid, currentRow, makeWallAt)) {
                grid[currentRow][makeWallAt].setWall(true);
                steps.add(new MazeStep(currentRow, makeWallAt, true, "wall"));
            }
        }

        // Recursively divide left and right
        divide(grid, row, col, height, (makeWallAt - col + 1) / 2, start, end, steps);
        divide(grid, row, makeWallAt + 1, height, width - (makeWallAt - col + 1) / 2, start, end, steps);
    }
}

    private boolean isInBounds(Tile[][] grid, int r, int c) {
        return r >= 0 && r < grid.length && c >= 0 && c < grid[0].length;
    }


    public MazeGenerationResult generateBinaryTreeMaze(int rows, int cols, Position start, Position end) {
    Tile[][] grid = new Tile[rows][cols];
    List<MazeStep> steps = new ArrayList<>();

    // Step 1: Set all even-indexed cells as walls (outer boundary too)
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            grid[i][j] = new Tile();
            boolean isEven = i % 2 == 0 || j % 2 == 0;
            grid[i][j].setWall(isEven);
            steps.add(new MazeStep(i, j, isEven, isEven ? "wall" : "passage"));
        }
    }

    // Step 2: For each odd cell, remove wall to the right or down
    for (int r = 1; r < rows; r += 2) {
        for (int c = 1; c < cols; c += 2) {
            if (r == rows - 2 && c == cols - 2) continue; // bottom-right skip

            if (r == rows - 2) {
                // last row — destroy right
                if (c + 1 < cols) {
                    grid[r][c + 1].setWall(false);
                    steps.add(new MazeStep(r, c + 1, false, "passage"));
                }
            } else if (c == cols - 2) {
                // last col — destroy down
                if (r + 1 < rows) {
                    grid[r + 1][c].setWall(false);
                    steps.add(new MazeStep(r + 1, c, false, "passage"));
                }
            } else {
                // randomly right or down
                if (random.nextBoolean()) {
                    if (c + 1 < cols) {
                        grid[r][c + 1].setWall(false);
                        steps.add(new MazeStep(r, c + 1, false, "passage"));
                    }
                } else {
                    if (r + 1 < rows) {
                        grid[r + 1][c].setWall(false);
                        steps.add(new MazeStep(r + 1, c, false, "passage"));
                    }
                }
            }
        }
    }

    // Ensure start and end are passages
    grid[start.getRow()][start.getCol()].setWall(false);
    grid[end.getRow()][end.getCol()].setWall(false);
    steps.add(new MazeStep(start.getRow(), start.getCol(), false, "passage"));
    steps.add(new MazeStep(end.getRow(), end.getCol(), false, "passage"));

    return new MazeGenerationResult(grid, steps);
}

    
    private boolean chooseOrientation(int width, int height) {
        if (width < height) {
            return true; // horizontal
        } else if (height < width) {
            return false; // vertical  
        } else {
            return random.nextBoolean(); // random for squares
        }
    }
}