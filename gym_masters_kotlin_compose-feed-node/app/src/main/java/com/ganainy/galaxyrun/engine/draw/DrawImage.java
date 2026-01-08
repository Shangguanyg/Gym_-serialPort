package com.ganainy.galaxyrun.engine.draw;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Stores instructions for drawing a Bitmap
 *
 * TODO: refactor the various DrawImage classes
 */

public class DrawImage implements DrawInstruction {

    // Bitmap to be drawn
    private Bitmap bitmap;
    // Source and destination rects
    private Rect src, dst;
    // Degrees to rotate clockwise
    private float degRotation;
    private Paint paint;

    public DrawImage(Bitmap bitmap, Rect src, Rect dst) {
        this.bitmap = bitmap;
        this.src = src;
        this.dst = dst;
        paint = new Paint();
    }

    public DrawImage(Bitmap bitmap, Rect src, int x, int y) {
        this(
                bitmap,
                src,
                new Rect(x, y, x + src.width(), y + src.height())
        );
    }

    public DrawImage(Bitmap bitmap, int x, int y) {
        this(
                bitmap,
                new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                new Rect(x, y, x + bitmap.getWidth(), y + bitmap.getHeight())
        );
    }

    public void setRotation(float degRotation) {
        this.degRotation = degRotation;
    }

    public void setColorMatrix(ColorMatrix colorMatrix) {
        this.paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
    }

    @Override
    public void draw(Canvas canvas) {
        // Save and rotate canvas if a rotation was specified
        if (degRotation != 0) {
            canvas.save();
            canvas.rotate(degRotation, dst.centerX(), dst.centerY());
        }

        canvas.drawBitmap(bitmap, src, dst, paint);

        // Restore canvas if it was previously rotated
        if (degRotation != 0) {
            canvas.restore();
        }
    }
}
