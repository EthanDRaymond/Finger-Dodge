package com.edr.fingerdodgeserver.util;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Ethan Raymond
 */
public class Log {

    public static void i(String text){
        System.out.println("[ " + getTimestamp() + " ] " + text);
    }

    public static void e(String text){
        System.err.println("[ " + getTimestamp() + " ] " + text);
    }

    public static void sqlQuery(String query) {
        i("Executing SQL: \t" + query);
    }

    private static String getTimestamp() {
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1; // Note: zero based!
        int day = now.get(Calendar.DAY_OF_MONTH);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);
        int millis = now.get(Calendar.MILLISECOND);
        String output = "";
        output += year;
        output += "-";
        if (month < 10){
            output += "0" + month;
        } else {
            output += month;
        }
        output += "-";
        if (day < 10){
            output += "0" + day;
        } else {
            output += day;
        }
        output += " ";
        if (hour < 10){
            output += "0" + hour;
        } else {
            output += hour;
        }
        output += ":";
        if (minute < 10){
            output += "0" + minute;
        } else {
            output += minute;
        }
        output += ":";
        if (second < 10){
            output += "0" + second;
        } else {
            output += second;
        }
        output += ".";
        if (millis < 10) {
            output += "000" + millis;
        } else if (millis < 100) {
            output += "00" + millis;
        } else if (millis < 1000) {
            output += "0" + millis;
        } else {
            output += "1000";
        }
        return output;
    }

}
