package com.ganainy.galaxyrun.engine.external;

import com.ganainy.galaxyrun.engine.EventID;
import com.ganainy.galaxyrun.engine.draw.DrawInstruction;
import com.ganainy.galaxyrun.engine.audio.SoundID;
import com.ganainy.galaxyrun.util.FastQueue;

/**
 * Stores data created by a game update.
 */

public class GameUpdateMessage {
    private FastQueue<DrawInstruction> drawInstructions;
    private FastQueue<EventID> events;
    private FastQueue<SoundID> sounds;
    public final boolean isMuted;

    public GameUpdateMessage(
            FastQueue<DrawInstruction> drawInstructions,
            FastQueue<EventID> events,
            FastQueue<SoundID> sounds,
            boolean isMuted
    ) {
        this.drawInstructions = drawInstructions;
        this.events = events;
        this.sounds = sounds;
        this.isMuted = isMuted;
    }

    public FastQueue<DrawInstruction> getDrawInstructions() {
        return drawInstructions;
    }

    public FastQueue<EventID> getEvents() {
        return events;
    }

    public FastQueue<SoundID> getSounds() {
        return sounds;
    }
}