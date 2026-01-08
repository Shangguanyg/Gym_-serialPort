package com.ganainy.galaxyrun.engine.ui;

import com.ganainy.galaxyrun.engine.GameContext;
import com.ganainy.galaxyrun.engine.UpdateContext;
import com.ganainy.galaxyrun.engine.draw.DrawInstruction;
import com.ganainy.galaxyrun.helper.Rectangle;
import com.ganainy.galaxyrun.util.ProtectedQueue;

public class ShootButton extends UIElement {
    private boolean isShooting;
    private static final double WIDTH_PCT = 0.9;
    private static final double HEIGHT_PCT = 0.9;

    public ShootButton(GameContext gameContext) {
        super(gameContext, calcLayout(gameContext));
    }

    private static Rectangle calcLayout(GameContext gameContext) {
        double width = gameContext.gameWidthPx * WIDTH_PCT;
        double height = gameContext.gameHeightPx * HEIGHT_PCT;
        return new Rectangle(
                gameContext.gameWidthPx - width,
                (gameContext.gameHeightPx - height) / 2,
                width,
                height
        );
    }

    @Override
    public void update(UpdateContext updateContext) {
        if (isShooting) {
            createdInput.add(UIInputId.SHOOT);
        }
    }

    @Override
    public void getDrawInstructions(ProtectedQueue<DrawInstruction> drawInstructions) {

    }

    @Override
    public void onTouchEnter(float x, float y) {
        isShooting = true;
    }

    @Override
    public void onTouchMove(float x, float y) {

    }

    @Override
    public void onTouchLeave(float x, float y) {
        isShooting = false;
    }
}
