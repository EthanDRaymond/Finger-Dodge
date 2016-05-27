package com.edr.fingerdodge.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * This is a static class used for simplifying reading, writing, and appending to files. It contains
 * methods that simplify the process.
 *
 * @author Ethan Raymond
 */
public class Files {

    public static final String FILE_BASIC = "basic.sav";

    public static final String KEY_SETTINGS_HIGHSCORE = "highscore";

    /**
     * Reads and returns the data from the given file if it is available.
     *
     * @param file the file that will be read from
     * @return the data in the given file
     * @throws IOException thrown if there is an error finding or opening the file
     */
    public static String readFile(File file) throws IOException {
        int length = (int) file.length();
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] bytes = new byte[length];
        fileInputStream.read(bytes);
        return new String(bytes);
    }

    /**
     * Writes the given string of data to the given file.
     *
     * @param data the data that will be written to the file.
     * @param file the file that will be opened and written to
     * @throws IOException thrown if the file cannot be opened or the data cannot be written to it
     */
    public static void writeToFile(String data, File file) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(data.getBytes());
    }

}
