import {GridType, MazeType, SpeedType, TileType} from "./types";

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

    const response = await fetch(`http://localhost:8080/api/maze/${maze}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ grid, start: startTile, end: endTile, speed }),
    });

    if (!response.ok) {
      console.error("Failed to generate maze:", response.statusText);
      return null;
    }

    return await response.json();
  } catch (error) {
    console.error("Error while generating maze:", error);
    return null;
  } finally {
    setIsDisabled(false);
  }
};
