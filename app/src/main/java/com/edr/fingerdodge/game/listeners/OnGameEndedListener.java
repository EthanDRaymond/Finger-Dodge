package com.edr.fingerdodge.game.listeners;

/**
 * This is the listener used when a game ends.
 * @author  Ethan Raymond
 */
public abstract class OnGameEndedListener {

    /**
     * Call when the game ends.
     * @param message   the exit message
     * @param endTime   the time when the game ended
     */
    public void endGame(String message, long endTime){
        onGameEnded(message, endTime);
    }

    /**
     * Called when the game ends
     * @param message   the exit message
     * @param endTime   the time when the game ended
     */
    public abstract void onGameEnded(String message, long endTime);

}
