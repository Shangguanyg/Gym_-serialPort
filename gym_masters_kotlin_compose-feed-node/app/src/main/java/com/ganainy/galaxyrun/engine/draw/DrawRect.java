package com.ganainy.galaxyrun.engine.draw;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Stores instructions for drawing a Rectangle
 */

public class DrawRect implements DrawInstruction {

    private Rect rect;
    private Paint paint;

    public DrawRect(Rect rect, Paint paint) {
        this.rect = rect;
        this.paint = paint;
    }

    /*
    Create a DrawRect filled with the specified color.
     */
    public static DrawRect filled(Rect rect, int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        return new DrawRect(rect, paint);
    }

    /*
    Create a DrawRect to draw an outline with the specified color and thickness.
     */
    public static DrawRect outline(Rect rect, int color, float strokeWidth) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        return new DrawRect(rect, paint);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(rect, paint);
    }
}
