package com.ganainy.galaxyrun.helper;

import android.graphics.ColorMatrix;

/*
Class used to animate a ColorMatrix. Currently the only supported animation
is a flash, where every pixel's Red/Green/Blue values are briefly jacked up
to 255 before coming back down to what they originally were.
The ColorMatrix uses a 20-element float[], visualized as a 4x5 matrix.
The 5th column of each row tells by how much to increase each
Red/Green/Blue/Alpha value. So, to transition to full white, we want to change
elems 4, 9, and 14 to 255, before bringing them back down to zero.
 */
public class ColorMatrixAnimator {

    // How long it has been flashing for the current flash()
    private long timeSinceAnimStartMs = Integer.MAX_VALUE;
    // The currently-calculated ColorMatrix
    private ColorMatrix colorMatrix;

    // number of frames it takes for sprite to reach completely white
    private final long flashInMs;
    // number of frames sprite will stay completely white
    private final long flashStayMs;
    // number of frames it takes for sprite to go back to normal after being
    // completely white.
    private final long flashOutMs;
    // total frame count for an animation
    private final long totalFlashDurationMs;

    public ColorMatrixAnimator(long flashInMs, long flashStayMs, long flashOutMs) {
        this.flashInMs = flashInMs;
        this.flashStayMs = flashStayMs;
        this.flashOutMs = flashOutMs;
        totalFlashDurationMs = flashInMs + flashStayMs + flashOutMs;
        colorMatrix = new ColorMatrix();
    }

    /*
    Get the currently-calculated ColorMatrix.
    */
    public ColorMatrix getMatrix() {
        return colorMatrix;
    }

    /*
    Trigger a flash.
     */
    public void flash() {
        if (isFlashing()) {
            if (timeSinceAnimStartMs < flashInMs) {
                // Already flashing in: no state change
            } else if (timeSinceAnimStartMs < flashInMs + flashStayMs) {
                // In `flashStay` state: reset flashStay
                timeSinceAnimStartMs = flashInMs;
            } else {
                // In `flashOut` state: invert to flashIn
                long timeRemainingMs = totalFlashDurationMs - timeSinceAnimStartMs;
                timeSinceAnimStartMs = flashInMs - timeRemainingMs;
                timeSinceAnimStartMs = timeSinceAnimStartMs < 0 ? 0 : timeSinceAnimStartMs;
            }
        } else {
            // Trigger new flash
            timeSinceAnimStartMs = 0;
        }
    }

    // updates the matrix by one frame
    public void update(long ms) {
        timeSinceAnimStartMs += ms;
        colorMatrix.reset();

        if (isFlashing()) {
            // Calculate how much "flash" to apply.
            // We want it to go to 1.0 during the full flash, and otherwise
            // fade in or out linearly.
            double flashFraction;
            if (timeSinceAnimStartMs < flashInMs) {
                flashFraction = timeSinceAnimStartMs * 1.0 / flashInMs;
            } else if (timeSinceAnimStartMs < flashInMs + flashStayMs) {
                // Full flash
                flashFraction = 1.0;
            }
            else {
                long timeRemainingMs = totalFlashDurationMs - timeSinceAnimStartMs;
                flashFraction = timeRemainingMs * 1.0 / flashOutMs;
            }

            // Update the 5th column of each color value
            float[] matrixVals = colorMatrix.getArray();
            for (int i = 0; i < 3; i++) {
                matrixVals[4 + 5 * i] += flashFraction * 255;
            }
            colorMatrix.set(matrixVals);
        }
    }

    private boolean isFlashing() {
        return timeSinceAnimStartMs < totalFlashDurationMs;
    }
}
