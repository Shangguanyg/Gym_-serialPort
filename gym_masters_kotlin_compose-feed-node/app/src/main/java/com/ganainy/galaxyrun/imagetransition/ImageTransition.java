package com.ganainy.galaxyrun.imagetransition;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import java.util.InputMismatchException;

/**
 * ImageTransition superclass. Provides methods for managing
 * the animation.
 * Copyright(C) Plain Simple Apps 2015
 * Licensed under GPL GNU Version 3 (see license.txt)
 * See plain-simple.github.io for more information.
 */
public abstract class ImageTransition {

    // starting image
    protected Bitmap startImage;
    // actual working frame to be drawn on
    protected Bitmap workingFrame;
    // working Canvas
    protected Canvas workingCanvas;
    // ending image
    protected Bitmap endImage;
    // current frame
    protected int frameCounter = 0;
    // total frames in animation
    protected int totalFrames;
    // whether or not transition has been completed
    protected boolean hasFinished = false;
    // whether or not transition animation is currently playing
    protected boolean isPlaying = false;
    // dimensions of image
    protected int imgWidth;
    protected int imgHeight;

    public boolean hasFinished() {
        return hasFinished;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public ImageTransition(Bitmap startImage, Bitmap endImage, int totalFrames) throws InputMismatchException { // todo: more specific exception
        if (startImage == null && endImage == null) {
            throw new InputMismatchException("startImage and endImage cannot both be null!");
        } else if (startImage == null) {
            Log.d("Transition", "startImage is null");
            this.startImage = createBlankBitmap(endImage.getWidth(), endImage.getHeight());
            this.endImage = endImage;
        } else if (endImage == null){
            this.endImage = createBlankBitmap(startImage.getWidth(), startImage.getHeight());
            this.startImage = startImage;
        }
        workingFrame = this.startImage.copy(Bitmap.Config.ARGB_8888, true);
        this.totalFrames = totalFrames;
        imgWidth = this.startImage.getWidth();
        imgHeight = this.startImage.getHeight();
    }

    public void start() {
        isPlaying = true;
    }

    // sets hasFinished to true
    public void stop() {
        hasFinished = true;
        isPlaying = false;
    }

    // resets frameCounter to zero and recopies startImage into workingFrame
    public void reset() {
        frameCounter = 0;
        hasFinished = false;
        workingFrame = startImage.copy(Bitmap.Config.ARGB_8888, true);
    }

    // renders and returns next frame in sequence
    public Bitmap nextFrame() {
        if (frameCounter == 0) {
            start();
        }
        if (frameCounter < totalFrames) {
            frameCounter++;
            return getFrame(frameCounter);
        } else {
            stop();
            return endImage;
        }
    }

    // renders and returns frame as a Bitmap based on completion of
    // animation sequence
    public Bitmap getFrame(float completion) {
        Canvas this_frame = new Canvas(workingFrame); // todo: avoid creating new canvas?
        drawFrame(completion, this_frame);
        return workingFrame;
    }

    // renders and returns frame based on completion of sequence
    public abstract void drawFrame(float completion, Canvas canvas);

    // renders and returns frame based on frameNumber in sequence
    public Bitmap getFrame(int frameNumber) {
        return getFrame(frameNumber / (float) totalFrames);
    }

    // returns a mutable Bitmap of specified size with all pixels black
    public static Bitmap createBlankBitmap(int width, int height) {
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    }
}
