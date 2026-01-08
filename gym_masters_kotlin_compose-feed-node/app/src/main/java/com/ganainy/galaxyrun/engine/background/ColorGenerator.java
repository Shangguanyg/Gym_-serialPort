package com.ganainy.galaxyrun.engine.background;

import android.graphics.Color;

import androidx.annotation.ColorInt;

/**
 * Generates sequences of colors.
 */
public class ColorGenerator {
    // Returns an array of size `n` with each element equal to `color`.
    @ColorInt
    static int[] makeSolidColor(@ColorInt int color, int n) {
        int[] result = new int[n];
        for (int i = 0; i < n; i++) {
            result[i] = color;
        }
        return result;
    }

    // Returns a linear interpolation from `startColor` to `endColor` over `n` elements.
    // The first element of the returned array will be `start`; the last will be `end`.
    // Throws IllegalArgumentException if n is less than two.
    @ColorInt
    static int[] makeTransition(@ColorInt int start, @ColorInt int end, int n) {
        if (n < 2) {
            throw new IllegalArgumentException("n cannot be less than 2");
        }

        int[] result = new int[n];
        result[0] = start;
        result[n-1] = end;

        // Calculate the change in each color channel, per element.
        float dA = (Color.alpha(end) - Color.alpha(start)) * 1f / (n - 1);
        float dR = (Color.red(end) - Color.red(start)) * 1f / (n - 1);
        float dG = (Color.green(end) - Color.green(start)) * 1f / (n - 1);
        float dB = (Color.blue(end) - Color.blue(start)) * 1f / (n - 1);

        for (int i = 1; i < n-1; i++) {
            int a = (int) (Color.alpha(start) + i * dA);
            int r = (int) (Color.red(start) + i * dR);
            int g = (int) (Color.green(start) + i * dG);
            int b = (int) (Color.blue(start) + i * dB);
            result[i] = Color.argb(a, r, g, b);
        }

        return result;
    }
}
