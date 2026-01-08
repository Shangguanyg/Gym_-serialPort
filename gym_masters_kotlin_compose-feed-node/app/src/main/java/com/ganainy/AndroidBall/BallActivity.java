package com.ganainy.AndroidBall;
import com.ganainy.gymmasterscompose.R;
import android.app.*;
import android.os.*;
import android.widget.*;
import java.util.*;

public class BallActivity extends Activity {

	final float executionRate = 0.001f; // in sec
	Ball ball;
	BallSensor ballSensor;

	final Handler coordinatorChangeHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			((BallView) findViewById(R.id.ballview)).setCoord(ball.posX,ball.posY);
		}
	};

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		ballSensor = new BallSensor(this);

		setContentView(R.layout.ball);
		((BallView) findViewById(R.id.ballview)).setParent(this);

		ball = new Ball();
		Timer timer = new Timer();
		TimerTask timertask = new TimerTask() {
			public void run() {
				ball.calculateAcceleration();
				ball.updateVelocity(executionRate);
				ball.updatePosition(executionRate);
				coordinatorChangeHandler.sendEmptyMessage(0);
			}
		};
		timer.schedule(timertask,0,(int)(executionRate*1000));
	}

	@Override
	protected void onResume() {
		super.onResume();
		ballSensor.registerListener();
		if( !ballSensor.hasAppropriateSensor() ) {
			Toast.makeText(this, "No appropriate sensors are available", Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		ballSensor.unregisterListener();
	}

	void setOrientationText(float orientation[]) {
		((TextView) findViewById(R.id.pitchtext)).setText(String.format(Locale.US,"Pitch:   %2.4f", orientation[1]));
		((TextView) findViewById(R.id.rolltext)).setText(String.format(Locale.US,"Roll:    %2.4f", orientation[2]));
		ball.calculateForce(-1 * orientation[1], orientation[2]);
	}
}

