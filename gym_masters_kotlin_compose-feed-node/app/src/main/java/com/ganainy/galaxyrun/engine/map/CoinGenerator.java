package com.ganainy.galaxyrun.engine.map;

import android.util.Log;

import java.util.Random;

/*
Generate Coins in a chunk.
 */
public class CoinGenerator {

    /*
    Generate a coin trail on the given chunk.
     */
    public static void generateCoins(
            Chunk chunk,
            Path knownPath,
            Random rand,
            double difficulty
    ) {
        // Higher difficulty -> longer coin trail
        int numCoins = 4 + (int) (10 * rand.nextDouble() * difficulty);
        // Limit to the length of the path
        if (numCoins > knownPath.path.size()) {
            numCoins = knownPath.path.size();
        }
        // Start coin trail somewhere along the known path
        Log.d("CoinGenerator", knownPath.path.size() + ", " + numCoins);
        int startIndex = knownPath.path.size() == numCoins ?
                0 : rand.nextInt(knownPath.path.size() - numCoins);
        // Place coins
        for (int i = startIndex; i < startIndex + numCoins; i++) {
            TileLocation loc = knownPath.path.get(i);
            chunk.tiles[loc.row][loc.col] = TileType.COIN;
        }
    }
}
