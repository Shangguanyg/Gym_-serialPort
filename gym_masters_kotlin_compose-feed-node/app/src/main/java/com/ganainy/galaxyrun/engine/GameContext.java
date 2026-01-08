package com.ganainy.galaxyrun.engine;

import android.content.Context;

import com.ganainy.galaxyrun.engine.map.Map;
import com.ganainy.galaxyrun.helper.BitmapCache;
import com.ganainy.galaxyrun.helper.FontCache;

import java.util.Random;

/**
 * Context.
 */

public class GameContext {
    public final Context appContext;
    public final boolean inDebugMode;
    public final BitmapCache bitmapCache;
    public final FontCache fontCache;
    public final AnimFactory animFactory;  // TODO: remove (unnecessary)
    public final Random rand;
    public final int gameWidthPx;
    public final int gameHeightPx;
    public final int screenWidthPx;
    public final int screenHeightPx;
    // Width of a (square) tile in the game
    public final int tileWidthPx;

    public GameContext(
            Context appContext,
            boolean inDebugMode,
            BitmapCache bitmapCache,
            FontCache fontCache,
            AnimFactory animCache,
            Random rand,
            int gameWidthPx,
            int gameHeightPx,
            int screenWidthPx,
            int screenHeightPx
    ) {
        this.appContext = appContext;
        this.inDebugMode = inDebugMode;
        this.bitmapCache = bitmapCache;
        this.fontCache = fontCache;
        this.animFactory = animCache;
        this.rand = rand;
        this.gameWidthPx = gameWidthPx;
        this.gameHeightPx = gameHeightPx;
        this.screenWidthPx = screenWidthPx;
        this.screenHeightPx = screenHeightPx;
        tileWidthPx = gameHeightPx / Map.NUM_ROWS;
    }
}
