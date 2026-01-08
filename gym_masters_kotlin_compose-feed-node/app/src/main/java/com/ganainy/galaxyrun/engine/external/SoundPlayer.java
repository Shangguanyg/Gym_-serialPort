package com.ganainy.galaxyrun.engine.external;


import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import com.ganainy.galaxyrun.engine.audio.SoundID;

import java.util.HashMap;

// TODO: does this belong in GameEngine?
// TODO: support pause() and resume() (via autoPause(), autoResume())
public class SoundPlayer {
    private final SoundPool soundPool;
    // Maps SoundID to the "resID" it's assigned by the SoundPool
    private final HashMap<SoundID, Integer> resourceIds;
    // Number of audio streams to use in the SoundPool.
    private static final int NUM_STREAMS = 10;

    public SoundPlayer(Context appContext) {
        SoundPool.Builder builder = new SoundPool.Builder();
        builder.setMaxStreams(NUM_STREAMS);
        builder.setAudioAttributes(new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build());
        soundPool = builder.build();
        resourceIds = new HashMap<>();
        // Load all sounds on init so that there is no delay when playing for the first time
        for (SoundID soundId : SoundID.values()) {
            resourceIds.put(soundId, soundPool.load(appContext, soundId.getId(), 1));
        }
    }

    public void playSound(SoundID sound) {
        Integer resId = resourceIds.get(sound);
        if (resId == null) {
            // Should never happen.
            return;
        }
        soundPool.play(resId, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    // Release all associated memory
    public void release() {
        soundPool.release();
    }
}
