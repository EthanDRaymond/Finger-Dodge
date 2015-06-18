package com.edr.fingerdodge.ui.pages;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.edr.fingerdodge.R;
import com.edr.fingerdodge.services.AnnouncementService;
import com.edr.fingerdodge.util.Files;
import com.edr.fingerdodge.util.Settings;

import java.io.File;

/**
 * @author Ethan Raymond
 */
public class SplashActivity extends StatisticsTrackingActivity {

    private TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        isFirstTime();
        loadSettings();
        File statFile = new File(getFilesDir(), Files.FILE_STAT_SAVES);
        /*textView = (TextView) findViewById(R.id.splash_textView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(getBaseContext(), GameActivity.class);
                        startActivity(i);
                        finish();
                    }
                });
            }
        }).start();
        initializeAnnouncementServer();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This sees if this activity is being opened for the very first time.
     *
     * @return true if the app has never been opened before, false if the app has been opened
     * before
     */
    public boolean isFirstTime() {
        SharedPreferences settings = getSharedPreferences(Files.FILE_BASIC, 0);
        if (settings.getBoolean("my_first_time", true)) {
            settings.edit().putBoolean("my_first_time", false).commit();
            settings.edit().putFloat(Files.KEY_SETTINGS_HIGHSCORE, 0.0f).commit();
            settings.edit().putBoolean("stat_onoff", true).commit();
            return true;
        } else {
            return false;
        }
    }

    private void loadSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        Settings.loadSettings(sharedPreferences);
    }

    private void initializeAnnouncementServer() {
        Intent intent = new Intent(getBaseContext(), AnnouncementService.class);
        startService(intent);
    }

}
