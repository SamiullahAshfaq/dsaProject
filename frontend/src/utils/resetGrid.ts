import {
  END_TILE_CONFIGURATION,
  MAX_COLS,
  MAX_ROWS,
  START_TILE_CONFIGURATION,
  TILE_STYLE,
  START_TILE_STYLE,
  END_TILE_STYLE,
} from "./constants";
import { isEqual } from "./helpers";
import { GridType, TileType } from "./types";

export const resetGrid = ({
  grid,
  startTile = START_TILE_CONFIGURATION,
  endTile = END_TILE_CONFIGURATION,
}: {
  grid: GridType;
  startTile?: TileType;
  endTile?: TileType;
}) => {
  for (let row = 0; row < MAX_ROWS; row++) {
    for (let col = 0; col < MAX_COLS; col++) {
      const tile = grid[row][col];
      
      // Reset pathfinding properties
      tile.distance = Infinity;
      tile.isTraversed = false;
      tile.isPath = false;
      tile.parent = null;
      tile.isWall = false;
      
      // Determine if this is start or end tile by position
      const isStartTile = tile.row === startTile.row && tile.col === startTile.col;
      const isEndTile = tile.row === endTile.row && tile.col === endTile.col;
      
      // Set tile properties
      tile.isStart = isStartTile;
      tile.isEnd = isEndTile;
      
      // Update DOM styling
      const tileElement = document.getElementById(`${tile.row}-${tile.col}`);
      if (tileElement) {
        if (isStartTile) {
          tileElement.className = START_TILE_STYLE;
        } else if (isEndTile) {
          tileElement.className = END_TILE_STYLE;
        } else {
          tileElement.className = TILE_STYLE;
        }
        
        // Add border classes for grid appearance
        if (tile.row === MAX_ROWS - 1) {
          tileElement.classList.add("border-b");
        }
        if (tile.col === 0) {
          tileElement.classList.add("border-l");
        }
      }
    }
  }
};