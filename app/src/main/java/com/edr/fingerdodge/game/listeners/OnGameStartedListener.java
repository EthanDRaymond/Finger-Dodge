package com.edr.fingerdodge.game.listeners;

/**
 * This is the listener used when a game starts.
 *
 * @author Ethan Raymond
 * @version 1.0
 */
public abstract class OnGameStartedListener {

    /**
     * Call when the game starts.
     *
     * @param startTime the time when the game starts
     */
    //public void startGame(long startTime) {
    //    onGameStarted(startTime);
    //}

    /**
     * Call when the game starts. The timestamp sent through is the current time when this method
     * is called.
     */
    public void startGame() {
        onGameStarted(System.currentTimeMillis());
    }

    /**
     * Called when the game is started.
     *
     * @param startTime the time when the game starts
     */
    public abstract void onGameStarted(long startTime);

}
