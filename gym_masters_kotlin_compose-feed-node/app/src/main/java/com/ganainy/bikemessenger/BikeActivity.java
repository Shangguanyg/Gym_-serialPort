package com.ganainy.bikemessenger;

import android.content.Intent;
import android.os.Bundle;

import android.graphics.Point;
import android.media.Image;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ganainy.gymmasterscompose.R;

import java.util.Timer;
import java.util.TimerTask;

public class BikeActivity extends AppCompatActivity {

    private TextView scoreText;
    private TextView startText;
    private ImageView bike;
    private ImageView nail;
    private ImageView taxi;
    private ImageView packageNorm;
    private ImageView packageRed;


    // Checks status of movement
    private boolean move_check = false;
    private boolean start_flag = false;

    // used to make sure the biker does not go out of screen
    private int height_of_frame;
    private int size_of_biker;

    // Used for the points and gameover icons
    private int width_of_screen;
    private int height_of_screen;

    // for speed of moving objects (to fix bug of different speeds on different devices)
    private int bikeSpeed;
    private int nailSpeed;
    private int taxiSpeed;
    private int packageNormSpeed;
    private int packageRedSpeed;

    // Positions
    private int bikeY; // for position of Bike
    // for position of normal package (less points)
    private int packageNormX;
    private int packageNormY;
    // for position of red package (more points)
    private int packageRedX;
    private int packageRedY;
    // for position of nail (game over)
    private int nailX;
    private int nailY;
    // for position of taxi (also game over)
    private int taxiX;
    private int taxiY;

    private int money =0;

    // to initialize class
    private Sounds sounds;
    private Handler handler = new Handler();
    private Timer timer = new Timer();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // for sounds
        sounds = new Sounds(this);

        // assigns the various xml widgets
        scoreText = (TextView) findViewById(R.id.score);
        startText = (TextView) findViewById(R.id.start);
        bike = (ImageView) findViewById(R.id.bike);
        nail = (ImageView) findViewById(R.id.nail);
        taxi = (ImageView) findViewById(R.id.taxi);
        packageNorm = (ImageView) findViewById(R.id.packageNorm);
        packageRed = (ImageView) findViewById(R.id.packageRed);

        // To get the size of screen
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width_of_screen = size.x;
        height_of_screen = size.y;

        // set speed of moving objects so that experience is same for all devices
        bikeSpeed = Math.round(height_of_screen / 40f);
        nailSpeed = Math.round(width_of_screen/ 45f);
        packageNormSpeed= Math.round(width_of_screen/ 65f);
        packageRedSpeed= Math.round(width_of_screen/ 38f);
        taxiSpeed = Math.round(width_of_screen/ 40f);

        // set score to 0
        scoreText.setText("Money: $0"); // set intial score to 0 onCreate

        // moves the potential points and game over off screen
        packageNorm.setX(-80);
        packageNorm.setY(-80);
        packageRed.setX(-80);
        packageRed.setY(-80);
        nail.setX(-80);
        nail.setY(-80);
        taxi.setX(-80);
        taxi.setY(-80);




    }

    public void changePos(){
        hitterOrQuitter();

        // to move Normal package
        packageNormX -= packageNormSpeed; // set speed of prize
        if (packageNormX < 0){
            packageNormX = width_of_screen;
            packageNormY = (int) Math.floor(Math.random() * (height_of_frame - packageNorm.getHeight())); // so package randomly follows on some y axis
        }
        packageNorm.setX(packageNormX);
        packageNorm.setY(packageNormY);

        // red package
        //packageRedX -= 20; not a good way of setting speed since different screens will have different speeds
        packageRedX -= packageRedSpeed;
        if (packageRedX < 0){
            packageRedX = width_of_screen + 10;
            packageRedY = (int) Math.floor(Math.random() * (height_of_frame - packageRed.getHeight())); // so package randomly follows on some y axis
        }
        packageRed.setX(packageRedX);
        packageRed.setY(packageRedY);

        // nail
        nailX -= nailSpeed; // so nail has a faster speed then normal package
        if (nailX < 0){
            nailX = width_of_screen + 15;
            nailY = (int) Math.floor(Math.random() * (height_of_frame - nail.getHeight())); // so package randomly follows on some y axis
        }
        nail.setX(nailX);
        nail.setY(nailY);

        // taxi
        taxiX -= taxiSpeed; // so both nail and taxi have same speed
        if (taxiX < 0){
            taxiX = width_of_screen + 20;
            taxiY = (int) Math.floor(Math.random() * (height_of_frame - taxi.getHeight())); // so package randomly follows on some y axis
        }
        taxi.setX(taxiX);
        taxi.setY(taxiY);



        // to move bike
        if(move_check == true){
            bikeY -= bikeSpeed; // when the screen recieves touch input the biker will go up on screen
        }
        else{
            bikeY += bikeSpeed; // when the touch input is release, the biker drops down the screen
        }
        // check to make sure the biker stays in frame
        if (bikeY < 0){ // for top of screen
            bikeY = 0;
        }
        if (bikeY > height_of_frame - size_of_biker){ // for bottom of screen
            bikeY = height_of_frame - size_of_biker;
        }


        bike.setY(bikeY);
        scoreText.setText("Money: $" + money); // update money (score)

    }

    public void hitterOrQuitter(){
        // In this function we will check for hits on the four different objects. If the center of any of the three objects pass through the biker = hit

        // NORMAL PACKAGE
        // first we find the center of the package
        int packageNormCenterX = packageNormX + packageNorm.getWidth()/2;
        int packageNormCenterY = packageNormY + packageNorm.getHeight()/2;

        if(0 <= packageNormCenterX && packageNormCenterX <= size_of_biker && bikeY <= packageNormCenterY && packageNormCenterY <= bikeY + size_of_biker){
            money += 10;
            packageNormX = -10;
            sounds.playPointSound();
        }

        // RED PACKAGE
        int packageRedCenterX = packageRedX + packageRed.getWidth()/2;
        int packageRedCenterY = packageRedY + packageRed.getHeight()/2;
        if(0 <= packageRedCenterX && packageRedCenterX <= size_of_biker && bikeY <= packageRedCenterY && packageRedCenterY <= bikeY + size_of_biker){
            money += 20;
            packageRedX = -10;
            sounds.playPointSound();
        }

        // NAIL
        int nailCenterX = nailX + nail.getWidth()/2;
        int nailCenterY = nailY + nail.getHeight()/2;
        if(0 <= nailCenterX && nailCenterX <= size_of_biker && bikeY <= nailCenterY && nailCenterY <= bikeY + size_of_biker){
            // if this happens, this means our biker has ran over a nail and game is over
            // So we stop timer
            sounds.playNailSound();
            timer.cancel();
            timer = null;

            // Now we show the players total cash before the play had to clock out
            Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
            intent.putExtra("MONEY", money);
            startActivity(intent);
        }

        // TAXI
        int taxiCenterX = taxiX + taxi.getWidth()/2;
        int taxiCenterY = taxiY + taxi.getHeight()/2;
        if(0 <= taxiCenterX && taxiCenterX <= size_of_biker && bikeY <= taxiCenterY && taxiCenterY <= bikeY + size_of_biker){
            // if this happens, this means our biker has ran over a nail and game is over
            // So we stop timer
            sounds.playTaxiSound();
            timer.cancel();
            timer = null;

            // Now we show the players total cash before the play had to clock out
            Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
            intent.putExtra("MONEY", money);
            startActivity(intent);
        }


    }


    public boolean onTouchEvent(MotionEvent motionEvent){
        if(start_flag == false){
            start_flag = true;

            // We are getting the biker and frame height here because the UI was not set on screen onCreate()
            FrameLayout frame = (FrameLayout)  findViewById(R.id.frame);
            height_of_frame = frame.getHeight();
            bikeY =(int) bike.getY();

            size_of_biker = bike.getHeight();

            startText.setVisibility(View.GONE);

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    });
                }
            }, 0, 20); // calls change position every 20 milliseconds
        }
        else{
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                move_check = true;
            }
            else if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                move_check = false;
            }
        }

        return true;
    }
    // We do not want the user to be able to go back to previous events using the back button, this is because the user may end up at the MainActivity again after the result page shows
    @Override
    public boolean dispatchKeyEvent(KeyEvent Kevent){
        if (Kevent.getAction()== KeyEvent.ACTION_DOWN){
            switch (Kevent.getKeyCode()){
                case KeyEvent.KEYCODE_BACK:
                    return true;
            }
        }
        return super.dispatchKeyEvent(Kevent);
    }
}
