package com.ganainy.galaxyrun.helper;

import android.graphics.Rect;

/**
 * Created by Stefan on 8/13/2015.
 */
public class SpriteAnimation { // todo: pause (returns same frame each time) and reverse methods

    private BitmapID bitmapID;

    // Dimensions of each frame (px)
    private int frameW, frameH;
    // Number of frames in the animation
    private int numFrames;
    // Duration (ms) to show each frame.
    private int[] frameDurationsMs;
    // Whether the animation loops to the start after completing
    private boolean shouldLoop;
    // Whether this animation is in the process of playing
    private boolean isPlaying;
    // Whether this animation has completed at least once
    private boolean hasPlayed;
    // Index of next frame to play
    private int currFrameIndex;
    // Time spent on the current frame (ms)
    private int timeOnCurrFrameMs;
    // Total runtime of one loop of the animation (ms)
    private int totalRuntimeMs;


    /*
    Create animation using provided spritesheet image.
     */
    public SpriteAnimation(BitmapData bitmapData, int[] frameDurations, boolean loop) {
        bitmapID = bitmapData.getId();
        frameDurationsMs = frameDurations;
        shouldLoop = loop;

        numFrames = frameDurations.length;
        frameW = bitmapData.getWidth() / numFrames;
        frameH = bitmapData.getHeight();

        for (int frame_duration : frameDurations) {
            totalRuntimeMs += frame_duration;
        }
    }

    /*
    Starts the animation, setting `isPlaying` = True. Must be called before using the
    other methods!  TODO: MUCH BETTER DOCUMENTATION
     */
    public void start() {
        if (isPlaying) {
            throw new IllegalStateException("Animation already playing");
        } else if (hasPlayed && !shouldLoop) {
            throw new IllegalStateException(
                    "Animation has already played and does not loop--call reset()"
            );
        } else {
            isPlaying = true;
        }
    }

    // stops animation
    public void stop() {
        if (!isPlaying) {
            throw new IllegalStateException("Animation isn't playing, therefore cannot be stopped");
        }
        isPlaying = false;
    }

    public void reset() {
        currFrameIndex = 0;
        timeOnCurrFrameMs = 0;
        hasPlayed = false;
        isPlaying = false;
    }

    // whether animation has finished or not
    public boolean isPlaying() {
        return isPlaying;
    }

    // whether animation has played
    public boolean hasPlayed() {
        return hasPlayed;
    }

    /*
    Progresses the animation by the specified number of milliseconds.
     */
    public void update(long milliseconds) {
        while (isPlaying && milliseconds > 0) {
            long ms_left_this_frame = frameDurationsMs[currFrameIndex] - timeOnCurrFrameMs;
            // Enough time has passed to move to the next frame
            if (milliseconds >= ms_left_this_frame) {
                incrementFrame();
                milliseconds -= ms_left_this_frame;
            } else {  // Stay on current frame, but increment `timeOnCurrFrameMs`
                timeOnCurrFrameMs += milliseconds;
                milliseconds = 0;
            }
        }
    }

    private void incrementFrame() {
        if (!isPlaying) {
            throw new IllegalStateException("Animation is not playing");
        }

        currFrameIndex++;
        timeOnCurrFrameMs = 0;

        // Reached end of animation
        if (currFrameIndex == numFrames) {
            hasPlayed = true;

            if (shouldLoop) {
                currFrameIndex = 0;
            } else {
                stop();
            }
        }
    }

    /*
    Returns Rect containing coordinates of the current frame, on the spritesheet
    */
    public Rect getCurrentFrameSrc() {
        if (!isPlaying) {
            throw new IllegalStateException("Animation is not playing");
        }
        return new Rect(frameW * currFrameIndex, 0, frameW * (currFrameIndex + 1), frameH);
    }

    public BitmapID getBitmapID() {
        return bitmapID;
    }

    public int getFrameWidth() {
        return frameW;
    }

    public int getFrameHeight() {
        return frameH;
    }

    // return number of frames left in animation (not total number of frameCounts!!)
    public int getFramesLeft() {
        if (!isPlaying) {
            throw new IllegalStateException("Animation is not playing");
        }
        return numFrames - currFrameIndex;
    }

    /*
    Return the duration of one loop.
     */
    public int getRuntimeMs() {
        return totalRuntimeMs;
    }

//    public String getDebug() {
//        return ("Frame " + frameCounter + " of " + numFrames + " with current speed at " + frameSpeedCounter + " of " + frameSpeeds[frameCounter] +
//                " " + (isPlaying ? "(playing)" : "(stopped)") + (loop ? " (looping)" : ""));
//    }
}
