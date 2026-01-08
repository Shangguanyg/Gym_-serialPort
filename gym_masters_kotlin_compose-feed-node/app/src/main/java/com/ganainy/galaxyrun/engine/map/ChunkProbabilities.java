package com.ganainy.galaxyrun.engine.map;

/*
Given the current difficulty, return the probability of
each ChunkType being generated.

The probability of each ChunkType depends on the current difficulty.
These are determined by piecewise step functions. Basically, I worked
it all out on a piece of paper.

NOTE: have to be careful that the probabilities add up to 1 at
all values of difficulty.
 */
public class ChunkProbabilities {
    /*
    Return the probability of `ChunkType` at the given `difficulty`.
     */
    public static double getProbability(ChunkType chunkType, double difficulty) {
        switch (chunkType) {
            case EMPTY:
                return getProbabilityOfEmpty(difficulty);
            case OBSTACLE_FIELD:
                return getProbabilityOfObstacleField(difficulty);
            case TUNNEL:
                return getProbabilityOfTunnel(difficulty);
            case ALIEN:
                return getProbabilityOfAlien(difficulty);
            case ALIEN_SWARM:
                return getProbabilityOfAlienSwarm(difficulty);
            case ASTEROID:
                return getProbabilityOfAsteroid(difficulty);
            case ASTEROID_FIELD:
                return getProbabilityOfAsteroidField(difficulty);
            default:
                throw new IllegalArgumentException("Unsupported ChunkType");
        }
    }

    public static double getProbabilityOfEmpty(double difficulty) {
        return 0.0;
    }

    public static double getProbabilityOfObstacleField(double difficulty) {
        if (difficulty < 0.2) {
            return 1;
        } else if (difficulty < 0.4) {
            return 0.4;
        } else if (difficulty < 0.6) {
            return 0.25;
        } else if (difficulty < 0.8) {
            return 0.25;
        } else {
            return 0.25;
        }
    }

    public static double getProbabilityOfTunnel(double difficulty) {
        if (difficulty < 0.2) {
            return 0;
        } else if (difficulty < 0.4) {
            return 0.3;
        } else if (difficulty < 0.6) {
            return 0.25;
        } else if (difficulty < 0.8) {
            return 0.25;
        } else {
            return 0.25;
        }
    }

    public static double getProbabilityOfAlien(double difficulty) {
        if (difficulty < 0.2) {
            return 0;
        } else if (difficulty < 0.4) {
            return 0;
        } else if (difficulty < 0.6) {
            return 0.25;
        } else if (difficulty < 0.8) {
            return 0.15;
        } else {
            return 0.1;
        }
    }

    public static double getProbabilityOfAlienSwarm(double difficulty) {
        if (difficulty < 0.2) {
            return 0;
        } else if (difficulty < 0.4) {
            return 0;
        } else if (difficulty < 0.6) {
            return 0;
        } else if (difficulty < 0.8) {
            return 0.1;
        } else {
            return 0.15;
        }
    }

    public static double getProbabilityOfAsteroid(double difficulty) {
        if (difficulty < 0.2) {
            return 0;
        } else if (difficulty < 0.4) {
            return 0.3;
        } else if (difficulty < 0.6) {
            return 0.25;
        } else if (difficulty < 0.8) {
            return 0.15;
        } else {
            return 0.1;
        }
    }

    public static double getProbabilityOfAsteroidField(double difficulty) {
        if (difficulty < 0.2) {
            return 0;
        } else if (difficulty < 0.4) {
            return 0;
        } else if (difficulty < 0.6) {
            return 0;
        } else if (difficulty < 0.8) {
            return 0.1;
        } else {
            return 0.15;
        }
    }

    public static double getProbabilityOfCoins(double difficulty) {
        if (difficulty < 0.2) {
            return 0;
        } else if (difficulty < 0.4) {
            return 0.2;
        } else if (difficulty < 0.8) {
            return 0.4;
        } else {
            return 0.6;
        }
    }
}
