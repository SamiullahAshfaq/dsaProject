import { GridType, MazeType, SpeedType, TileType } from "./types";
import { animateMazeGeneration } from "../utils/animatePath";
import { preserveStartEndTiles } from ".utils/helpers";
import { START_TILE_STYLE, END_TILE_STYLE } from "./constants";
import { MAX_ROWS } from "./constants/grid";

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

    // Send POST request with start and end positions
    const response = await fetch(
      `http://localhost:8080/api/maze/${maze}?rows=${grid.length}&cols=${grid[0].length}`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          start: { row: startTile.row, col: startTile.col },
          end: { row: endTile.row, col: endTile.col },
        }),
      }
    );

    if (!response.ok) {
      console.error("Maze generation failed:", response.statusText);
      return null;
    }

    const completeMaze: GridType = await response.json();

    // Restore start and end tile data
    preserveStartEndTiles(completeMaze, startTile, endTile);

    // Animate generated maze
    await animateMazeGeneration(grid, completeMaze, startTile, endTile, speed, maze);

    // Set styles for start tile
    const startElement = document.getElementById(`${startTile.row}-${startTile.col}`);
    if (startElement) {
      startElement.className = START_TILE_STYLE;
      if (startTile.row === MAX_ROWS - 1) startElement.classList.add("border-b");
      if (startTile.col === 0) startElement.classList.add("border-l");
    }

    // Set styles for end tile
    const endElement = document.getElementById(`${endTile.row}-${endTile.col}`);
    if (endElement) {
      endElement.className = END_TILE_STYLE;
      if (endTile.row === MAX_ROWS - 1) endElement.classList.add("border-b");
      if (endTile.col === 0) endElement.classList.add("border-l");
    }

    return completeMaze;
  } catch (error) {
    console.error("Error during maze algorithm run:", error);
    return null;
  } finally {
    setIsDisabled(false);
  }
};
