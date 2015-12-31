package com.edr.fingerdodge.ui.pages;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.edr.fingerdodge.services.StatisticsService;
import com.edr.fingerdodge.stat.ActivityCloseStatistic;
import com.edr.fingerdodge.stat.ActivityOpenStatistic;
import com.edr.fingerdodge.util.Version;

/**
 * @author Ethan Raymond
 */
public class StatisticsTrackingActivity extends Activity {

    StatisticsService statisticsService;
    boolean isBound;
    private ServiceConnection statisticsServiceConnection;

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
                    ActivityOpenStatistic activityOpenStatistic = new ActivityOpenStatistic(
                            ActivityOpenStatistic.TYPE,
                            getStatisticsService().getID(),
                            System.currentTimeMillis(),
                            Version.API_CODE,
                            getThisActivity().getLocalClassName());
                    getStatisticsService().addNewStatistic(activityOpenStatistic);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ActivityCloseStatistic activityOpenStatistic = new ActivityCloseStatistic(
                        ActivityCloseStatistic.TYPE,
                        getStatisticsService().getID(),
                        System.currentTimeMillis(),
                        Version.API_CODE,
                        getThisActivity().getLocalClassName());
                getStatisticsService().addNewStatistic(activityOpenStatistic);
            }
        }).start();
    }

    StatisticsService getStatisticsService() {
        return statisticsService;
    }

    private Activity getThisActivity() {
        return this;
    }

}
