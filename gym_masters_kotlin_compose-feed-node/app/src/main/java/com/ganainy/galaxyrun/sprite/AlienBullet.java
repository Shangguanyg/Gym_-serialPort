package com.ganainy.galaxyrun.sprite;

import com.ganainy.galaxyrun.engine.EventID;
import com.ganainy.galaxyrun.engine.GameContext;
import com.ganainy.galaxyrun.engine.UpdateContext;
import com.ganainy.galaxyrun.helper.BitmapID;
import com.ganainy.galaxyrun.engine.draw.DrawImage;
import com.ganainy.galaxyrun.engine.draw.DrawInstruction;
import com.ganainy.galaxyrun.util.ProtectedQueue;

/**
 * An AlienBullet is a projectile fired by an Alien at a
 * certain set of coordinates (specified in the constructor).
 * The AlienBullet will determine its own speedX and speedY
 * to reach the target. It will also request to be drawn at
 * the correct angle to reach its target.
 */
public class AlienBullet extends Sprite {

    // Angle at which bullet travels
    private final double travelAngle;

    public AlienBullet(
            GameContext gameContext,
            double x,
            double y,
            double targetX,
            double targetY,
            double scrollSpeedPx
    ) {
        super(gameContext, x, y, gameContext.bitmapCache.getData(BitmapID.ALIEN_BULLET));

        setHealth(10);
        // SpeedX is fixed TODO: use current scrollspeed
        setSpeedX(-scrollSpeedPx * 0.8);

        // Calculate travelAngle based on distance to target in x and y.
        // Keep in mind this gets tricky because we're in canvas coordinates
        double distX = x - targetX;
        double distY = y - targetY;
        travelAngle = Math.toDegrees(Math.atan(distY / distX));

        // Calculate speedY using relative screen dimensions
        double timeToImpactSec = Math.abs(distX / getSpeedX());
        setSpeedY(-distY / timeToImpactSec);
    }

    @Override
    public void updateActions(UpdateContext updateContext) {
        if (!isVisibleInBounds()) {
            updateContext.createEvent(EventID.ALIEN_BULLET_OFF_SCREEN);
            setCurrState(SpriteState.TERMINATED);
        }
    }

    @Override
    public void updateSpeeds(UpdateContext updateContext) {

    }

    @Override
    public void updateAnimations(UpdateContext updateContext) {

    }

    @Override
    public void handleCollision(Sprite s, int damage, UpdateContext updateContext) {
        updateContext.createEvent(EventID.ALIEN_BULLET_COLLIDED);
        setCollidable(false);
        setCurrState(SpriteState.TERMINATED);
    }

    @Override
    public void getDrawInstructions(ProtectedQueue<DrawInstruction> drawQueue) {
        DrawImage drawBullet = new DrawImage(
                gameContext.bitmapCache.getBitmap(BitmapID.ALIEN_BULLET),
                (int) getX(),
                (int) getY()
        );
        drawBullet.setRotation((int) travelAngle);
        drawQueue.push(drawBullet);
    }
}
