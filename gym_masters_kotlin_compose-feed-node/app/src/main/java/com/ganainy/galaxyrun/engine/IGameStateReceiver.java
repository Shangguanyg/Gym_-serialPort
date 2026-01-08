package com.ganainy.galaxyrun.engine;

/**
 * Interface that allows receiving callbacks when the game enters a new GameState.
 */
public interface IGameStateReceiver {
    void enterWaitingState();
    void enterStartingState();
    void enterPlayingState();
    void enterPlayerDeadState();
    void enterGameOverState();
}
