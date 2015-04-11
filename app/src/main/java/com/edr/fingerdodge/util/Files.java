package com.edr.fingerdodge.util;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ethanraymond on 1/30/15.
 */
public class Files {

    public static final String FILE_BASIC = "basic.sav";
    public static final String FILE_HIGHSCORE = "highscore.sav";
    public static final String FILE_STAT_SAVES = "stat_saves.sav";

    public static final String KEY_SETTINGS_HIGHSCORE = "highscore";

    public static String readFile(File file) throws IOException {
        int length = (int) file.length();
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] bytes = new byte[length];
        fileInputStream.read(bytes);
        return new String(bytes);
    }

    public static void writeToFile(String data, File file) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(data.getBytes());
    }

    public static void appendToFile(String data, File file) throws IOException {
        writeToFile(readFile(file) + data, file);
    }

}
