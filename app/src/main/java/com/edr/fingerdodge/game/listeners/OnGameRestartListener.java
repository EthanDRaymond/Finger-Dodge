package com.edr.fingerdodge.game.listeners;

import com.edr.fingerdodge.game.Game;

/**
 * Created by ethanraymond on 2/3/15.
 */
public abstract class OnGameRestartListener {

    public void restartGame(Game game){
        onGameRestarted(game);
    }

    public abstract void onGameRestarted(Game game);

}
