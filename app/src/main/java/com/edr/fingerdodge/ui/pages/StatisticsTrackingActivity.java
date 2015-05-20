package com.edr.fingerdodge.ui.pages;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.util.Log;

import com.edr.fingerdodge.game.listeners.OnGameEndedListener;
import com.edr.fingerdodge.services.StatisticsService;
import com.edr.fingerdodge.stat.ActivityCloseStatistic;
import com.edr.fingerdodge.stat.ActivityOpenStatistic;
import com.edr.fingerdodge.stat.GameStatistic;

/**
 * @author Ethan Raymond
 */
public class StatisticsTrackingActivity extends Activity {

    protected StatisticsService statisticsService;
    protected ServiceConnection statisticsServiceConnection;
    protected boolean isBound;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statisticsServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                StatisticsService.LocalBinder binder = (StatisticsService.LocalBinder) service;
                statisticsService = binder.getService();
                isBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                isBound = false;
            }
        };
        isBound = false;
        Intent intent = new Intent(this, StatisticsService.class);
        bindService(intent, statisticsServiceConnection, Context.BIND_AUTO_CREATE);
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    ActivityOpenStatistic activityOpenStatistic
                            = new ActivityOpenStatistic(System.currentTimeMillis(), getParent());
                    getStatisticsService().addNewStatistic(activityOpenStatistic);
                } catch (Exception e) {
                }
            }
        });*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    ActivityOpenStatistic activityOpenStatistic
                            = new ActivityOpenStatistic(System.currentTimeMillis(), getThisActivity());
                    getStatisticsService().addNewStatistic(activityOpenStatistic);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ActivityCloseStatistic activityOpenStatistic
                = new ActivityCloseStatistic(System.currentTimeMillis(), this);
        getStatisticsService().addNewStatistic(activityOpenStatistic);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(statisticsServiceConnection);
    }

    public StatisticsService getStatisticsService() {
        return statisticsService;
    }

    public ServiceConnection getStatisticsServiceConnection() {
        return statisticsServiceConnection;
    }

    public boolean isBound() {
        return isBound;
    }

    public Activity getThisActivity() {
        return this;
    }

}
