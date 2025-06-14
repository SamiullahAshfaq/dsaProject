import { twMerge } from "tailwind-merge";
import { MutableRefObject} from "react";
import { usePathfinding } from "../hooks/usePathfinding";
import { useTile } from "../hooks/useTile";
import { MAX_COLS, MAX_ROWS } from "../utils/constants";
import { Tile } from "./Tile";

export function Grid({}: {
  isVisualizationRunningRef: MutableRefObject<boolean>;
}) {
  const { grid } = usePathfinding();
  useTile();


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
                  />
              ))}
            </div>
        ))}
      </div>
  );
}
