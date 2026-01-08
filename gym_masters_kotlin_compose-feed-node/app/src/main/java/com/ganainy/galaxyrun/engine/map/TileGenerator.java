package com.ganainy.galaxyrun.engine.map;

import java.util.Arrays;
import java.util.Random;

/**
 * Generates "chunks" of map tiles.
 */
public class TileGenerator {

    public static Chunk generateChunk(
            Random rand,
            ChunkType chunkType,
            double difficulty
    ) {
        switch (chunkType) {
            case EMPTY: {
                return generateEmpty(5);
            }
            case OBSTACLE_FIELD: {
                return generateObstacleField(rand, difficulty);
            }
            case TUNNEL: {
                return generateTunnel(rand, difficulty);
            }
            case ALIEN: {
                return generateAlien(rand, difficulty);
            }
            case ALIEN_SWARM: {
                return generateAlienSwarm(rand, difficulty);
            }
            case ASTEROID: {
                return generateAsteroid(rand, difficulty);
            }
            case ASTEROID_FIELD: {
                return generateAsteroidField(rand, difficulty);
            }
            default: {
                throw new IllegalArgumentException(
                        String.format("ChunkType %s is not supported", chunkType.toString())
                );
            }
        }
    }

    /*
    Generate array of dimensions (NUM_ROWS, num_cols) filled with TileType t.
     */
    public static TileType[][] createArray(TileType t, int numRows, int numCols) {
        TileType[][] arr = new TileType[numRows][numCols];
        for (TileType[] row : arr) {
            Arrays.fill(row, t);
        }
        return arr;
    }

    public static Chunk generateEmpty(int numCols) {
        return new Chunk(createArray(TileType.EMPTY, Map.NUM_ROWS, numCols));
    }

    public static Chunk generateObstacleField(
            Random rand,
            double difficulty
    ) {
        int chunkLength = 8 + rand.nextInt(8);
        Chunk chunk = generateEmpty(chunkLength);
        // Higher difficulty -> higher obstacle density
        float pGenerate = 0.3f + (float) (difficulty * 0.4);
        for (int col = 0; col < chunkLength; col++) {
            if (testRandom(rand, pGenerate)) {
                // Place obstacle at random row
                int row = rand.nextInt(Map.NUM_ROWS);
                chunk.tiles[row][col] = TileType.OBSTACLE;

                // Possibly generate another obstacle immediately above or below
                if (testRandom(rand, 0.25f)) {
                    if (row == 0) {
                        chunk.tiles[row + 1][col] = TileType.OBSTACLE;
                    } else if (row == Map.NUM_ROWS - 1) {
                        chunk.tiles[row - 1][col] = TileType.OBSTACLE;
                    } else {
                        chunk.tiles[row + (rand.nextBoolean() ? 1 : -1)][col] = TileType.OBSTACLE;
                    }
                }
                if (col + 1 < chunkLength && testRandom(rand, 0.25f)) {
                    // Possibly generate another obstacle immediately to the right
                    chunk.tiles[row][col + 1] = TileType.OBSTACLE;
                }

                // Do not generate a new Obstacle in the next column
                col++;
            }
        }

        return chunk;
    }

    /*
    A 2-tile wide tunnel.
     */
    public static Chunk generateTunnel(
            Random rand,
            double difficulty
    ) {
        int chunkLength = 6 + rand.nextInt(10);
        // Create array filled with OBSTACLE, then "carve out" the passage
        Chunk chunk = new Chunk(createArray(TileType.OBSTACLE, Map.NUM_ROWS, chunkLength));

        // Randomly choose starting row of the tunnel passage
        int passage_top = 1 + rand.nextInt(Map.NUM_ROWS - 3);
        int passage_btm = passage_top + 1;
        // Probability that the passage will move up or down
        float p_change_path = 0.0f;
        // The amount that the probability increases per generated column.
        // Higher difficulty = higher likelihood of path changes
        float d_change_path = 0.06f * (float) difficulty;

        for (int col = 0; col < chunkLength; col++) {
            chunk.tiles[passage_top][col] = TileType.EMPTY;
            chunk.tiles[passage_btm][col] = TileType.EMPTY;

            // Check whether to change direction
            if (col < chunkLength - 3 && testRandom(rand, p_change_path)) {
                // Decide in which direction to change
                int direction_change;
                if (passage_top == 0) {
                    direction_change = +1;
                } else if (passage_btm == Map.NUM_ROWS - 1) {
                    direction_change = -1;
                } else {
                    direction_change = (testRandom(rand, 0.5f) ? +1 : -1);
                }

                // Construct the change in the tunnel.
                // This can be accomplished by simply setting one tile in
                // the direction of change to EMPTY
                chunk.tiles[passage_top + direction_change][col] = TileType.EMPTY;
                chunk.tiles[passage_btm + direction_change][col] = TileType.EMPTY;
                chunk.tiles[passage_top][col + 1] = TileType.EMPTY;
                chunk.tiles[passage_btm][col + 1] = TileType.EMPTY;

                passage_top += direction_change;
                passage_btm = passage_top + 1;
                p_change_path = 0.0f;
            } else {
                // Increase probability of changing path
                p_change_path += d_change_path;
            }
        }
        return chunk;
    }

    public static Chunk generateAlien(
            Random rand,
            double difficulty
    ) {
        // Higher difficulty -> smaller chunk (less time)
        int chunkLength = 3 + (int) (9 * (1.0 - difficulty));
        Chunk chunk = generateEmpty(chunkLength);
        chunk.tiles[2 + rand.nextInt(Map.NUM_ROWS - 4)][0] = TileType.ALIEN;
        return chunk;
    }

    public static Chunk generateAlienSwarm(
            Random rand,
            double difficulty
    ) {
        // Higher difficulty -> more aliens
        int numAliens = 2 + (int) (4 * difficulty * rand.nextDouble());
        int chunkLength = 8 * numAliens;
        Chunk chunk = generateEmpty(chunkLength);
        for (int j = 0; j < numAliens; j++) {
            chunk.tiles[2 + rand.nextInt(Map.NUM_ROWS - 4)][j * 8] = TileType.ALIEN;
        }
        return chunk;
    }

    public static Chunk generateAsteroid(
            Random rand,
            double difficulty
    ) {
        // Higher difficulty -> smaller chunk (less time)
        int chunkLength = 5 + (int) (9 * (1.0 - difficulty));;
        Chunk chunk = generateEmpty(chunkLength);
        chunk.tiles[1 + rand.nextInt(Map.NUM_ROWS - 1)][0] = TileType.ASTEROID;
        return chunk;
    }

    public static Chunk generateAsteroidField(
            Random rand,
            double difficulty
    ) {
        // Higher difficulty -> more asteroids
        int numAsteroids = 2 + (int) (4 * difficulty * rand.nextDouble());
        int chunkLength = 8 * numAsteroids;
        Chunk chunk = generateEmpty(chunkLength);
        for (int ia = 0; ia < numAsteroids; ia++) {
            chunk.tiles[1 + rand.nextInt(Map.NUM_ROWS - 2)][8 * ia + rand.nextInt(7)] =
                    TileType.ASTEROID;
        }
        return chunk;
    }

    /*
    Generates a random float in range [0, 1] and returns whether
    it is less than the given probability.
     */
    private static boolean testRandom(Random rand, float probability) {
        return rand.nextFloat() <= probability;
    }
}
