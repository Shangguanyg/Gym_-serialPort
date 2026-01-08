package com.ganainy.galaxyrun.engine;

import com.ganainy.galaxyrun.helper.BitmapCache;
import com.ganainy.galaxyrun.helper.BitmapID;
import com.ganainy.galaxyrun.helper.SpriteAnimation;

/**
 * Created by Stefan on 8/31/2020.
 */

public class AnimFactory {

    private BitmapCache bitmapCache;

    public AnimFactory(BitmapCache bitmapCache) {
        this.bitmapCache = bitmapCache;
    }

    public SpriteAnimation get(AnimID animationID) {
        // TODO: IDEALLY WE'D READ SOME KIND OF CONFIGURATION FILE
        switch (animationID) {
            case SPACESHIP_MOVE: {
                return new SpriteAnimation(
                        bitmapCache.getData(BitmapID.SPACESHIP_MOVE),
                        new int[]{150, 150},
                        true
                );
            }
            case SPACESHIP_SHOOT: {
                return new SpriteAnimation(
                        bitmapCache.getData(BitmapID.SPACESHIP_SHOOT),
                        new int[]{200, 150, 150},
                        false
                );
            }
            // NOTE: CURRENTLY, WE'RE JUST RE-USING THE SPACESHIP EXPLOSION ANIMATION (TODO)
            case SPACESHIP_EXPLODE:
            case ALIEN_EXPLODE: {
                return new SpriteAnimation(
                        bitmapCache.getData(BitmapID.SPACESHIP_EXPLODE),
                        new int[]{150, 150, 150, 150, 150, 150, 150, 150},
                        false
                );
            }
            case COIN_SPIN: {
                return new SpriteAnimation(
                        bitmapCache.getData(BitmapID.COIN_SPIN),
                        new int[]{120, 120, 120, 120, 120, 120},
                        true
                );
            }
            case BULLET_EXPLODE:
                return new SpriteAnimation(
                        bitmapCache.getData(BitmapID.BULLET_EXPLODE),
                        new int[]{70, 60, 60, 60, 60, 60},
                        false
                );
            default: {
                throw new IllegalArgumentException(
                        String.format("Unsupported AnimID %s", animationID.toString())
                );
            }
        }
    }
}
