package com.example.pathfinder.service;

import com.example.pathfinder.model.Tile;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class MazeService {

    private final Random random = new Random();

    public void generateRecursiveDivisionMaze(Tile[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;
        // Initialize all tiles as walls
        for (Tile[] row : grid) {
            for (Tile tile : row) {
                tile.setWall(true);
            }
        }
        divide(grid, 0, 0, cols, rows);
    }

    private void divide(Tile[][] grid, int x, int y, int width, int height) {
        if (width <= 2 || height <= 2) {
            return;
        }

        boolean horizontal = width < height;

        int wx = x + (horizontal ? 0 : random.nextInt(width - 2));
        int wy = y + (horizontal ? random.nextInt(height - 2) : 0);

        int px = wx + (horizontal ? random.nextInt(width) : 0);
        int py = wy + (horizontal ? 0 : random.nextInt(height));

        int dx = horizontal ? 1 : 0;
        int dy = horizontal ? 0 : 1;

        int length = horizontal ? width : height;

        for (int i = 0; i < length; i++) {
            int nx = wx + i * dx;
            int ny = wy + i * dy;
            if (nx != px || ny != py) {
                grid[ny][nx].setWall(true);
            }
        }

        int nx = x;
        int ny = y;
        int w = horizontal ? width : wx - x + 1;
        int h = horizontal ? wy - y + 1 : height;
        divide(grid, nx, ny, w, h);

        nx = horizontal ? x : wx + 1;
        ny = horizontal ? wy + 1 : y;
        w = horizontal ? width : x + width - wx - 1;
        h = horizontal ? y + height - wy - 1 : height;
        divide(grid, nx, ny, w, h);
    }

    public void generateBinaryTreeMaze(Tile[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;
        // Initialize all tiles as walls
        for (Tile[] row : grid) {
            for (Tile tile : row) {
                tile.setWall(true);
            }
        }
        // Carve passages
        for (int y = 1; y < rows; y += 2) {
            for (int x = 1; x < cols; x += 2) {
                grid[y][x].setWall(false);
                if (x > 1 && y > 1) {
                    boolean carveNorth = random.nextBoolean();
                    if (carveNorth) {
                        grid[y - 1][x].setWall(false);
                    } else {
                        grid[y][x - 1].setWall(false);
                    }
                } else if (x > 1) {
                    grid[y][x - 1].setWall(false);
                } else if (y > 1) {
                    grid[y - 1][x].setWall(false);
                }
            }
        }
    }
}
