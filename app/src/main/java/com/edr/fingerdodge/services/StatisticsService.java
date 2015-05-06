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

public class StatisticsService extends Service {

    private static final String JSON_KEY_STATISTIC_TYPE = "type";
    private static final String JSON_KEY_STATISTIC_LOG_TIME = "log-time";
    private static final String JSON_KEY_STATISTIC_START_TIME = "start-time";
    private static final String JSON_KEY_STATISTIC_END_TIME = "end-time";

    private static final String STATISTIC_TYPE_GAME_PLAY = "game-play";

    private static final String FILE_SAVED_STATISTICS = "saved_statistics.txt";

    private ServerConnection serverConnection;
    private ArrayList<String> unwrittenStatistics;

    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

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

    public void addStatisticGameplay(long startTime, long endTime){
        Log.i("STATISTICS", "Added new Gameplay Statistic.");
        try {
            JSONObject statistic = new JSONObject();
            statistic.put(JSON_KEY_STATISTIC_TYPE, STATISTIC_TYPE_GAME_PLAY);
            statistic.put(JSON_KEY_STATISTIC_START_TIME, startTime);
            statistic.put(JSON_KEY_STATISTIC_END_TIME, endTime);
            unwrittenStatistics.add(statistic.toString());
        } catch (Exception e){
            e.printStackTrace();
        }
        try {
            //if (isNetworkAvailable()){
            //    sendStatisticsToServer();
            //} else {
                saveStatisticsToFile();
            //}
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("STATISTICS", "Failed to save statistics to file. Reason: IOException");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("STATISTICS", "Failed to save statistics to file. Reason: JSONException");
        }
    }

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

    private void sendStatisticsToServer(){
        Log.i("STATISTICS", "Sending Statistics To Server.");
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public class LocalBinder extends Binder {
        public StatisticsService getService() {
            return StatisticsService.this;
        }
    }

}
