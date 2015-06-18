package com.edr.fingerdodge.stat;

import com.edr.fingerdodge.json.JSONArray;
import com.edr.fingerdodge.json.JSONKeys;
import com.edr.fingerdodge.json.JSONObject;

import java.util.ArrayList;

/**
 * This holds the value for a general statistic. This class should not be initialized by itself,
 * but as a inherited form.
 * @author Ethan Raymond
 */
public class Statistic {

    private String type;
    private long userID;
    private long time;
    private int api;

    /**
     * Creates a new statistics with the given values.
     *
     * @param type   this is the type of statistics that is being created. this information is
     *               usually specified in a constant string called 'TYPE'
     * @param userID this is the userID of the user who is sending the statistic
     * @param time   this is the time the statistics is collected
     * @param api    this is the API of the user's current app
     */
    public Statistic(String type, long userID, long time, int api) {
        this.type = type;
        this.userID = userID;
        this.time = time;
        this.api = api;
    }

    /**
     * This creates a new statistics using raw JSON code.
     * @param json      this is the raw JSON code used
     */
    public Statistic(JSONObject json) {
        this.type = json.getString(JSONKeys.KEY_TYPE);
        this.userID = json.getLong(JSONKeys.KEY_USER_ID);
        this.time = json.getLong(JSONKeys.KEY_TIME);
        this.api = json.getInt(JSONKeys.KEY_API);
    }

    /**
     * Returns the type of statistic.
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the time the statistics is logged.
     */
    public long getTime() {
        return time;
    }

    /**
     * Return the user's app's API.
     */
    public int getApi() {
        return api;
    }

    /**
     * Returns the user ID.
     */
    public long getUserID() {
        return userID;
    }

    /**
     * Makes a JSON object out of this statistic.
     */
    public JSONObject getJSONObject() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JSONKeys.KEY_TYPE, getType());
            jsonObject.put(JSONKeys.KEY_USER_ID, getUserID());
            jsonObject.put(JSONKeys.KEY_TIME, getTime());
            jsonObject.put(JSONKeys.KEY_API, api);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Used to make a JSON array from the given list of statistics.
     * @param statistics    the statistics used to make the array
     */
    public static JSONArray makeJSONArray(ArrayList<Statistic> statistics) {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < statistics.size(); i++) {
            jsonArray.put(statistics.get(i).getJSONObject());
        }
        return jsonArray;
    }

    /**
     * Used to make a JSON array from the given list of statistics.
     * @param statistics    the statistics used to make the array
     */
    public static JSONArray makeJSONArray(Statistic[] statistics) {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < statistics.length; i++) {
            jsonArray.put(statistics[i].getJSONObject());
        }
        return jsonArray;
    }

}
