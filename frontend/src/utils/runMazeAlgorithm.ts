import {GridType, MazeType, SpeedType, TileType} from "./types";
import { sleep } from "./helpers";
import { MAX_ROWS, SPEEDS, WALL_TILE_STYLE, TILE_STYLE, START_TILE_STYLE, END_TILE_STYLE } from "./constants";

// Interface for backend animation steps
interface MazeStep {
  row: number;
  col: number;
  isWall: boolean;
  stepType: 'wall' | 'passage' | 'border';
}

interface MazeGenerationResult {
  finalMaze: GridType;
  animationSteps: MazeStep[];
}

// Helper function to get the correct tile style based on position and tile properties
const getTileStyle = (tile: TileType, startTile: TileType, endTile: TileType): string => {
  // Check by position first (most reliable)
  if (tile.row === startTile.row && tile.col === startTile.col) return START_TILE_STYLE;
  if (tile.row === endTile.row && tile.col === endTile.col) return END_TILE_STYLE;
  
  // Fallback to properties
  if (tile.isStart) return START_TILE_STYLE;
  if (tile.isEnd) return END_TILE_STYLE;
  if (tile.isWall) return WALL_TILE_STYLE;
  return TILE_STYLE;
};

// Helper function to check if a tile is start or end
const isStartOrEndTile = (row: number, col: number, startTile: TileType, endTile: TileType): boolean => {
  return (row === startTile.row && col === startTile.col) || 
         (row === endTile.row && col === endTile.col);
};

// Animate binary tree maze generation
const animateBinaryTreeGeneration = async (
  grid: GridType,
  steps: MazeStep[],
  startTile: TileType,
  endTile: TileType,
  speed: SpeedType
): Promise<void> => {
  // First, create all walls quickly (similar to createWall function)
  const wallSteps = steps.filter(step => step.stepType === 'wall');
  for (const step of wallSteps) {
    if (!isStartOrEndTile(step.row, step.col, startTile, endTile)) {
      grid[step.row][step.col].isWall = true;
      
      const element = document.getElementById(`${step.row}-${step.col}`);
      if (element) {
        element.className = `${WALL_TILE_STYLE} animate-wall`;
        
        // Add border classes
        if (step.row === MAX_ROWS - 1) {
          element.classList.add("border-b");
        }
        if (step.col === 0) {
          element.classList.add("border-l");
        }
      }
      await sleep(5 * SPEEDS.find((s) => s.value === speed)!.value);
    }
  }
  
  await sleep(50 * SPEEDS.find((s) => s.value === speed)!.value);

  // Then animate passage carving (similar to destroyWall function)
  const passageSteps = steps.filter(step => step.stepType === 'passage');
  for (const step of passageSteps) {
    if (!isStartOrEndTile(step.row, step.col, startTile, endTile)) {
      grid[step.row][step.col].isWall = false;
      
      const element = document.getElementById(`${step.row}-${step.col}`);
      if (element) {
        element.className = getTileStyle(grid[step.row][step.col], startTile, endTile);
        
        // Add border classes
        if (step.row === MAX_ROWS - 1) {
          element.classList.add("border-b");
        }
        if (step.col === 0) {
          element.classList.add("border-l");
        }
      }
      
      await sleep(20 * SPEEDS.find((s) => s.value === speed)!.value - 5);
    }
  }
};

// Main animation function that uses backend-generated steps
const animateMazeGeneration = async (
  grid: GridType,
  result: MazeGenerationResult,
  startTile: TileType,
  endTile: TileType,
  speed: SpeedType,
  maze: MazeType
): Promise<void> => {
  if (maze === "BINARY_TREE") {
    await animateBinaryTreeGeneration(grid, result.animationSteps, startTile, endTile, speed);
  } else {
    // For other maze types, use generic step-by-step animation
    for (const step of result.animationSteps) {
      if (!isStartOrEndTile(step.row, step.col, startTile, endTile)) {
        grid[step.row][step.col].isWall = step.isWall;
        
        const element = document.getElementById(`${step.row}-${step.col}`);
        if (element) {
          const tileStyle = getTileStyle(grid[step.row][step.col], startTile, endTile);
          element.className = step.isWall ? 
            `${WALL_TILE_STYLE} animate-wall` : 
            tileStyle;
            
          // Add border classes
          if (step.row === MAX_ROWS - 1) {
            element.classList.add("border-b");
          }
          if (step.col === 0) {
            element.classList.add("border-l");
          }
        }
        
        await sleep(speed === 2 ? 10 : speed === 1 ? 25 : 50);
      }
    }
  }
};

// Helper function to preserve start/end tile properties in the maze
const preserveStartEndTiles = (maze: GridType, startTile: TileType, endTile: TileType): void => {
  // Ensure start tile properties are preserved
  if (maze[startTile.row] && maze[startTile.row][startTile.col]) {
    maze[startTile.row][startTile.col].isStart = true;
    maze[startTile.row][startTile.col].isWall = false;
  }
  
  // Ensure end tile properties are preserved
  if (maze[endTile.row] && maze[endTile.row][endTile.col]) {
    maze[endTile.row][endTile.col].isEnd = true;
    maze[endTile.row][endTile.col].isWall = false;
  }
};

export const runMazeAlgorithm = async ({
  maze,
  grid,
  startTile,
  endTile,
  setIsDisabled,
  speed,
}: {
  maze: MazeType;
  grid: GridType;
  startTile: TileType;
  endTile: TileType;
  setIsDisabled: (isDisabled: boolean) => void;
  speed: SpeedType;
}): Promise<GridType | null> => {
  try {
    setIsDisabled(true);
    
    // Get maze generation result with animation steps from backend
    const response = await fetch(
      `http://localhost:8080/api/maze/${maze}?rows=${grid.length}&cols=${grid[0].length}&startRow=${startTile.row}&startCol=${startTile.col}&endRow=${endTile.row}&endCol=${endTile.col}`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" }
      }
    );
    
    if (!response.ok) {
      console.error("Failed to generate maze:", response.statusText);
      return null;
    }
    
    const result: MazeGenerationResult = await response.json();
    
    // Preserve start and end tile properties in the final maze
    preserveStartEndTiles(result.finalMaze, startTile, endTile);
    
    // Delay animation slightly to let DOM render
    await new Promise(resolve => requestAnimationFrame(resolve));

    // Animate the maze generation using backend-generated steps
    await animateMazeGeneration(grid, result, startTile, endTile, speed, maze);

    
    // Apply final maze state to grid
    for (let row = 0; row < grid.length; row++) {
      for (let col = 0; col < grid[0].length; col++) {
        grid[row][col].isWall = result.finalMaze[row][col].isWall;
      }
    }
    
    // Final pass to ensure start/end tiles are correctly styled
    const startElement = document.getElementById(`${startTile.row}-${startTile.col}`);
    const endElement = document.getElementById(`${endTile.row}-${endTile.col}`);
    
    if (startElement) {
      startElement.className = START_TILE_STYLE;
      if (startTile.row === MAX_ROWS - 1) startElement.classList.add("border-b");
      if (startTile.col === 0) startElement.classList.add("border-l");
    }
    
    if (endElement) {
      endElement.className = END_TILE_STYLE;
      if (endTile.row === MAX_ROWS - 1) endElement.classList.add("border-b");
      if (endTile.col === 0) endElement.classList.add("border-l");
    }
    
    return grid;
    
  } catch (error) {
    console.error("Error while generating maze:", error);
    return null;
  } finally {
    setIsDisabled(false);
  }
};