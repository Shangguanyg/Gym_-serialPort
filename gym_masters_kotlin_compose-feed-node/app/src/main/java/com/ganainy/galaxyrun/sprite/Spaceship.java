package com.ganainy.galaxyrun.sprite;

import com.ganainy.galaxyrun.engine.AnimID;
import com.ganainy.galaxyrun.engine.EventID;
import com.ganainy.galaxyrun.engine.GameConstants;
import com.ganainy.galaxyrun.engine.GameContext;
import com.ganainy.galaxyrun.engine.GameEngine;
import com.ganainy.galaxyrun.engine.UpdateContext;
import com.ganainy.galaxyrun.engine.controller.ControlDirection;
import com.ganainy.galaxyrun.engine.controller.ControlState;
import com.ganainy.galaxyrun.helper.ColorMatrixAnimator;
import com.ganainy.galaxyrun.helper.BitmapID;
import com.ganainy.galaxyrun.engine.draw.DrawImage;
import com.ganainy.galaxyrun.engine.draw.DrawInstruction;
import com.ganainy.galaxyrun.engine.audio.SoundID;
import com.ganainy.galaxyrun.helper.SpriteAnimation;
import com.ganainy.galaxyrun.util.ProtectedQueue;

/**
 * Created by Stefan on 8/13/2015.
 */
public class Spaceship extends Sprite {

    // SpriteAnimations used
    private SpriteAnimation moveAnim;
    private SpriteAnimation explodeAnim;
    private SpriteAnimation shootAnim;

    // used to create the spaceship flash animation when hit
    private ColorMatrixAnimator colorMatrixAnimator = new ColorMatrixAnimator(90, 120, 60);

    // Whether user control inputs have any effect.
    private boolean isControllable;
    // Current state of user control input.
    private ControlState controlState;
    // Timestamp *in game time* at which the cannons were last fired
    private long prevCannonShotTime = 0;

    // SoundIDs Spaceship uses
    private static final SoundID BULLET_SOUND = SoundID.PLAYER_SHOOT;
    private static final SoundID EXPLODE_SOUND = SoundID.PLAYER_EXPLODE;

    public Spaceship(GameContext gameContext, double x, double y) {
        super(gameContext, x, y, gameContext.bitmapCache.getData(BitmapID.SPACESHIP));
        controlState = new ControlState(ControlDirection.NEUTRAL, 0, false);
        setHealth(GameConstants.FULL_PLAYER_HEALTH);

        // Position hitbox
        setHitboxWidth(getWidth() * 0.7);
        setHitboxHeight(getHeight() * 0.6);
        setHitboxOffsetX(getWidth() * 0.17);
        setHitboxOffsetY(getHeight() * 0.2f);

        // Load animations from AnimCache
        moveAnim = gameContext.animFactory.get(AnimID.SPACESHIP_MOVE);
        explodeAnim = gameContext.animFactory.get(AnimID.SPACESHIP_EXPLODE);
        shootAnim = gameContext.animFactory.get(AnimID.SPACESHIP_SHOOT);
        moveAnim.start();
    }

    @Override
    public void updateActions(UpdateContext updateContext) {
        // fires cannons if in correct FireMode, has waited long enough, and is still alive
        if (canShoot(updateContext)) {
            fireCannons(updateContext);
            prevCannonShotTime = updateContext.getGameTime().runTimeMs;
        }

        // Checks if explosion has played, in which case terminate should be set to true and onInvisible() called
        if (getState() == SpriteState.DEAD && explodeAnim.hasPlayed()) {
            setCollidable(false);
            setCurrState(SpriteState.TERMINATED);
            updateContext.createEvent(EventID.SPACESHIP_INVISIBLE);
        }
    }

    private boolean canShoot(UpdateContext updateContext) {
        long msSinceLastShot = updateContext.getGameTime().runTimeMs - prevCannonShotTime;
        long shootingDelay = calcShootingDelayMs(updateContext.difficulty);
        return isControllable && controlState.isShooting && getState() == SpriteState.ALIVE &&
                (prevCannonShotTime == 0 || msSinceLastShot >= shootingDelay);
    }

    // Calculate shooting delay based on difficulty.
    // Higher difficulty = faster reload => smaller delay
    private static long calcShootingDelayMs(double difficulty) {
        return (int) (500 - difficulty * 200);
    }

    // It is assumed that the programmer has called `canShoot()` to make sure that
    // the cannons can be fired before calling this method. This method does not
    // check for validity.
    private void fireCannons(UpdateContext updateContext) {
        updateContext.registerSprite(new Bullet(
                gameContext,
                getX() + getWidth() * 0.78f,
                getY() + 0.28f * getHeight(),
                updateContext.difficulty
        ));
        updateContext.registerSprite(new Bullet(
                gameContext,
                getX() + getWidth() * 0.78f,
                getY() + 0.66f * getHeight(),
                updateContext.difficulty
        ));
        updateContext.createSound(BULLET_SOUND);
        updateContext.createEvent(EventID.BULLET_FIRED);
        updateContext.createEvent(EventID.BULLET_FIRED);
        shootAnim.reset();
        shootAnim.start();
    }

    // TODO: ControlDirection enum.
    // Plus, internally a ControlState struct called `controls`
    // Then, simple acceleration model, which will help to further smooth things out
    // ... OR, we do the filtering at the gyroscope level (probably smarter). OR we do both.
    // Then remove the old control UI code.
    // updates the direction the Spaceship is moving in
    public void setControls(ControlState controls) {
        controlState = controls;
    }

    @Override
    public void updateSpeeds(UpdateContext updateContext) {
        // TODO: MORE NUANCED CONTROLS, WITH SIMPLE ACCELERATION/DECELLERATION
        if (isControllable) {
            double percentPerSec = 0.8 + (updateContext.difficulty * 0.2);
            if (controlState.direction == ControlDirection.UP) {
                setSpeedY(-percentPerSec * gameContext.gameHeightPx * controlState.magnitude);
            } else if (controlState.direction == ControlDirection.DOWN){
                setSpeedY(percentPerSec * gameContext.gameHeightPx * controlState.magnitude);
            } else {
                // Slow down
                setSpeedY(getSpeedY() / 1.7);
            }
        }
    }

    @Override
    public void move(UpdateContext updateContext) {
        super.move(updateContext);
        // prevent spaceship from going off-screen
        if (getY() < 0) {
            setY(0);
        } else if (getY() > gameContext.gameHeightPx - getHeight()) {
            setY(gameContext.gameHeightPx - getHeight());
        }
    }

    @Override
    public void updateAnimations(UpdateContext updateContext) {
        colorMatrixAnimator.update(updateContext.getGameTime().msSincePrevUpdate);
        if (moveAnim.isPlaying()) {
            moveAnim.update(updateContext.getGameTime().msSincePrevUpdate);
        }
        if (shootAnim.isPlaying()) {
            shootAnim.update(updateContext.getGameTime().msSincePrevUpdate);
        }
        if (explodeAnim.isPlaying()) {
            explodeAnim.update(updateContext.getGameTime().msSincePrevUpdate);
        }
    }

    @Override
    public void handleCollision(
            Sprite s,
            int damage,
            UpdateContext updateContext
    ) {
        if (!(s instanceof Bullet)) {
            takeDamage(damage);

            // Handle coin collision
            if (s instanceof Coin) {
                updateContext.createEvent(EventID.COIN_COLLECTED);
                updateContext.createSound(SoundID.PLAYER_COLLECT_COIN);
            }

            if (getState() == SpriteState.ALIVE && damage > 0 && getHealth() > 0) {
                // Took damage but still alive
                updateContext.createEvent(EventID.SPACESHIP_DAMAGED);
                updateContext.createSound(SoundID.PLAYER_TAKE_DAMAGE);
                colorMatrixAnimator.flash();
            }
            else if (getState() == SpriteState.ALIVE && getHealth() == 0) {
                // The damage just killed us
                updateContext.createEvent(EventID.SPACESHIP_KILLED);
                updateContext.createSound(EXPLODE_SOUND);
                explodeAnim.start();
                setCurrState(SpriteState.DEAD);
            }
        }
    }

    @Override
    public void getDrawInstructions(ProtectedQueue<DrawInstruction> drawQueue) {
        if (!explodeAnim.hasPlayed()) {
            // Draw the Spaceship
            DrawImage drawShip = new DrawImage(
                    gameContext.bitmapCache.getBitmap(BitmapID.SPACESHIP),
                    (int) getX(),
                    (int) getY()
            );
            drawShip.setColorMatrix(colorMatrixAnimator.getMatrix());
            drawQueue.push(drawShip);

            // Draw the moving animation
            DrawImage drawExhaust = new DrawImage(
                    gameContext.bitmapCache.getBitmap(moveAnim.getBitmapID()),
                    moveAnim.getCurrentFrameSrc(),
                    (int) getX(),
                    (int) getY()
            );
            drawExhaust.setColorMatrix(colorMatrixAnimator.getMatrix());
            drawQueue.push(drawExhaust);

            // Draw the shooting animation
            if (shootAnim.isPlaying()) {
                DrawImage drawShooting = new DrawImage(
                        gameContext.bitmapCache.getBitmap(shootAnim.getBitmapID()),
                        shootAnim.getCurrentFrameSrc(),
                        (int) getX(),
                        (int) getY()
                );
                drawShooting.setColorMatrix(colorMatrixAnimator.getMatrix());
                drawQueue.push(drawShooting);
            }

            // Draw the explosion animation if it is playing
            if (explodeAnim.isPlaying()) {
                DrawImage drawExplosion = new DrawImage(
                        gameContext.bitmapCache.getBitmap(explodeAnim.getBitmapID()),
                        explodeAnim.getCurrentFrameSrc(),
                        (int) getX(),
                        (int) getY()
                );
                drawQueue.push(drawExplosion);
            }
        }
    }

    public void setControllable(boolean controllable) {
        this.isControllable = controllable;
    }
}