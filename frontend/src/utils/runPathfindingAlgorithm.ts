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
  // Fix: Use X/Y naming to match controller
  const response = await fetch(`http://localhost:8080/api/pathfinding?algorithm=${algorithm}&rows=${grid.length}&cols=${grid[0].length}&startX=${startTile.col}&startY=${startTile.row}&endX=${endTile.col}&endY=${endTile.row}`, {
    method: "GET",
  });
  const data = await response.json();
  return data;
};
