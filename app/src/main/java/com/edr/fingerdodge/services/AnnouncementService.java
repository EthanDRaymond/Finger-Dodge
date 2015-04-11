package com.edr.fingerdodge.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.edr.fingerdodge.R;
import com.edr.fingerdodge.util.Files;
import com.edr.fingerdodge.util.Version;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class AnnouncementService extends Service {

    private static final String FILE_RECIEVED_ANNOUCEMENTS = "recieved_notificaitons.txt";
    private static final String JSON_KEY_RECIEVED_ANNOUNCEMENTS = "recievedAnnouccements";

    private long lastRefreshTime;

    @Override
    public void onCreate() {
        super.onCreate();
        loadRefreshTime();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void onNewAnnouncementRecieved(Announcement announcement){
        sendNotification(announcement);
    }

    public void sendNotification(Announcement announcement){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.icon)
                        .setContentTitle(announcement.getTitle())
                        .setContentText(announcement.getMessage());
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(announcement.getId(), mBuilder.build());
    }

    private void loadRefreshTime(){
        SharedPreferences settings = getSharedPreferences(Files.FILE_BASIC, 0);
        long pussy = settings.getLong("REFRESH_TIME", -1);
        if (pussy == -1){
            lastRefreshTime = System.currentTimeMillis();
        } else {
            lastRefreshTime = pussy;
        }
    }

    private class AnnouncementListener extends Thread{

        private static final String SERVER_ADDRESS = "localhost";
        private static final int SERVER_PORT = 8910;

        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public AnnouncementListener(){
            super();
        }

        @Override
        public void run() {
            while (true){
                if (hasInternet()){
                    try {
                        connectToServer();

                        sleepFor(240);
                    } catch (IOException e) {
                        e.printStackTrace();
                        sleepFor(15);
                    }
                } else {
                    sleepFor(60);
                }
            }
        }

        private void listenAndHandleInput() throws IOException, JSONException {
            String input;
            long startTime = System.currentTimeMillis();
            if ((input = in.readLine()) != null && System.currentTimeMillis() - startTime < 60){
                JSONObject data = new JSONObject(input);
                Announcement announcement = new Announcement(data.getJSONObject("announcement").toString());
            } else {

            }
        }

        private void connectToServer() throws IOException {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        public void sendRequestToServer(String request){
            out.println(request);
        }

        public String buildRequest() throws JSONException {
            JSONObject details = new JSONObject();
            details.put("api", Version.API_CODE);
            details.put("aftertime", lastRefreshTime);
            JSONObject request = new JSONObject();
            request.put("request", details);
            return request.toString();
        }

        public void sleepFor(int minutes) {
            try {
                Thread.sleep(minutes * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public boolean hasInternet() {
            NetworkInfo info = (NetworkInfo) ((ConnectivityManager) getBaseContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (info == null || !info.isConnected()) {
                return false;
            }
            if (info.isRoaming()) {
                return false;
            }
            return true;
        }



    }

    private class Announcement{

        public static final String KEY_ID = "id";
        public static final String KEY_TIMESTAMP = "timestamp";
        public static final String KEY_TITLE = "title";
        public static final String KEY_MESSAGE = "message";

        private int id;
        private long timestamp;
        private String title;
        private String message;

        public Announcement(int id, long timestamp, String title, String message){
            this.id = id;
            this.timestamp = timestamp;
            this.title = title;
            this.message = message;
        }

        public Announcement(String json) throws JSONException {
            JSONObject jsonObject = new JSONObject(json);
            this.id = jsonObject.getInt(KEY_ID);
            this.timestamp = jsonObject.getLong(KEY_TIMESTAMP);
            this.title = jsonObject.getString(KEY_TITLE);
            this.message = jsonObject.getString(KEY_MESSAGE);
        }

        public int getId() {
            return id;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public String getTitle() {
            return title;
        }

        public String getMessage() {
            return message;
        }
    }

}
