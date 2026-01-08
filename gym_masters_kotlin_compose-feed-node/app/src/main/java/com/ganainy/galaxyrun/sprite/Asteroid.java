package com.ganainy.galaxyrun.sprite;

import com.ganainy.galaxyrun.engine.EventID;
import com.ganainy.galaxyrun.engine.GameContext;
import com.ganainy.galaxyrun.engine.UpdateContext;
import com.ganainy.galaxyrun.engine.audio.SoundID;
import com.ganainy.galaxyrun.helper.BitmapID;
import com.ganainy.galaxyrun.engine.draw.DrawImage;
import com.ganainy.galaxyrun.engine.draw.DrawInstruction;
import com.ganainy.galaxyrun.helper.HealthBarAnimation;
import com.ganainy.galaxyrun.helper.LoseHealthAnimation;
import com.ganainy.galaxyrun.util.ProtectedQueue;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * The Asteroid is a fairly slow-moving sprite that rotates. It bounces
 * off the edges of the screen and has relatively high hp. Rotation rate
 * as well as speedY are randomized to give each Asteroid an element of
 * uniqueness.
 */

public class Asteroid extends Sprite {

    // Current rotation, in degrees, of asteroid
    private float currentRotation;
    // Degrees rotated per frame (positive or negative)
    private final double degRotationPerSecond;

    // draws animated healthbar above Asteroid if Asteroid takes damage
    private HealthBarAnimation healthBarAnimation;
    // stores any running animations showing Asteroid taking damage
    private List<LoseHealthAnimation> loseHealthAnimations = new LinkedList<>();

    public Asteroid(
            GameContext gameContext,
            double x,
            double y,
            double difficulty,
            double scrollSpeedPx
    ) {
        super(gameContext, x, y, gameContext.bitmapCache.getData(BitmapID.ASTEROID));

        // Set SpeedX to a random value between 60% and 120% of current scrollSpeed
        setSpeedX(-scrollSpeedPx * (0.6 + gameContext.rand.nextInt(60) / 100.0));
        // Set SpeedY to a random value between -20% and +20% of current scrollSpeed
        setSpeedY(-scrollSpeedPx * (-0.2 + gameContext.rand.nextInt(40) / 100.0));
        // Set health relatively high
        setHealth(10 + (int) (difficulty * 20));
        // Make Hitbox 20% smaller than sprite
        setHitboxWidth(getWidth() * 0.8);
        setHitboxHeight(getHeight() * 0.8);
        setHitboxOffsetX(getWidth() * 0.1);
        setHitboxOffsetY(getHeight() * 0.1);
        // Set the current rotation to a random angle
        currentRotation = gameContext.rand.nextInt(360);
        // Set rotation rate as function of speedY (faster speed = faster rotation)
        degRotationPerSecond = 60 + getSpeedY() / gameContext.gameHeightPx * 400;
        // Init HealthBarAnimation
        healthBarAnimation = new HealthBarAnimation(this);
    }

    @Override
    public void updateActions(UpdateContext updateContext) {
        if (getX() < -getWidth()) {
            setCurrState(SpriteState.TERMINATED);
        }
    }

    @Override
    public void updateSpeeds(UpdateContext updateContext) {
        // Reverse speedY if it is nearly headed off a screen edge (i.e. "bounce")
        boolean leavingAbove =
                getY() + getHeight() >= gameContext.gameHeightPx && getSpeedY() > 0;
        boolean leavingBelow = getY() <= 0 && getSpeedY() < 0;

        if (leavingAbove || leavingBelow) {
            setSpeedY(-1 * getSpeedY());
        }
    }

    @Override
    public void updateAnimations(UpdateContext updateContext) {
        // Increment currentRotation to create the rotating animation
        currentRotation += degRotationPerSecond * updateContext.gameTime.secSincePrevUpdate;

        // Update LoseHealthAnimations
        Iterator<LoseHealthAnimation> health_anims = loseHealthAnimations.iterator();
        while(health_anims.hasNext()) {
            LoseHealthAnimation anim = health_anims.next();
            // Remove animation if finished
            if (anim.isFinished()) {
                health_anims.remove();
            } else {  // Update animation
                anim.update(this, updateContext.getGameTime().msSincePrevUpdate);
            }
        }

        // Update HealthBarAnimation
        healthBarAnimation.update(this, updateContext.getGameTime().msSincePrevUpdate);
    }

    @Override
    public void handleCollision(Sprite s, int damage, UpdateContext updateContext) {
        if (s instanceof Spaceship || s instanceof Bullet) {
            if (s instanceof Bullet) {
                updateContext.createEvent(EventID.ASTEROID_SHOT);
            }

            takeDamage(damage);
            if (getState() == SpriteState.ALIVE && getHealth() == 0) {
                updateContext.createEvent(EventID.ASTEROID_DESTROYED);
                setCurrState(SpriteState.TERMINATED);
                updateContext.createSound(SoundID.ASTEROID_EXPLODE);
            }

            healthBarAnimation.triggerShow();
            if (getState() == SpriteState.ALIVE) {
                loseHealthAnimations.add(new LoseHealthAnimation(
                        getWidth(),
                        getHeight(),
                        s.getX() - getX(),
                        s.getY() - getY(),
                        damage
                ));
                updateContext.createSound(SoundID.ASTEROID_TAKE_DAMAGE);
            }
        }
    }

    @Override
    public void getDrawInstructions(ProtectedQueue<DrawInstruction> drawQueue) {
        DrawImage drawAsteroid = new DrawImage(
                gameContext.bitmapCache.getBitmap(BitmapID.ASTEROID),
                (int) getX(),
                (int) getY()
        );
        drawAsteroid.setRotation((int) currentRotation);
        drawQueue.push(drawAsteroid);

        for (LoseHealthAnimation anim : loseHealthAnimations) {
            anim.getDrawInstructions(drawQueue);
        }

        healthBarAnimation.getDrawInstructions(drawQueue);
    }
}
