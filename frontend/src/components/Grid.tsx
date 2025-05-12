import { twMerge } from "tailwind-merge";
import { MutableRefObject, useState } from "react";
import { usePathfinding } from "../hooks/usePathfinding";
import { useTile } from "../hooks/useTile";
import { MAX_COLS, MAX_ROWS } from "../utils/constants";
import { Tile } from "./Tile";
import { checkIfStartOrEnd, createNewGrid } from "../utils/helpers";

export function Grid({
                       isVisualizationRunningRef,
                     }: {
  isVisualizationRunningRef: MutableRefObject<boolean>;
}) {
  const { grid, setGrid } = usePathfinding();
  useTile();
  const [isMouseDown, setIsMouseDown] = useState(false);

  const handleMouseDown = (row: number, col: number) => {
    if (isVisualizationRunningRef.current || checkIfStartOrEnd(row, col)) return;

    setIsMouseDown(true);
    const newGrid = createNewGrid(grid, row, col);
    setGrid(newGrid);
  };

  const handleMouseUp = () => {
    if (isVisualizationRunningRef.current) return;
    setIsMouseDown(false);
  };

  const handleMouseEnter = (row: number, col: number) => {
    if (isVisualizationRunningRef.current || checkIfStartOrEnd(row, col)) return;

    if (isMouseDown) {
      const newGrid = createNewGrid(grid, row, col);
      setGrid(newGrid);
    }
  };

  return (
      <div
          className={twMerge(
              "flex items-center flex-col justify-center border-sky-300 mt-10",
              `lg:min-h-[${MAX_ROWS * 17}px] md:min-h-[${MAX_ROWS * 15}px] xs:min-h-[${MAX_ROWS * 8}px] min-h-[${MAX_ROWS * 7}px]`,
              `lg:w-[${MAX_COLS * 17}px] md:w-[${MAX_COLS * 15}px] xs:w-[${MAX_COLS * 8}px] w-[${MAX_COLS * 7}px]`
          )}
      >
        {grid.map((r, rowIndex) => (
            <div key={rowIndex} className="flex">
              {r.map((tile, tileIndex) => (
                  <Tile
                      key={tileIndex}
                      row={tile.row}
                      col={tile.col}
                      isEnd={tile.isEnd}
                      isStart={tile.isStart}
                      isPath={tile.isPath}
                      isTraversed={tile.isTraversed}
                      isWall={tile.isWall}
                      handleMouseDown={() => handleMouseDown(tile.row, tile.col)}
                      handleMouseUp={handleMouseUp}
                      handleMouseEnter={() => handleMouseEnter(tile.row, tile.col)}
                  />
              ))}
            </div>
        ))}
      </div>
  );
}
