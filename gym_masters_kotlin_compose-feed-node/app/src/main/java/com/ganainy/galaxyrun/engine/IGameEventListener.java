package com.ganainy.galaxyrun.engine;

/**
 * Interface implemented by objects that want to be notified
 * of in-game events, e.g. player health change, etc.
 */

public interface IGameEventListener {
    void onGameStarted();
    void onGameFinished();
    void onHealthChanged(int healthChange);
}
