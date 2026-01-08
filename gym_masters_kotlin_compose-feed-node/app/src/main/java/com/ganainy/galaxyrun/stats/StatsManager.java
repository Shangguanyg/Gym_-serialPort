package com.ganainy.galaxyrun.stats;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;

/**
 * Tracks lifetime statistics. Saves them in SharedPreferences.
 * Manages updates.
 */

public class StatsManager {

    // name of preferences file where statistics are stored
    private static final String PREFERENCES_FILE_KEY = "com.plainsimple.spaceships.STATS_KEY";

    private static final String GAMES_PLAYED = "GAMES PLAYED";
    private static final String LIFETIME_SCORE = "LIFETIME SCORE";
    private static final String HIGH_SCORE = "HIGHSCORE";
    private static final String MOST_COINS = "MOST COINS COLLECTED";
    private static final String TOTAL_COINS = "TOTAL COINS COLLECTED";
    private static final String FARTHEST_FLOWN = "FARTHEST DISTANCE FLOWN";
    private static final String TOTAL_FLOWN = "TOTAL DISTANCE FLOWN";
    private static final String TOTAL_TIME_PLAYED = "TOTAL TIME PLAYED";
    private static final String LONGEST_GAME = "LONGEST GAME";
    private static final String MOST_ALIENS_KILLED = "MOST ALIENS KILLED";
    private static final String TOTAL_ALIENS_KILLED = "TOTAL ALIENS KILLED";
    private static final String TOTAL_ASTEROIDS_KILLED = "TOTAL_ASTEROIDS_KILLED";
    private static final String MOST_ASTEROIDS_KILLED = "MOST_ASTEROIDS_KILLED";
    private static final String CANNONS_FIRED = "CANNONS_FIRED";
    private static final String ROCKETS_FIRED = "ROCKETS_FIRED";
    private static final String COINS_SPENT = "COINS_SPENT";
    private static final String UPGRADES_BOUGHT = "UPGRADES_BOUGHT";

    // stores pairings of key to default val
    private static final HashMap<String, Double> defaultVals = getDefaultVals();
    // handle to SharedPreferences
    private static SharedPreferences prefs;

    private StatsManager() {

    }

    // get a handle to SharedPreferences using given Context
    public static void init(Context context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
        }
    }

    // get data from preferences and convert from long to double
    private static Double retrieve(String key) {
        return Double.longBitsToDouble(prefs.getLong(key, 0));
    }

    // convert data from double to long and commits it to SharedPreferences
    private static void put(String key, Double val) {
        prefs.edit().putLong(key, Double.doubleToLongBits(val)).commit();
    }

    // increments value associated with given key in statsCache and commits to Prefs
    // throws IllegalArgumentException if key is invalid
    private static void incrementValue(String key, double amount) throws IllegalArgumentException {
        if (defaultVals.containsKey(key)) {
            put(key, amount + retrieve(key));
        } else {
            throw new IllegalArgumentException("Key '"+ key + "' not recognized");
        }
    }

    // increments COINS_SPENT stat. Useful method for outer classes to access
    public static void incrementCoinsSpent(int coinsSpent) {
        incrementValue(COINS_SPENT, coinsSpent);
    }

    // increments UPGRADES_BOUGHT stat by one. Useful method for outer classes to access
    public static void incrementUpgradesBought() {
        incrementValue(UPGRADES_BOUGHT, 1);
    }

    // updates all stats using stats from the given game and commits to prefs
    public static void update(GameStats game) { // todo: share keys between StatsManager and StatsObjects?
        incrementValue(GAMES_PLAYED, 1);
        incrementValue(LIFETIME_SCORE, game.get(GameStats.GAME_SCORE));
        incrementValue(TOTAL_COINS, game.get(GameStats.COINS_COLLECTED));
        incrementValue(TOTAL_FLOWN, game.get(GameStats.DISTANCE_TRAVELED));
        incrementValue(TOTAL_TIME_PLAYED, game.get(GameStats.TIME_PLAYED));
        incrementValue(TOTAL_ALIENS_KILLED, game.get(GameStats.ALIENS_KILLED));
        incrementValue(TOTAL_ASTEROIDS_KILLED, game.get(GameStats.ASTEROIDS_KILLED));
        incrementValue(CANNONS_FIRED, game.get(GameStats.CANNONS_FIRED));
        incrementValue(ROCKETS_FIRED, game.get(GameStats.ROCKETS_FIRED));
        if (game.get(GameStats.COINS_COLLECTED) > retrieve(MOST_COINS)) {
            put(MOST_COINS, game.get(GameStats.COINS_COLLECTED));
        }
        if (game.get(GameStats.DISTANCE_TRAVELED) > retrieve(FARTHEST_FLOWN)) {
            put(FARTHEST_FLOWN, game.get(GameStats.DISTANCE_TRAVELED));
        }
        if (game.get(GameStats.TIME_PLAYED) > retrieve(LONGEST_GAME)) {
            put(LONGEST_GAME, game.get(GameStats.TIME_PLAYED));
        }
        if (game.get(GameStats.ALIENS_KILLED) > retrieve(MOST_ALIENS_KILLED)) {
            put(MOST_ALIENS_KILLED, game.get(GameStats.ALIENS_KILLED));
        }
        if (game.get(GameStats.ASTEROIDS_KILLED) > retrieve(MOST_ASTEROIDS_KILLED)) {
            put(MOST_ASTEROIDS_KILLED, game.get(GameStats.ASTEROIDS_KILLED));
        }
        if (game.get(GameStats.GAME_SCORE) > retrieve(HIGH_SCORE)) {
            put(HIGH_SCORE, game.get(GameStats.GAME_SCORE));
        }
    }

    // returns keys in a String[] array sorted in display order
    public static String[] getKeysToDisplay() {
        return new String[] {
            HIGH_SCORE,
            LIFETIME_SCORE,
            GAMES_PLAYED,
            TOTAL_FLOWN,
            FARTHEST_FLOWN,
            TOTAL_TIME_PLAYED,
            LONGEST_GAME,
            TOTAL_ALIENS_KILLED,
            MOST_ALIENS_KILLED,
            TOTAL_ASTEROIDS_KILLED,
            MOST_ASTEROIDS_KILLED,
            CANNONS_FIRED,
            ROCKETS_FIRED,
            TOTAL_COINS,
            MOST_COINS,
            COINS_SPENT,
            UPGRADES_BOUGHT
        };
    }

    // retrieves value, formats it into a String and returns.
    // different statsCache get formatted differently. These are ready-to-display
    public static String getFormatted(String key) throws IllegalArgumentException {
        switch (key) {
            case GAMES_PLAYED:
            case LIFETIME_SCORE:
            case HIGH_SCORE:
            case MOST_COINS:
            case TOTAL_COINS:
            case MOST_ALIENS_KILLED:
            case TOTAL_ALIENS_KILLED:
            case TOTAL_ASTEROIDS_KILLED:
            case MOST_ASTEROIDS_KILLED:
            case CANNONS_FIRED:
            case ROCKETS_FIRED:
            case COINS_SPENT:
            case UPGRADES_BOUGHT: // todo: better formatting (commas)
                return NumberFormat.getInstance().format(retrieve(key).intValue());
            case FARTHEST_FLOWN:
            case TOTAL_FLOWN:
                return (new DecimalFormat("###,##0.00")).format(retrieve(key)) + " km";
            case TOTAL_TIME_PLAYED:
            case LONGEST_GAME:
                return formatTime(retrieve(key));
            default:
                throw new IllegalArgumentException("Key '" + key + "' not recognized");
        }
    }

    // takes the given time in ms and formats into "hh:mm:ss time"
    public static String formatTime(double ms) {
        String to_str = "";
        // add hours field
        if (ms >= 3_600_000) {
            to_str += (int) (ms / 3_600_000) + "h";
            ms %= 3_600_000;
        }
        // add minutes field
        if (ms >= 60_000) {
            to_str += (int) (ms / 60_000) + "m";
            ms %= 60_000;
        }
        // add seconds field with whatever's left, rounded down
        to_str += (int) (ms / 1_000) + "s";
        return to_str;
    }

    // generate and return HashMap containing key, default val pairings
    private static HashMap<String, Double> getDefaultVals() {
        HashMap<String, Double> map = new HashMap<>();
        map.put(GAMES_PLAYED, new Double(0));
        map.put(LIFETIME_SCORE, new Double(0));
        map.put(HIGH_SCORE, new Double(0));
        map.put(MOST_COINS, new Double(0));
        map.put(TOTAL_COINS, new Double(0));
        map.put(FARTHEST_FLOWN, new Double(0));
        map.put(TOTAL_FLOWN, new Double(0));
        map.put(TOTAL_TIME_PLAYED, new Double(0));
        map.put(LONGEST_GAME, new Double(0));
        map.put(MOST_ALIENS_KILLED, new Double(0));
        map.put(TOTAL_ALIENS_KILLED, new Double(0));
        map.put(TOTAL_ASTEROIDS_KILLED, new Double(0));
        map.put(MOST_ASTEROIDS_KILLED, new Double(0));
        map.put(CANNONS_FIRED, new Double(0));
        map.put(ROCKETS_FIRED, new Double(0));
        map.put(COINS_SPENT, new Double(0));
        map.put(UPGRADES_BOUGHT, new Double(0));
        return map;
    }

    public static String getDebugString() {
        String to_string = "";
        for (String key : defaultVals.keySet()) {
            to_string += key + ": " + retrieve(key) + "\n";
        }
        return to_string;
    }
}
