package com.ganainy.galaxyrun.imagetransition;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Given starting image of screen, creates a "sliding out" animation
 * from bottom left to top right.
 * If pushOffScreen = true, the incoming image will appear to push the
 * current image off the screen as the animation progresses.
 * The threshold value sets how far across the screen a row can transition
 * before the next row can start transitioning--a value of 1.0f will make
 * each row transition fully before the next can begin.
 * Copyright(C) Plain Simple Apps 2015
 * Licensed under GPL GNU Version 3 (see license.txt)
 * See plain-simple.github.io for more information.
 */
public class SlideInTransition extends ImageTransition {

    // num rows to slide across screen
    private int numRows;
    // percentage a row should slide across screen before next row starts moving
    private float threshold;
    // whether start image shoulc be "pushed" off the screen
    private boolean pushOffScreen;

    public SlideInTransition(Bitmap startImage, Bitmap endImage, int numRows, int totalFrames, float threshold, boolean pushOffScreen) {
        super(startImage, endImage, totalFrames);
        this.numRows = numRows;
        this.threshold = threshold;
        this.pushOffScreen = pushOffScreen;
    }

    // draws frame based on completion of animation sequence onto
    // given canvas
    public void drawFrame(float completion, Canvas canvas) {
        int row_height = imgHeight / numRows;
        int threshold_width = (int) (imgWidth * threshold);
        if (completion >= 1.0) {
            canvas.drawBitmap(endImage, 0, 0, null);
        } else if (completion <= 0.0) {
            canvas.drawBitmap(startImage, 0, 0, null);
        } else {
            // count total thresholds on the screen, including in last row
            float total_thresholds = (numRows - 1) + 1.0f / threshold;
            float num_thresholds = total_thresholds * completion;
            // getDrawParams from bottom to top
            for (int i = 0; i < numRows && i <= num_thresholds; i++) {
                // represents section of the row on canvas that is in transition
                Rect src = new Rect(0, (numRows - i - 1) * row_height,
                        (int) ((num_thresholds - i) * threshold_width), (numRows - i) * row_height);
                // force bottom row to fill screen to bottom
                if (i == 0) {
                    src.bottom = imgHeight;
                }
                // limit to width of screen
                if (src.width() > imgWidth) {
                    src.right = imgWidth;
                }
                if (pushOffScreen) {
                    // pixels from startImage to be shifted right
                    Rect start_src = new Rect(0, src.top, imgWidth - src.width(), src.bottom);
                    // new location of shifted pixels
                    Rect start_dst = new Rect(imgWidth - start_src.width(), src.top, imgWidth, src.bottom);
                    canvas.drawBitmap(startImage, start_src, start_dst, null);

                    // pixels from endImage to be drawn on canvas
                    Rect end_src = new Rect(imgWidth - src.width(), src.top, imgWidth, src.bottom);
                    // new location of pixels on canvas
                    Rect end_dst = new Rect(0, src.top, end_src.width(), src.bottom);
                    canvas.drawBitmap(endImage, end_src, end_dst, null);
                } else {
                    // simply transfer pixels from endImage to startImage. No change necessary.
                    canvas.drawBitmap(endImage, src, src, null);
                }
            }
        }
    }
}
