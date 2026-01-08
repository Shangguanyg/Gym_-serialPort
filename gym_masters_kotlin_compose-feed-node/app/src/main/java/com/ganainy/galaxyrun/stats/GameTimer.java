package com.ganainy.galaxyrun.stats;

import com.ganainy.galaxyrun.engine.GameTime;

/**
 * Keeps track of time elapsed. Meant to track in-game time.
 */

public class GameTimer {

    // Whether the timer has been started
    private boolean isStarted;
    // Timestamp of the most recent call to `recordUpdate()`
    private long lastUpdateMs;
    // Whether the timer is currently paused
    private boolean isPaused;
    // Total number of milliseconds tracked
    public long msTracked;

    public GameTimer() {

    }

    public void start() {
        isStarted = true;
        lastUpdateMs = System.currentTimeMillis();
    }

    public void pause() {
        if (!isStarted) {
            throw new IllegalStateException("Timer hasn't been started");
        }
        if (!isPaused) {
            // Add time since the previous update
            msTracked += System.currentTimeMillis() - lastUpdateMs;
            isPaused = true;
        }
    }

    public void resume() {
        if (!isStarted) {
            throw new IllegalStateException("Timer hasn't been started");
        }
        if (isPaused) {
            lastUpdateMs = System.currentTimeMillis();
            isPaused = false;
        }
    }

    public GameTime recordUpdate() {
        long currTime = System.currentTimeMillis();
        if (!isStarted) {
            return new GameTime(currTime, 0, 0);
        }
        if (isPaused) {
            return new GameTime(currTime, 0, msTracked);
        } else {
            long msThisUpdate = currTime - lastUpdateMs;
            lastUpdateMs = currTime;
            msTracked += msThisUpdate;
            return new GameTime(currTime, msThisUpdate, msTracked);
        }
    }
}
