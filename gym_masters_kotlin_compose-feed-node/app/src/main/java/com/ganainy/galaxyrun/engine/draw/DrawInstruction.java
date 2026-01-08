package com.ganainy.galaxyrun.engine.draw;

import android.graphics.Canvas;

/**
 * Interface for all sub-classes that draw onto the screen.
 */
public interface DrawInstruction {

    // Draw to the canvas.
    void draw(Canvas canvas);

}
