package com.edr.fingerdodge.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.edr.fingerdodge.json.JSONArray;
import com.edr.fingerdodge.json.JSONException;
import com.edr.fingerdodge.json.JSONKeys;
import com.edr.fingerdodge.json.JSONObject;
import com.edr.fingerdodge.net.ServerConnection;
import com.edr.fingerdodge.stat.ActivityCloseStatistic;
import com.edr.fingerdodge.stat.ActivityOpenStatistic;
import com.edr.fingerdodge.stat.GameStatistic;
import com.edr.fingerdodge.stat.Statistic;
import com.edr.fingerdodge.stat.StatisticPacket;
import com.edr.fingerdodge.util.Files;
import com.edr.fingerdodge.util.Settings;
import com.edr.fingerdodge.util.Version;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * This service handles the following tasks:
 * <ol>
 * <li>Collecting and Saving statistics to a file.</li>
 * <li>Deleting statistics older than one month.</li>
 * <li>Sending statistics to the server when an internet connection is availible.</li>
 * </ol>
 *
 * The following is the steps this takes to collecting and storing statistics. First statistics are
 * added from bound activities using the addStatistic() method. Those statistics are stored in an
 * ArrayList for later use, and they are written to a json file. Everytime the service closes the
 * existing unwritten statistics are written to the same json file. When the service opens, that
 * file is read and those statistics are either sent to the server or placed in memory for next
 * time. The service will only attempt to send statistics to the server on starting.
 *
 * @author Ethan Raymond
 */
public class StatisticsService extends Service {

    /**
     * This is the name of the file the saved statistics are stored in using JSON.
     */
    private static final String FILE_SAVED_STATISTICS = "saved_statistics.txt";

    private final IBinder mBinder = new LocalBinder();

    /**
     * This is the list of unwritten statistics.
     */
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
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (sendStatisticsToServer()) {
                        unwrittenStatistics.clear();
                        try {
                            saveStatisticsToFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
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

    /**
     * Called when a new statistic is added.
     */
    @SuppressWarnings("EmptyMethod")
    private void onAddNewStatistic() {
        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (sendStatisticsToServer()) {
                    unwrittenStatistics.clear();
                }
            }
        }).start();
        */
    }

    /**
     * Calls this when adding a new statistics to the list. This adds the statistics, and saves the
     * data to both memory and to a file.
     *
     * @param statistic the statistics to be added
     */
    public void addNewStatistic(Statistic statistic) {
        if (Settings.doCollectStatistics) {
            Log.i("STATISTICS", "Adding new Statistic: " + statistic.getJSONObject().toString());
            unwrittenStatistics.add(statistic);
            onAddNewStatistic();
            try {
                saveStatisticsToFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
     */
    private boolean sendStatisticsToServer() {
        Log.i("STATISTICS", "Sending Statistics To Server.");
        if (unwrittenStatistics.size() > 0) {
            ServerConnection serverConnection = new ServerConnection();
            serverConnection.start();
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (serverConnection.isConnected()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(JSONKeys.KEY_STATISTICS, Statistic.makeJSONArray(unwrittenStatistics));
                StatisticPacket statisticPacket = new StatisticPacket(Version.API_CODE, getID(), jsonObject);
                serverConnection.sendData(statisticPacket.getJSONObject().toString(), null);
                serverConnection.end();
                return true;
            } else {
                serverConnection.end();
                return false;
            }
        } else {
            return true;
        }
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

    /**
     * This takes raw JSON code and builds the array of unwritten statistics out of it.
     * @param json              this is the raw JSON code
     * @throws JSONException    thrown if there is a problem with the JSON code
     */
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

    /**
     * This makes a JSON array from the list of unwritten statistics.
     * @return a JSON array of unwritten statistics
     */
    private JSONArray getJSONArrayOfStatistics() {
        JSONArray array = new JSONArray();
        for (int i = 0; i < unwrittenStatistics.size(); i++) {
            array.put(unwrittenStatistics.get(i).getJSONObject());
        }
        return array;
    }

    /**
     * This gets the user's statistics ID. If there is not a statistics ID then it makes a new one
     * and saves it.
     * @return the users statistics ID
     */
    public long getID() {
        SharedPreferences sharedPref = getSharedPreferences("stat", Context.MODE_PRIVATE);
        long id = sharedPref.getLong("stat-id", -1);
        if (id == -1 || id < 0) {
            Random random = new Random();
            SharedPreferences.Editor editor = sharedPref.edit();
            long newID = random.nextLong();
            if (newID < 0) {
                newID *= -1;
            }
            editor.putLong("stat-id", newID);
            editor.apply();
            return newID;
        } else {
            return id;
        }
    }

    public class LocalBinder extends Binder {
        public StatisticsService getService() {
            return StatisticsService.this;
        }
    }

}
