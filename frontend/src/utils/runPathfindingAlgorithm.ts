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
  const response = await fetch(`http://localhost:8080/api/pathfinding/${algorithm}`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ grid, start: startTile, end: endTile }),
  });

  const data = await response.json();
  return data; // should contain { traversedTiles, path }
};
