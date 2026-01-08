package com.ganainy.galaxyrun.engine.draw;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

/**
 * Stores instructions for drawing text
 */

public class DrawText implements DrawInstruction {

    // Text to draw
    private String text;
    // Draw coordinates (bottom left)
    private float bottomX, bottomY;
    // Paint to use when drawing
    private Paint paint;

    public DrawText(String text, float bottomX, float bottomY, Paint paint) {
        this.text = text;
        this.bottomX = bottomX;
        this.bottomY = bottomY;
        this.paint = paint;
    }

    public DrawText(String text, float bottomX, float bottomY, int color, int size, Typeface typeface) {
        this(text, bottomX, bottomY, new Paint());
        paint.setColor(color);
        paint.setTextSize(size);
        paint.setTypeface(typeface);
    }

    public DrawText(String text, float x, float y, int color, int size) {
        this(text, x, y, color, size, Typeface.DEFAULT);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawText(text, bottomX, bottomY, paint);
    }
}
