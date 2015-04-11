package com.edr.fingerdodge.util;

/**
 * Created by ethanraymond on 1/28/15.
 */
public class XML {

    public static final String TAG_GAME_HIGHSCORE_OPEN = "<highscore>";
    public static final String TAG_GAME_HIGHSCORE_CLOSE = "</highscore>";

    public static String addOneFieldToTag(String tag, String field, String value){
        String output = "";
        output += tag.substring(0, tag.length() - 1);
        output += " ";
        output += field;
        output += "='";
        output += value;
        output += "' />";
        return output;
    }

    public static String getDataFromWithinTag(String openTag, String closeTag, String input){
        int start = input.indexOf(openTag) + openTag.length();
        int end = input.indexOf(closeTag);
        if (start == -1 || end == -1){
            return  null;
        } else {
            return input.substring(start, end);
        }
    }

}
