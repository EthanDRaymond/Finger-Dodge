package com.edr.fingerdodge.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edr.fingerdodge.game.Game;
import com.edr.fingerdodge.util.Files;

import java.io.File;

/**
 * This view displays the high score at the top right of the screen.
 * @author Ethan Raymond
 */
public class HighScoreView extends RelativeLayout {

    private TextView clockView;
    private Game game;
    private float highScore;

    public HighScoreView(Context context){
        super(context);
        init();
    }

    public HighScoreView(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    public HighScoreView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        clockView = new TextView(getContext());
        clockView.setPadding(10, 10, 10, 10);
        clockView.setText("00:00");
        clockView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        clockView.setTextColor(Color.WHITE);
        addView(clockView);
        setBackgroundColor(Color.parseColor("#55222222"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (game == null){
            clockView.setText("High Score: 0");
        } else if (game.getGameState() == Game.STATE_PRE_GAME) {
            clockView.setText(getTimeString(game));
        } else {
            clockView.setText(getTimeString(game));
        }
        invalidate();
    }

    public void setGame(Game game){
        this.game = game;
    }

    private String getTimeString(Game game){
        String output = "High Score: ";
        output += (int) (game.getHighScore() * 100);
        return output;
    }

}
