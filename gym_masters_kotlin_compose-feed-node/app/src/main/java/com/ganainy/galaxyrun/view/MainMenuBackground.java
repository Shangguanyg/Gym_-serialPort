package com.ganainy.galaxyrun.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.ganainy.galaxyrun.imagetransition.SlideInTransition;

import com.ganainy.gymmasterscompose.R;

/**
 * Created by Stefan on 6/28/2016.
 */
public class MainMenuBackground extends View {

    private Bitmap backgroundImg;
    private SlideInTransition transition;
    private int width;
    private int height;

    public MainMenuBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
        backgroundImg = BitmapFactory.decodeResource(this.getResources(), R.drawable.space_background);
    }

    @Override
    public void onDraw(Canvas canvas) { // todo: may require invalidate() to force animation update
        canvas.drawBitmap(backgroundImg, 0, 0, null);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w;
        height = h;
        backgroundImg = Bitmap.createScaledBitmap(backgroundImg, width, height, true);
    }
}
