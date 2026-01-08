package com.ganainy.galaxyrun.util;

/*import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;*/

import android.content.Context;
import android.graphics.*;

/**
 * Created by Stefan on 8/17/2015.
 */
public class ImageUtil {

    // convenience method to decode specified R.id and scale it to the given
    // width/height. Returns decoded and scaled Bitmap
    public static Bitmap decodeAndScaleTo(Context context, int rId, int scaleToWidth, int scaleToHeight) {
        return Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(context.getResources(), rId),
                scaleToWidth,
                scaleToHeight,
                true);
    }

    // returns a new Bitmap of the same size, but with the image rotated 180 degrees about its center
    public static Bitmap rotate180(Bitmap toRotate) {
        Matrix matrix = new Matrix();
        matrix.preScale(1.0f, -1.0f);
        return Bitmap.createBitmap(toRotate, 0, 0, toRotate.getWidth(), toRotate.getHeight(), matrix, true);
        /*// create new bitmap with same dimensions
        Bitmap rotated = Bitmap.createBitmap(toRotate.getWidth(), toRotate.getHeight(), Bitmap.Config.ARGB_8888);
        // get canvas to draw to it
        Canvas canvas = new Canvas(rotated);
        canvas.save();
        // rotate 180 degrees about center coordinates
        canvas.rotate(180, toRotate.getWidth(), toRotate.getHeight());
        // draw toRotate to the canvas
        canvas.drawBitmap(toRotate, 0, 0, null);
        canvas.restore();
        return toRotate;*/
    }

    // creates a linear gradient across the Bitmap from left to right using the specified colors
    public static void drawGradient(Bitmap b, int leftColor, int rightColor) {
        int w = b.getWidth();
        int h = b.getHeight();
        Canvas c = new Canvas(b);
        Paint p = new Paint();
        int start_a = Color.alpha(leftColor);
        int start_r = Color.red(leftColor); // todo: alpha values necessary?
        int start_g = Color.green(leftColor);
        int start_b = Color.blue(leftColor);
        float da = (Color.alpha(rightColor) - Color.alpha(leftColor)) / (float) w;
        float dr = (Color.red(rightColor) - Color.red(leftColor)) / (float) w;
        float dg = (Color.green(rightColor) - Color.green(leftColor)) / (float) w;
        float db = (Color.blue(rightColor) - Color.blue(leftColor)) / (float) w;
        int current;
        p.setDither(true);
        for (float i = 0; i < w; i++) {
            current = Color.argb(
                    start_a + (int) (i * da),
                    start_r + (int) (i * dr),
                    start_g + (int) (i * dg),
                    start_b + (int) (i * db)
            );
            p.setColor(current);
            c.drawLine(i, 0, i, h, p);
        }

    }

    // takes a spriteSheet and the base image (model)
    // for each frame on spriteSheet, compares pixels with base image
    // returns a BufferedImage of spritesheet containing only different pixels (basically a diff)
    // this allows for layering without lost pixels
    /*public static BufferedImage getSpriteSheetDiff(BufferedImage model, BufferedImage spriteSheet) {
        BufferedImage diff = new BufferedImage(spriteSheet.getWidth(), spriteSheet.getHeight(), BufferedImage.TYPE_INT_ARGB);
        int frames_width = spriteSheet.getWidth() / model.getWidth();
        int frames_height = spriteSheet.getHeight() / model.getHeight();
        int compared_rgb, spriteSheet_x, spriteSheet_y;

        for (int i = 0; i < frames_width; i++) {
            for (int j = 0; j < frames_height; j++) {
                for (int w = 0; w < model.getWidth(); w++) {
                    for (int h = 0; h < model.getHeight(); h++) {
                        spriteSheet_x = i * model.getWidth() + w;
                        spriteSheet_y = j * model.getHeight() + h;
                        compared_rgb = spriteSheet.getRGB(spriteSheet_x, spriteSheet_y);
                        if (compared_rgb != model.getRGB(w % 50, h % 50)) {
                            diff.setRGB(spriteSheet_x, spriteSheet_y, compared_rgb);
                        }
                    }
                }
            }
        }

        return diff;
    }

    public static void getSpriteSheetDiff(String modelPath, String spriteSheetPath, String diffPath) {
        try {
            BufferedImage model = ImageIO.read(new File(modelPath));
            System.out.println((new File(spriteSheetPath).exists()));
            BufferedImage spriteSheet = ImageIO.read(new File(spriteSheetPath));
            BufferedImage diff = getSpriteSheetDiff(model, spriteSheet);
            ImageIO.write(diff, "png", new File(diffPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
