package com.ganainy.galaxyrun.helper;

import android.graphics.Color;

import com.ganainy.galaxyrun.engine.draw.DrawInstruction;
import com.ganainy.galaxyrun.engine.draw.DrawText;
import com.ganainy.galaxyrun.sprite.Sprite;
import com.ganainy.galaxyrun.util.ProtectedQueue;

/**
 * Class used to draw the damage floating up from a sprite.
 * Tracks with the sprite and moves relative to it.
 */

public class LoseHealthAnimation {

    // Draw coordinates
    private double x, y;
    // Remaining amount of time to show the animation
    private long elapsedTimeMs;

    private final int textSize;
    // amount of health lost, which will be displayed
    private final int damage;
    private final double totalMovementX, totalMovementY;
    // Offset from sprite's coordinates (px)
    private final double baseOffsetX, baseOffsetY;

    // Duration of the animation
    private static final long DURATION_MS = 1000;
    // color of text to be drawn
    private static final int TEXT_COLOR = Color.WHITE;
    // size of text to be drawn
    private static final double REL_TEXT_SIZE = 0.05;
    // movement of the text in x (as fraction of game width)
    private static final double REL_MOVEMENT_X = 0.1;
    // movement of the text in y (as fraction of game height)
    private static final double REL_MOVEMENT_Y = -0.1;

    /*
    Create and start the animation.
     */
    public LoseHealthAnimation(
            int gameWidthPx,
            int gameHeightPx,
            double offsetX,
            double offsetY,
            int damage
    ) {
        totalMovementX = gameWidthPx * REL_MOVEMENT_X;
        totalMovementY = gameHeightPx * REL_MOVEMENT_Y;
        textSize = (int) (gameHeightPx * REL_TEXT_SIZE);
        baseOffsetX = offsetX;
        baseOffsetY = offsetY;
        this.damage = damage;
    }

    /*
    Updates the animation if it is playing, including shifting based on the updated
    sprite coordinates.
     */
    public void update(Sprite sprite, long ms) {
        elapsedTimeMs += ms;
        double fractionElapsed = elapsedTimeMs * 1.0 / DURATION_MS;
        x = sprite.getX() + baseOffsetX + fractionElapsed * totalMovementX;
        y = sprite.getY() + baseOffsetY + fractionElapsed * totalMovementY;
    }

    public void getDrawInstructions(ProtectedQueue<DrawInstruction> drawQueue) {
        if (!isFinished()) {
            drawQueue.push(new DrawText(
                    Integer.toString(damage),
                    (float) x,
                    (float) y,
                    TEXT_COLOR,
                    textSize
            ));
        }
    }

    /*
    Returns whether animation has finished
     */
    public boolean isFinished() {
        return elapsedTimeMs > DURATION_MS;
    }
}
