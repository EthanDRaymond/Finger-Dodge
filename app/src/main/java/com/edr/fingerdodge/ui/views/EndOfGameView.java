package com.edr.fingerdodge.ui.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edr.fingerdodge.R;
import com.edr.fingerdodge.game.Game;

/**
 * This view appears when a game is complete. It displays the score from the game and a button
 * to restart the game.
 *
 * @author Ethan Raymond
 */
public class EndOfGameView extends LinearLayout {

    private TextView displayView, scoreView;
    private Button restartGameButton;
    private RelativeLayout.LayoutParams layoutParams;

    public EndOfGameView(Context context) {
        super(context);
        init();
    }

    public EndOfGameView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public EndOfGameView(Context context, AttributeSet attributeSet, int defStyleInt) {
        super(context, attributeSet, defStyleInt);
        init();
    }

    private void init() {
        displayView = new TextView(getContext());
        displayView.setText(getResources().getText(R.string.misc_you_lost));
        displayView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        displayView.setGravity(Gravity.CENTER);
        displayView.setPadding(20, 20, 20, 20);
        scoreView = new TextView(getContext());
        scoreView.setTypeface(Typeface.SERIF);
        scoreView.setText("24.65");
        scoreView.setTextSize(18);
        scoreView.setTextColor(Color.BLACK);
        scoreView.setGravity(Gravity.CENTER);
        restartGameButton = new Button(getContext());
        restartGameButton.setText(getResources().getText(R.string.misc_play_again));
        restartGameButton.setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        restartGameButton.setPadding(20, 20, 20, 20);
        restartGameButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.INVISIBLE);
            }
        });
        layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(20, 20, 20, 20);
        setLayoutParams(layoutParams);
        setBackgroundResource(R.drawable.bg_endofgameview);
        setOrientation(VERTICAL);
        addView(displayView);
        addView(scoreView);
        addView(restartGameButton);
    }

    public void setMessage(String message) {
        this.displayView.setText(message);
    }

    public TextView getScoreView() {
        return scoreView;
    }

    public Button getRestartGameButton() {
        return restartGameButton;
    }
}
