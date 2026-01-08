package com.ganainy.galaxyrun.engine.background;

import androidx.annotation.ColorInt;

/**
 * Defines the parameters to use when generating a "galaxy" image.
 *
 * Together, `startColor` and `endColor` are used to draw a color gradient
 * across the image, from left to right.
 *
 * Then a number of stars is generated according to `starDensity`.
 * These stars will have the `starColor` and `starRadiusPx` specified.
 * However, to create interesting variation, `sizeVariance` and
 * `brightnessVariance` can be set between 0 and 1 to specify how much
 * to randomly vary the size and brightness, respectively. Here, "variance"
 * doesn't have the same meaning as variance in statistics--instead, it is
 * a fractional value in the range [0, 1], where 0 = no variance,
 * and 1 allows for up to 100% variance from the set value.
 *
 * For example, if starRadiusPx=100 and sizeVariance=0.4, stars will
 * be generated with size uniformly distributed between [60, 140]. The
 * same concept applies to brightnessVariance, which varies the alpha
 * of the starColor.
 */
public class GalaxyDrawOptions {
    // Color of the background on the left side.
    public int startColor;
    // Color of the background on the right side.
    public int endColor;
    // Number of stars to generate per 50px * 50px square.
    public float starDensity;
    // Standard color of generated stars.
    public int starColor;
    // Standard radius of generated stars.
    public int starRadiusPx;
    // How much random variance to apply to the star size as a fraction of `starRadiusPx`.
    // Must be between [0, 1].
    public float sizeVariance;
    // How much random variance to apply to the star brightness as a fraction.
    // Must be between [0, 1].
    public float brightnessVariance;

    public GalaxyDrawOptions(
            @ColorInt int startColor,
            @ColorInt int endColor,
            float starDensity,
            @ColorInt int starColor,
            int starRadiusPx,
            float sizeVariance,
            float brightnessVariance
    ) {
        this.startColor = startColor;
        this.endColor = endColor;
        this.starDensity = starDensity;
        this.starColor = starColor;
        this.starRadiusPx = starRadiusPx;
        this.sizeVariance = sizeVariance;
        this.brightnessVariance = brightnessVariance;
    }
}