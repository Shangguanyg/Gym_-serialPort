package com.ganainy.galaxyrun.engine.ui;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.ganainy.galaxyrun.engine.GameContext;
import com.ganainy.galaxyrun.engine.UpdateContext;
import com.ganainy.galaxyrun.engine.audio.SoundID;
import com.ganainy.galaxyrun.engine.draw.DrawImage;
import com.ganainy.galaxyrun.engine.draw.DrawInstruction;
import com.ganainy.galaxyrun.helper.BitmapID;
import com.ganainy.galaxyrun.helper.Rectangle;
import com.ganainy.galaxyrun.util.ProtectedQueue;

public class PauseButton extends UIElement {

    private boolean isPaused;

    // Layout configuration
    private static final double WIDTH_PCT = 0.08;
    private static final double MARGIN_RIGHT_PCT = 0.15;
    private static final double MARGIN_TOP_PCT = 0.02;

    public PauseButton(GameContext gameContext) {
        super(gameContext, calcLayout(gameContext));
    }

    private static Rectangle calcLayout(GameContext gameContext) {
        return new Rectangle(
            gameContext.screenWidthPx * (1.0 - MARGIN_RIGHT_PCT - WIDTH_PCT),
            gameContext.screenHeightPx * MARGIN_TOP_PCT,
            gameContext.screenWidthPx * WIDTH_PCT,
            gameContext.screenWidthPx * WIDTH_PCT
        );
    }

    public void update(UpdateContext updateContext) {
        isPaused = updateContext.isPaused;
    }

    public void getDrawInstructions(ProtectedQueue<DrawInstruction> drawInstructions) {
        BitmapID bitmapId = (isPaused ? BitmapID.PAUSE_BUTTON_UNPAUSED : BitmapID.PAUSE_BUTTON_PAUSED);
        Bitmap bitmap = gameContext.bitmapCache.getBitmap(bitmapId);
        Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        DrawImage drawBtn = new DrawImage(bitmap, src, bounds.toRect());
        drawInstructions.push(drawBtn);
    }

    public void onTouchEnter(float x, float y) {
//        Log.d("PauseButton", "onTouchEnter " + x + ", " + y);
    }

    public void onTouchMove(float x, float y) {
//        Log.d("PauseButton", "onTouchMove " + x + ", " + y);
    }

    public void onTouchLeave(float x, float y) {
//        Log.d("PauseButton", "onTouchLeave " + x + ", " + y);
        // Register event to toggle state
        createdInput.add((isPaused ? UIInputId.RESUME : UIInputId.PAUSE));
        createdSounds.add(SoundID.UI_CLICK_BUTTON);
    }
}
