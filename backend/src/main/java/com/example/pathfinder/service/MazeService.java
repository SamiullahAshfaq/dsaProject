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
        divideWithSteps(grid, 1, 1, cols - 2, rows - 2, start, end, steps);
        
        return new MazeGenerationResult(grid, steps);
    }

    private void divideWithSteps(Tile[][] grid, int x, int y, int width, int height, 
                                Position start, Position end, List<MazeStep> steps) {
        if (width < 2 || height < 2) {
            return;
        }

        boolean horizontal = chooseOrientation(width, height);
        
        if (horizontal) {
            // Horizontal division
            int wallY = y + (random.nextInt((height - 1) / 2) * 2) + 1;
            int passageX = x + random.nextInt(width);
            
            // Create horizontal wall and record steps
            for (int i = x; i < x + width; i++) {
                if (i == passageX || 
                    (i == start.getCol() && wallY == start.getRow()) || 
                    (i == end.getCol() && wallY == end.getRow())) {
                    continue;
                }
                
                if (wallY < grid.length && i < grid[0].length) {
                    grid[wallY][i].setWall(true);
                    steps.add(new MazeStep(wallY, i, true, "wall"));
                }
            }
            
            // Recursive calls
            divideWithSteps(grid, x, y, width, wallY - y, start, end, steps);
            divideWithSteps(grid, x, wallY + 1, width, height - (wallY - y + 1), start, end, steps);
            
        } else {
            // Vertical division
            int wallX = x + (random.nextInt((width - 1) / 2) * 2) + 1;
            int passageY = y + random.nextInt(height);
            
            // Create vertical wall and record steps
            for (int i = y; i < y + height; i++) {
                if (i == passageY || 
                    (wallX == start.getCol() && i == start.getRow()) || 
                    (wallX == end.getCol() && i == end.getRow())) {
                    continue;
                }
                
                if (i < grid.length && wallX < grid[0].length) {
                    grid[i][wallX].setWall(true);
                    steps.add(new MazeStep(i, wallX, true, "wall"));
                }
            }
            
            // Recursive calls
            divideWithSteps(grid, x, y, wallX - x, height, start, end, steps);
            divideWithSteps(grid, wallX + 1, y, width - (wallX - x + 1), height, start, end, steps);
        }
    }

    public MazeGenerationResult generateBinaryTreeMaze(int rows, int cols, Position start, Position end) {
        Tile[][] grid = new Tile[rows][cols];
        List<MazeStep> steps = new ArrayList<>();
        
        // Initialize all tiles as walls
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = new Tile();
                grid[i][j].setWall(true);
                steps.add(new MazeStep(i, j, true, "wall"));
            }
        }
        
        // Carve passages and record steps
        for (int y = 1; y < rows; y += 2) {
            for (int x = 1; x < cols; x += 2) {
                // Make current cell a passage
                grid[y][x].setWall(false);
                steps.add(new MazeStep(y, x, false, "passage"));
                
                // Decide which direction to carve
                boolean canGoNorth = (y > 1);
                boolean canGoWest = (x > 1);
                
                if (canGoNorth && canGoWest) {
                    if (random.nextBoolean()) {
                        grid[y - 1][x].setWall(false);
                        steps.add(new MazeStep(y - 1, x, false, "passage"));
                    } else {
                        grid[y][x - 1].setWall(false);
                        steps.add(new MazeStep(y, x - 1, false, "passage"));
                    }
                } else if (canGoNorth) {
                    grid[y - 1][x].setWall(false);
                    steps.add(new MazeStep(y - 1, x, false, "passage"));
                } else if (canGoWest) {
                    grid[y][x - 1].setWall(false);
                    steps.add(new MazeStep(y, x - 1, false, "passage"));
                }
            }
        }
        
        // Ensure start and end are passages
        grid[start.getRow()][start.getCol()].setWall(false);
        grid[end.getRow()][end.getCol()].setWall(false);
        
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