package com.edr.fingerdodge.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.edr.fingerdodge.net.ServerConnection;
import com.edr.fingerdodge.util.Files;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This service handles the following tasks:
 * <ol>
 *     <li>Collecting and Saving statistics to a file.</li>
 *     <li>Deleting statistics older than one month.</li>
 *     <li>Sending statistics to the server when an internet connection is availible.</li>
 * </ol>
 * @author Ethan Raymond
 */
public class StatisticsService extends Service {

    private static final String JSON_KEY_STATISTIC_TYPE = "type";
    private static final String JSON_KEY_STATISTIC_LOG_TIME = "log-time";
    private static final String JSON_KEY_STATISTIC_START_TIME = "start-time";
    private static final String JSON_KEY_STATISTIC_END_TIME = "end-time";

    private static final String STATISTIC_TYPE_GAME_PLAY = "game-play";

    private static final String FILE_SAVED_STATISTICS = "saved_statistics.txt";

    private final IBinder mBinder = new LocalBinder();

    private ServerConnection serverConnection;
    private ArrayList<String> unwrittenStatistics;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Initializes the service and all of the properties.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        serverConnection = new ServerConnection();
        serverConnection.start();
        unwrittenStatistics = new ArrayList<>();
        try {
            readStatisticsFromFile();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("STATISTICS", "Failed to read statistics from file. Reason: IOException");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("STATISTICS", "Failed to read statistics from file. Reason: JSONException");
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private void onAddNewStatistic(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (sendStatisticsToServer()){
                    unwrittenStatistics.clear();
                }
            }
        }).start();
    }

    /**
     * Adds a game play statistic to the statistics list.
     * @param startTime     the time the game starts
     * @param endTime       the time the game ends
     */
    public void addStatisticGameplay(long startTime, long endTime){
        Log.i("STATISTICS", "Added new Gameplay Statistic.");
        try {
            JSONObject statistic = new JSONObject();
            statistic.put(JSON_KEY_STATISTIC_TYPE, STATISTIC_TYPE_GAME_PLAY);
            statistic.put(JSON_KEY_STATISTIC_START_TIME, startTime);
            statistic.put(JSON_KEY_STATISTIC_END_TIME, endTime);
            unwrittenStatistics.add(statistic.toString());
            onAddNewStatistic();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Saves the statistics in JSON format to a file called saved_statistics.txt in the root
     * directory of the internal file system for the app.
     * @throws JSONException    thrown if the JSON code is improper
     * @throws IOException      thrown if the file cannot be opened
     */
    private void saveStatisticsToFile() throws JSONException, IOException {
        Log.i("STATISTICS", "Saving Statistics To File.");
        if (unwrittenStatistics.size() > 0) {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < unwrittenStatistics.size(); i++){
                jsonArray.put(new JSONObject(unwrittenStatistics.get(i)));
            }
            String output = jsonArray.toString();
            File file = new File(getFilesDir(), FILE_SAVED_STATISTICS);
            System.out.println(getFilesDir().getAbsolutePath());
            Files.writeToFile(output, file);;
        }
    }

    /**
     * This reads and imports the statistics from the JSON file.
     * @throws IOException      thrown if the file cannot be located or opened
     * @throws JSONException    thrown if the JSON code is incorrect.
     */
    private void readStatisticsFromFile() throws IOException, JSONException {
        Log.i("STATISTICS", "Reading Statistics From File.");
        unwrittenStatistics.clear();
        File file = new File(getFilesDir(), FILE_SAVED_STATISTICS);
        String input = Files.readFile(file);
        if (input.length() > 0){
            JSONArray jsonArray = new JSONArray(input);
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                try {
                    long logTime = jsonObject.getLong(JSON_KEY_STATISTIC_START_TIME);
                    if (System.currentTimeMillis() - logTime > 2628000000L) {
                        continue;
                    }
                } catch (Exception e){}
                unwrittenStatistics.add(jsonObject.toString());
            }
        }
    }

    /**
     * Sends the statistics to the server and then clears the statistics from memory if they were
     * send successfully.
     * @return  true if the data is successfully send, false if the data is not.e
     * TODO:    Make this method actually send the data. It currently just pretends to and fails every time.
     */
    private boolean  sendStatisticsToServer() {
        Log.i("STATISTICS", "Sending Statistics To Server.");
        return false;
        /*
        try {
            String output = getAllStatisticsDataString();
            ServerConnection connection = new ServerConnection();
            connection.start();
            boolean outcome = connection.sendData(output, null);
            connection.end();
            return outcome;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        */
    }

    /**
     * Checks to see if there is an internet connection.
     * @return  true if there is a network connection, false if there is not.
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private String getAllStatisticsDataString() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < unwrittenStatistics.size(); i++){
            jsonArray.put(new JSONObject(unwrittenStatistics.get(i)));
        }
        return jsonArray.toString();
    }

    public class LocalBinder extends Binder {
        public StatisticsService getService() {
            return StatisticsService.this;
        }
    }

}
