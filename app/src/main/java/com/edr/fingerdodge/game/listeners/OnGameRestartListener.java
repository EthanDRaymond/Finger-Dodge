package com.edr.fingerdodge.game.listeners;

import com.edr.fingerdodge.game.Game;

/**
 * This is a listener used for when the game is paused and un-paused.
 *
 * @author Ethan Raymond
 */
public abstract class OnGameRestartListener {

    /**
     * Call this when the game is restarted.
     *
     * @param game the game that is restarted
     */
    public void restartGame(Game game) {
        onGameRestarted(game);
    }

    /**
     * This is called whenever the game is restarted.
     *
     * @param game the game that is restarted
     */
    public abstract void onGameRestarted(Game game);

}
