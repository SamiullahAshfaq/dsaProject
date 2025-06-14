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

}