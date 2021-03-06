package com.edr.fingerdodge.ui.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edr.fingerdodge.R;
import com.edr.fingerdodge.game.Game;
import com.edr.fingerdodge.game.listeners.OnGameEndedListener;
import com.edr.fingerdodge.game.listeners.OnGamePausedListener;
import com.edr.fingerdodge.game.listeners.OnGameRestartListener;
import com.edr.fingerdodge.game.listeners.OnGameStartedListener;
import com.edr.fingerdodge.stat.GameStatistic;
import com.edr.fingerdodge.ui.views.EndOfGameView;
import com.edr.fingerdodge.ui.views.GameView;
import com.edr.fingerdodge.ui.views.HighScoreView;
import com.edr.fingerdodge.ui.views.InfoIconView;
import com.edr.fingerdodge.ui.views.ScoreView;
import com.edr.fingerdodge.util.Files;
import com.edr.fingerdodge.util.Version;

/**
 * @author Ethan Raymond
 */
public class GameActivity extends StatisticsTrackingActivity {

    private RelativeLayout mainLayout;
    private GameView gameView;
    private ScoreView timeView;
    private HighScoreView highScoreView;
    private EndOfGameView endOfGameView;
    private InfoIconView infoIconView;
    private RelativeLayout settingsIconView;
    private RelativeLayout preGameMessageView;
    private TextView gameMessageView;

    private Game game;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        gameView = (GameView) findViewById(R.id.gameView);
        timeView = (ScoreView) findViewById(R.id.timeView);
        highScoreView = (HighScoreView) findViewById(R.id.highScoreView);
        mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
        endOfGameView = (EndOfGameView) findViewById(R.id.endOfGameView);
        endOfGameView.getRestartGameButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.restartGame();
            }
        });
        infoIconView = (InfoIconView) findViewById(R.id.infoView);
        infoIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), InfoActivity.class);
                startActivity(i);
            }
        });
        settingsIconView = (RelativeLayout) findViewById(R.id.settingsView);
        settingsIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), SettingsActivity.class);
                startActivity(i);
            }
        });
        preGameMessageView = (RelativeLayout) findViewById(R.id.preGameMessageView);
        gameMessageView = (TextView) findViewById(R.id.gameMessageView);
        mainLayout.removeView(endOfGameView);
        game = new Game(gameView);
        //noinspection deprecation
        game.setSettingsFile(getSharedPreferences(Files.FILE_BASIC, MODE_MULTI_PROCESS));
        game.registerOnGameStartedListener(new OnGameStartedListener() {
            @Override
            public void onGameStarted(long startTime) {
                mainLayout.removeView(endOfGameView);
                mainLayout.removeView(preGameMessageView);
            }
        });
        game.registerOnGamePausedListener(new OnGamePausedListener() {
            @Override
            public void onGamePaused(Game game) {
                mainLayout.addView(preGameMessageView);
                gameMessageView.setText(getText(R.string.misc_paused));
            }

            @Override
            public void onGameUnPaused(Game game) {
                mainLayout.removeView(preGameMessageView);
            }
        });
        game.registerOnGameEndedListener(new OnGameEndedListener() {
            @Override
            public void onGameEnded(String message, long endTime) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainLayout.addView(endOfGameView);
                    }
                });
                endOfGameView.setMessage(message);
                endOfGameView.getScoreView().setText(timeView.getScore());
            }
        });
        game.registerOnGameRestartListener(new OnGameRestartListener() {
            @Override
            public void onGameRestarted(Game game) {
                mainLayout.removeView(endOfGameView);
                mainLayout.addView(preGameMessageView);
                gameMessageView.setText(getBaseContext().getResources().getText(R.string.game_touch_my_circle));
            }
        });
        gameView.setGame(game);
        timeView.setGame(game);
        highScoreView.setGame(game);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (game.getGameState() == Game.STATE_PLAYING) {
            gameView.getGame().endGame(Game.EXIT_LIFTED_FINGER);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        game.registerOnGameEndedListener(new OnGameEndedListener() {
            @Override
            public void onGameEnded(String message, long endTime) {
                if (isBound()) {
                    boolean beatHighScore;
                    beatHighScore = game.getScore() >= game.getHighScore();
                    statisticsService.addNewStatistic(
                            new GameStatistic(
                                    GameStatistic.TYPE,
                                    getStatisticsService().getID(),
                                    System.currentTimeMillis(),
                                    Version.API_CODE,
                                    (long) (game.getScore() * 1000),
                                    beatHighScore));
                }
            }
        });
    }

}
