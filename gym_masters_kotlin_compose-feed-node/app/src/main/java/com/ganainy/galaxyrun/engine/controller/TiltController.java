package com.ganainy.galaxyrun.engine.controller;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

import com.ganainy.galaxyrun.util.CircularBuffer;

/**
 * Takes a real-time stream of gyroscope SensorEvents and turns them into control inputs for the
 * spaceship.
 *
 * Gyroscope events should be given to the TiltController in real-time using
 * `inputGyroscopeEvent()`. Then, call `calculateState()` at any time to calculate and return the
 * current control input.
 *
 * TiltController calculates the control state using the y-axis angular velocity reported by the
 * gyroscope, which reports the speed at which the phone is being tilted. It calculates a rolling
 * average on a small number of the most recent samples in order to reduce possible noise in the
 * gyroscope readings.
 *
 * The model for calculating control input works as follows:
 * - Calculate the average y-velocity of the most recent `MAX_NUM_SAMPLES` samples, ignoring any
 * that are older than `MAX_SAMPLE_AGE`. Let this value be `AvgY`.
 * - Cap AvgY such that its magnitude is less than or equal to `MAX_MAGNITUDE`.
 * - Normalize AvgY with respect to `MAX_MAGNITUDE`. If this value is less than
 * `NOISE_THRESHOLD`, consider the phone to be stationary.
 * - Determine the control input based on the sign of AvgY. A positive value means the device is
 * being tilted away from the user, resulting in an `UP` input; a negative value means the reverse.
 *
 * Overall, it is a simplistic model, but it seems to work fairly well.
 * TODO: account for screen rotations, which will flip the direction input.
 */
public class TiltController {
    // Stores the `MAX_NUM_SAMPLES` most recent sensor readings from the gyroscope.
    private final CircularBuffer<GyroReading> history;

    // Minimum magnitude of angular velocity needed to be considered legitimate input. Values below
    // this are treated as noise and "clipped" to zero.
    private final float NOISE_THRESHOLD = 0.01f;
    // Maximum magnitude of angular velocity considered. Values above this are "clipped" to
    // MAX_MAGNITUDE.
    private final float MAX_MAGNITUDE = 2;
    // The maximum number of samples to store as sensor history.
    private final int MAX_NUM_SAMPLES = 10;
    // The maximum age of samples in the history that will be considered for the rolling average.
    private final int MAX_SAMPLE_AGE = 30;

    // A simple struct holding the relevant information for a gyroscope sensor reading
    private static class GyroReading {
        public final long timestamp;
        // Angular velocity along the y-axis.
        public final float yVel;
        public GyroReading(long timestamp, float yVel) {
            this.timestamp = timestamp;
            this.yVel = yVel;
        }
        public GyroReading(SensorEvent e) {
            this(e.timestamp, e.values[1]);
        }
    }

    public TiltController() {
        history = new CircularBuffer<>(MAX_NUM_SAMPLES);
    }

    // Registers a new gyroscope sensor reading with the controller.
    public void inputGyroscopeEvent(SensorEvent gyroEvent) {
        if (gyroEvent.sensor.getType() != Sensor.TYPE_GYROSCOPE) {
            throw new IllegalArgumentException("Only accepts GYROSCOPE events.");
        }
        history.push(new GyroReading(gyroEvent));
    }

    // Calculates the control input based on the most recent gyroscope readings.
    // `currTimestamp` is used to ensure old sensor readings are not considered in the calculation.
    public TiltState calculateState(long currTimestamp) {
        float averageVelocity = calculateAverageVelocity(currTimestamp - MAX_SAMPLE_AGE);
        return calculateTiltState(averageVelocity);
    }

    // Calculates the average velocity of samples in `history` which happened at or after
    // `minTimestamp`.
    private float calculateAverageVelocity(long minTimestamp) {
        int numSamples = 0;
        float sum = 0;
        // Note: we don't even bother garbage-collecting `history` because it is small enough
        // that it's likely more efficient to simply ignore stale values.
        for (GyroReading reading : history) {
            if (reading.timestamp >= minTimestamp) {
                sum += reading.yVel;
                ++numSamples;
            }
        }
        return numSamples == 0 ? 0 : sum / numSamples;
    }

    // Given a velocity, returns the TiltState that it results in.
    private TiltState calculateTiltState(float velocity) {
        // Normalize to [-1, 1]
        if (velocity > MAX_MAGNITUDE) {
            velocity = MAX_MAGNITUDE;
        } else if (velocity < -MAX_MAGNITUDE) {
            velocity = -MAX_MAGNITUDE;
        }
        velocity = velocity / MAX_MAGNITUDE;

        if (velocity > NOISE_THRESHOLD) {
            return new TiltState(ControlDirection.UP, Math.abs(velocity));
        } else if (velocity < -NOISE_THRESHOLD) {
            return new TiltState(ControlDirection.DOWN, Math.abs(velocity));
        } else {
            return new TiltState(ControlDirection.NEUTRAL, 0);
        }
    }
}