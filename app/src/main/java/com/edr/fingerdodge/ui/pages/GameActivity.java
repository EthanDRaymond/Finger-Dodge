package com.edr.fingerdodge.ui.pages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edr.fingerdodge.R;
import com.edr.fingerdodge.game.Game;
import com.edr.fingerdodge.game.listeners.OnGameEndedListener;
import com.edr.fingerdodge.game.listeners.OnGamePausedListener;
import com.edr.fingerdodge.game.listeners.OnGameRestartListener;
import com.edr.fingerdodge.game.listeners.OnGameStartedListener;
import com.edr.fingerdodge.ui.views.EndOfGameView;
import com.edr.fingerdodge.ui.views.GameView;
import com.edr.fingerdodge.ui.views.HighScoreView;
import com.edr.fingerdodge.ui.views.InfoView;
import com.edr.fingerdodge.ui.views.ScoreView;
import com.edr.fingerdodge.util.Files;


public class GameActivity extends ActionBarActivity {

    private RelativeLayout mainLayout;
    private GameView gameView;
    private ScoreView timeView;
    private HighScoreView highScoreView;
    private EndOfGameView endOfGameView;
    private InfoView infoView;
    private RelativeLayout preGameMessageView;
    private TextView gameMessageView;

    private Game game;

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
        infoView = (InfoView) findViewById(R.id.infoView);
        infoView.setOnClickListener(new View.OnClickListener() {
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
        game.setOnGameStartedListener(new OnGameStartedListener() {
            @Override
            public void onGameStarted(long startTime) {
                mainLayout.removeView(endOfGameView);
                mainLayout.removeView(preGameMessageView);
            }
        });
        game.setOnGamePausedListener(new OnGamePausedListener() {
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
        game.setOnGameEndedListener(new OnGameEndedListener() {
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
        game.setOnGameRestartListener(new OnGameRestartListener() {
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
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
