package com.ganainy.galaxyrun.engine.external;

/**
 * Base class for external inputs. These are basically equivalent to events.
 */
public class ExternalInput {
    public final ExternalInputId inputId;
    public ExternalInput(ExternalInputId id) {
        inputId = id;
    }
}
