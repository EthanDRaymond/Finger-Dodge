package com.edr.fingerdodge.game.listeners;

import com.edr.fingerdodge.game.Game;

/**
 * Created by ethanraymond on 3/22/15.
 */
public abstract class OnGamePausedListener {

    public void pauseGame(Game game){
        onGamePaused(game);
    }

    public void unPauseGame(Game game) {
        onGameUnPaused(game);
    }

    public abstract void onGamePaused(Game game);

    public abstract void onGameUnPaused(Game game);

}
