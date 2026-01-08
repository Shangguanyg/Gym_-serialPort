package com.ganainy.galaxyrun.engine.map;

import android.util.Log;

import com.ganainy.galaxyrun.util.WeightedRandomChooser;

import java.util.Random;

public class MapGenerator {
    private final Random rand;

    // Set the number of columns of empty space before each created chunk
    private static final int LEADING_BUFFER_LENGTH = 3;

    public MapGenerator(Random rand) {
        this.rand = rand;
    }

    public Chunk generateChunk(double difficulty) {
        Log.d("MapGenerator", "Generating chunk with difficulty " + difficulty);
        // Generate a few columns of EMPTY to start the game TODO: is this necessary?
        if (difficulty == 0) {
            Log.d("MapGenerator", "Generating empty chunk for game start");
            return TileGenerator.generateEmpty(3);
        }

        ChunkType nextChunkType = decideChunkType(difficulty);
        boolean generateCoins = decideGenerateCoins(difficulty);
        Log.d("Map", "Generating a chunk of " + nextChunkType.name());
        Log.d("Map", "ShouldGenerateCoins = " + generateCoins);

        // Generate next chunk with several columns of leading EMPTY
        Chunk leadEmpty = TileGenerator.generateEmpty(LEADING_BUFFER_LENGTH);
        Chunk feature = TileGenerator.generateChunk(rand, nextChunkType, difficulty);
        Chunk fullChunk = Chunk.concatenateChunks(leadEmpty, feature);
        try {
            // TODO: would be cool to draw the path on the game while debugging
            Path foundPath = PathFinder.findPath(fullChunk, 200);
            Log.d("PathFinder", "Found a path! " + foundPath);
            if (generateCoins) {
                CoinGenerator.generateCoins(fullChunk, foundPath, rand, difficulty);
            }
        } catch (NoPathFoundException e) {
            Log.e("PathFinder", "No path found");
        }
        return fullChunk;
    }

    private ChunkType decideChunkType(double difficulty) {
        WeightedRandomChooser<ChunkType> chooser = new WeightedRandomChooser<>(rand);
        for (ChunkType chunkType : ChunkType.values()) {
            double chunkProb = ChunkProbabilities.getProbability(chunkType, difficulty);
            chooser.addItem(chunkType, chunkProb);
        }
        return chooser.choose();
    }

    private boolean decideGenerateCoins(double difficulty) {
        return rand.nextDouble() <= ChunkProbabilities.getProbabilityOfCoins(difficulty);
    }
}
