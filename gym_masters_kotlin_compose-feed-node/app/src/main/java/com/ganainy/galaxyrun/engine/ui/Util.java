package com.ganainy.galaxyrun.engine.ui;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import com.ganainy.galaxyrun.util.Dimension2D;

public class Util {
    /*
    Calculate the dimensions of rendered text.
    https://stackoverflow.com/a/26975371
     */
    public static Dimension2D calcTextDimensions(
            String text,
            int textSize,
            Typeface typeface
    ) {
        Paint paint = new Paint();
        Rect textBounds = new Rect();
        paint.setTypeface(typeface);
        paint.setTextSize(textSize);
        paint.getTextBounds(text, 0, text.length(), textBounds);
        return new Dimension2D(textBounds.width(), textBounds.height());
    }
}
