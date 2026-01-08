package com.ganainy.galaxyrun.engine;

import android.util.Log;

import com.ganainy.galaxyrun.sprite.Spaceship;
import com.ganainy.galaxyrun.sprite.SpriteState;

/**
 * Manages the state of the game.
 *
 * Usage:
 * - Register yourself as an `IGameStateReceiver` to receive callbacks when the state changes.
 * - Call startGame() to move out of WAITING and into STARTING. This is the only way to leave the
 *     WAITING state.
 * - Call updateState() at the beginning of each game update. GameStateMachine will recalculate
 *     the current state and call the appropriate callback if the state has changed.
 * - Call getCurrState() at any time to get the most-recently calculated state.
 */
public class GameStateMachine {
    private final GameContext gameContext;
    // The receiver for state-change callbacks.
    private final IGameStateReceiver callbackReceiver;
    // Most-recently calculated state of the game.
    private GameState currState;

    public GameStateMachine(
        GameContext gameContext,
        IGameStateReceiver callbackReceiver
    ) {
        this.gameContext = gameContext;
        this.callbackReceiver = callbackReceiver;
        this.currState = GameState.WAITING_FOR_START;
    }

    // Returns the state of the game calculated in the most recent call to `updateState()`.
    public GameState getCurrState() {
        return currState;
    }

    // Sends the signal to move into the STARTING state.
    public void startGame() {
        // TODO: enforce that the currState is WAITING or FINISHED?
        currState = GameState.STARTING;
        callbackReceiver.enterStartingState();
    }

    // Recalculates the current state. If this results in a change of state, this function will
    // call the appropriate callback. Therefore it is important to avoid loops.
    public void updateState(Spaceship player) {
        GameState shouldState = calcState(player);
        if (shouldState != currState) {
            currState = shouldState;
            Log.d("GameEngine", "Setting state to " + shouldState.name());
            switch (shouldState) {
                case WAITING_FOR_START:
                    callbackReceiver.enterWaitingState();
                    break;
                case STARTING:
                    callbackReceiver.enterStartingState();
                    break;
                case PLAYING:
                    callbackReceiver.enterPlayingState();
                    break;
                case PLAYER_DEAD:
                    callbackReceiver.enterPlayerDeadState();
                    break;
                case GAME_OVER:
                    callbackReceiver.enterGameOverState();
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported GameState");
            }
        }
    }

    // Calculate what state the game *should* be in. This is essentially the state-machine
    // transition function.
    private GameState calcState(Spaceship player) {
        switch (currState) {
            case WAITING_FOR_START: {
                // WAITING can only be exited by a call to `startGame()`.
                return GameState.WAITING_FOR_START;
            }
            case STARTING: {
                // Move to PLAYING once the player has reached the start position
                // (one quarter of the screen width)
                if (player.getX() >= gameContext.gameWidthPx / 4.0) {
                    return GameState.PLAYING;
                }
                return GameState.STARTING;
            }
            case PLAYING: {
                // Move to DEAD if the player has died.
                if (player.getState() != SpriteState.ALIVE) {
                    return GameState.PLAYER_DEAD;
                }
                return GameState.PLAYING;
            }
            case PLAYER_DEAD: {
                // Move to GAME_OVER once the player sprite is ready to be terminated.
                // TODO: a different criterion--all sprites have left the screen
                if (player.getState() == SpriteState.TERMINATED) {
                    return GameState.GAME_OVER;
                }
                return GameState.PLAYER_DEAD;
            }
            case GAME_OVER: {
                // GAME_OVER can only be exited by input from GameEngine.
                return GameState.GAME_OVER;
            }
            default: {
                throw new IllegalArgumentException("Unsupported GameState");
            }
        }
    }
}
