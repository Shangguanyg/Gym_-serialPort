package com.ganainy.galaxyrun.engine.ui;

import android.graphics.Color;
import android.graphics.Typeface;

import com.ganainy.galaxyrun.engine.GameContext;
import com.ganainy.galaxyrun.engine.UpdateContext;
import com.ganainy.galaxyrun.engine.audio.SoundID;
import com.ganainy.galaxyrun.engine.draw.DrawInstruction;
import com.ganainy.galaxyrun.engine.draw.DrawRect;
import com.ganainy.galaxyrun.engine.draw.DrawText;
import com.ganainy.galaxyrun.helper.FontId;
import com.ganainy.galaxyrun.helper.Rectangle;
import com.ganainy.galaxyrun.util.Dimension2D;
import com.ganainy.galaxyrun.util.ProtectedQueue;

// TODO: add score display, stars earned, etc.
public class GameoverOverlay extends UIElement {

    private boolean isButtonTouched;
    private final Typeface font;
    private final int titleFontSize;
    private final int drawTitleX, drawTitleY;
    private final int playBtnFontSize;
    private final int drawPlayBtnX, drawPlayBtnY;

    private final static FontId FONT = FontId.GALAXY_MONKEY;
    private final int TEXT_COLOR = Color.parseColor("#ffb30f");

    // As percent of GAME size
    private final static double WIDTH_PCT = 0.7;
    private final static double HEIGHT_PCT = 0.7;

    // Define the title ("Game Over")
    private final static String TITLE_TEXT = "Game Over";
    private final static double TITLE_TEXT_SIZE_PCT = 0.2;
    private final static double TITLE_MARGIN_TOP_PCT = 0.05;

    // Define the "Play Again" button
    private final static String PLAY_BUTTON_TEXT = "Play Again";
    private final static double PLAY_BUTTON_MARGIN_TOP_PCT = 0.1;
    private final static double PLAY_BUTTON_WIDTH_PCT = 0.4;
    private final static double PLAY_BUTTON_HEIGHT_PCT = 0.2;
    private final static double PLAY_BUTTON_TEXT_SIZE_PCT = 0.12;
    private final static int PLAY_BUTTON_COLOR_NORMAL = Color.parseColor("#7F7F7F");
    private final static int PLAY_BUTTON_COLOR_TOUCHED = Color.WHITE;

    // Bounds of the "Play Again" button
    private final Rectangle playAgainBounds;

    public GameoverOverlay(GameContext gameContext) {
        super(gameContext, calcLayout(gameContext));
        isTouchable = false;
        isVisible = false;
        font = gameContext.fontCache.get(FONT);

        titleFontSize = (int) (gameContext.gameHeightPx * TITLE_TEXT_SIZE_PCT);
        Dimension2D titleTextDims =
                Util.calcTextDimensions(TITLE_TEXT, titleFontSize, font);
        double titleMarginTop = gameContext.gameHeightPx * TITLE_MARGIN_TOP_PCT;
        drawTitleX = (int) (bounds.getX() + (bounds.getWidth() - titleTextDims.width) / 2);
        drawTitleY = (int) (bounds.getY() + titleTextDims.height + titleMarginTop);

        // Calc button bounds
        double playAgainMarginTop = gameContext.gameHeightPx * PLAY_BUTTON_MARGIN_TOP_PCT;
        double playAgainWidth = gameContext.gameWidthPx * PLAY_BUTTON_WIDTH_PCT;
        double playAgainHeight = gameContext.gameHeightPx * PLAY_BUTTON_HEIGHT_PCT;
        playAgainBounds = new Rectangle(
                bounds.getX() + (bounds.getWidth() - playAgainWidth) / 2,
                drawTitleY + playAgainMarginTop,
                playAgainWidth,
                playAgainHeight
        );

        playBtnFontSize = (int) (gameContext.gameHeightPx * PLAY_BUTTON_TEXT_SIZE_PCT);
        Dimension2D playBtnTextDims =
                Util.calcTextDimensions(PLAY_BUTTON_TEXT, playBtnFontSize, font);
        drawPlayBtnX = (int) (playAgainBounds.getX() + (playAgainBounds.getWidth() - playBtnTextDims.width) / 2);
        drawPlayBtnY = (int) (playAgainBounds.getY() + (playAgainBounds.getHeight() - playBtnTextDims.height) / 2 + playBtnTextDims.height);
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
        drawInstructions.push(DrawRect.filled(bounds.toRect(), Color.BLACK));

        drawInstructions.push(new DrawText(
                TITLE_TEXT,
                drawTitleX,
                drawTitleY,
                TEXT_COLOR,
                titleFontSize,
                font
        ));

        drawInstructions.push(DrawRect.filled(
                playAgainBounds.toRect(),
                isButtonTouched ? PLAY_BUTTON_COLOR_TOUCHED : PLAY_BUTTON_COLOR_NORMAL)
        );

        drawInstructions.push(new DrawText(
                PLAY_BUTTON_TEXT,
                drawPlayBtnX,
                drawPlayBtnY,
                TEXT_COLOR,
                playBtnFontSize,
                font
        ));
    }

    @Override
    public void onTouchEnter(float x, float y) {
        if (playAgainBounds.isInBounds(x, y)) {
            isButtonTouched = true;
        }
    }

    @Override
    public void onTouchMove(float x, float y) {
        isButtonTouched = playAgainBounds.isInBounds(x, y);
    }

    @Override
    public void onTouchLeave(float x, float y) {
        if (playAgainBounds.isInBounds(x, y)) {
            createdSounds.add(SoundID.UI_CLICK_BUTTON);
            createdInput.add(UIInputId.RESTART);
        }
        isButtonTouched = false;
    }
}
