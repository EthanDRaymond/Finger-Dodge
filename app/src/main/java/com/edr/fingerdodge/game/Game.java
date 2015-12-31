package com.edr.fingerdodge.game;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import com.edr.fingerdodge.game.listeners.OnGameEndedListener;
import com.edr.fingerdodge.game.listeners.OnGamePausedListener;
import com.edr.fingerdodge.game.listeners.OnGameRestartListener;
import com.edr.fingerdodge.game.listeners.OnGameStartedListener;
import com.edr.fingerdodge.math.geo.Circle;
import com.edr.fingerdodge.math.geo.Point;
import com.edr.fingerdodge.math.geo.Rectangle;
import com.edr.fingerdodge.util.Files;

import java.util.ArrayList;
import java.util.Random;

/**
 * This is one game.
 *
 * @author Ethan Raymond
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
    private static final int COLUMN_COUNT = 5;
    private static final String EXIT_COLLISION = "You Lost!";
    public static final String EXIT_LIFTED_FINGER = "You lifted your finger!";

    private ArrayList<Rectangle> rectangles;
    private Circle finger;
    private View gameView;

    private int gameState;
    private long startTime;
    //private long endTime;
    private float highScore;
    private long score;
    private ArrayList<OnGameStartedListener> onGameStartedListeners;
    private ArrayList<OnGamePausedListener> onGamePausedListeners;
    private ArrayList<OnGameEndedListener> onGameEndedListeners;
    private ArrayList<OnGameRestartListener> onGameRestartListeners;

    private SharedPreferences settings;

    /**
     * Creates a new game. The game is by default in the "pre-game" state.
     *
     * @param view the GameView that this game is being displayed on
     */
    public Game(View view) {
        this.gameView = view;
        this.rectangles = new ArrayList<Rectangle>();
        this.finger = new Circle(new Point(50, 50), 25);
        this.gameState = STATE_PRE_GAME;
        this.startTime = 0;
        //this.endTime = 0;
        this.highScore = -1;
        this.score = 0;
        this.onGameStartedListeners = new ArrayList<OnGameStartedListener>();
        this.onGamePausedListeners = new ArrayList<OnGamePausedListener>();
        this.onGameEndedListeners = new ArrayList<OnGameEndedListener>();
        this.onGameRestartListeners = new ArrayList<OnGameRestartListener>();
        handleRectangleCount();
    }

    /**
     * Updates the game.
     *
     * @param elapsedTime the amount of time that has passed since the last update
     */
    private void update(float elapsedTime) {
        if (gameState == STATE_PLAYING) {
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
    private void handleCollisions() {
        for (int i = 0; i < rectangles.size(); i++) {
            Rectangle rectangle = rectangles.get(i);
            if (isColliding(finger, rectangle)) {
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
    private void handleRectangleCount() {
        destoryOldRectangles();
        addNewRectangles();
    }

    /**
     * This looks at each rectangle to see if it has passed the bottom of the screen. If it has
     * passed the bottom of the screen, then it it destroyed and removed from the list.
     */
    private void destoryOldRectangles() {
        float bottom = gameView.getHeight();
        for (int i = 0; i < rectangles.size(); ) {
            Rectangle rectangle = rectangles.get(i);
            if (rectangle.top > bottom) {
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
    private void addNewRectangles() {
        if (rectangles.size() == 0) {
            Rectangle rectangle = new Rectangle(-500, -100, 0, gameView.getWidth() / COLUMN_COUNT);
            rectangles.add(rectangle);
        }
        float topLimit = -gameView.getHeight();
        Rectangle topRectangle = null;
        Random random = new Random();
        while ((topRectangle = getTopRectangle()).top > topLimit) {
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
     *
     * @param elapsedTime the amount of time that has passed since the last update
     */
    private void moveRectangles(float elapsedTime) {
        for (int i = 0; i < rectangles.size(); i++) {
            rectangles.get(i).shiftRectangle(0, getGameVelocity() * elapsedTime);
        }
    }

    /**
     * This gets rid of rectangles that would trap the user's finger and prevent them from being
     * forced into a loss.
     */
    private void removeTrapRectangles() {
        for (int i = 1; i < rectangles.size() - 1; ) {
            int lastRectangleColumn = getRectangleColumn(rectangles.get(i - 1));
            int thisRectangleColumn = getRectangleColumn(rectangles.get(i));
            if (thisRectangleColumn == 0) {
                if (lastRectangleColumn == 1) {
                    rectangles.remove(i);
                } else {
                    i++;
                }
            } else if (thisRectangleColumn == COLUMN_COUNT - 1) {
                if (lastRectangleColumn == COLUMN_COUNT - 2) {
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
    public void startGame() {
        Log.i("GAME", "Starting game.");
        this.gameState = STATE_PLAYING;
        this.startTime = System.currentTimeMillis();
        if (onGameStartedListeners.size() > 0) {
            for (int i = 0; i < onGameStartedListeners.size(); i++) {
                onGameStartedListeners.get(i).startGame();
            }
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (getGameState() == Game.STATE_PLAYING) {
                        update(.02f);
                        Thread.sleep(20);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * This pauses the game and halts any updating processes.
     */
    public void pauseGame() {
        Log.i("GAME", "Pausing game.");
        this.gameState = STATE_PAUSED;
        if (onGamePausedListeners.size() > 0) {
            for (int i = 0; i < onGamePausedListeners.size(); i++) {
                onGamePausedListeners.get(i).pauseGame(this);
            }
        }
    }

    /**
     * This ends the game.
     */
    public void endGame(String message) {
        Log.i("GAME", "Ending game.");
        this.gameState = STATE_END;
        // this.endTime = System.currentTimeMillis();
        if (getScore() > getHighScore()) {
            setHighScore(getScore());
        }
        if (onGameEndedListeners.size() > 0) {
            for (int i = 0; i < onGameEndedListeners.size(); i++) {
                onGameEndedListeners.get(i).endGame(message, System.currentTimeMillis());
            }
        }
    }

    /**
     * This resets the game to its initial state.
     */
    public void restartGame() {
        this.gameState = STATE_PRE_GAME;
        this.startTime = -1;
        // this.endTime = -1;
        this.finger.getCenter().x = gameView.getWidth() / 2.0f;
        this.finger.getCenter().y = gameView.getHeight() / 2.0f;
        this.rectangles.clear();
        this.score = 0;
        if (onGameRestartListeners.size() > 0) {
            for (int i = 0; i < onGameRestartListeners.size(); i++) {
                onGameRestartListeners.get(i).restartGame(this);
            }
        }
    }

    /**
     * This checks to see if the finger has collided with a given rectangle. There are three steps taken to determine whether the objects are colliding. This is to minimise computing power required.
     * <p/>
     * <ol>
     * <li>This checks the with both objects' radii to see if they are close enought to each
     * other to collide.</li>
     * <li>Gets AABBs from both of the objects and sees if they overlap.</li>
     * <li>Checks if the shapes do truly overlap.</li>
     * </ol>
     *
     * @param circle    the finger shape
     * @param rectangle the rectangle shape
     * @return true if the shapes overlap, false if they do not
     */
    private boolean isColliding(Circle circle, Rectangle rectangle) {
        float actualDistance = Point.getDistance(circle.getCenter(), rectangle.getCenterPoint());
        float minimumDistance = circle.getRadius() + rectangle.getRadius();
        if (actualDistance < minimumDistance) {
            Rectangle aabb1 = circle.getAABB();
            //Rectangle aabb2 = rectangle;
            if (Rectangle.isColliding(aabb1, rectangle)) {
                Point centerPoint = circle.getCenter();
                byte xSlot, ySlot;
                if (centerPoint.y > rectangle.bottom) {
                    ySlot = 1;
                } else if (centerPoint.y < rectangle.top) {
                    ySlot = -1;
                } else {
                    ySlot = 0;
                }
                if (centerPoint.x > rectangle.right) {
                    xSlot = 1;
                } else if (centerPoint.x < rectangle.left) {
                    xSlot = -1;
                } else {
                    xSlot = 0;
                }
                if (xSlot == 0 && ySlot == 0) {
                    return true;
                } else if (xSlot == 0 && ySlot != 0) {
                    float yMin = rectangle.top - circle.radius;
                    float yMax = rectangle.bottom + circle.radius;
                    return (circle.getCenter().y > yMin && circle.getCenter().y < yMax);
                } else if (xSlot != 0 && ySlot == 0) {
                    float xMin = rectangle.left - circle.radius;
                    float xMax = rectangle.right + circle.radius;
                    return (circle.getCenter().x > xMin && circle.getCenter().x < xMax);
                } else {
                    if (xSlot == -1 && ySlot == -1) {
                        Point corner = rectangle.getTopLeftCorner();
                        Point circleCenter = circle.getCenter();
                        return (Point.getDistance(corner, circleCenter) < circle.radius);
                    } else if (xSlot == -1 && ySlot == 1) {
                        Point corner = rectangle.getBottomLeftCorner();
                        Point circleCenter = circle.getCenter();
                        return  (Point.getDistance(corner, circleCenter) < circle.radius);
                    } else if (xSlot == 1 && ySlot == -1) {
                        Point corner = rectangle.getTopRightCorner();
                        Point circleCenter = circle.getCenter();
                        return (Point.getDistance(corner, circleCenter) < circle.radius);
                    } else {
                        Point corner = rectangle.getBottomRightCorner();
                        Point circleCenter = circle.getCenter();
                        return (Point.getDistance(corner, circleCenter) < circle.radius);
                    }
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /*
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
    */

    /**
     * Adds the given OnGameStartedListener to the list.
     *
     * @param onGameStartedListener the given OnGameStartedListener
     * @see OnGameStartedListener
     */
    public void registerOnGameStartedListener(OnGameStartedListener onGameStartedListener) {
        this.onGameStartedListeners.add(onGameStartedListener);
    }

    /**
     * Adds the given OnGamePausedListener to the list.
     *
     * @param onGamePausedListener the given OnGamePausedListener
     * @see OnGamePausedListener
     */
    public void registerOnGamePausedListener(OnGamePausedListener onGamePausedListener) {
        this.onGamePausedListeners.add(onGamePausedListener);
    }

    /**
     * Adds the given OnGameEndedListener to the list.
     *
     * @param onGameEndedListener the given OnGameEndedListener
     * @see OnGameEndedListener
     */
    public void registerOnGameEndedListener(OnGameEndedListener onGameEndedListener) {
        this.onGameEndedListeners.add(onGameEndedListener);
    }

    /**
     * Adds the given OnGameRestartListener to the list.
     *
     * @param onGameRestartListener the given OnGameRestartListener
     * @see OnGameRestartListener
     */
    public void registerOnGameRestartListener(OnGameRestartListener onGameRestartListener) {
        this.onGameRestartListeners.add(onGameRestartListener);
    }

    /**
     * Sets a new high score. The high score is updated in memory and in the key-value sets.
     *
     * @param highScore the new high score
     */
    private void setHighScore(float highScore) {
        this.highScore = highScore;
        settings.edit().putFloat(Files.KEY_SETTINGS_HIGHSCORE, highScore).apply();
    }

    /**
     * Sets a new settings file.
     *
     * @param settings
     */
    public void setSettingsFile(SharedPreferences settings) {
        this.settings = settings;
    }

    /**
     * Returns the highest up rectangle in the list.
     *
     * @return the highest rectangle
     */
    private Rectangle getTopRectangle() {
        Rectangle rectangle = rectangles.get(0);
        for (int i = 0; i < rectangles.size(); i++) {
            if (rectangles.get(i).top < rectangle.top) {
                rectangle = rectangles.get(i);
            }
        }
        return rectangle;
    }

    /**
     * Returns the column number of the given rectangle.
     *
     * @param rectangle the given rectangle
     * @return the column integer [0, infinity]
     */
    private int getRectangleColumn(Rectangle rectangle) {
        if (rectangle.left == 0.0f) {
            return 0;
        } else if (Math.abs(rectangle.left - gameView.getWidth() / COLUMN_COUNT) < 5) {
            return 1;
        } else if (Math.abs(rectangle.left - 2.0f * gameView.getWidth() / COLUMN_COUNT) < 5) {
            return 2;
        } else if (Math.abs(rectangle.left - 3.0f * gameView.getWidth() / COLUMN_COUNT) < 5) {
            return 3;
        } else {
            return 4;
        }
    }

    /**
     * Returns the velocity of the rectangles. This uses the formula:
     * ( Starting Velocity ) + ( ( Acceleration ) * ( Time Since Start of Game ))
     *
     * @return the velocity of the rectangles (pixels / second)
     */
    private float getGameVelocity() {
        return VELOCITY_START + ACCELERATION * ((System.currentTimeMillis() - startTime) / 1000.0f);
    }

    /**
     * Gets the game state integer
     *
     * @return the game state
     */
    public int getGameState() {
        return gameState;
    }

    /**
     * Gets the list of rectangles on the field.
     *
     * @return the rectangles
     */
    public ArrayList<Rectangle> getRectangles() {
        return rectangles;
    }

    /**
     * Get the circle shape of the the finger object.
     *
     * @return the finger shape
     */
    public Circle getFinger() {
        return finger;
    }

    /**
     * Returns the current score of the game.
     */
    public float getScore() {
        return (float) (score) / 1000.0f;
    }

    /**
     * Returns the player's high score.
     */
    public float getHighScore() {
        if (highScore == -1) {
            highScore = settings.getFloat(Files.KEY_SETTINGS_HIGHSCORE, 0.0f);
            setHighScore(highScore);
        }
        return highScore;
    }

}
