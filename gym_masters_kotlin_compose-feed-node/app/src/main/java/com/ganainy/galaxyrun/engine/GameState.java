package com.ganainy.galaxyrun.engine;

/**
 * The possible states that the game can be in.
 */
public enum GameState {
    // Initial state, before any logic has started.
    WAITING_FOR_START,
    // Spaceship is flying onto the screen, but not yet at its start position. It is not
    // controllable.
    STARTING,
    // Spaceship is alive and controllable.
    PLAYING,
    // Spaceship has hit zero health and explodes. The game slows down to a stop (but hasn't
    // stopped yet).
    PLAYER_DEAD,
    // The game is over. Nothing is moving. The only option is to quit or restart.
    GAME_OVER
}
