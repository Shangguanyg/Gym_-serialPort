package com.ganainy.galaxyrun.activity;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ganainy.gymmasterscompose.R;

public class GalaxyRunActivity extends Activity {

    private MediaPlayer songPlayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide title and action bar. Make navbar transparent.
        // Note: may be possible to do some of this via the Manifest.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        );
        setContentView(R.layout.mainscreen_layout);

        // Animate the Title to "inflate"/zoom in on start
        TextView title = findViewById(R.id.title);
        AnimatorSet titleAnimation = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.menu_button);
        titleAnimation.setTarget(title);
        titleAnimation.start();

        // Animate the PlayButton to fade in after slight delay
        Button playButton = findViewById(R.id.playbutton);
        AnimatorSet playButtonAnimation = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.play_button);
        playButtonAnimation.setTarget(playButton);
        playButtonAnimation.start();

        songPlayer = MediaPlayer.create(getApplicationContext(), R.raw.main_song);
        songPlayer.setLooping(true);
        songPlayer.setVolume(0.3f, 0.3f);
    }

    @Override
    public void onResume() {
        super.onResume();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        songPlayer.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        songPlayer.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        songPlayer.release();
        songPlayer = null;
    }

    /*
    Handle user pressing the "Play" button
     */
    public void onPlayPressed(View view) {
        // TODO: play a "button clicked" sound
        // Launch the GameActivity
        Intent game_intent = new Intent(this, GameActivity.class);
        startActivity(game_intent);
    }

    public void onPrivacyPolicyPressed(View view) {
        // See: https://stackoverflow.com/a/50188704
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.privacy_policy, null);

        // Set "focusable" to true to close the window when defocused
        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true
        );

        // Show in screen center
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // Dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }
}
