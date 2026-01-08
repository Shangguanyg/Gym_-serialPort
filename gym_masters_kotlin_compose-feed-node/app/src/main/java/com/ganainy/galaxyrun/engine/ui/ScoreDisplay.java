package com.ganainy.galaxyrun.engine.ui;

import android.graphics.Color;
import android.graphics.Typeface;

import com.ganainy.galaxyrun.engine.GameContext;
import com.ganainy.galaxyrun.engine.UpdateContext;
import com.ganainy.galaxyrun.engine.draw.DrawInstruction;
import com.ganainy.galaxyrun.engine.draw.DrawText;
import com.ganainy.galaxyrun.helper.FontId;
import com.ganainy.galaxyrun.helper.Rectangle;
import com.ganainy.galaxyrun.util.Dimension2D;
import com.ganainy.galaxyrun.util.ProtectedQueue;

/**
 * Displays the game score in the top left of the screen.
 * TODO: would be nice to animate it, so that it gets bigger as the player earns more points
 */
public class ScoreDisplay extends UIElement {

    // Score to display
    private int score;
    // Draw coordinates for text (bottom left)
    private final float drawX, drawY;
    private final int fontSize;
    private final Typeface font;

    private final static FontId FONT = FontId.GALAXY_MONKEY;
    private static final int TEXT_COLOR = Color.parseColor("#ffb30f");
    private static final double TEXT_SIZE_PCT = 0.10;
    private static final float MARGIN_H_PCT = 0.1f;
    private static final float MARGIN_V_PCT = 0.03f;

    public ScoreDisplay(GameContext gameContext) {
        super(gameContext, calcLayout(gameContext));

        font = gameContext.fontCache.get(FONT);
        fontSize = (int) (gameContext.gameHeightPx * TEXT_SIZE_PCT);
        Dimension2D textDims = Util.calcTextDimensions("1234", fontSize, font);
        drawX = (int) bounds.getX();
        drawY = (int) (bounds.getY() + textDims.height);
    }

    private static Rectangle calcLayout(GameContext gameContext) {
        return new Rectangle(
                gameContext.screenWidthPx * MARGIN_H_PCT,
                gameContext.screenHeightPx * MARGIN_V_PCT,
                gameContext.screenHeightPx * TEXT_SIZE_PCT,
                gameContext.screenHeightPx * TEXT_SIZE_PCT
        );
    }

    public void update(UpdateContext updateContext) {
        score = (int) updateContext.score;
    }

    public void getDrawInstructions(ProtectedQueue<DrawInstruction> drawInstructions) {
        drawInstructions.push(new DrawText(
                Integer.toString(score),
                drawX,
                drawY,
                TEXT_COLOR,
                fontSize,
                font
        ));
    }

    public void reset() {
        score = 0;
    }

    public void onTouchEnter(float x, float y) {

    }

    public void onTouchMove(float x, float y) {

    }

    public void onTouchLeave(float x, float y) {

    }
}
