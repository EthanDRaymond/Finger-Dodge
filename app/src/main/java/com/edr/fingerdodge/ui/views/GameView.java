package com.edr.fingerdodge.ui.views;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.edr.fingerdodge.game.Game;
import com.edr.fingerdodge.math.geo.Point;
import com.edr.fingerdodge.math.geo.Rectangle;

import java.util.ArrayList;

/**
 * This view displays the visuals of the game and handles any touch information to interact with
 * the game.
 *
 * @author Ethan Raymond
 */
public class GameView extends View implements View.OnTouchListener {

    private static final float SHADOW_OFFSET = 10.0f;
    //private static final float BG_VELOCITY_1 = 10.0f;
    //private static final float BG_VELOCITY_2 = 10.0f;
    //private static final float BG_ACCELERATION_1 = 1.25f;
    //private static final float BG_ACCELERATION_2 = 1.25f;
    //private static float BG_CIRCLE1_RADIUS = 150.0f;
    //private static float BG_CIRCLE2_RADIUS = 400.0f;
    private static float RECTANGLE_PIN_SIZE = 5.0f;

    private Game game;

    private Paint rectanglePaint1;
    private Paint rectanglePaint2;
    private Paint shadowPaint;
    private Paint fingerPaint;
    private Paint bg1Paint;
    private Paint bg2Paint;
    private Paint messagePaint;

    //private long lastTouchEventTime = 0;

    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnTouchListener(this);
        game = new Game(this);
        game.getFinger().getCenter().x = getWidth() / 2.0f;
        game.getFinger().getCenter().y = getHeight() / 2.0f;
        rectanglePaint1 = new Paint();
        rectanglePaint1.setColor(Color.DKGRAY);
        rectanglePaint1.setAntiAlias(true);
        rectanglePaint2 = new Paint();
        rectanglePaint2.setColor(Color.GRAY);
        rectanglePaint2.setAntiAlias(true);
        shadowPaint = new Paint();
        shadowPaint.setColor(Color.BLACK);
        shadowPaint.setAlpha(90);
        shadowPaint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));
        fingerPaint = new Paint();
        fingerPaint.setColor(Color.RED);
        fingerPaint.setAntiAlias(true);
        bg1Paint = new Paint();
        bg1Paint.setColor(Color.WHITE);
        bg1Paint.setTextSize(20);
        bg2Paint = new Paint();
        bg2Paint.setColor(Color.BLACK);
        bg2Paint.setAlpha(32);
        messagePaint = new Paint();
        messagePaint.setColor(Color.DKGRAY);
        messagePaint.setAntiAlias(true);
        messagePaint.setTextSize(35);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int gameState = game.getGameState();
        if (gameState == Game.STATE_PRE_GAME) {
            onDrawFinger(canvas);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (gameState == Game.STATE_PLAYING) {
            onDrawRectangles(canvas);
            onDrawFinger(canvas);
        } else if (gameState == Game.STATE_PAUSED) {
            onDrawRectangles(canvas);
            onDrawFinger(canvas);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (gameState == Game.STATE_END) {
            onDrawRectangles(canvas);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        invalidate();
    }

    private void onDrawRectangles(Canvas canvas) {
        ArrayList<Rectangle> rectangles = game.getRectangles();
        for (int i = 0; i < rectangles.size(); i++) {
            Rectangle rectangle = rectangles.get(i);
            float top = rectangle.top;
            float bottom = rectangle.bottom;
            float left = rectangle.left;
            float right = rectangle.right;
            if (bottom > 0) {
                onDrawRectangle(canvas, top + 2, bottom - 2, left + 2, right - 2);
            }
        }
    }

    private void onDrawRectangle(Canvas canvas, float top, float bottom, float left, float right) {
        canvas.drawRect(left + SHADOW_OFFSET, top + SHADOW_OFFSET, right + SHADOW_OFFSET, bottom + SHADOW_OFFSET, shadowPaint);
        canvas.drawRect(left, top, right, bottom, rectanglePaint2);
        canvas.drawRect(left + RECTANGLE_PIN_SIZE, top + RECTANGLE_PIN_SIZE,
                right - RECTANGLE_PIN_SIZE, bottom - RECTANGLE_PIN_SIZE, rectanglePaint1);
        canvas.drawCircle(left + 2.5f * RECTANGLE_PIN_SIZE, top + 2.5f * RECTANGLE_PIN_SIZE,
                RECTANGLE_PIN_SIZE / 2.0f, rectanglePaint2);
        canvas.drawCircle(right - 2.5f * RECTANGLE_PIN_SIZE, top + 2.5f * RECTANGLE_PIN_SIZE,
                RECTANGLE_PIN_SIZE / 2.0f, rectanglePaint2);
        canvas.drawCircle(left + 2.5f * RECTANGLE_PIN_SIZE, bottom - 2.5f * RECTANGLE_PIN_SIZE,
                RECTANGLE_PIN_SIZE / 2.0f, rectanglePaint2);
        canvas.drawCircle(right - 2.5f * RECTANGLE_PIN_SIZE, bottom - 2.5f * RECTANGLE_PIN_SIZE,
                RECTANGLE_PIN_SIZE / 2.0f, rectanglePaint2);
    }

    private void onDrawFinger(Canvas canvas) {
        Point center = game.getFinger().getCenter();
        float radius = game.getFinger().getRadius();
        canvas.drawCircle(center.x, center.y, radius, fingerPaint);
    }

    /*
    private void onDrawBackground(Canvas canvas){
        long time;
        if (game.getGameState() == Game.STATE_PLAYING){
            time = System.currentTimeMillis() - game.getStartTime();
        } else {
            time = 0;
        }
        float yBase = getBackground1Position(time) % (BG_CIRCLE1_RADIUS * 4);
        for (int i = -10; i <= 10; i++){
            float x, y;
            if (i % 2 == 0){
                x = 0;
            } else {
                x = getWidth();
            }
            y = BG_CIRCLE1_RADIUS * 2 * i + yBase;
            canvas.drawCircle(x, y, BG_CIRCLE1_RADIUS, bg1Paint);
        }
    }
    */

    //private void printRenderTime() {
    //    Log.i("RENDER TIME", Long.toString(System.currentTimeMillis()));
    //}

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        game.getRectangles().clear();
        game.getFinger().getCenter().x = w / 2.0f;
        game.getFinger().getCenter().y = h / 2.0f;
        game.getFinger().radius = w / 28.0f;
        Game.VELOCITY_START = h / 2.5f;
        Game.ACCELERATION = Game.VELOCITY_START / 8.0f;
        Game.RECTANGLE_SEPARATION_MIN = getHeight() / 28.0f;
        Game.RECTANGLE_SEPARATION_MAX = getHeight() / 14.0f;
        Game.RECTANGLE_LENGTH_MIN = getHeight() / 7.0f;
        Game.RECTANGLE_LENGTH_MAX = getHeight();
        if (w < h) {
            //BG_CIRCLE1_RADIUS = h / 6.0f;
            //BG_CIRCLE2_RADIUS = h / 3.0f;
            RECTANGLE_PIN_SIZE = h / 225.f;
            shadowPaint.setMaskFilter(new BlurMaskFilter(h / 175.0f, BlurMaskFilter.Blur.NORMAL));
        } else {
            //BG_CIRCLE1_RADIUS = w / 6.0f;
            //BG_CIRCLE2_RADIUS = w / 3.0f;
            RECTANGLE_PIN_SIZE = w / 225.f;
            shadowPaint.setMaskFilter(new BlurMaskFilter(w / 175.0f, BlurMaskFilter.Blur.NORMAL));
        }

    }


    /**
     * This handles all of the touch input from the user. The algorithm in this method is a follows:
     * <ul>
     * <li>If the game has not started yet...</li>
     * <ul>
     * <li>If the user is placing their finger on the screen...</li>
     * <ul>
     * <li>If the finger is touching the dot...</li>
     * <ul>
     * <li>The game is started.</li>
     * <li>The dot is moved where the user's finger is.</li>
     * </ul>
     * </ul>
     * </ul>
     * <li>If the game is currently playing...</li>
     * <ul>
     * <li>If the user is moving their finger across the screen...</li>
     * <ul>
     * <li>The dot is moved where the user's finger is.</li>
     * </ul>
     * <li>If the user lifts their finger...</li>
     * <ul>
     * <li>The game is paused</li>
     * </ul>
     * </ul>
     * <li>If the game is paused...</li>
     * <ul>
     * <li>If the user is placing their finger on the screen...</li>
     * <ul>
     * <li>If the finger is touching the dot...</li>
     * <ul>
     * <li>The game is resumed.</li>
     * <li>The dot is moved where the user's finger is.</li>
     * </ul>
     * </ul>
     * </ul>
     * </ul>
     *
     * @param v     the view that is being touched
     * @param event the motion event
     * @return true if the event was handled successfully, false if it was not
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        int gameState = game.getGameState();
        float fingerRadius = game.getFinger().getRadius();
        if (gameState == Game.STATE_PRE_GAME) {
            if (action == MotionEvent.ACTION_DOWN) {
                float x = event.getX();
                float y = event.getY();
                if (x < getWidth() / 2.0f + fingerRadius && x > getWidth() / 2.0f - fingerRadius
                        && y < getHeight() / 2.0f + fingerRadius && y > getHeight() / 2.0f - fingerRadius) {
                    float distance = (float) Math.sqrt(Math.pow(x - getWidth() / 2.0f, 2) + Math.pow(y - getHeight() / 2.0f, 2));
                    if (distance < fingerRadius) {
                        game.getFinger().getCenter().x = x;
                        game.getFinger().getCenter().y = y /*- 2 * fingerRadius*/;
                        game.startGame();
                    }
                }
            }
        } else if (gameState == Game.STATE_PLAYING) {
            if (action == MotionEvent.ACTION_MOVE) {
                float x = event.getX();
                float y = event.getY();
                /*float oldX = game.getFinger().getCenter().x;
                float oldY = game.getFinger().getCenter().y;
                double distance = Math.sqrt(Math.pow(x - oldX, 2) + Math.pow(y - oldY, 2));
                if (radius > 350){
                    game.endGame(Game.EXIT_LIFTED_FINGER);
                    return true;
                }*/
                game.getFinger().getCenter().x = x;
                game.getFinger().getCenter().y = y /*- 2 * fingerRadius*/;
            } else if (action == MotionEvent.ACTION_UP) {
                game.pauseGame();
            } else if (action == MotionEvent.ACTION_POINTER_DOWN || action == MotionEvent.ACTION_POINTER_UP) {
                game.pauseGame();
            }
        } else if (gameState == Game.STATE_PAUSED) {
            if (action == MotionEvent.ACTION_DOWN) {
                float x = event.getX();
                float y = event.getY();
                float fingerX = game.getFinger().getCenter().x;
                float fingerY = game.getFinger().getCenter().y;
                if (x < fingerX + fingerRadius && x > fingerX - fingerRadius
                        && y < fingerY + fingerRadius && y > fingerY - fingerRadius) {
                    float distance = (float) Math.sqrt(Math.pow(x - fingerX, 2) + Math.pow(y - fingerY, 2));
                    if (distance < fingerRadius) {
                        game.getFinger().getCenter().x = x;
                        game.getFinger().getCenter().y = y /*- 2 * fingerRadius*/;
                        game.startGame();
                    }
                }
            }
        }
        return true;
    }

    /**
     * This sets the game this view will be displaying.
     */
    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * This gets the game this view is displaying
     */
    public Game getGame() {
        return game;
    }

}
