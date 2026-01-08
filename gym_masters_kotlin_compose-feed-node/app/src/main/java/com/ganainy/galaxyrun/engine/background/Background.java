package com.ganainy.galaxyrun.engine.background;

import android.graphics.*;

import com.ganainy.galaxyrun.engine.GameContext;
import com.ganainy.galaxyrun.engine.GameState;
import com.ganainy.galaxyrun.engine.UpdateContext;
import com.ganainy.galaxyrun.engine.draw.DrawImage;
import com.ganainy.galaxyrun.engine.draw.DrawInstruction;
import com.ganainy.galaxyrun.util.ProtectedQueue;

/**
 * Draws the background of the game. Renders a galaxy background using a
 * GalaxyDrawer, then scrolls from left to right in a loop.
 */
public class Background {

    private final GameContext gameContext;
    private final BackgroundGenerator generator;
    // Number of pixels that the background has scrolled.
    private long pixelsScrolled;
    // Rendered background "panels". We always store a "left" panel and a "right" panel.
    private Bitmap panelLeft;
    private Bitmap panelRight;
    // The value of `pixelsScrolled` that `panelLeft` began being shown at.
    private long leftStartedAt;
    // Relative speed of background scrolling to foreground scrolling.
    // A value below 1 gives a "parallax" effect.
    public static final float SCROLL_SPEED_FACTOR = 0.3f;

    public Background(GameContext gameContext) {
        this.gameContext = gameContext;
        generator = new BackgroundGenerator(
                gameContext.screenWidthPx, gameContext.screenHeightPx, gameContext.rand);
        leftStartedAt = 0;
        panelLeft = generator.nextPanel();
        panelRight = generator.nextPanel();
    }

    public void update(UpdateContext updateContext) {
        // Only scroll the background while the rest of the game is moving.
        if (updateContext.gameState == GameState.PLAYING || updateContext.gameState == GameState.PLAYER_DEAD) {
            this.pixelsScrolled +=
                    updateContext.scrollSpeedPx * SCROLL_SPEED_FACTOR * updateContext.gameTime.secSincePrevUpdate;
        }
    }

    public void getDrawInstructions(ProtectedQueue<DrawInstruction> drawInstructions) {
        // How many pixels have we progressed along the left panel?
        int offset = (int) (pixelsScrolled - leftStartedAt);
        if (offset > panelLeft.getWidth()) {
            // We are beyond the left panel; swap right to left and get the next one.
            offset -= panelLeft.getWidth();
            panelLeft = panelRight;
            panelRight = generator.nextPanel();
            leftStartedAt = pixelsScrolled - offset;
        }

        // Draw from the left panel.
        // How many pixels from the left panel will go on-screen?
        int leftWidth = Math.min(panelLeft.getWidth() - offset, gameContext.screenWidthPx);
        Rect srcLeft = new Rect(offset, 0, offset + leftWidth, gameContext.screenHeightPx);
        Rect dstLeft = new Rect(0, 0, leftWidth, gameContext.screenHeightPx);
        drawInstructions.push(new DrawImage(panelLeft, srcLeft, dstLeft));

        // How many pixels from the right panel will go on-screen?
        int rightWidth = gameContext.screenWidthPx - leftWidth;
        if (rightWidth > 0) {
            Rect srcRight = new Rect(0, 0, rightWidth, gameContext.screenHeightPx);
            Rect dstRight = new Rect(leftWidth, 0, gameContext.screenWidthPx, gameContext.screenHeightPx);
            drawInstructions.push(new DrawImage(panelRight, srcRight, dstRight));
        }
    }
}