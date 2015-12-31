package com.edr.fingerdodge.ui.pages;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.edr.fingerdodge.R;
import com.edr.fingerdodge.util.Settings;

public class SettingsActivity extends StatisticsTrackingActivity {

    private CheckBox settingsUsageStatisticsCheckBox;

    private void findViewsFromID() {
        settingsUsageStatisticsCheckBox
                = (CheckBox) findViewById(R.id.settings_usage_statistics_checkbox);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        findViewsFromID();
        setOnClickListeners();
        setViewsToCorrectValues();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveSettings();
    }

    private void saveSettings() {
        SharedPreferences sharedPreferences
                = getSharedPreferences("settings", Context.MODE_PRIVATE);
        Settings.saveSettings(sharedPreferences);
    }

    private void setOnClickListeners() {
        settingsUsageStatisticsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Settings.doCollectStatistics = isChecked;
            }
        });
    }

    private void setViewsToCorrectValues() {
        settingsUsageStatisticsCheckBox.setChecked(Settings.doCollectStatistics);
    }

}
