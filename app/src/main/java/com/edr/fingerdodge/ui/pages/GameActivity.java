package com.edr.fingerdodge.ui.pages;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edr.fingerdodge.R;
import com.edr.fingerdodge.game.Game;
import com.edr.fingerdodge.game.listeners.OnGameEndedListener;
import com.edr.fingerdodge.game.listeners.OnGamePausedListener;
import com.edr.fingerdodge.game.listeners.OnGameRestartListener;
import com.edr.fingerdodge.game.listeners.OnGameStartedListener;
import com.edr.fingerdodge.services.StatisticsService;
import com.edr.fingerdodge.ui.views.EndOfGameView;
import com.edr.fingerdodge.ui.views.GameView;
import com.edr.fingerdodge.ui.views.HighScoreView;
import com.edr.fingerdodge.ui.views.InfoIconView;
import com.edr.fingerdodge.ui.views.ScoreView;
import com.edr.fingerdodge.util.Files;

/**
 * @author Ethan Raymond
 */
public class GameActivity extends ActionBarActivity {

    private RelativeLayout mainLayout;
    private GameView gameView;
    private ScoreView timeView;
    private HighScoreView highScoreView;
    private EndOfGameView endOfGameView;
    private InfoIconView infoIconView;
    private RelativeLayout preGameMessageView;
    private TextView gameMessageView;

    private Game game;

    private StatisticsService statisticsService;
    private ServiceConnection statisticsServiceConnection;
    private boolean isBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        preGameMessageView = (RelativeLayout) findViewById(R.id.preGameMessageView);
        gameMessageView = (TextView) findViewById(R.id.gameMessageView);
        mainLayout.removeView(endOfGameView);
        game = new Game(gameView);
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
                gameMessageView.setText("Paused.");
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
                gameMessageView.setText("Touch the Circle");
            }
        });
        gameView.setGame(game);
        timeView.setGame(game);
        highScoreView.setGame(game);
        statisticsServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                // We've bound to LocalService, cast the IBinder and get LocalService instance
                StatisticsService.LocalBinder binder = (StatisticsService.LocalBinder) service;
                statisticsService = binder.getService();
                isBound = true;
            }
            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                isBound = false;
            }
        };
        isBound = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, StatisticsService.class);
        bindService(intent, statisticsServiceConnection, Context.BIND_AUTO_CREATE);
        game.registerOnGameEndedListener(new OnGameEndedListener() {
            @Override
            public void onGameEnded(String message, long endTime) {
                if (isBound){
                    long startTime = endTime - (long) game.getScore();
                    statisticsService.addStatisticGameplay(startTime, endTime);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (game.getGameState() == Game.STATE_PLAYING) {
            gameView.getGame().endGame(Game.EXIT_LIFTED_FINGER);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(statisticsServiceConnection);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
