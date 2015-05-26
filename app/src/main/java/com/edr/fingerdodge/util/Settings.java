package com.edr.fingerdodge.util;

import android.content.SharedPreferences;

/**
 * This holds various static values for settings.
 * @author Ethan Raymond
 */
public class Settings {

    public static final String DO_COLLECT_STATISTICS = "do-collect-statistics";

    public static final boolean DEFAULT_DO_COLLECT_STATISTICS = false;

    public static boolean doCollectStatistics;

    public static void loadSettings(SharedPreferences sharedPreferences) {
        doCollectStatistics = sharedPreferences.getBoolean(DO_COLLECT_STATISTICS, DEFAULT_DO_COLLECT_STATISTICS);
    }

    public static void saveSettings(SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(DO_COLLECT_STATISTICS, doCollectStatistics);
        editor.commit();
    }

}
