package com.edr.fingerdodge.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.edr.fingerdodge.json.JSONKeys;
import com.edr.fingerdodge.stat.ActivityCloseStatistic;
import com.edr.fingerdodge.stat.ActivityOpenStatistic;
import com.edr.fingerdodge.stat.GameStatistic;
import com.edr.fingerdodge.stat.Statistic;
import com.edr.fingerdodge.util.Files;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This service handles the following tasks:
 * <ol>
 * <li>Collecting and Saving statistics to a file.</li>
 * <li>Deleting statistics older than one month.</li>
 * <li>Sending statistics to the server when an internet connection is availible.</li>
 * </ol>
 *
 * @author Ethan Raymond
 */
public class StatisticsService extends Service {

    private static final String FILE_SAVED_STATISTICS = "saved_statistics.txt";

    private final IBinder mBinder = new LocalBinder();

    //private ServerConnection serverConnection;
    private ArrayList<Statistic> unwrittenStatistics;

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
        unwrittenStatistics = new ArrayList<Statistic>();
        try {
            readStatisticsFromFile();
            removeOldStatistics();
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
        try {
            saveStatisticsToFile();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return super.onUnbind(intent);
    }

    private void onAddNewStatistic() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (sendStatisticsToServer()) {
                    unwrittenStatistics.clear();
                }
            }
        }).start();
    }

    public void addNewStatistic(Statistic statistic) {
        Log.i("STATISTICS", "Adding new Statistic: " + statistic.getJSONObject().toString());
        unwrittenStatistics.add(statistic);
    }

    /**
     * Saves the statistics in JSON format to a file called saved_statistics.txt in the root
     * directory of the internal file system for the app.
     *
     * @throws JSONException thrown if the JSON code is improper
     * @throws IOException   thrown if the file cannot be opened
     */
    private void saveStatisticsToFile() throws JSONException, IOException {
        File file = new File(getFilesDir(), FILE_SAVED_STATISTICS);
        String output = getJSONArrayOfStatistics().toString();
        Files.writeToFile(output, file);
        Log.i("STATISTICS", "Saving Statistics To File: " + output);
    }


    /**
     * This reads and imports the statistics from the JSON file.
     *
     * @throws IOException   thrown if the file cannot be located or opened
     * @throws JSONException thrown if the JSON code is incorrect.
     */
    private void readStatisticsFromFile() throws IOException, JSONException {
        File file = new File(getFilesDir(), FILE_SAVED_STATISTICS);
        String input = Files.readFile(file);
        if (input.length() > 0) {
            setStatisticsFromJSONArray(input);
            Log.i("STATISTICS", "Reading Statistics From File: " + input);
        }
    }

    /**
     * Sends the statistics to the server and then clears the statistics from memory if they were
     * send successfully.
     *
     * @return true if the data is successfully send, false if the data is not.e
     * TODO:    Make this method actually send the data. It currently just pretends to and fails every time.
     */
    private boolean sendStatisticsToServer() {
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
     * If there is more than 1000 statistics, this removes ones more than 60 days old.
     */
    private void removeOldStatistics() {
        if (unwrittenStatistics.size() > 1000) {
            final long SECONDS_OLD = 5184000000l;
            for (int i = 0; i < unwrittenStatistics.size(); ) {
                long time = unwrittenStatistics.get(i).getTime();
                if (System.currentTimeMillis() - time > SECONDS_OLD) {
                    unwrittenStatistics.remove(i);
                } else {
                    i++;
                }
            }
        }
    }

    /**
     * Checks to see if there is an internet connection.
     *
     * @return true if there is a network connection, false if there is not.
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void setStatisticsFromJSONArray(String json) throws JSONException {
        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {
            Statistic statistic = null;
            String type = array.getJSONObject(i).getString(JSONKeys.KEY_TYPE);
            if (type.equals(ActivityCloseStatistic.TYPE)) {
                statistic = new ActivityCloseStatistic(array.getJSONObject(i));
            } else if (type.equals(ActivityOpenStatistic.TYPE)) {
                statistic = new ActivityOpenStatistic(array.getJSONObject(i));
            } else if (type.equals(GameStatistic.TYPE)) {
                statistic = new GameStatistic(array.getJSONObject(i));
            }
            if (statistic != null) {
                unwrittenStatistics.add(statistic);
            }
        }
    }

    private String getAllStatisticsDataString() throws JSONException {
        /*
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < unwrittenStatistics.size(); i++){
            jsonArray.put(new JSONObject(unwrittenStatistics.get(i)));
        }
        return jsonArray.toString();
        */
        return null;
    }

    private JSONArray getJSONArrayOfStatistics() {
        JSONArray array = new JSONArray();
        for (int i = 0; i < unwrittenStatistics.size(); i++) {
            array.put(unwrittenStatistics.get(i).getJSONObject());
        }
        return array;
    }

    public class LocalBinder extends Binder {
        public StatisticsService getService() {
            return StatisticsService.this;
        }
    }

}
