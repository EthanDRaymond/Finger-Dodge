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
    public static final String EXIT_LIFTED_FINGER = "You lifted your finger!";
    private static final int COLUMN_COUNT = 5;
    private static final String EXIT_COLLISION = "You Lost!";
    public static float VELOCITY_START = 200.0f;
    public static float ACCELERATION = 18.0f;
    public static float RECTANGLE_SEPARATION_MIN = 25.0f;
    public static float RECTANGLE_SEPARATION_MAX = 50.0f;
    public static float RECTANGLE_LENGTH_MIN = 125;
    public static float RECTANGLE_LENGTH_MAX = 700;
    private ArrayList<Rectangle> rectangles;
    private Circle finger;
    private View gameView;

    private int gameState;
    private long startTime;
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
        this.highScore = -1;
        this.score = 0;
        this.onGameStartedListeners = new ArrayList<OnGameStartedListener>();
        this.onGamePausedListeners = new ArrayList<OnGamePausedListener>();
        this.onGameEndedListeners = new ArrayList<OnGameEndedListener>();
        this.onGameRestartListeners = new ArrayList<OnGameRestartListener>();
        handleRectangleCount();
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
        Rectangle topRectangle;
        Random random = new Random();
        while ((topRectangle = getTopRectangle()).getTop() > topLimit) {
            float bottom = topRectangle.getTop() + RECTANGLE_SEPARATION_MIN
                    + random.nextInt((int) (RECTANGLE_SEPARATION_MAX - RECTANGLE_SEPARATION_MIN));
            float top = bottom - (RECTANGLE_LENGTH_MIN
                    + random.nextInt((int) (RECTANGLE_LENGTH_MAX - RECTANGLE_LENGTH_MIN)));
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
     * This looks at each rectangle to see if it has passed the bottom of the screen. If it has
     * passed the bottom of the screen, then it it destroyed and removed from the list.
     */
    private void destroyOldRectangles() {
        float bottom = gameView.getHeight();
        for (int i = 0; i < rectangles.size(); ) {
            Rectangle rectangle = rectangles.get(i);
            if (rectangle.getTop() > bottom) {
                rectangles.remove(i);
            } else {
                i++;
            }
        }
    }

    /**
     * This ends the game.
     */
    public void endGame(String message) {
        Log.i("GAME", "Ending game.");
        this.gameState = STATE_END;
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
     * Returns the column number of the given rectangle.
     *
     * @param rectangle the given rectangle
     * @return the column integer [0, infinity]
     */
    private int getRectangleColumn(Rectangle rectangle) {
        if (rectangle.getLeft() == 0.0f) {
            return 0;
        } else if (Math.abs(rectangle.getLeft() - gameView.getWidth() / COLUMN_COUNT) < 5) {
            return 1;
        } else if (Math.abs(rectangle.getLeft() - 2.0f * gameView.getWidth() / COLUMN_COUNT) < 5) {
            return 2;
        } else if (Math.abs(rectangle.getLeft() - 3.0f * gameView.getWidth() / COLUMN_COUNT) < 5) {
            return 3;
        } else {
            return 4;
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
        destroyOldRectangles();
        addNewRectangles();
    }

    /**
     * This checks to see if the finger has collided with a given rectangle. There are three steps taken to determine whether the objects are colliding. This is to minimise computing power required.
     * <p/>
     * <ol>
     * <li>This checks the with both objects' radii to see if they are close enough to each
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
            if (Rectangle.isColliding(aabb1, rectangle)) {
                Point centerPoint = circle.getCenter();
                byte xSlot, ySlot;
                if (centerPoint.getY() > rectangle.getBottom()) {
                    ySlot = 1;
                } else if (centerPoint.getY() < rectangle.getTop()) {
                    ySlot = -1;
                } else {
                    ySlot = 0;
                }
                if (centerPoint.getX() > rectangle.getRadius()) {
                    xSlot = 1;
                } else if (centerPoint.getX() < rectangle.getLeft()) {
                    xSlot = -1;
                } else {
                    xSlot = 0;
                }
                if (xSlot == 0 && ySlot == 0) {
                    return true;
                } else if (xSlot == 0 && ySlot != 0) {
                    float yMin = rectangle.getTop() - circle.getRadius();
                    float yMax = rectangle.getBottom() + circle.getRadius();
                    return (circle.getCenter().getY() > yMin && circle.getCenter().getY() < yMax);
                } else if (xSlot != 0 && ySlot == 0) {
                    float xMin = rectangle.getLeft() - circle.getRadius();
                    float xMax = rectangle.getRight() + circle.getRadius();
                    return (circle.getCenter().getX() > xMin && circle.getCenter().getX() < xMax);
                } else {
                    if (xSlot == -1 && ySlot == -1) {
                        Point corner = rectangle.getTopLeftCorner();
                        Point circleCenter = circle.getCenter();
                        return (Point.getDistance(corner, circleCenter) < circle.getRadius());
                    } else if (xSlot == -1 && ySlot == 1) {
                        Point corner = rectangle.getBottomLeftCorner();
                        Point circleCenter = circle.getCenter();
                        return (Point.getDistance(corner, circleCenter) < circle.getRadius());
                    } else if (xSlot == 1 && ySlot == -1) {
                        Point corner = rectangle.getTopRightCorner();
                        Point circleCenter = circle.getCenter();
                        return (Point.getDistance(corner, circleCenter) < circle.getRadius());
                    } else {
                        Point corner = rectangle.getBottomRightCorner();
                        Point circleCenter = circle.getCenter();
                        return (Point.getDistance(corner, circleCenter) < circle.getRadius());
                    }
                }
            } else {
                return false;
            }
        } else {
            return false;
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
     * Adds the given OnGameEndedListener to the list.
     *
     * @param onGameEndedListener the given OnGameEndedListener
     * @see OnGameEndedListener
     */
    public void registerOnGameEndedListener(OnGameEndedListener onGameEndedListener) {
        this.onGameEndedListeners.add(onGameEndedListener);
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
     * Adds the given OnGameRestartListener to the list.
     *
     * @param onGameRestartListener the given OnGameRestartListener
     * @see OnGameRestartListener
     */
    public void registerOnGameRestartListener(OnGameRestartListener onGameRestartListener) {
        this.onGameRestartListeners.add(onGameRestartListener);
    }

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
     * This resets the game to its initial state.
     */
    public void restartGame() {
        this.gameState = STATE_PRE_GAME;
        this.startTime = -1;
        this.finger.getCenter().setX(gameView.getWidth() / 2.0f);
        this.finger.getCenter().setY(gameView.getHeight() / 2.0f);
        this.rectangles.clear();
        this.score = 0;
        if (onGameRestartListeners.size() > 0) {
            for (int i = 0; i < onGameRestartListeners.size(); i++) {
                onGameRestartListeners.get(i).restartGame(this);
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
     * @param settings The settings file to set to
     */
    public void setSettingsFile(SharedPreferences settings) {
        this.settings = settings;
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
     * Gets the game state integer
     *
     * @return the game state
     */
    public int getGameState() {
        return gameState;
    }

    /**
     * Returns the velocity of the rectangles. This uses the formula:
     * ( Starting Velocity ) + ( ( Acceleration ) * ( Time Since Start of Game ))
     *
     * @return the velocity of the rectangles (pixels / second)
     */
    private float getGameVelocity() {
        return VELOCITY_START + ACCELERATION * ((score) / 1000.0f);
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

    /**
     * Gets the list of rectangles on the field.
     *
     * @return the rectangles
     */
    public ArrayList<Rectangle> getRectangles() {
        return rectangles;
    }

    /**
     * Returns the current score of the game.
     */
    public float getScore() {
        return (float) (score) / 1000.0f;
    }

    /**
     * Returns the highest up rectangle in the list.
     *
     * @return the highest rectangle
     */
    private Rectangle getTopRectangle() {
        Rectangle rectangle = rectangles.get(0);
        for (int i = 0; i < rectangles.size(); i++) {
            if (rectangles.get(i).getTop() < rectangle.getTop()) {
                rectangle = rectangles.get(i);
            }
        }
        return rectangle;
    }

}
