package com.ganainy.galaxyrun.engine.map;

import androidx.annotation.NonNull;

public class Chunk {
    public int numRows;
    public int numCols;
    public TileType[][] tiles;

    public Chunk(TileType[][] tiles) {
        numRows = tiles.length;
        numCols = tiles[0].length;
        this.tiles = tiles;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (TileType[] row : tiles) {
            for (TileType tile : row) {
                result.append(tile.ordinal());
                result.append('\t');
            }
            result.append('\n');
        }
        return result.toString();
    }

    /*
    Concatenate two chunks, c1 followed by c2, and return the result.

    The chunks must have the same number of rows.
     */
    public static Chunk concatenateChunks(Chunk c1, Chunk c2) {
        if (c1.numRows != c2.numRows) {
            throw new IllegalArgumentException("Chunks must have the same number of rows");
        }
        // TODO: use System.arraycopy() for everything
        TileType[][] result = new TileType[c1.numRows][c1.numCols + c2.numCols];
        for (int i = 0; i < c1.numRows; i++) {
            for (int j = 0; j < c1.numCols; j++) {
                result[i][j] = c1.tiles[i][j];
            }
        }
        for (int i = 0; i < c2.numRows; i++) {
            for (int j = 0; j < c2.numCols; j++) {
                result[i][j + c1.numCols] = c2.tiles[i][j];
            }
        }
        return new Chunk(result);
    }
}
