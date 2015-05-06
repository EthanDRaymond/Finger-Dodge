package com.edr.fingerdodge.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edr.fingerdodge.game.Game;

/**
 * @author Ethan Raymond
 */
public class ScoreView extends RelativeLayout {

    private TextView clockView;
    private Game game;

    public ScoreView(Context context){
        super(context);
        init();
    }

    public ScoreView(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    public ScoreView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
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
            clockView.setText("Score: 0");
        } else if (game.getGameState() == Game.STATE_PRE_GAME) {
            clockView.setText("Score: 0");
        } else {
            clockView.setText(getTimeString(game));
        }
        invalidate();
    }

    public void setGame(Game game){
        this.game = game;
    }

    private String getTimeString(Game game){
        String output = "Score: ";
        output += (int) (game.getScore() * 100);
        return output;
    }

    public String getScore(){
        return getTimeString(game);
    }

}
