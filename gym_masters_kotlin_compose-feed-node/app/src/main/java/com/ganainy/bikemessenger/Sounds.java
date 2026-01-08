package com.ganainy.bikemessenger;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.ganainy.gymmasterscompose.R;

/**
 * Created by Sanjeev on 1/17/2017.
 */

// This class is made to handle sounds
public class Sounds {
    private static int pointSound;
    private static int nailSound;
    private static int taxiSound;
    private static SoundPool soundPool;

    public Sounds(Context context){
        soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC,0);

        // gain access to the sound files
        pointSound = soundPool.load(context, R.raw.hit, 1);
        nailSound = soundPool.load(context, R.raw.tire, 1);
        taxiSound = soundPool.load(context, R.raw.taxi, 1);
    }

    public void playPointSound(){soundPool.play(pointSound, 1.0f, 1.0f, 1, 0, 1.0f);}
    public void playNailSound(){soundPool.play(nailSound, 1.0f, 1.0f, 1, 0, 1.0f);}
    public void playTaxiSound(){soundPool.play(taxiSound, 1.0f, 1.0f, 1, 0, 1.0f);}
}
