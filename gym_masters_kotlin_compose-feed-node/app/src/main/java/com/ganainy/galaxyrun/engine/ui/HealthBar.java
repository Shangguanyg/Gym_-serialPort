package com.ganainy.galaxyrun.engine.ui;

import android.graphics.Color;
import android.graphics.Rect;
import android.util.DisplayMetrics;

import com.ganainy.galaxyrun.engine.GameConstants;
import com.ganainy.galaxyrun.engine.GameContext;
import com.ganainy.galaxyrun.engine.UpdateContext;
import com.ganainy.galaxyrun.engine.draw.DrawInstruction;
import com.ganainy.galaxyrun.engine.draw.DrawRect;
import com.ganainy.galaxyrun.engine.draw.DrawText;
import com.ganainy.galaxyrun.helper.Rectangle;
import com.ganainy.galaxyrun.util.ProtectedQueue;

/**
 * Draws player's health.
 */

public class HealthBar extends UIElement {

    // current health level
    int currentHealth;
    // full health level
    int fullHealth;
    // health level the bar is transitioning to
    int movingToHealth;
    // width of health bar on screen
    double width;
    // height of health bar on screen
    double height;
    // starting x-coordinate
    double startX;
    // starting y-coordinate
    double startY;

    // left, right, and bottom padding (dp)
    private static final int PADDING = 10;
    // Specify upper and lower RGB values
    private final int BLUE = 0;
    private final int LOWER_GREEN = 0;
    private final int UPPER_GREEN = 255;
    private final int LOWER_RED = 0;
    private final int UPPER_RED = 255;

    // viewWidth and Height will be used to determine size and location of healthbar
    // startHealth and fullHealth configure healthbar values
    public HealthBar(GameContext gameContext) {
        super(gameContext, calcLayout(gameContext));
        startX = bounds.getX();
        startY = bounds.getY();
        width = bounds.getWidth();
        height = bounds.getHeight();
        setFullHealth(GameConstants.FULL_PLAYER_HEALTH);
        setCurrentHealth(GameConstants.FULL_PLAYER_HEALTH);
    }

    private static Rectangle calcLayout(GameContext gameContext) {
        DisplayMetrics metrics = gameContext.appContext.getResources().getDisplayMetrics();
        float padding = PADDING * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        float width = gameContext.screenWidthPx - 2 * padding;
        float height = gameContext.screenHeightPx * 0.03f;
        float x = padding;
        float y = gameContext.screenHeightPx - height - padding;
        return new Rectangle(x, y, width, height);
    }

    // sets currentHealth to given value without triggering animated change
    public void setCurrentHealth(int currentHealth) {
        if (currentHealth < 0) {
            currentHealth = 0;
        }
        this.currentHealth = currentHealth;
        movingToHealth = currentHealth;
    }

    // triggers animated change to given value
    public void setMovingToHealth(int movingToHealth) {
        if (movingToHealth < 0) {
            movingToHealth = 0;
        }
        this.movingToHealth = movingToHealth;
    }

    public void setFullHealth(int fullHealth) {
        if (fullHealth < 0) {
            fullHealth = 0;
        }
        this.fullHealth = fullHealth;
    }

    public void update(UpdateContext updateContext) {
        setMovingToHealth(updateContext.playerHealth);

        if (currentHealth != movingToHealth) {
            // Transition down 20% of the remaining distance between
            // currentHealth and movingToHealth
            currentHealth -= (double) (currentHealth - movingToHealth) * 0.2;
        }
    }

    public void getDrawInstructions(ProtectedQueue<DrawInstruction> drawInstructions) {
        // Draw outline
        drawInstructions.push(DrawRect.outline(
                new Rect((int) startX, (int) startY, (int) (startX + width), (int) (startY + height)),
                Color.GRAY,
                (float) (height * 0.1)
        ));

        // Draw fill
        float innerPadding = (float) (height * 0.1);
        float pctHealth = currentHealth / (float) fullHealth;
        DrawRect fill = DrawRect.filled(
                new Rect(
                        (int) (startX + innerPadding),
                        (int) (startY + innerPadding),
                        (int) (startX + innerPadding + pctHealth * (width - height * 0.1f)),
                        (int) (startY + height - innerPadding)
                ), getHealthBarColor()
        );
        drawInstructions.push(fill);

        // Draw number
        String hpString = currentHealth + "/" + fullHealth;
        DrawText text = new DrawText(
                hpString,
                (float) (startX + width * 0.9), (float) (startY + height * 0.85),
                Color.GRAY,
                (int) (height * 0.8f)
        );
        drawInstructions.push(text);
    }

    /*
    Calculates what color the health bar should be using the ratio of
    currentHealth to fullHealth
     */
    private int getHealthBarColor() {
        double ratio = currentHealth / (double) fullHealth;
        // green: 0, 255, blue (or lowerRed, upperGreen, blue)
        // yellow: 255, 255, blue (or upperRed, upperGreen, blue)
        // red: 255, 0, blue (or upperRed, lowerGreen, blue)
        double red = (UPPER_RED - LOWER_RED) / 2.0 - (ratio - 0.5) * (UPPER_RED - LOWER_RED);
        double green = (UPPER_GREEN - LOWER_GREEN) / 2.0 + (ratio - 0.5) * (UPPER_GREEN - LOWER_GREEN);
        return Color.rgb((int) red, (int) green, BLUE);
    }

    public void onTouchEnter(float x, float y) {
//        Log.d("HealthBar", "onTouchEnter " + x + ", " + y);
    }

    public void onTouchMove(float x, float y) {
//        Log.d("HealthBar", "onTouchMove " + x + ", " + y);
    }

    public void onTouchLeave(float x, float y) {
//        Log.d("HealthBar", "onTouchLeave " + x + ", " + y);
    }
}
