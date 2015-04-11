package com.edr.fingerdodge.game;

import android.content.SharedPreferences;
import android.util.Xml;
import android.view.View;

import com.edr.fingerdodge.game.listeners.OnGameEndedListener;
import com.edr.fingerdodge.game.listeners.OnGamePausedListener;
import com.edr.fingerdodge.game.listeners.OnGameRestartListener;
import com.edr.fingerdodge.game.listeners.OnGameStartedListener;
import com.edr.fingerdodge.math.geo.Circle;
import com.edr.fingerdodge.math.geo.Point;
import com.edr.fingerdodge.math.geo.Rectangle;
import com.edr.fingerdodge.util.Files;
import com.edr.fingerdodge.util.XML;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * This is one game.
 */
public class Game {

    public static final int STATE_PRE_GAME = 0;
    public static final int STATE_PLAYING = 1;
    public static final int STATE_PAUSED = 2;
    public static final int STATE_END = 3;
    public static float VELOCITY_START = 200.0f;
    public static float ACCELERATION = 18.0f;
    public static float RECTANGLE_SEPARATION_MIN = 25.0f;
    public static float RECTANGLE_SEPARATION_MAX = 50.0f;
    public static float RECTANGLE_LENGTH_MIN = 125;
    public static float RECTANGLE_LEGNTH_MAX = 700;
    public static final int COLUMN_COUNT = 5;
    public static final String EXIT_COLLISION = "You Lost!";
    public static final String EXIT_LIFTED_FINGER = "You lifted your finger!";

    private ArrayList<Rectangle> rectangles;
    private Circle finger;
    private View gameView;

    private int gameState;
    private long startTime;
    private long endTime;
    private float highScore;
    private long score;
    private OnGameStartedListener onGameStartedListener;
    private OnGamePausedListener onGamePausedListener;
    private OnGameEndedListener onGameEndedListener;
    private OnGameRestartListener onGameRestartListener;

    private SharedPreferences settings;

    /**
     * Creates a new game. The game is by default in the "pre-game" state.
     * @param view  the GameView that this game is being displayed on
     */
    public Game(View view){
        this.gameView = view;
        this.rectangles = new ArrayList<Rectangle>();
        this.finger = new Circle(new Point(50, 50), 25);
        this.gameState = STATE_PRE_GAME;
        this.startTime = 0;
        this.endTime = 0;
        this.highScore = -1;
        this.score = 0;
        handleRectangleCount();
    }

    /**
     * Updates the game.
     * @param elapsedTime   the amount of time that has passed since the last update
     */
    public void update(float elapsedTime){
        if (gameState == STATE_PLAYING){
            this.score += elapsedTime * 1000;
            handleRectangleCount();
            moveRectangles(elapsedTime);
            removeTrapRectangles();
            handleCollisions();
        }
    }

    /**
     * This checks for collisions between the finger and the rectangles. If there is a collision
     * then the game is ended.
     */
    public void handleCollisions(){
        for (int i = 0; i < rectangles.size(); i++){
            Rectangle rectangle = rectangles.get(i);
            if (isColliding(finger, rectangle)){
                endGame(EXIT_COLLISION);
                return;
            }
        }
    }

    /**
     * This adjusts the amount of rectangles that are stored in memory. First this removes any
     * rectangles that have passed the bottom of the screen, then this adds new rectangles above
     * the top of the screen.
     */
    public void handleRectangleCount(){
        destoryOldRectangles();
        addNewRectangles();
    }

    /**
     * This looks at each rectangle to see if it has passed the bottom of the screen. If it has
     * passed the bottom of the screen, then it it destroyed and removed from the list.
     */
    public void destoryOldRectangles(){
        float bottom = gameView.getHeight();
        for (int i = 0; i < rectangles.size();){
            Rectangle rectangle = rectangles.get(i);
            if (rectangle.top > bottom){
                rectangles.remove(i);
            } else {
                i++;
            }
        }
    }

    /**
     * This checks to see if the highest rectangle is above a certain line. This imaginary line is
     * exactly one screen height above the top of the screen. If the highest rectangle is below
     * this limit, then new rectangles are created until the highest rectangle is above the line.
     */
    public void addNewRectangles(){
        if (rectangles.size() == 0){
            Rectangle rectangle = new Rectangle(-500, -100, 0, gameView.getWidth() / COLUMN_COUNT);
            rectangles.add(rectangle);
        }
        float topLimit = -gameView.getHeight();
        Rectangle topRectangle = null;
        Random random = new Random();
        while ((topRectangle = getTopRectangle()).top > topLimit){
            float bottom = topRectangle.top + RECTANGLE_SEPARATION_MIN
                    + random.nextInt((int) (RECTANGLE_SEPARATION_MAX - RECTANGLE_SEPARATION_MIN));
            float top = bottom - (RECTANGLE_LENGTH_MIN
                    + random.nextInt((int) (RECTANGLE_LEGNTH_MAX - RECTANGLE_LENGTH_MIN)));
            int lastColumn = getRectangleColumn(topRectangle);
            int newColumn;
            do {
                if (lastColumn == 0) {
                    newColumn = random.nextInt(COLUMN_COUNT - 1) + 1;
                } else if (lastColumn == 1) {
                    newColumn = random.nextInt(COLUMN_COUNT - 1) + 2;
                } else if (lastColumn == COLUMN_COUNT) {
                    newColumn = random.nextInt(COLUMN_COUNT - 1);
                } else if (lastColumn == COLUMN_COUNT - 1) {
                    newColumn = random.nextInt(COLUMN_COUNT - 2);
                } else {
                    newColumn = random.nextInt(COLUMN_COUNT - 1);
                    if (newColumn == lastColumn) {
                        newColumn++;
                    }
                }
            } while (newColumn == lastColumn);
            float left = newColumn * (gameView.getWidth() / COLUMN_COUNT);
            float right = (newColumn + 1) * (gameView.getWidth() / COLUMN_COUNT);
            rectangles.add(new Rectangle(top, bottom, left, right));
        }
    }

    /**
     * This moves all of the rectangles based on the elapsed time.
     * @param elapsedTime   the amount of time that has passed since the last update
     */
    public void moveRectangles(float elapsedTime){
        for (int i = 0; i < rectangles.size(); i++){
            rectangles.get(i).shiftRectangle(0, getGameVelocity() * elapsedTime);
        }
    }

    /**
     * This gets rid of rectangles that would trap the user's finger and prevent them from being
     * forced into a loss.
     */
    public void removeTrapRectangles(){
        for (int i = 1; i < rectangles.size() - 1;){
            int lastRectangleColumn = getRectangleColumn(rectangles.get(i-1));
            int thisRectangleColumn = getRectangleColumn(rectangles.get(i));
            if (thisRectangleColumn == 0){
                if (lastRectangleColumn == 1){
                    rectangles.remove(i);
                } else {
                    i++;
                }
            } else if (thisRectangleColumn == COLUMN_COUNT - 1){
                if (lastRectangleColumn == COLUMN_COUNT - 2){
                    rectangles.remove(i);
                } else {
                    i++;
                }
            } else {
                i++;
            }
        }
    }

    /**
     * This begins the game. The game state is changed and the start timestamp is set.
     */
    public void startGame(){
        this.gameState = STATE_PLAYING;
        this.startTime = System.currentTimeMillis();
        if (onGameStartedListener != null){
            onGameStartedListener.startGame();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (getGameState() == Game.STATE_PLAYING){
                        update(.02f);
                        Thread.sleep(20);
                    }
                } catch (Exception e){
                }
            }
        }).start();
    }

    public void pauseGame(){
        this.gameState = STATE_PAUSED;
        if (onGamePausedListener != null){
            onGamePausedListener.pauseGame(this);
        }
    }

    public void unPauseGame(){
        if (onGamePausedListener != null){
            onGamePausedListener.unPauseGame(this);
        }
        startGame();
    }

    /**
     * This ends the game.
     */
    public void endGame(String message){
        this.gameState = STATE_END;
        this.endTime = System.currentTimeMillis();
        if (getScore() > getHighScore()){
            setHighScore(getScore());
        }
        if (onGameEndedListener != null){
            onGameEndedListener.endGame(message, System.currentTimeMillis());
        }
    }

    public void restartGame(){
        this.gameState = STATE_PRE_GAME;
        this.startTime = -1;
        this.endTime = -1;
        this.finger.getCenter().x = gameView.getWidth() / 2.0f;
        this.finger.getCenter().y = gameView.getHeight() / 2.0f;
        this.rectangles.clear();
        this.score = 0;
        if (onGameRestartListener != null){
            onGameRestartListener.restartGame(this);
        }
    }

    /**
     * This checks to see if the finger has collided with a given rectangle. There are three steps taken to determine whether the objects are colliding. This is to minimise computing power required.
     *
     * <ol>
     *     <li>This checks the with both objects' radii to see if they are close enought to each
     *          other to collide.</li>
     *     <li>Gets AABBs from both of the objects and sees if they overlap.</li>
     *     <li>Checks if the shapes do truly overlap.</li>
     * </ol>
     *
     * @param circle        the finger shape
     * @param rectangle     the rectangle shape
     * @return              true if the shapes overlap, false if they do not
     */
    public boolean isColliding(Circle circle, Rectangle rectangle){
        float actualDistance = Point.getDistance(circle.getCenter(), rectangle.getCenterPoint());
        float minimumDistance = circle.getRadius() + rectangle.getRadius();
        if (actualDistance < minimumDistance){
            Rectangle aabb1 = circle.getAABB();
            Rectangle aabb2 = rectangle;
            if (Rectangle.isColliding(aabb1, aabb2)){
                Point centerPoint = circle.getCenter();
                byte xSlot, ySlot;
                if (centerPoint.y > rectangle.bottom){
                    ySlot = 1;
                } else if (centerPoint.y < rectangle.top){
                    ySlot = -1;
                } else {
                    ySlot = 0;
                }
                if (centerPoint.x > rectangle.right){
                    xSlot = 1;
                } else if (centerPoint.x < rectangle.left){
                    xSlot = -1;
                } else {
                    xSlot = 0;
                }
                if (xSlot == 0 && ySlot == 0){
                    return true;
                } else if (xSlot == 0 && ySlot != 0){
                    float yMin = rectangle.top - circle.radius;
                    float yMax = rectangle.bottom + circle.radius;
                    if (circle.getCenter().y > yMin && circle.getCenter().y < yMax){
                        return true;
                    } else {
                        return false;
                    }
                } else if (xSlot != 0 && ySlot == 0){
                    float xMin = rectangle.left - circle.radius;
                    float xMax = rectangle.right + circle.radius;
                    if (circle.getCenter().x > xMin && circle.getCenter().x < xMax){
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    if (xSlot == -1 && ySlot == -1){
                        Point corner = rectangle.getTopLeftCorner();
                        Point circleCenter = circle.getCenter();
                        if (Point.getDistance(corner, circleCenter) < circle.radius){
                            return true;
                        } else {
                            return false;
                        }
                    } else if (xSlot == -1 && ySlot == 1){
                        Point corner = rectangle.getBottomLeftCorner();
                        Point circleCenter = circle.getCenter();
                        if (Point.getDistance(corner, circleCenter) < circle.radius){
                            return true;
                        } else {
                            return false;
                        }
                    } else if (xSlot == 1 && ySlot == -1){
                        Point corner = rectangle.getTopRightCorner();
                        Point circleCenter = circle.getCenter();
                        if (Point.getDistance(corner, circleCenter) < circle.radius){
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        Point corner = rectangle.getBottomRightCorner();
                        Point circleCenter = circle.getCenter();
                        if (Point.getDistance(corner, circleCenter) < circle.radius){
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void writeHighScoreToFile(float highScore){
        try {
            FileOutputStream outputStream = new FileOutputStream(
                    new File(gameView.getContext().getFilesDir(), Files.FILE_HIGHSCORE));
            outputStream.write(Float.toString(highScore).getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Assigns a new OnGameStartedListener.
     * @param onGameStartedListener     this listener
     * @see     com.edr.fingerdodge.game.listeners.OnGameStartedListener
     */
    public void setOnGameStartedListener(OnGameStartedListener onGameStartedListener) {
        this.onGameStartedListener = onGameStartedListener;
    }

    public void setOnGamePausedListener(OnGamePausedListener onGamePausedListener){
        this.onGamePausedListener = onGamePausedListener;
    }

    /**
     * Assigns a new OnGameStartedListener.
     * @param onGameEndedListener     this listener
     * @see     com.edr.fingerdodge.game.listeners.OnGameEndedListener
     */
    public void setOnGameEndedListener(OnGameEndedListener onGameEndedListener) {
        this.onGameEndedListener = onGameEndedListener;
    }

    public void setOnGameRestartListener(OnGameRestartListener onGameRestartListener){
        this.onGameRestartListener = onGameRestartListener;
    }

    public void setHighScore(float highScore){
        this.highScore = highScore;
        settings.edit().putFloat(Files.KEY_SETTINGS_HIGHSCORE, highScore).commit();
    }

    public void setSettingsFile(SharedPreferences settings){
        this.settings = settings;
    }

    /**
     * Returns the highest up rectangle in the list.
     * @return      the highest rectangle
     */
    private Rectangle getTopRectangle(){
        Rectangle rectangle = rectangles.get(0);
        for (int i = 0; i < rectangles.size(); i++){
            if (rectangles.get(i).top < rectangle.top){
                rectangle = rectangles.get(i);
            }
        }
        return rectangle;
    }

    /**
     * Returns the column number of the given rectangle.
     * @param rectangle     the given rectangle
     * @return              the column integer [0, infinity]
     */
    public int getRectangleColumn(Rectangle rectangle){
        if (rectangle.left == 0.0f){
            return 0;
        } else if (Math.abs(rectangle.left - gameView.getWidth() / COLUMN_COUNT) < 5){
            return 1;
        } else if (Math.abs(rectangle.left - 2.0f * gameView.getWidth() / COLUMN_COUNT) < 5){
            return 2;
        } else if (Math.abs(rectangle.left - 3.0f * gameView.getWidth() / COLUMN_COUNT) < 5){
            return 3;
        } else {
            return 4;
        }
    }

    /**
     * Returns the velocity of the rectangles. This uses the formula:
     * ( Starting Velocity ) + ( ( Acceleration ) * ( Time Since Start of Game ))
     * @return      the velocity of the rectangles (pixels / second)
     */
    public float getGameVelocity(){
        return VELOCITY_START + ACCELERATION * ((System.currentTimeMillis() - startTime) / 1000.0f);
    }

    /**
     * Gets the game state integer
     * @return  the game state
     */
    public int getGameState(){
        return gameState;
    }

    /**
     * Gets the list of rectangles on the field.
     * @return      the rectangles
     */
    public ArrayList<Rectangle> getRectangles(){
        return rectangles;
    }

    /**
     * Get the circle shape of the the finger object.
     * @return      the finger shape
     */
    public Circle getFinger(){
        return finger;
    }

    /**
     * Get the game view this game is being displayed on.
     * @return  the game view
     * @see     com.edr.fingerdodge.ui.views.GameView
     */
    public View getGameView() {
        return gameView;
    }

    /**
     * Gets the timestamp of when the game started.
     * @return  the timestamp when the game started
     */
    public long getStartTime() {
        return startTime;
    }

    public long getEndTime(){
        return endTime;
    }

    public float getScore(){
        return (float) (score) / 1000.0f;
    }

    public float getHighScore(){
        if (highScore == -1){
            highScore = settings.getFloat(Files.KEY_SETTINGS_HIGHSCORE, 0.0f);
            setHighScore(highScore);
        }
        return highScore;
    }

    /**
     * Gets the OnGameStartedListener.
     * @return  what do you think
     */
    public OnGameStartedListener getOnGameStartedListener() {
        return onGameStartedListener;
    }

    public OnGamePausedListener getOnGamePausedListener(){
        return onGamePausedListener;
    }

    /**
     * Gets the OnGameEndedListener.
     * @return  what do you think
     */
    public OnGameEndedListener getOnGameEndedListener() {
        return onGameEndedListener;
    }

    public OnGameRestartListener getOnGameRestartListener(){
        return onGameRestartListener;
    }

}
