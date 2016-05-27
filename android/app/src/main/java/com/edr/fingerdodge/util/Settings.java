package com.edr.fingerdodge.util;

import android.content.SharedPreferences;

/**
 * This holds various static values for settings.
 *
 * @author Ethan Raymond
 */
public class Settings {

    private static final String DO_COLLECT_STATISTICS = "do-collect-statistics";

    private static final boolean DEFAULT_DO_COLLECT_STATISTICS = true;

    /**
     * This is for if the user has elected to send usage statistics to the server or not.
     */
    public static boolean doCollectStatistics;

    /**
     * This loads all of the previously saved settings into memory. If there were not previously
     * saved settings, the it uses the defaults.
     *
     * @param sharedPreferences the shared preferences that is used for reading and writing
     *                          settings
     */
    public static void loadSettings(SharedPreferences sharedPreferences) {
        doCollectStatistics = sharedPreferences.getBoolean(DO_COLLECT_STATISTICS, DEFAULT_DO_COLLECT_STATISTICS);
    }

    /**
     * This saves all of the settings into the given shared preferences.
     *
     * @param sharedPreferences this is the given shared preferences
     */
    public static void saveSettings(SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(DO_COLLECT_STATISTICS, doCollectStatistics);
        editor.apply();
    }

}
