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

public class PauseOverlay extends UIElement {
    private final int fontSize;
    private final Typeface font;
    // Draw coordinates for text
    private final int drawX, drawY;

    // Overlay width/height
    private final static double WIDTH_PCT = 0.5;
    private final static double HEIGHT_PCT = 0.3;

    // Configure text
    private final static String TEXT = "Paused";
    private final static double TEXT_SIZE_PCT = 0.2;
    private final static FontId FONT = FontId.GALAXY_MONKEY;
    private final int TEXT_COLOR = Color.parseColor("#ffb30f");

    public PauseOverlay(GameContext gameContext) {
        super(gameContext, calcLayout(gameContext));
        isTouchable = false;
        isVisible = false;

        font = gameContext.fontCache.get(FONT);
        fontSize = (int) (gameContext.gameHeightPx * TEXT_SIZE_PCT);
        Dimension2D textDims = Util.calcTextDimensions(TEXT, fontSize, font);
        drawX = (int) (bounds.getX() + (bounds.getWidth() - textDims.width) / 2);
        drawY = (int) (bounds.getY() + (bounds.getHeight() - textDims.height) / 2 + textDims.height);
    }

    private static Rectangle calcLayout(GameContext gameContext) {
        double width = gameContext.gameWidthPx * WIDTH_PCT;
        double height = gameContext.gameHeightPx * HEIGHT_PCT;
        return new Rectangle(
                (gameContext.gameWidthPx - width) / 2,
                (gameContext.gameHeightPx - height) / 2,
                width,
                height
        );
    }

    public void show() {
        isTouchable = true;
        isVisible = true;
    }

    public void hide() {
        isTouchable = false;
        isVisible = false;
    }

    @Override
    public void update(UpdateContext updateContext) {

    }

    @Override
    public void getDrawInstructions(ProtectedQueue<DrawInstruction> drawInstructions) {
//        drawInstructions.push(DrawRect.filled(bounds.toRect(), Color.BLACK));

        drawInstructions.push(new DrawText(
                TEXT,
                drawX,
                drawY,
                TEXT_COLOR,
                fontSize,
                font
        ));
    }

    @Override
    public void onTouchEnter(float x, float y) {

    }

    @Override
    public void onTouchMove(float x, float y) {

    }

    @Override
    public void onTouchLeave(float x, float y) {

    }
}
