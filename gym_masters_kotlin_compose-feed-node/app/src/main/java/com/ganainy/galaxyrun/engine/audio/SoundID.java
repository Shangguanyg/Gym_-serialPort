package com.ganainy.galaxyrun.engine.audio;

import com.ganainy.gymmasterscompose.R;

/**
 * Stores IDs for referring to raw resources
 */
public enum SoundID {
    PLAYER_SHOOT(R.raw.player_shoot_cannons),
    PLAYER_TAKE_DAMAGE(R.raw.player_take_damage),
    PLAYER_EXPLODE(R.raw.player_explode),
    PLAYER_COLLECT_COIN(R.raw.coin_collected),
    BULLET_DESTROYED(R.raw.bullet_destroyed),
    ALIEN_SHOOT(R.raw.alien_shoot),
    ALIEN_TAKE_DAMAGE(R.raw.alien_take_damage),
    ALIEN_EXPLODE(R.raw.alien_explode),
    ASTEROID_TAKE_DAMAGE(R.raw.asteroid_take_damage),
    ASTEROID_EXPLODE(R.raw.asteroid_explode),
    UI_CLICK_BUTTON(R.raw.button_clicked);

    // r.raw id specifying sound
    private final int rId;

    SoundID(int rId) {
        this.rId = rId;
    }

    public int getId() {
        return rId;
    }
}
