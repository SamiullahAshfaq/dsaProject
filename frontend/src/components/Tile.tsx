// Fixed Tile Component with better logic and debugging
import { twMerge } from "tailwind-merge";
import {
  END_TILE_STYLE,
  MAX_ROWS,
  PATH_TILE_STYLE,
  START_TILE_STYLE,
  TILE_STYLE,
  TRAVERSED_TILE_STYLE,
  WALL_TILE_STYLE,
} from "../utils/constants";

interface MouseFunction {
  (row: number, col: number): void;
}

export function Tile({
  row,
  col,
  isStart,
  isEnd,
  isTraversed,
  isWall,
  isPath,
  handleMouseDown,
  handleMouseUp,
  handleMouseEnter,
}: {
  row: number;
  col: number;
  isStart: boolean;
  isEnd: boolean;
  isTraversed: boolean;
  isWall: boolean;
  isPath: boolean;
  handleMouseDown: MouseFunction;
  handleMouseUp: MouseFunction;
  handleMouseEnter: MouseFunction;
}) {
  // Fixed logic: Start and End tiles should NEVER be walls during maze generation
  let tileStyle;
  
  if (isStart) {
    tileStyle = START_TILE_STYLE;
  } else if (isEnd) {
    tileStyle = END_TILE_STYLE;
  } else if (isPath) {
    // Path has higher priority than traversed
    tileStyle = PATH_TILE_STYLE;
  } else if (isTraversed) {
    tileStyle = TRAVERSED_TILE_STYLE;
  } else if (isWall) {
    tileStyle = WALL_TILE_STYLE;
  } else {
    tileStyle = TILE_STYLE;
  }

  // Fixed border logic
  const borderClasses = [];
  if (row === MAX_ROWS - 1) {
    borderClasses.push("border-b");
  }
  if (col === 0) {
    borderClasses.push("border-l");
  }
  
  const borderStyle = borderClasses.join(" ");

  return (
    <div
      className={twMerge(tileStyle, borderStyle)}
      id={`${row}-${col}`}
      onMouseDown={() => handleMouseDown(row, col)}
      onMouseUp={() => handleMouseUp(row, col)}
      onMouseEnter={() => handleMouseEnter(row, col)}
      // Add data attributes for debugging
      data-row={row}
      data-col={col}
      data-is-start={isStart}
      data-is-end={isEnd}
      data-is-wall={isWall}
    />
  );
}