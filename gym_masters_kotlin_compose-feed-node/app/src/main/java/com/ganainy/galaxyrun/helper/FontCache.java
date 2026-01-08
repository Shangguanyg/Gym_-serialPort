package com.ganainy.galaxyrun.helper;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import java.util.Hashtable;

import androidx.core.content.res.ResourcesCompat;

/**
 * Simple cache for loaded Typefaces.
 */
public class FontCache {

    private final Context context;
    // Fallback used if a font cannot be loaded
    private final Typeface fallback;
    private Hashtable<FontId, Typeface> fontCache = new Hashtable<>();

    public FontCache(Context context, Typeface fallback) {
        this.context = context;
        this.fallback = fallback;
    }

    public Typeface get(FontId fontId) {
        Typeface tf = fontCache.get(fontId);
        if (tf == null) {
            try {
                tf = ResourcesCompat.getFont(context, fontId.getRId());
                Log.d("FontCache", "Successfully loaded font " + fontId.name());
            }
            catch (Exception e) {
                tf = fallback;  // fallback
                Log.e("FontCache", "Failed to load font " + fontId.name());
            }
            fontCache.put(fontId, tf);
        }
        return tf;
    }
}
