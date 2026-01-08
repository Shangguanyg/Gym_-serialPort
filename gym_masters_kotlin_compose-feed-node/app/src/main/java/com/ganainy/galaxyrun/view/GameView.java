package com.ganainy.galaxyrun.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.ganainy.galaxyrun.engine.draw.DrawInstruction;
import com.ganainy.galaxyrun.util.FastQueue;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Draws the game. Uses a background thread to draw asynchronously. Also sends events to a
 * callback via IGameViewListener.
 * Call `queueDrawFrame()` to queue the next frame update.
 * For more information, see
 * <a href="https://google-developer-training.github.io/android-developer-advanced-course-practicals/unit-5-advanced-graphics-and-views/lesson-11-canvas/11-2-p-create-a-surfaceview/11-2-p-create-a-surfaceview.html">...</a>
 * TODO: better description of how to use the GameView with start/stop thread, etc.
 */
public class GameView extends SurfaceView implements Runnable {

    /**
     * Event callbacks triggered by GameView.
     */
    public interface IGameViewListener {
        // Called when the size of the view is determined after measurement.
        void onViewSizeSet();
        // Called when a MotionEvent is received on the view.
        void handleScreenTouch(MotionEvent motionEvent);
    }

    // Whether the drawing thread is running.
    private boolean isRunning;
    // Thread used to perform draw() calls asynchronously.
    private Thread drawThread;
    private final SurfaceHolder surfaceHolder;
    // Queue of game frames to draw.
    private final ConcurrentLinkedQueue<FastQueue<DrawInstruction>> drawFramesQueue;
    // Listener registered to this view.
    private IGameViewListener gameViewListener;

    public GameView(Context context, AttributeSet attributes) {
        super(context, attributes);
        surfaceHolder = getHolder();
        surfaceHolder.setFormat(PixelFormat.RGBA_8888);
        drawFramesQueue = new ConcurrentLinkedQueue<>();
    }

    public void setListener(IGameViewListener gameViewListener) {
        this.gameViewListener = gameViewListener;
    }

    public void queueDrawFrame(FastQueue<DrawInstruction> drawInstructions) {
        drawFramesQueue.add(drawInstructions);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        Log.d("GameView", String.format("surfaceChanged() called with %d, %d.", width, height));

        if (width == 0 || height == 0){
            Log.d("GameView", "Doing nothing in response to surfaceChanged().");
            return;
        }

        gameViewListener.onViewSizeSet();
    }

    // Runs in a separate thread. All drawing happens here. DrawInstructions
    // are passed in via thread-safe queue.
    @Override
    public void run() {
        Canvas canvas;
        // TODO: THIS IS NOT THE CORRECT WAY TO DO IT
        while (isRunning) {
            // TODO: MAKE SURE THAT THERE ISN'T A BUILD-UP OF DRAW FRAMES
            if (drawFramesQueue.size() > 1) {
                Log.w("GameView", drawFramesQueue.size() + " frames queued");
            }
            if (!drawFramesQueue.isEmpty()) {
                FastQueue<DrawInstruction> drawInstructions = drawFramesQueue.poll();
                if (surfaceHolder.getSurface().isValid()) {
                    canvas = surfaceHolder.lockCanvas();
                    drawFrame(canvas, drawInstructions);
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    private void drawFrame(Canvas canvas, FastQueue<DrawInstruction> drawInstructions) {
        for (DrawInstruction drawInst : drawInstructions) {
            drawInst.draw(canvas);
        }
    }

    /**
     * Starts the background drawing thread.
      */
    public void startThread() {
        Log.d("GameView", "Starting GameView thread.");
        isRunning = true;
        drawThread = new Thread(this);
        drawThread.start();
    }

    /**
     * Stops the drawing thread.
     */
    public void stopThread() {
        isRunning = false;
        try {
            drawThread.join();
        } catch (InterruptedException e) {
            Log.e("GameView", String.format("drawThread.join() threw InterruptedException: %s",
                    e.getMessage()));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        assert(gameViewListener != null);
        gameViewListener.handleScreenTouch(motionEvent);
        return true;
    }
}
