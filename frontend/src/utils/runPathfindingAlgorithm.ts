import { AlgorithmType, GridType, TileType } from "./types";

export const runPathfindingAlgorithm = async ({
                                                algorithm,
                                                grid,
                                                startTile,
                                                endTile,
                                              }: {
  algorithm: AlgorithmType;
  grid: GridType;
  startTile: TileType;
  endTile: TileType;
}) => {
  const response = await fetch(`http://localhost:8080/api/pathfinding?algorithm=${algorithm}&rows=${grid.length}&cols=${grid[0].length}&startX=${startTile.col}&startY=${startTile.row}&endX=${endTile.col}&endY=${endTile.row}`, {
  method: "POST",
});

  const data = await response.json();
  return data; // should contain { traversedTiles, path }
};
