package com.ganainy.galaxyrun.sprite;

import android.util.Log;

import com.ganainy.galaxyrun.engine.AnimID;
import com.ganainy.galaxyrun.engine.EventID;
import com.ganainy.galaxyrun.engine.GameContext;
import com.ganainy.galaxyrun.engine.UpdateContext;
import com.ganainy.galaxyrun.engine.audio.SoundID;
import com.ganainy.galaxyrun.helper.BitmapID;
import com.ganainy.galaxyrun.engine.draw.DrawImage;
import com.ganainy.galaxyrun.engine.draw.DrawInstruction;
import com.ganainy.galaxyrun.helper.SpriteAnimation;
import com.ganainy.galaxyrun.util.ProtectedQueue;

/**
 * A bullet fired by a spaceship. Travels at a fixed speed based on the current game difficulty.
 * Plays an explosion animation upon colliding with another sprite.
 *
 * Created by Stefan on 8/17/2015.
 */
public class Bullet extends Sprite {

    // Animation to play when the bullet collides with a sprite and explodes.
    private final SpriteAnimation explodeAnim;
    // The image used for the explosion is larger than the image used for the bullet.
    // Calculate how much the explosion image has to be offset to be "centered" on where the
    // bullet was.
    private final int explodeYOffset;

    public Bullet(GameContext gameContext, double x, double y, double difficulty) {
        super(gameContext, x, y, gameContext.bitmapCache.getData(BitmapID.BULLET));

        setHitboxOffsetX(getWidth() * 0.7);
        setHitboxOffsetY(-getHeight() * 0.2);
        setHitboxWidth(getWidth() * 0.45);
        setHitboxHeight(getHeight() * 1.4);

        // Calculate damage and speed based on game difficulty. Bullets become more powerful as
        // the game progresses.
        setHealth(calcDamage(difficulty));
        setSpeedX(calcSpeed(gameContext.gameWidthPx, difficulty));

        explodeAnim = gameContext.animFactory.get(AnimID.BULLET_EXPLODE);
        explodeYOffset = -(explodeAnim.getFrameHeight() - getHeight()) / 2;
    }

    private static int calcDamage(double difficulty) {
        // Damage starts at 4, maxes out at 8
        return (int) (4 * (difficulty + 1));
    }

    private static double calcSpeed(int gameWidthPx, double difficulty) {
        // Speed starts at 30% of screen width per second,
        // maxes out at 50%
        double percentPerSec = 0.3 + difficulty * 0.2;
        return gameWidthPx * percentPerSec;
    }

    @Override
    public void updateActions(UpdateContext updateContext) {
        if (!isVisibleInBounds()) {
            setCurrState(SpriteState.TERMINATED);
        }
        // Terminate if the explosion has finished.
        if (explodeAnim.hasPlayed()) {
            setCurrState(SpriteState.TERMINATED);
            Log.d("Bullet", "Terminating bullet");
        }
    }

    @Override
    public void updateSpeeds(UpdateContext updateContext) {

    }

    @Override
    public void updateAnimations(UpdateContext updateContext) {
        if (explodeAnim.isPlaying()) {
            explodeAnim.update(updateContext.gameTime.msSincePrevUpdate);
        }
    }

    @Override
    public void handleCollision(Sprite s, int damage, UpdateContext updateContext) {
        // TODO: this canCollide() should not be needed. However, I've noticed issues.
        if (canCollide() && !(s instanceof Spaceship)) {
            Log.d("Bullet", "Collided with sprite at " + s.getX() + ", " + s.getY());
            updateContext.createEvent(EventID.BULLET_COLLIDED);
//            setCurrState(SpriteState.DEAD);
            setCollidable(false);
            updateContext.createSound(SoundID.BULLET_DESTROYED);

            // Set speed to match the colliding sprite and start explosion animation.
            setSpeedX(s.getSpeedX());
            explodeAnim.start();
        }
    }

    @Override
    public void getDrawInstructions(ProtectedQueue<DrawInstruction> drawQueue) {
        if (explodeAnim.isPlaying()) {
            Log.d("Bullet",
                    "Playing explodeAnim at x=" + (getX() + getWidth()) + ", y=" + (getY() + explodeYOffset) + ". src.x = " + explodeAnim.getCurrentFrameSrc().left);
            drawQueue.push(new DrawImage(
                    gameContext.bitmapCache.getBitmap(explodeAnim.getBitmapID()),
                    explodeAnim.getCurrentFrameSrc(),
                    (int) getX() + getWidth(),
                    (int) (getY() + explodeYOffset)
            ));
        } else {
            if (explodeAnim.hasPlayed()) {
                Log.e("Bullet", "Drawing bullet after explodeAnim.hasPlayed()");
            }
            drawQueue.push(new DrawImage(
                    gameContext.bitmapCache.getBitmap(BitmapID.BULLET),
                    (int) getX(),
                    (int) getY()
            ));
        }
    }
}
