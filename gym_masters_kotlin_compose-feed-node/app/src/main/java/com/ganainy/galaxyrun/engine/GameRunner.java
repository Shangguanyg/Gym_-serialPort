package com.ganainy.galaxyrun.engine;

import android.content.Context;
import android.graphics.Typeface;
import android.hardware.SensorEvent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.MotionEvent;

import com.ganainy.galaxyrun.engine.audio.SoundID;
import com.ganainy.galaxyrun.engine.external.ExternalInput;
import com.ganainy.galaxyrun.engine.external.GameUpdateMessage;
import com.ganainy.galaxyrun.engine.external.MotionInput;
import com.ganainy.galaxyrun.engine.external.PauseGameInput;
import com.ganainy.galaxyrun.engine.external.SensorInput;
import com.ganainy.galaxyrun.engine.external.SoundPlayer;
import com.ganainy.galaxyrun.engine.external.StartGameInput;
import com.ganainy.galaxyrun.engine.ui.GameUI;
import com.ganainy.galaxyrun.helper.BitmapCache;
import com.ganainy.galaxyrun.helper.FontCache;
import com.ganainy.galaxyrun.helper.FpsCalculator;
import com.ganainy.galaxyrun.util.Pair;
import com.ganainy.galaxyrun.view.GameView;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.ganainy.gymmasterscompose.R;

/**
 * Runs the GameEngine in a worker thread.
 */
public class GameRunner extends HandlerThread {
    private Handler mWorkerHandler;
    private final Handler mResponseHandler;
    // Whether the update thread is currently paused.
    private boolean isThreadPaused;
    // Context passed to the Game.
    private final GameContext mGameContext;
    private final GameEngine mGameEngine;
    private final FpsCalculator mFpsCalculator;
    // The view that the game is drawn on.
    private final GameView mGameView;
    // Event queue that will be passed into the Game. Stores SensorEvents and MotionEvents
    // received from the GameActivity, among other things.
    private final ConcurrentLinkedQueue<ExternalInput> externalInputQueue;
    // Whether game is currently muted.
    private boolean isMuted;
    // Plays game audio
    private final SoundPlayer soundPlayer;
    // Plays background song.
    // TODO: this should really be controlled by commands from GameEngine.
    //   I am taking a shortcut by directly playing the song from `GameActivity`
    private MediaPlayer songPlayer;
    private static final float MUSIC_VOLUME = 0.25f;

    private static final double TARGET_FPS = 30.0;
    private static final int MS_PER_UPDATE = (int) (1000.0 / TARGET_FPS);

    public GameRunner(
            Context appContext,
            GameView gameView,
            boolean inDebugMode
    ) {
        super("GameRunner");
        mGameView = gameView;
        mResponseHandler = new Handler();
        soundPlayer = new SoundPlayer(appContext);
        songPlayer = MediaPlayer.create(appContext, R.raw.game_song);
        songPlayer.setVolume(MUSIC_VOLUME, MUSIC_VOLUME);
        songPlayer.setLooping(true);

        externalInputQueue = new ConcurrentLinkedQueue<>();
        mGameContext = makeGameContext(appContext, gameView, inDebugMode);
        mGameEngine = new GameEngine(mGameContext);
        mFpsCalculator = new FpsCalculator(30);

        // Start the update thread and kick off the first prepareHandler() call.
        isThreadPaused = false;
        start();
        prepareHandler();
        mGameView.startThread();
    }

    private static GameContext makeGameContext(
            Context appContext, GameView gameView, boolean inDebugMode) {
        // Calculate game dimensions based on screen dimensions.
        // TODO: This should be calculated in a much more general fashion. The fact that we need
        //  to calculate it here is a code smell.
        Pair<Integer, Integer> gameDimensions =
                GameUI.calcGameDimensions(gameView.getWidth(), gameView.getHeight());
        int gameWidthPx = gameDimensions.first;
        int gameHeightPx = gameDimensions.second;

        BitmapCache bitmapCache = new BitmapCache(appContext, gameWidthPx, gameHeightPx);
        FontCache fontCache = new FontCache(appContext, Typeface.MONOSPACE);
        // TODO: rename "AnimationFactory"
        AnimFactory animFactory = new AnimFactory(bitmapCache);

        // TODO: Have a struct for game width/height and screen width/height?
        return new GameContext(
                appContext,
                inDebugMode,
                bitmapCache,
                fontCache,
                animFactory,
                new Random(System.currentTimeMillis()),
                gameWidthPx,
                gameHeightPx,
                gameView.getWidth(),
                gameView.getHeight()
        );
    }

    /**
     * Starts the game.
     */
    public void startGame() {
        Log.d("GameRunner", "Starting the game.");
        externalInputQueue.add(new StartGameInput());
        songPlayer.start();
        queueUpdate();
    }

    public void onPause() {
        externalInputQueue.add(new PauseGameInput());
        songPlayer.pause();
        mGameView.stopThread();
        isThreadPaused = true;
    }

    public void onResume() {
        mGameView.startThread();
        isThreadPaused = false;
        queueUpdate();
    }

    /**
     * Destroys allocated resources.
     */
    public void finish() {
        soundPlayer.release();
        songPlayer.release();
        songPlayer = null;
    }

    public void inputMotionEvent(MotionEvent e) {
        externalInputQueue.add(new MotionInput(e));
    }

    public void inputSensorEvent(SensorEvent e) {
        externalInputQueue.add(new SensorInput(e));
    }

    public void prepareHandler() {
        // Run a game update.
        mWorkerHandler = new Handler(getLooper(), msg -> {
            long updateTime = System.currentTimeMillis();

            // Poll all events that have been received since the previous update.
            List<ExternalInput> queuedInputs = new LinkedList<>();
            while (!externalInputQueue.isEmpty()) {
                ExternalInput input = externalInputQueue.poll();
                queuedInputs.add(input);
            }

            final GameUpdateMessage updateMessage = mGameEngine.update(queuedInputs);

            // Report results
            mResponseHandler.post(() -> {
                mFpsCalculator.recordFrame();
                double fps = mFpsCalculator.calcFps();
                // FPS will be zero if not enough frames have elapsed.
                if (fps != 0 && fps < TARGET_FPS / 2) {
                    Log.w("GameActivity", "Low FPS: " + fps);
                }

                // Handle change of "muted" state.
                // TODO: "muting" should be entirely up to the in-game logic.
                if (isMuted != updateMessage.isMuted) {
                    if (songPlayer != null) {
                        float newVolume = updateMessage.isMuted ? 0 : MUSIC_VOLUME;
                        songPlayer.setVolume(newVolume, newVolume);
                    }
                    isMuted = updateMessage.isMuted;
                }

                // Play sounds if not muted
                if (soundPlayer != null && !isMuted) {
                    for (SoundID sound : updateMessage.getSounds()) {
                        soundPlayer.playSound(sound);
                    }
                }

                // Send draw instructions to GameView to be drawn.
                mGameView.queueDrawFrame(updateMessage.getDrawInstructions());
            });

            // Queue next update
            if (!isThreadPaused) {
                // Calculate when the next update should occur in order to
                // achieve TARGET_FPS.
                long nextUpdate = updateTime + MS_PER_UPDATE;
                long currTime = System.currentTimeMillis();
                if (currTime < nextUpdate) {
                    try {
                        // Sleep until it's time to run the next frame.
                        Thread.sleep(nextUpdate - currTime);
                    } catch (InterruptedException e) {
                        Log.e("GameRunner", "Sleep was interrupted: " + e.getMessage());
                    }
                } else {
                    Log.w("GameRunner", "Slipped by " + (currTime - nextUpdate) + "ms");
                }

                queueUpdate();
            }

            return true;
        });
    }

    /**
     * Triggers a game update on the background updater thread.
     */
    private void queueUpdate() {
        mWorkerHandler.obtainMessage().sendToTarget();
    }
}