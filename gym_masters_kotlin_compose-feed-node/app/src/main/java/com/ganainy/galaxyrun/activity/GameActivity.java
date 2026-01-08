package com.ganainy.galaxyrun.activity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.ganainy.galaxyrun.engine.GameRunner;
import com.ganainy.galaxyrun.view.GameView;

import androidx.fragment.app.FragmentActivity;
import com.ganainy.gymmasterscompose.R;

/**
 * The activity that runs the game. The game logic is run in a parallel thread (`GameRunner`).
 * This activity simply forwards events and sends signals to the GameRunner.
 */
public class GameActivity extends FragmentActivity implements
        GameView.IGameViewListener,
        SensorEventListener
{
    // Runs the game in a separate thread.
    private GameRunner gameRunner;
    // View element that draws the game.
    private GameView gameView;
    private SensorManager sensorManager;

    @Override
    public void onCreate(Bundle savedInstanceState) throws IllegalArgumentException {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        
        // Hide title and action bar. Make navbar transparent.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        );
        setContentView(R.layout.game_layout);
        gameView = findViewById(R.id.spaceships);
        gameView.setListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                    SensorManager.SENSOR_DELAY_GAME);
        } else {
            // TODO: how to handle this?
            Log.d("GameActivity", "No Gyroscope");
        }
        if (gameRunner != null) {
            gameRunner.onResume();
        }
    }

    /**
     * Initializes and starts the game. Must be called *after* the GameView has been sized,
     * as we need to know how big the screen is.
     */
    private void startGame() {
        // TODO: set inDebugMode=BuildConfig.DEBUG?
        gameRunner = new GameRunner(
                getApplicationContext(),
                gameView,
                /*inDebugMode=*/false
        );
        gameRunner.startGame();
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        if (gameRunner != null) {
            gameRunner.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (gameRunner != null) {
            gameRunner.finish();
        }
    }

    @Override
    public void onViewSizeSet() {
        // Start the game once the view has been measured.
        startGame();
    }

    @Override
    public void handleScreenTouch(MotionEvent motionEvent) {
        if (gameRunner != null) {
            gameRunner.inputMotionEvent(motionEvent);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (gameRunner != null) {
            gameRunner.inputSensorEvent(sensorEvent);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
