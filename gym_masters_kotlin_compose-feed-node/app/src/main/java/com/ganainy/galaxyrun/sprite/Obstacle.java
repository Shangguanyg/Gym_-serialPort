package com.ganainy.galaxyrun.sprite;

import android.graphics.Color;

import com.ganainy.galaxyrun.engine.GameContext;
import com.ganainy.galaxyrun.engine.UpdateContext;
import com.ganainy.galaxyrun.engine.draw.DrawInstruction;
import com.ganainy.galaxyrun.engine.draw.DrawRect;
import com.ganainy.galaxyrun.util.ProtectedQueue;

/**
 * The Obstacle is a basic sprite that looks like a regular gray rectangle. Because there it has no
 * image, its hitbox is used to determine both its collision area as well as drawing bounds.
 *
 * The Obstacle only responds to collisions with the Spaceship. In this case it does base damage,
 * as determined by what it originally was set to. Then, each frame it canCollide with the spaceship
 * it does that amount of damage plus a small amount added each frame (so damage increases each frame).
 * This way if the player reacts quickly enough, the damage may not be too deadly.
 */
public class Obstacle extends Sprite {

    // Color
    private static final int OBSTACLE_COLOR = Color.rgb(103, 103, 103);
    // Amount of damage done
    public static final int OBSTACLE_DAMAGE = 5;

    public Obstacle(GameContext gameContext, double x, double y, int width, int height) {
        super(gameContext, x, y, width, height);
        setHealth(OBSTACLE_DAMAGE);
    }

    @Override
    public void updateActions(UpdateContext updateContext) {
        // terminate when hitBox is out of bounds to the left of the screen
        if (getX() < -getWidth()) {
            setCurrState(SpriteState.TERMINATED);
        }
    }

    @Override
    public void updateSpeeds(UpdateContext updateContext) {
        // Set speed to game's scroll speed
        setSpeedX(-updateContext.scrollSpeedPx);
    }

    @Override
    public void updateAnimations(UpdateContext updateContext) {

    }

    @Override
    public void handleCollision(Sprite s, int damage, UpdateContext updateContext) {
//        if (s.getSpriteType() == SpriteType.SPACESHIP) {
//            hp += 2; // todo: too high?
//        }
    }

    @Override
    public void getDrawInstructions(ProtectedQueue<DrawInstruction> drawQueue) {
        drawQueue.push(DrawRect.filled(getHitbox().toRect(), OBSTACLE_COLOR));
        // Draw red outline. Makes it a little more interesting.
        drawQueue.push(DrawRect.outline(getHitbox().toRect(), Color.RED, 3f));
    }
}
