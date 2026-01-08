package com.ganainy.galaxyrun.sprite;

import com.ganainy.galaxyrun.engine.AnimID;
import com.ganainy.galaxyrun.engine.EventID;
import com.ganainy.galaxyrun.engine.GameContext;
import com.ganainy.galaxyrun.engine.UpdateContext;
import com.ganainy.galaxyrun.helper.BitmapID;
import com.ganainy.galaxyrun.engine.draw.DrawImage;
import com.ganainy.galaxyrun.engine.draw.DrawInstruction;
import com.ganainy.galaxyrun.helper.HealthBarAnimation;
import com.ganainy.galaxyrun.helper.LoseHealthAnimation;
import com.ganainy.galaxyrun.helper.Point2D;
import com.ganainy.galaxyrun.engine.audio.SoundID;
import com.ganainy.galaxyrun.helper.SpriteAnimation;
import com.ganainy.galaxyrun.util.ProtectedQueue;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * An Alien is an enemy sprite that flies across the screen
 * in a sine wave. Its hp is calculated based on the difficulty,
 * and Aliens become steadily stronger as difficulty increases.
 * An Alien fires AlienBullets at the Spaceship, with this behavior
 * determined by the bulletDelay.
 * The Alien also has a HealthBarAnimation, which
 * displays when it loses health, and additionally displays
 * LoseHealthAnimations when it loses damage.
 */
public class Alien extends Sprite {

    private enum AlienState {
        FLYING_IN,
        HOVERING
    };

    private AlienState alienState;
    // Time since the last bullet was fired
    private int msSinceLastBullet;
    // X-Coordinate at which the Alien will hover
    private final double hoverX;
    // Defines sine wave of Alien's trajectory while hovering
    private final int amplitudePx;
    private final long periodMs;
    // Minimum amount of time that must elapse between each shot
    private final int bulletWaitMs;

    private SpriteAnimation explodeAnim;
    // draws animated healthbar above Alien if Alien is damaged
    private HealthBarAnimation healthBarAnimation;
    // stores any running animations showing health leaving alien
    private List<LoseHealthAnimation> loseHealthAnimations = new LinkedList<>();

    public Alien(
            GameContext gameContext,
            double x,
            double y,
            double currDifficulty
    ) {
        super(gameContext, x, y, gameContext.bitmapCache.getData(BitmapID.ALIEN));
        setHealth((int) (currDifficulty * 20));

        setHitboxOffsetX(getWidth() * 0.2);
        setHitboxOffsetY(getHeight() * 0.2);
        setHitboxWidth(getWidth() * 0.8);
        setHitboxHeight(getHeight() * 0.8);

        alienState = AlienState.FLYING_IN;
        // Set to between 7/10 and 8/10ths of screenWidth
        hoverX = gameContext.gameWidthPx * (0.7 + gameContext.rand.nextDouble() / 10);
        // Configure sine wave to fly in while hovering
        amplitudePx = (int) (gameContext.tileWidthPx * (1 + gameContext.rand.nextDouble()));
        periodMs = 800 + gameContext.rand.nextInt(400);

        bulletWaitMs = 2000 - (int) (currDifficulty * 1000);
        explodeAnim = gameContext.animFactory.get(AnimID.ALIEN_EXPLODE);
        healthBarAnimation = new HealthBarAnimation(this);
    }

    @Override
    public void updateActions(UpdateContext updateContext) {
        if (alienState == AlienState.HOVERING) {
            msSinceLastBullet += updateContext.getGameTime().msSincePrevUpdate;
        }

        if (shouldTerminate()) {
            setCurrState(SpriteState.TERMINATED);
        }

        if (canShoot(updateContext)) {
            updateContext.createEvent(EventID.ALIEN_FIRED_BULLET);
            updateContext.createSound(SoundID.ALIEN_SHOOT);
            shootBullet(updateContext, updateContext.playerSprite);
            msSinceLastBullet = 0;
        }

        if (alienState == AlienState.FLYING_IN && getX() < hoverX) {
            // Switch to HOVERING
            alienState = AlienState.HOVERING;
        }
    }

    private boolean shouldTerminate() {
        // Terminate if dead and explosion has finished playing,
        // or if no longer visible
        return getState() == SpriteState.DEAD && explodeAnim.hasPlayed();
    }

    private boolean canShoot(UpdateContext updateContext) {
        return getState() == SpriteState.ALIVE &&
                alienState == AlienState.HOVERING &&
                updateContext.playerSprite.getState() == SpriteState.ALIVE &&
                msSinceLastBullet >= bulletWaitMs;
    }

    /*
    Shoots a bullet at the specified sprite. Introduces some randomized inaccuracy.
    The Bullet is initialized halfway down the alien on the left side
     */
    public void shootBullet(UpdateContext updateContext, Sprite s) {
        Point2D target_center = s.getHitbox().getCenter();
        double vertInaccuracy = (gameContext.rand.nextBoolean() ? -1 : +1) *
                gameContext.tileWidthPx * gameContext.rand.nextDouble();
        updateContext.registerSprite(new AlienBullet(
                gameContext,
                getX(),
                getY() + getHeight() * 0.5,
                target_center.getX(),
                target_center.getY() + vertInaccuracy,
                updateContext.scrollSpeedPx
        ));
    }

    @Override
    public void updateSpeeds(UpdateContext updateContext) {
        if (alienState == AlienState.FLYING_IN) {
            setSpeedX(-updateContext.scrollSpeedPx);
        } else {
            setSpeedX(0);
        }
        setSpeedY(Math.cos(updateContext.getGameTime().runTimeMs * 1.0 / periodMs) * amplitudePx);
    }

    @Override
    public void updateAnimations(UpdateContext updateContext) {
        if (explodeAnim.isPlaying()) {
            explodeAnim.update(updateContext.getGameTime().msSincePrevUpdate);
        }

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

        // Update HealthbarAnimation
        healthBarAnimation.update(this, updateContext.getGameTime().msSincePrevUpdate);
    }

    @Override
    public void handleCollision(Sprite s, int damage, UpdateContext updateContext) {
        if (s instanceof Spaceship || s instanceof Bullet) {
            if (s instanceof Bullet) {
                updateContext.createEvent(EventID.ALIEN_SHOT);
            }

            takeDamage(damage);
            if (getState() == SpriteState.ALIVE && health == 0) {
                updateContext.createEvent(EventID.ALIEN_DIED);
                setCurrState(SpriteState.DEAD);
                explodeAnim.start();
                updateContext.createSound(SoundID.ALIEN_EXPLODE);
            }

            healthBarAnimation.triggerShow();
            if (getState() == SpriteState.ALIVE) {
                loseHealthAnimations.add(new LoseHealthAnimation(
                        gameContext.gameWidthPx,
                        gameContext.gameHeightPx,
                        s.getX() - getX(),
                        s.getY() - getY(),
                        damage
                ));
                updateContext.createSound(SoundID.ALIEN_TAKE_DAMAGE);
            }
        }
    }

    @Override
    public void getDrawInstructions(ProtectedQueue<DrawInstruction> drawQueue) {
        // Draw alien, unless it is exploding and in the last frame of the explosion animation
        if (!(explodeAnim.isPlaying() && explodeAnim.getFramesLeft() <= 1)) {
            drawQueue.push(new DrawImage(
                    gameContext.bitmapCache.getBitmap(BitmapID.ALIEN),
                    (int) getX(),
                    (int) getY())
            );
        }
        // Draw loseHealthAnimations
        for (LoseHealthAnimation anim : loseHealthAnimations) {
            anim.getDrawInstructions(drawQueue);
        }
        // Draw HealthBarAnimation if showing
        healthBarAnimation.getDrawInstructions(drawQueue);

        // Draw explosion
        if (explodeAnim.isPlaying()) {
            DrawImage explodeImg = new DrawImage(
                    gameContext.bitmapCache.getBitmap(explodeAnim.getBitmapID()),
                    explodeAnim.getCurrentFrameSrc(),
                    (int) getX(),
                    (int) getY()
            );
            drawQueue.push(explodeImg);
        }
    }
}
