package com.ganainy.galaxyrun.helper;

import com.ganainy.gymmasterscompose.R;

/**
 * All bitmaps used by the game must have a BitmapID, which stores the
 * R.drawable id associated with a bitmap's title. This is to standardize
 * the way bitmaps are referenced and to ensure only valid R.drawable id's
 * are used.
 */
public enum BitmapID {
    SPACESHIP(R.drawable.spaceship),
    SPACESHIP_EXPLODE(R.drawable.spaceship_explode),
    SPACESHIP_SHOOT(R.drawable.spaceship_shoot),
    SPACESHIP_MOVE(R.drawable.spaceship_move),
    BULLET(R.drawable.bullet),
    BULLET_EXPLODE(R.drawable.bullet_explode),
    ALIEN(R.drawable.alien),
    ALIEN_BULLET(R.drawable.alienbullet),
    ASTEROID(R.drawable.asteroid),
    COIN(R.drawable.coin),
    COIN_SPIN(R.drawable.coin_spin),
    OBSTACLE(R.drawable.obstacle),
    PAUSE_BUTTON_PAUSED(R.drawable.pause),
    PAUSE_BUTTON_UNPAUSED(R.drawable.play),
    MUTE_BUTTON_MUTED(R.drawable.sound_off),
    MUTE_BUTTON_UNMUTED(R.drawable.sound_on),
    UP_ARROW(R.drawable.up_arrow);

    // r.drawable id specifying bitmap
    private int rId;

    public int getrId() {
        return rId;
    }

    BitmapID(int rId) {
        this.rId = rId;
    }
}
