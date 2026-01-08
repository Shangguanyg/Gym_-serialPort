package com.ganainy.galaxyrun.engine.external;

import android.hardware.SensorEvent;

public class SensorInput extends ExternalInput {
    public final SensorEvent event;
    public SensorInput(SensorEvent event) {
        super(ExternalInputId.SENSOR);
        this.event = event;
    }
}
