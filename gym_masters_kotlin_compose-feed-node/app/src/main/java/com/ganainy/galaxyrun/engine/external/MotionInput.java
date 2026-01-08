package com.ganainy.galaxyrun.engine.external;

import android.view.MotionEvent;

public class MotionInput extends ExternalInput {
    public final MotionEvent motion;
    public MotionInput(MotionEvent motion) {
        super(ExternalInputId.MOTION);
        this.motion = motion;
    }
}
