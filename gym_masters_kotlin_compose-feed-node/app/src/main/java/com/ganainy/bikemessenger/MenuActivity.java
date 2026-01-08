package com.ganainy.bikemessenger;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import com.ganainy.gymmasterscompose.R;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void start(View view){
        startActivity(new Intent(getApplicationContext(), BikeActivity.class));
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
