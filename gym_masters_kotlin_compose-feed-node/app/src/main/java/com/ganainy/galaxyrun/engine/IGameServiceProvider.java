package com.ganainy.galaxyrun.engine;

import android.content.Context;

/**
 * Passed to
 */

public interface IGameServiceProvider {
    // TODO: PROVIDE REFERENCES TO THE VARIOUS SINGLETONS NEEDED, RESOURCES NEEDED
    // TODO: PROBABLY MAKE INTO ABSTRACT CLASS
    Context getActivityContext();
    void playSound();  // TODO
    int getScreenWidth();
    int getScreenHeight();
}
