package com.ganainy.AndroidBall;

import android.content.*;
import android.graphics.*;
import android.graphics.Paint.*;
import android.util.*;
import android.view.*;

public class BallView extends View {

 	BallActivity parent;
    Paint paint;
	
    public BallView(Context context) {
        super(context);
        init();
	}
	
    public BallView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BallView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
	}

	void init() {
        paint = new Paint();
        //setFocusableInTouchMode(true);
    }

	void setParent(BallActivity parent) {
		this.parent = parent;	
	}

	double x;
	double y;
	int radius = 15;

    public void setCoord(double x, double y) {
		this.x = x;
		this.y = y;
		invalidate();
	}

    int width;
    int height;
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.width = w;
        this.height = h;
        parent.ball.setArenaSize(this.width-2*radius,this.height-2*radius); // -2*radius to handle border correctly
		parent.ball.resetPosition();
        super.onSizeChanged(w, h, oldw, oldh);
	}
    
    @Override
    protected void onDraw(Canvas canvas) {
		paint.setColor(Color.BLACK);
		canvas.drawRect(0,0,width,height,paint);
		paint.setColor(Color.BLUE);
        paint.setStyle(Style.FILL);
		// limiting just to be sure
		int xpos = Math.min(width-radius,Math.max(radius,(int)x+radius));
		int ypos = Math.min(height-radius,Math.max(radius,(int)y+radius));
 	    canvas.drawCircle(
			 xpos,
			 ypos,
			 radius,
			 paint);
    }
}
