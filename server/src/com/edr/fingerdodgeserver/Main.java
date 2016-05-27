package com.edr.fingerdodgeserver;

import com.edr.fingerdodgeserver.net.StatisticsDatabaseConnection;
import com.edr.fingerdodgeserver.server.StatisticsServer;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @author Ethan Raymond
 */
public class Main {

    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/finger-dodge-statistics";
    private static final String DATABASE_USERNAME = "fdstat";
    private static final String DATABASE_PASSWORD = "5pXr2eNg49QDN5FLQdJ4BYPn0OS0rU0m";

    private static StatisticsDatabaseConnection statisticsDatabaseConnection;
    private static StatisticsServer statisticsServer;

    public static StatisticsDatabaseConnection getStatisticsDatabaseConnection() {
        return statisticsDatabaseConnection;
    }

    public static StatisticsServer getStatisticsServer() {
        return statisticsServer;
    }

    public static void main(String args[]) throws SQLException, IOException, InterruptedException {
        statisticsDatabaseConnection
                = new StatisticsDatabaseConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
        statisticsDatabaseConnection.start();
        statisticsServer = new StatisticsServer(4500);
        statisticsServer.start();

        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ActivityOpenStatistic activityOpenStatistic = new ActivityOpenStatistic(ActivityOpenStatistic.TYPE, System.currentTimeMillis(), 3, "com.edr.MainActivity");
                statisticsDatabaseConnection.addActivityOpenStatistic(activityOpenStatistic);
                ActivityCloseStatistic activityCloseStatistic = new ActivityCloseStatistic(ActivityCloseStatistic.TYPE, System.currentTimeMillis(), 3, "com.edr.MainActivity");
                statisticsDatabaseConnection.addActivityCloseStatistic(activityCloseStatistic);
                GameStatistic gameStatistic = new GameStatistic(GameStatistic.TYPE, System.currentTimeMillis(), 3, 6000, true);
                statisticsDatabaseConnection.addGameStatistic(gameStatistic);
            }
        }).start();
        */

    }

}
