package com.ganainy.galaxyrun.engine.audio;

/**
 * Stores parameters for playing a raw file using SoundPool
 */
public class SoundParams {

    // ID of raw resource to be played
    private SoundID resourceID;
    // x0 volume value (range = 0.0 to 1.0)
    private float leftVolume;
    // x1 volume value (range = 0.0 to 1.0)
    private float rightVolume;
    // stream priority (0 = lowest priority)
    private int priority;
    // loop mode (0 = no loop, -1 = loop forever)
    private int loop;
    // playback rate (1.0 = normal playback, range 0.5 to 2.0)
    private float rate;

    public SoundParams(SoundID resourceID, float leftVolume, float rightVolume, int priority, int loop, float rate) {
        this.resourceID = resourceID;
        this.leftVolume = leftVolume;
        this.rightVolume = rightVolume;
        this.priority = priority;
        this.loop = loop;
        this.rate = rate;
    }

    public SoundID getResourceID() {
        return resourceID;
    }

    public float getLeftVolume() {
        return leftVolume;
    }

    public float getRightVolume() {
        return rightVolume;
    }

    public void setLeftVolume(float leftVolume) {
        this.leftVolume = leftVolume;
    }

    public void setRightVolume(float rightVolume) {
        this.rightVolume = rightVolume;
    }

    public int getPriority() {
        return priority;
    }

    public int getLoop() {
        return loop;
    }

    public float getRate() {
        return rate;
    }
}
