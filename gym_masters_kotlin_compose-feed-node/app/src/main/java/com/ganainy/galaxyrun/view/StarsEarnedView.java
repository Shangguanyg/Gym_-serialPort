package com.ganainy.galaxyrun.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.ganainy.galaxyrun.util.ImageUtil;

import com.ganainy.gymmasterscompose.R;

/**
 * A view element used to display the stars earned in a certain GameMode or game. This consists of
 * five stars in a row. "Filled" stars are colored. The filled and unfilled stars are drawn using
 * Bitmaps, specified by the fields FILLED_ID and UNFILLED_ID.
 *
 * Drawing is done by scaling the filled and unfilled bitmaps to the required height, and then to
 * the required width, if required. If there is extra width, they will be centered.
 */

public class StarsEarnedView extends View {

    // drawable id of bitmap to be used for unfilled star
    private static final int UNFILLED_ID = R.drawable.star_unfilled;
    // drawable id of bitmap to be used for filled star
    private static final int FILLED_ID = R.drawable.star_filled;
    // default/preferred height of the view, in dp
    private static final int DEFAULT_HEIGHT_DP = 25;

    // width of health bar on screen (px)
    private int width;
    // height of health bar on screen (px)
    private int height;
    // number of stars that are filled
    private int numFilled;
    // bitmap of filled and unfilled stars, respectively
    private Bitmap filled, unfilled;

    public StarsEarnedView(Context context) {
        super(context);
    }

    public StarsEarnedView(Context context, AttributeSet attrs) { // todo: use xml attributes
        super(context, attrs);
    }

    // set filled stars and call for a redraw of the view
    public void setFilledStars(int filled) {
        this.numFilled = filled;
        invalidate();
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // calculate desired height, based on DEFAULT_HEIGHT_DP
        int desired_height = (int) (DEFAULT_HEIGHT_DP * getResources().getDisplayMetrics().density);
        /*
        // retrieve preferred height the parent wants
        int preferred_height = MeasureSpec.getSize(heightMeasureSpec);
        Log.d("StarsEarnedView", "Heights: " + desired_height + "," + preferred_height);
        // decide actual width based on MeasureSpec requirements
        int actual_height;
        switch (MeasureSpec.getMode(widthMeasureSpec)) {
            case MeasureSpec.EXACTLY:
                actual_height = preferred_height;
                break;
            case MeasureSpec.AT_MOST:
                actual_height = Math.min(desired_height, preferred_height);
                break;
            default: // no restrictions
                actual_height = desired_height;
                break;
        }
        */
        // calculate desired width based on actual height
        int desired_width = 5 * desired_height;
        int actual_width = resolveSize(desired_width, widthMeasureSpec);
        int actual_height = resolveSize(desired_height, heightMeasureSpec);
        /*
        // get width the parent wants
        int preferred_width = MeasureSpec.getSize(widthMeasureSpec);
        Log.d("StarsEarnedView", "Widths: " + desired_width + "," + preferred_width);
        // determine actual width based on MeasureSpec requirements
        int actual_width;
        switch (MeasureSpec.getMode(heightMeasureSpec)) {
            case MeasureSpec.EXACTLY:
                actual_width = preferred_width;
                break;
            case MeasureSpec.AT_MOST:
                actual_width = Math.min(desired_width, preferred_width);
                break;
            default:
                actual_width = preferred_width;
                break;
        }*/
//        Log.d("StarsEarnedView", "Actual set to " + actual_width + "," + actual_height);
        // set dimensions to those determined
        setMeasuredDimension(actual_width, actual_height);
    }

    @Override // handle on size changed by loading filled and unfilled bitmaps and scaling them
    // properly (height to new height, width to new width / 5)
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != width || h != height) {
            filled = ImageUtil.decodeAndScaleTo(getContext(), FILLED_ID, w / 5, h);
            unfilled = ImageUtil.decodeAndScaleTo(getContext(), UNFILLED_ID, w / 5, h);
            width = w;
            height = h;
            Log.d("StarsEarnedView", "Size set to " + width + "," + height);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        for (int i = 0; i < 5; i++) {
            if (i < numFilled) { // draw a filled star
                canvas.drawBitmap(filled, i * filled.getWidth(), 0, null);
            } else { // draw an unfilled star
                canvas.drawBitmap(unfilled, i * unfilled.getWidth(), 0, null);
            }
        }
    }
}
