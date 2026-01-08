package com.ganainy.galaxyrun.engine.background;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.Random;

import androidx.annotation.ColorInt;

/**
 * Generates images ("panels") to be used for the game background.
 * Panels are rendered using GalaxyDraw and transition the background color
 * over time. The possible background colors are defined in `BACKGROUND_COLORS`.
 * The color always starts at `STANDARD_COLOR`, transitions to a randomly-chosen
 * color from `BACKGROUND_COLORS`, then transitions back to `STANDARD_COLOR`.
 * The `NUM_PANELS_STAY` and `NUM_PANELS_TRANSITION` define the length of these
 * transitions.
 */
public class BackgroundGenerator {
    // Width of panels to generate.
    private final int panelWidthPx;
    // Height of panels to generate.
    private final int panelHeightPx;
    // Random number generator used when generating panels.
    private final Random rand;
    // Used to render a starry "galaxy" background.
    private final GalaxyDrawer galaxyDrawer;
    // A sequence of colors to use for the background of the next N panels.
    // Each panel starts with color `i` and transitions to the color at `i+1`.
    // For example, the first panel to be generated will use `startColor=colors[0]`,
    // `endColor=colors[1]`. For a `colors` array of size N, we can generate N-1
    // panels. Once we reach the end of the colors array, we generate the next one.
    // `colors` is generated as a full transition at a time.
    private @ColorInt int[] colors;
    // The number of panels that have been generated using the current `colors` array.
    private int count;

    @ColorInt
    private static final int STANDARD_COLOR = Color.BLACK;
    // The number of panels of the `STANDARD_COLOR` to generate at the start of the game.
    private static final int NUM_STANDARD_AT_START = 2;
    // The number of panels over which the transition will occur.
    private static final int NUM_PANELS_TRANSITION = 2;
    // The number of panels to stay at the chosen color before transitioning
    // back to `STANDARD_COLOR`.
    private static final int NUM_PANELS_STAY = 1;
    // The colors that the background may transition to.
    @ColorInt
    private static final int[] BACKGROUND_COLORS = {
            // Light blue.
            Color.rgb(97, 148, 194),
            // Light green.
            Color.rgb(99, 207, 151),
            // Light purple.
            Color.rgb(224, 117, 221),
            // Turquoise.
            Color.rgb(113, 222, 222),
            // Pink.
            Color.rgb(217, 80, 137)
    };

    // Construct a BackgroundGenerator that will render panels of size
    // `panelWidthPx` by `panelHeightPx`.
    BackgroundGenerator(int panelWidthPx, int panelHeightPx, Random rand) {
        this.panelWidthPx = panelWidthPx;
        this.panelHeightPx = panelHeightPx;
        this.rand = rand;
        galaxyDrawer = new GalaxyDrawer(this.rand);
        colors = ColorGenerator.makeSolidColor(STANDARD_COLOR, NUM_STANDARD_AT_START+1);
    }

    public Bitmap nextPanel() {
        if (count == colors.length - 1) {
            // Randomly choose the next color to transition to.
            @ColorInt int nextColor = BACKGROUND_COLORS[rand.nextInt(BACKGROUND_COLORS.length)];
            colors = generateColorTransition(STANDARD_COLOR, nextColor);
            count = 0;
        }
        // Transition from `colors[count]` to `colors[count+1]`.
        GalaxyDrawOptions options = new GalaxyDrawOptions(
                /*startColor=*/colors[count],
                /*endColor=*/colors[count+1],
                /*starDensity=*/2,
                /*starColor=*/Color.argb(200, 255, 255, 238),
                /*starSize=*/2,
                /*sizeVariance=*/0.5f,
                /*colorVariance=*/0.3f
        );
        ++count;
        return galaxyDrawer.drawGalaxy(this.panelWidthPx, this.panelHeightPx, options);
    }

    // Generates colors for a color transition.
    // Transitions from `startColor` to `toColor` over `NUM_PANELS_TRANSITION` panels.
    // Then stays at `toColor` for NUM_PANELS_STAY.
    // Then transitions back to `startColor` over `NUM_PANELS_TRANSITION` panels.
    @ColorInt
    private int[] generateColorTransition(@ColorInt int startColor, @ColorInt int toColor) {
        int[] transition =
                ColorGenerator.makeTransition(startColor, toColor, NUM_PANELS_TRANSITION+1);
        // Subtract one from NUM_PANELS_STAY because merging `transition` and `returnTransition`
        // will effectively add a panel of "stay" in between.
        int[] stay = ColorGenerator.makeSolidColor(toColor, NUM_PANELS_STAY-1);
        int[] returnTransition =
                ColorGenerator.makeTransition(toColor, startColor, NUM_PANELS_TRANSITION+1);

        // Merge the three arrays in sequence.
        int[] merged = new int[transition.length + stay.length + returnTransition.length];
        System.arraycopy(transition, 0, merged, 0, transition.length);
        System.arraycopy(stay, 0, merged, transition.length, stay.length);
        System.arraycopy(returnTransition, 0, merged, transition.length + stay.length, returnTransition.length);
        return merged;
    }
}
