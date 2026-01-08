package com.ganainy.galaxyrun.sprite;

public enum SpriteState {
    // Sprite is "alive" and executing normal logic
    ALIVE,
    // Sprite has "died" (hit 0 health)
    DEAD,
    // Sprite has finished all logic and should be removed
    // from the game
    TERMINATED
}
