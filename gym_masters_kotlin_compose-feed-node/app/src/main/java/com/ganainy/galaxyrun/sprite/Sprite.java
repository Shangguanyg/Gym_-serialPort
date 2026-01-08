package com.ganainy.galaxyrun.sprite;

;

import android.graphics.Color;

import com.ganainy.galaxyrun.engine.GameContext;
import com.ganainy.galaxyrun.engine.UpdateContext;
import com.ganainy.galaxyrun.engine.draw.DrawRect;
import com.ganainy.galaxyrun.helper.BitmapData;
import com.ganainy.galaxyrun.engine.draw.DrawInstruction;
import com.ganainy.galaxyrun.helper.Rectangle;
import com.ganainy.galaxyrun.util.ProtectedQueue;

/**
 * Base class for all Sprite implementations.
 */
public abstract class Sprite {

    protected GameContext gameContext;
    // Coordinates of sprite, top-left
    private double x, y;
    // Dimensions specifying bounds of Sprite's image
    private int width, height;
    // Intended speed-per-second in x- and y- (pixels)
    private double speedX, speedY;
    // Current state that this sprite is in
    private SpriteState currState;
    // The sprite's health. Also the amount of damage the sprite will
    // inflict upon collision.
    protected int health;
    // Whether or not this sprite can collide with other sprites
    private boolean canCollide = true;
    // Hitbox for collision detection
    private Rectangle hitbox;
    // Offset from Sprite's coordinates to hitbox coordinates
    private double hitboxOffsetX, hitboxOffsetY;

    public Sprite(
            GameContext gameContext,
            double x,
            double y,
            int width,
            int height
    ) {
        this.gameContext = gameContext;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        hitbox = new Rectangle(x, y, width, height);
        setCurrState(SpriteState.ALIVE);
    }

    public Sprite(
            GameContext gameContext,
            double x,
            double y,
            BitmapData bitmapData
    ) {
        this(gameContext, x, y, bitmapData.getWidth(), bitmapData.getHeight());
    }

    /* Begin core abstract methods */
    // NOTE: TODO: CURRENTLY WE ARE JUST USING THE SPRITETYPE AS COLLISION LAYER
    // TODO: update
//    public int getCollisionLayer() {
//        return 0; //spriteType.ordinal();
//    }

    // Update/handle any actions sprite takes
    public abstract void updateActions(UpdateContext updateContext);

    // Update speedX and speedY
    public abstract void updateSpeeds(UpdateContext updateContext);

    // Start/stop/update any animations
    public abstract void updateAnimations(UpdateContext updateContext);

    // Handles collision with another sprite. Also passes damage taken
    // (health's are cross-subtracted simultaneously; see GameEngineUtil)
    public abstract void handleCollision(
            Sprite s,
            int damage,
            UpdateContext updateContext
    );

    /*
    Sprite should push its DrawInstructions onto the provided queue.
    Draw calls are executed in the order of addition to the queue (FIFO).
     */
    public abstract void getDrawInstructions(ProtectedQueue<DrawInstruction> drawQueue);

    /* Begin utility methods */
    // Moves the sprite based on current speeds and the number of
    // milliseconds since the previous update.
    public void move(UpdateContext updateContext) {
        x += speedX * updateContext.getGameTime().msSincePrevUpdate / 1000;
        y += speedY * updateContext.getGameTime().msSincePrevUpdate / 1000;
        hitbox.setX(x + hitboxOffsetX);
        hitbox.setY(y + hitboxOffsetY);
    }

    // Returns whether the sprite's coordinates + width/height
    // are in game bounds.
    public boolean isVisibleInBounds() {
        return x > -width &&
                y > -height &&
                x < gameContext.gameWidthPx + width &&
                y < gameContext.gameHeightPx;
    }

    /*
    Subtracts the specified damage from the sprite's health and floors
    health at 0.
     */
    public void takeDamage(int damage) {
        health = damage > health ? 0 : health - damage;
    }

    /*
    Returns whether this sprite collides with the specified other sprite.
     */
    public boolean collidesWith(Sprite s) {
        return canCollide && s.canCollide && hitbox.intersects(s.hitbox);
    }

    // Calculates and returns the distance between the center of this
    // Sprite's hitbox to the center of the specified Sprite's hitbox.
    public double distanceTo(Sprite s) {
        return hitbox.getCenter().calcDistance(s.hitbox.getCenter());
    }

    /*
    Utility method: draw red rectangle where sprite's hitbox is.
     */
    public DrawInstruction drawHitbox() {
        return DrawRect.outline(hitbox.toRect(), Color.RED, 3);
    }

    /* Begin getters and setters */
    public double getX() {
        return x;
    }

    // Set x and update hitbox
    public void setX(double x) {
        this.x = x;
        this.hitbox.setX(this.x + hitboxOffsetX);
    }

    public double getY() {
        return y;
    }

    // Set y and update hitbox
    public void setY(double y) {
        this.y = y;
        this.hitbox.setY(this.y + hitboxOffsetY);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public double getSpeedX() {
        return speedX;
    }

    public void setSpeedX(double speedX) {
        this.speedX = speedX;
    }

    public double getSpeedY() {
        return speedY;
    }

    public void setSpeedY(double speedY) {
        this.speedY = speedY;
    }

    public SpriteState getState() {
        return currState;
    }

    protected void setCurrState(SpriteState currState) {
        this.currState = currState;
    }

    public int getHealth() {
        return health;
    }

    protected void setHealth(int health) {
        this.health = health;
    }

    public boolean canCollide() {
        return canCollide;
    }

    protected void setCollidable(boolean canCollide) {
        this.canCollide = canCollide;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    protected void setHitbox(Rectangle hitbox) {
        this.hitbox = hitbox;
    }

    public double getHitboxOffsetX() {
        return hitboxOffsetX;
    }

    protected void setHitboxOffsetX(double hitboxOffsetX) {
        this.hitboxOffsetX = hitboxOffsetX;
    }

    public double getHitboxOffsetY() {
        return hitboxOffsetY;
    }

    protected void setHitboxOffsetY(double hitboxOffsetY) {
        this.hitboxOffsetY = hitboxOffsetY;
    }

    protected void setHitboxWidth(double width) {
        hitbox.setWidth(width);
    }

    protected void setHitboxHeight(double height) {
        hitbox.setHeight(height);
    }
}
