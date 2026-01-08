package com.ganainy.galaxyrun.stats;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Set;

/**
 * Contains keys for retrieving statistics to the preferences file or
 * storing single-use statistics during the run of a single game (in which
 * case the GameStats object should be instantiated and used)
 */

public class GameStats { // todo: use enums shared with all StatsContainers?

    // keys to data-values in the lifetime game statistics SharedPreference file
    public static final String ALIENS_KILLED = "ALIENS_KILLED";
    public static final String TIME_PLAYED = "TIME_PLAYED"; // todo: add this function
    public static final String DISTANCE_TRAVELED = "DISTANCE_TRAVELED";
    public static final String GAME_SCORE = "GAME_SCORE";
    public static final String COINS_COLLECTED = "COINS_COLLECTED";
    public static final String ASTEROIDS_KILLED = "ASTEROIDS_KILLED";
    public static final String CANNONS_FIRED = "CANNONS_FIRED";
    public static final String ROCKETS_FIRED = "ROCKETS_FIRED";

    protected HashMap<String, Double> values = new HashMap<>();

    public GameStats() { // todo: better design
        values.put(ALIENS_KILLED, new Double(0));
        values.put(TIME_PLAYED, new Double(0));
        values.put(DISTANCE_TRAVELED, new Double(0));
        values.put(GAME_SCORE, new Double(0));
        values.put(COINS_COLLECTED, new Double(0));
        values.put(ASTEROIDS_KILLED, new Double(0));
        values.put(CANNONS_FIRED, new Double(0));
        values.put(ROCKETS_FIRED, new Double(0));
    }

    // sets value of specified key
    public void set(String key, double value) throws IllegalArgumentException {
        if (!values.containsKey(key)) {
            throw new IllegalArgumentException("GameStats.java: Key not recognized");
        } else {
            values.put(key, value);
        }
    }

    // ads amountAdded to the value of the specified key
    public void addTo(String key, double amountAdded) throws IllegalArgumentException {
        if (!values.containsKey(key)) {
            throw new IllegalArgumentException("GameStats.java: Key not recognized");
        } else {
            values.put(key, values.get(key) + amountAdded);
        }
    }

    // returns value of given key
    public double get(String key) throws IllegalArgumentException {
        if (!values.containsKey(key)) {
            throw new IllegalArgumentException("GameStats.java: Key not recognized");
        } else {
            return values.get(key);
        }
    }

    // returns key set of values
    public Set<String> getKeySet() {
        return values.keySet();
    }

    // returns formatted value given key
    public String getFormatted(String key) throws IllegalArgumentException {
        switch (key) {
            case GAME_SCORE:
            case COINS_COLLECTED:
            case ALIENS_KILLED:
            case ASTEROIDS_KILLED:
            case CANNONS_FIRED:
            case ROCKETS_FIRED:
                return Integer.toString(values.get(key).intValue());
            case DISTANCE_TRAVELED:
                return (new DecimalFormat("#0.00")).format(values.get(key)) + " km";
            case TIME_PLAYED:
                return StatsManager.formatTime(values.get(key));
            default:
                throw new IllegalArgumentException("The key \'" + key + "\' was not recognized!");
        }
    }

    public Double getScore() {
        return values.get(GAME_SCORE);
    }

    @Override
    public String toString() {
        String to_string = "";
        for (String key : values.keySet()) {
            to_string += key + ": " + values.get(key) + "\n";
        }
        return to_string;
    }
}
