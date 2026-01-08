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

public class MuteButton extends UIElement {

    private boolean isMuted;

    // Layout configuration
    private static final double WIDTH_PCT = 0.08;
    private static final double MARGIN_RIGHT_PCT = 0.06;
    private static final double MARGIN_TOP_PCT = 0.02;

    public MuteButton(GameContext gameContext) {
        super(gameContext, calcLayout(gameContext));
    }

    public static Rectangle calcLayout(GameContext gameContext) {
        return new Rectangle(
                gameContext.screenWidthPx * (1.0 - MARGIN_RIGHT_PCT - WIDTH_PCT),
                gameContext.screenHeightPx * MARGIN_TOP_PCT,
                gameContext.screenWidthPx * WIDTH_PCT,
                gameContext.screenWidthPx * WIDTH_PCT
        );
    }

    public void update(UpdateContext updateContext) {
        isMuted = updateContext.isMuted;
    }

    public void getDrawInstructions(ProtectedQueue<DrawInstruction> drawInstructions) {
        BitmapID bitmapId = (isMuted ? BitmapID.MUTE_BUTTON_MUTED : BitmapID.MUTE_BUTTON_UNMUTED);
        Bitmap bitmap = gameContext.bitmapCache.getBitmap(bitmapId);
        Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        DrawImage drawBtn = new DrawImage(bitmap, src, bounds.toRect());
        drawInstructions.push(drawBtn);
    }

    public void onTouchEnter(float x, float y) {
//        Log.d("MuteButton", "onTouchEnter " + x + ", " + y);
    }

    public void onTouchMove(float x, float y) {
//        Log.d("MuteButton", "onTouchMove " + x + ", " + y);
    }

    public void onTouchLeave(float x, float y) {
//        Log.d("MuteButton", "onTouchLeave " + x + ", " + y);
        // Create event to toggle state
        createdInput.add((isMuted ? UIInputId.UNMUTE : UIInputId.MUTE));
        createdSounds.add(SoundID.UI_CLICK_BUTTON);
    }
}
