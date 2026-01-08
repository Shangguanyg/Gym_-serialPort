package com.ganainy.galaxyrun.helper;

/*
Calculate rolling average FPS.
 */
public class FpsCalculator {
    public final int ringSize;
    private final long[] timestamps;
    private int index = -1;
    private int numFrames;

    public int getNumFrames() {
        return numFrames;
    }

    public FpsCalculator(int ringSize) {
        this.ringSize = ringSize;
        timestamps = new long[ringSize];
    }

    public void recordFrame() {
        index = (index + 1) % ringSize;
        timestamps[index] = System.currentTimeMillis();
        numFrames++;
    }

    public double calcFps() {
        if (numFrames < ringSize) {
            return 0;
        }
        int oldestIndex = (index + 1 == ringSize ? 0 : index + 1);
        return ringSize * 1.0 / ((timestamps[index] - timestamps[oldestIndex]) / 1000.0);
    }

    public void reset() {
        index = -1;
        numFrames = 0;
    }
}
