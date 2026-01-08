package com.ganainy.galaxyrun.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Hashtable;
import java.util.NoSuchElementException;

import com.ganainy.gymmasterscompose.R;

/**
 * Bitmap cache for R.drawables. Retrieved using BitmapID.getDebugString()
 */
public class BitmapCache {

    // Reference to application context
    private Context context;

    // stores bitmaps
    private Hashtable<BitmapID, Bitmap> bmpCache = new Hashtable<>();
    // stores bitmap data
    private Hashtable<BitmapID, BitmapData> bmpData = new Hashtable<>();

    private double scalingFactor = 1.0f;

    /*
    Create cache with specified scaling factor.
     */
    public BitmapCache(Context context, double scalingFactor) {
        assert(context != null);
        this.context = context;
        this.scalingFactor = scalingFactor;
    }

    /*
    Create cache, auto-determining the scaling factor to use based on
    game dimensions.
     */
    public BitmapCache(Context context, int gameWidthPx, int gameHeightPx) {
        assert(context != null);
        this.context = context;
        scalingFactor = calcScalingFactor(gameWidthPx, gameHeightPx);
    }

    /*
     Determines proper image scaling factor, based on the screen dimensions
     We want the spaceship's height to be 1/6 of the screen height.
      */
    private double calcScalingFactor(int gameWidthPx, int gameHeightPx) {
        Bitmap spaceship = BitmapFactory.decodeResource(
                context.getResources(), R.drawable.spaceship);
        return (gameHeightPx / 6.0f) / (float) spaceship.getHeight();
    }

    /*
    Looks up the Bitmap with the given BitmapID. Will attempt to load the
    corresponding Bitmap from storage if not found (using the BitmapID's rId field).

    May throw [TODO: WHAT KIND OF EXCEPTION?]
     */
    public Bitmap getBitmap(BitmapID key) throws NoSuchElementException {
        // Lookup ID in Hashtable
        Bitmap bmp = bmpCache.get(key);
        // Bitmap not in cache: load, scale, and add to cache
        if (bmp == null) {
            bmp = BitmapFactory.decodeResource(context.getResources(), key.getrId());
            bmp = Bitmap.createScaledBitmap(
                    bmp,
                    (int) (bmp.getWidth() * scalingFactor),
                    (int) (bmp.getHeight() * scalingFactor),
                    true
            );

            bmpCache.put(key, bmp);
            bmpData.put(key, new BitmapData(key, bmp.getWidth(), bmp.getHeight()));
        }
        return bmp;
    }

    /*
    Looks up the BitmapData for the given BitmapID. Will attempt to load the
    corresponding Bitmap from storage if not found (using the BitmapID's rId field).

    TODO: WHAT KIND OF EXCEPTION WOULD BE THROWN?
    */
    public BitmapData getData(BitmapID key) throws NoSuchElementException {
        BitmapData data = bmpData.get(key);
        // Not found in cache
        if (data == null) {
            // Add to cache
            getBitmap(key);  // TODO: NOTE: METHOD SHOULD BE SPLIT INTO `GET()`, AND `LOAD/CACHE()`
            data = bmpData.get(key);
        }
        return data;
    }

    public void setScalingFactor(double scalingFactor) {
        this.scalingFactor = scalingFactor;
    }
}