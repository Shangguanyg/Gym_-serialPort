package com.ganainy.galaxyrun.engine.background;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;

import java.util.Random;

/**
 * Renders a customizable, randomly-generated "galaxy" image based on the parameters set in
 * `GalaxyDrawOptions`. The background is filled with a gradient, and then a number of "stars"
 * are generated in random positions with some amount of color- and size-randomization.
 */
public class GalaxyDrawer {
    // Used for random number generation.
    private final Random random;

    public GalaxyDrawer(Random random) {
        this.random = random;
    }

    public GalaxyDrawer() {
        this(new Random());
    }


    public Bitmap drawGalaxy(int width, int height, final GalaxyDrawOptions options) {
        Bitmap generated = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        drawGalaxy(generated, options);
        return generated;
    }

    public void drawGalaxy(Bitmap bitmap, final GalaxyDrawOptions options) {
        drawGalaxy(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), options);
    }

    public void drawGalaxy(Bitmap bitmap, Rect dst, final GalaxyDrawOptions options) {
        Canvas canvas = new Canvas(bitmap);
        fillBackground(canvas, dst, options);
        drawStars(canvas, dst, options);
    }

    private void fillBackground(Canvas canvas, Rect dst, final GalaxyDrawOptions options) {
        // Create a gradient for the background color which transitions from
        // `options.startColor` on the right to `options.endColor` on the left.
        LinearGradient backgroundGradient = new LinearGradient(
                dst.left,
                dst.top,
                dst.right,
                dst.top,
                options.startColor,
                options.endColor,
                Shader.TileMode.CLAMP
        );

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(backgroundGradient);
        paint.setDither(true);
        canvas.drawRect(dst, paint);
    }

    private void drawStars(Canvas canvas, Rect dst, final GalaxyDrawOptions options) {
        Paint paint = new Paint();
        paint.setColor(options.starColor);
        int numStars = (int) (dst.width() * dst.height() / 2500.0 * options.starDensity);
        for (int i = 0; i < numStars; i++) {
            int x = dst.left + random.nextInt(dst.width());
            int y = dst.top + random.nextInt(dst.height());
            int size = varySize(options.starRadiusPx, options.sizeVariance);
            int brightness = varyBrightness(
                    Color.alpha(options.starColor), options.brightnessVariance);
            paint.setColor(Color.argb(
                    brightness,
                    Color.red(options.starColor),
                    Color.green(options.starColor),
                    Color.blue(options.starColor)
            ));
            canvas.drawCircle(x, y, size, paint);
        }
    }

    private int varyBrightness(float defaultBrightness, float variance) {
        // Generate a random float between (0, variance).
        float r = random.nextFloat() * variance;
        float brightness = defaultBrightness * (random.nextBoolean() ? 1 + r : 1 - r);
        if (brightness > 255) {
            return 255;
        }
        if (brightness < 0){
            return 0;
        }
        return (int) brightness;
    }

    private int varySize(int defaultSize, float variance) {
        // Generate a random float between (0, variance).
        float r = random.nextFloat() * variance;
        float size = defaultSize * (random.nextBoolean() ? 1 + r : 1 - r);
        if (size < 1) {
            return 1;
        }
        return Math.round(size);
    }
}
