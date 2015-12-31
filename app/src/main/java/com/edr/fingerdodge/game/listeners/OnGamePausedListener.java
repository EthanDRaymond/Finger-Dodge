package com.edr.fingerdodge.game.listeners;

import com.edr.fingerdodge.game.Game;

/**
 * This is a listener used for when the game is paused and un-paused.
 *
 * @author Ethan Raymond
 */
public abstract class OnGamePausedListener {

    /**
     * This is called whenever the game is paused.
     *
     * @param game the game that is paused
     */
    public abstract void onGamePaused(Game game);

    /**
     * This is called whenever the game is un-paused.
     *
     * @param game the game that is un-paused
     */
    @SuppressWarnings("unused")
    public abstract void onGameUnPaused(Game game);

    /**
     * Call this when the game is paused.
     *
     * @param game the game that is paused
     */
    public void pauseGame(Game game) {
        onGamePaused(game);
    }

}
