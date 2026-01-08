package com.ganainy.motoman;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class MotomanActivity extends AndroidApplication {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useAccelerometer = true;
        cfg.useCompass = false;
        cfg.hideStatusBar = true;
        cfg.maxSimultaneousSounds = 32;
        
        initialize(new MotomanGame(), cfg);
    }
}