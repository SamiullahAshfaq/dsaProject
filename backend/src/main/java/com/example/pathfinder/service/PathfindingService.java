package com.example.pathfinder.service;

import java.util.*;

import com.example.pathfinder.model.GridResponse;
import com.example.pathfinder.model.Tile;
import org.springframework.stereotype.Service;

@Service
public class PathfindingService {

    // Dijkstra Algorithm
    public GridResponse runDijkstra(Tile[][] grid, Tile start, Tile end) {
        initializeTiles(grid);
        List<Tile> traversedTiles = new ArrayList<>();
        PriorityQueue<Tile> untraversedTiles = new PriorityQueue<>(Comparator.comparingInt(t -> t.distance));
        start.distance = 0;
        start.isTraversed = true;
        untraversedTiles.add(start);

        while (!untraversedTiles.isEmpty()) {
            Tile currentTile = untraversedTiles.poll();
            if (currentTile.isWall) continue;
            if (currentTile.distance == Integer.MAX_VALUE) break;
            currentTile.isTraversed = true;
            traversedTiles.add(currentTile);
            if (currentTile.equals(end)) break;

            for (Tile neighbor : getUntraversedNeighbors(grid, currentTile)) {
                int distanceToNeighbor = currentTile.distance + 1;
                if (distanceToNeighbor < neighbor.distance) {
                    neighbor.distance = distanceToNeighbor;
                    neighbor.parent = currentTile;
                    untraversedTiles.add(neighbor);
                }
            }
        }

        List<Tile> path = backtrackPath(end);
        return new GridResponse(traversedTiles, path);
    }

    // A* Algorithm
    public GridResponse runAStar(Tile[][] grid, Tile start, Tile end) {
        initializeTiles(grid);
        List<Tile> traversedTiles = new ArrayList<>();
        Map<Tile, Integer> heuristicCost = initHeuristicCost(grid, end);
        Map<Tile, Integer> functionCost = new HashMap<>();
        PriorityQueue<Tile> untraversedTiles = new PriorityQueue<>(Comparator.comparingInt(functionCost::get));

        start.distance = 0;
        functionCost.put(start, heuristicCost.getOrDefault(start, 0));
        start.isTraversed = true;
        untraversedTiles.add(start);

        while (!untraversedTiles.isEmpty()) {
            Tile currentTile = untraversedTiles.poll();
            if (currentTile.isWall) continue;
            if (currentTile.distance == Integer.MAX_VALUE) break;
            currentTile.isTraversed = true;
            traversedTiles.add(currentTile);
            if (currentTile.equals(end)) break;

            for (Tile neighbor : getUntraversedNeighbors(grid, currentTile)) {
                int tentativeDistance = currentTile.distance + 1;
                if (tentativeDistance < neighbor.distance) {
                    neighbor.distance = tentativeDistance;
                    functionCost.put(neighbor, tentativeDistance + heuristicCost.getOrDefault(neighbor, 0));
                    neighbor.parent = currentTile;
                    untraversedTiles.add(neighbor);
                }
            }
        }

        List<Tile> path = backtrackPath(end);
        return new GridResponse(traversedTiles, path);
    }

    // BFS Algorithm
    public GridResponse runBFS(Tile[][] grid, Tile start, Tile end) {
        initializeTiles(grid);
        List<Tile> traversedTiles = new ArrayList<>();
        Queue<Tile> unTraversed = new LinkedList<>();
        start.distance = 0;
        start.isTraversed = true;
        unTraversed.add(start);

        while (!unTraversed.isEmpty()) {
            Tile tile = unTraversed.poll();
            if (tile.isWall) continue;
            if (tile.distance == Integer.MAX_VALUE) break;
            tile.isTraversed = true;
            traversedTiles.add(tile);
            if (tile.equals(end)) break;

            for (Tile neighbor : getUntraversedNeighbors(grid, tile)) {
                if (neighbor.distance == Integer.MAX_VALUE) {  // Only add unvisited neighbors
                    neighbor.distance = tile.distance + 1;
                    neighbor.parent = tile;
                    unTraversed.add(neighbor);
    }
}
        }

        List<Tile> path = backtrackPath(end);
        return new GridResponse(traversedTiles, path);
    }

    // DFS Algorithm
    public GridResponse runDFS(Tile[][] grid, Tile start, Tile end) {
        initializeTiles(grid);
        List<Tile> traversedTiles = new ArrayList<>();
        Stack<Tile> unTraversed = new Stack<>();
        start.distance = 0;
        start.isTraversed = true;
        unTraversed.push(start);

        while (!unTraversed.isEmpty()) {
            Tile currentTile = unTraversed.pop();
            if (currentTile.isWall) continue;
            if (currentTile.distance == Integer.MAX_VALUE) break;
            currentTile.isTraversed = true;
            traversedTiles.add(currentTile);
            if (currentTile.equals(end)) break;

            for (Tile neighbor : getUntraversedNeighbors(grid, currentTile)) {
                neighbor.distance = currentTile.distance + 1;
                neighbor.parent = currentTile;
                unTraversed.push(neighbor);
            }
        }

        List<Tile> path = backtrackPath(end);
        return new GridResponse(traversedTiles, path);
    }

    // Helper methods
    private List<Tile> getUntraversedNeighbors(Tile[][] grid, Tile tile) {
        List<Tile> neighbors = new ArrayList<>();
        int[][] directions = {{0,1},{1,0},{0,-1},{-1,0}};
        for (int[] d : directions) {
            int newRow = tile.row + d[0];
            int newCol = tile.col + d[1];
            if (newRow >= 0 && newRow < grid.length && newCol >= 0 && newCol < grid[0].length) {
                Tile neighbor = grid[newRow][newCol];
                if (!neighbor.isTraversed && !neighbor.isWall) {
                    neighbors.add(neighbor);
                }
            }
        }
        return neighbors;
    }

    private Map<Tile, Integer> initHeuristicCost(Tile[][] grid, Tile end) {
        Map<Tile, Integer> heuristicCost = new HashMap<>();
        for (Tile[] row : grid) {
            for (Tile tile : row) {
                int cost = Math.abs(tile.row - end.row) + Math.abs(tile.col - end.col);
                heuristicCost.put(tile, cost);
            }
        }
        return heuristicCost;
    }

    // Add this at the start of each pathfinding algorithm
    private void initializeTiles(Tile[][] grid) {
        for (Tile[] row : grid) {
            for (Tile tile : row) {
                tile.distance = Integer.MAX_VALUE;
                tile.isTraversed = false;
                tile.parent = null;
        }
    }
}

    private List<Tile> backtrackPath(Tile endTile) {
        List<Tile> path = new ArrayList<>();
        Tile current = endTile;
        while (current != null) {
            path.add(current);
            current = current.parent;
        }
        Collections.reverse(path);
        return path;
    }
}
