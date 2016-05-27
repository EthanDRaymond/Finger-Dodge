package com.edr.fingerdodgeserver.net;

import com.edr.fingerdodgeserver.stat.ActivityCloseStatistic;
import com.edr.fingerdodgeserver.stat.ActivityOpenStatistic;
import com.edr.fingerdodgeserver.stat.GameStatistic;
import com.edr.fingerdodgeserver.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * @author Ethan Raymond
 */
public class StatisticsDatabaseConnection extends Thread {

    private static final int STATE_AQURING = 0;
    private static final int STATE_CONNECTED = 1;
    private static final int STATE_OFF = 2;
    private static final String DATABASE_FINGER_DODGE_STATISTICS = "`finger-dodge-statistics`";
    private static final String TABLE_ACTIVITY_OPEN_STATISTICS = "`activity-open-statistics`";
    private static final String TABLE_ACTIVITY_CLOSE_STATISTICS = "`activity-close-statistics`";
    private static final String TABLE_GAME_STATISTICS = "`game-statistics`";

    private String url;
    private String username;
    private String password;
    private Connection connection;
    private ArrayList<ActivityOpenStatistic> activityOpenStatistics;
    private ArrayList<ActivityCloseStatistic> activityCloseStatistics;
    private ArrayList<GameStatistic> gameStatistics;
    private int state;

    /**
     * Creates a database connection with the given information
     * @param url               the url of the database
     * @param username          the username used to login to the database
     * @param password          the password used to login to the database
     * @throws SQLException     thrown if there is a problem connecting to the database
     */
    public StatisticsDatabaseConnection(String url, String username, String password) throws SQLException {
        this.url = url;
        this.username = username;
        this.password = password;
        connectToDatabase();
        activityOpenStatistics = new ArrayList<ActivityOpenStatistic>();
        activityCloseStatistics = new ArrayList<ActivityCloseStatistic>();
        gameStatistics = new ArrayList<GameStatistic>();
        state = STATE_CONNECTED;
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    @Override
    public void run() {
        super.run();
        while (true) {
            if (state == STATE_CONNECTED) {
                try {
                    if (connection.isClosed()){
                        state = STATE_AQURING;
                    } else {
                        writeActivityOpenStatistics();
                        writeActivityCloseStatistics();
                        writeGameStatistics();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    state = STATE_AQURING;
                }
            } else {
                try {
                    connectToDatabase();
                    if (!connection.isClosed()){
                        state = STATE_CONNECTED;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                if (state == STATE_CONNECTED){
                    Thread.sleep(1000);
                } else {
                    Thread.sleep(5000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void connectToDatabase() throws SQLException {
        connection = DriverManager.getConnection(url, username, password);
    }

    public void addActivityOpenStatistic(ActivityOpenStatistic activityOpenStatistic){
        activityOpenStatistics.add(activityOpenStatistic);
    }

    public void addActivityCloseStatistic(ActivityCloseStatistic activityCloseStatistic){
        activityCloseStatistics.add(activityCloseStatistic);
    }

    public void addGameStatistic(GameStatistic gameStatistic){
        gameStatistics.add(gameStatistic);
    }

    public void writeActivityOpenStatistics(){
        for (int i = 0; i < activityOpenStatistics.size(); i++) {
            try {
                ActivityOpenStatistic activityOpenStatistic = activityOpenStatistics.get(i);
                writeActivityOpenStatistic(activityOpenStatistic);
                activityOpenStatistics.remove(i);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void writeActivityCloseStatistics(){
        for (int i = 0; i < activityCloseStatistics.size(); i++) {
            try {
                ActivityCloseStatistic activityCloseStatistic = activityCloseStatistics.get(i);
                writeActivityCloseStatistic(activityCloseStatistic);
                activityCloseStatistics.remove(i);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void writeGameStatistics(){
        for (int i = 0; i < gameStatistics.size(); i++) {
            try {
                writeGameStatistic(gameStatistics.get(i));
                gameStatistics.remove(i);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * TODO: Make this method work.
     * @param activityOpenStatistic
     */
    public void writeActivityOpenStatistic(ActivityOpenStatistic activityOpenStatistic) throws SQLException {
        Log.i("Adding activity open statistic: " + activityOpenStatistic.getJSONObject().toString());
        long time = activityOpenStatistic.getTime();
        long userID = activityOpenStatistic.getUserID();
        int api = activityOpenStatistic.getApi();
        String activityName = activityOpenStatistic.getActivityName();
        Statement statement = connection.createStatement();
        String sql = "INSERT INTO " + DATABASE_FINGER_DODGE_STATISTICS + "." + TABLE_ACTIVITY_OPEN_STATISTICS + " (timestamp, `user-id`, api, `activity-name`) VALUES (" + time + ", " + userID + ", " + api + ", '" + activityName + "'" + ");";
        Log.sqlQuery(sql);
        statement.executeUpdate(sql);
    }

    /**
     * TODO: Make this method work.
     * @param activityCloseStatistic
     */
    public void writeActivityCloseStatistic(ActivityCloseStatistic activityCloseStatistic) throws SQLException {
        Log.i("Adding activiy close statistic: " + activityCloseStatistic.getJSONObject().toString());
        long time = activityCloseStatistic.getTime();
        long userID = activityCloseStatistic.getUserID();
        int api = activityCloseStatistic.getApi();
        String activityName = activityCloseStatistic.getActivityName();
        Statement statement = connection.createStatement();
        String sql = "INSERT INTO " + DATABASE_FINGER_DODGE_STATISTICS + "." + TABLE_ACTIVITY_CLOSE_STATISTICS + " (timestamp, `user-id`, api, `activity-name`) VALUES (" + time + ", " + userID + ", " + api + ", '" + activityName + "'" + ");";
        Log.sqlQuery(sql);
        statement.executeUpdate(sql);
    }

    /**
     * TODO: Make this method work.
     * @param gameStatistic
     */
    public void writeGameStatistic(GameStatistic gameStatistic) throws SQLException {
        Log.i("Adding game statistic: " + gameStatistic.getJSONObject().toString());
        long time = gameStatistic.getTime();
        long userID = gameStatistic.getUserID();
        int api = gameStatistic.getApi();
        long duration = gameStatistic.getDuration();
        int beatHighScore;
        if (gameStatistic.isBeatHighScore()){
            beatHighScore = 0;
        } else {
            beatHighScore = 1;
        }
        Statement statement = connection.createStatement();
        String sql = "INSERT INTO " + DATABASE_FINGER_DODGE_STATISTICS + "." + TABLE_GAME_STATISTICS + " (timestamp, `user-id`, api, duration, `beat-high-score`) VALUES (" + time + ", " + userID + ", " + api + ", '" + duration + "'" + "," + beatHighScore + ");";
        Log.sqlQuery(sql);
        statement.executeUpdate(sql);
    }

}
