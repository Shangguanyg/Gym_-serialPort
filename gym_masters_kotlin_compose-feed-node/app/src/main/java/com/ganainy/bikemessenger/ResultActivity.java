package com.ganainy.bikemessenger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import com.ganainy.gymmasterscompose.R;


import org.w3c.dom.Text;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView gameScore = (TextView) findViewById(R.id.gamescore);
        TextView highScore = (TextView) findViewById(R.id.Highscore);

        int money = getIntent().getIntExtra("MONEY", 0);
        gameScore.setText("$" +money + "");

        SharedPreferences sp = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
        int hScore = sp.getInt("HIGH_SCORE", 0);

        if (money > hScore){
            highScore.setText("Highest Payout: $" + money);

            // now we save
            SharedPreferences.Editor ed = sp.edit();
            ed.putInt("HIGH_SCORE", money);
            ed.commit();
        }
        else{
            highScore.setText("Highest Payout: $" + hScore);
        }

    }


    public void rideAgain(View view){
        startActivity(new Intent(getApplicationContext(), MenuActivity.class));
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
