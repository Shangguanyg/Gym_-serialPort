package com.ganainy.galaxyrun.engine.controller;

public class TiltState {
    public final ControlDirection direction;
    public final float magnitude;
    public TiltState(ControlDirection direction, float magnitude) {
        if (magnitude < 0 || magnitude > 1) {
            throw new IllegalArgumentException("magnitude must be between [0, 1]. Was: " + magnitude);
        }
        this.direction = direction;
        this.magnitude = magnitude;
    }
}
