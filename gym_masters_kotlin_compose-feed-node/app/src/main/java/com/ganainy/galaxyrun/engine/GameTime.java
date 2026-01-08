package com.ganainy.galaxyrun.engine;

/**
 * Basically a struct to store a couple values related to game timing.
 */

public class GameTime {
    public final long currTimeMs;
    public final long msSincePrevUpdate;
    public final double secSincePrevUpdate;
    public final long runTimeMs;

    public GameTime(long currTimeMs, long msSincePrevUpdate, long runTimeMs) {
        this.currTimeMs = currTimeMs;
        this.msSincePrevUpdate = msSincePrevUpdate;
        this.secSincePrevUpdate = msSincePrevUpdate / 1000.0;
        this.runTimeMs = runTimeMs;
    }
}
