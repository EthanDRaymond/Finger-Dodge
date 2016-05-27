package com.edr.fingerdodgeserver.stat;

import com.edr.fingerdodgeserver.json.JSONArray;
import com.edr.fingerdodgeserver.json.JSONKeys;
import com.edr.fingerdodgeserver.json.JSONObject;

import java.util.ArrayList;

/**
 * @author Ethan Raymond
 */
public class Statistic {

    private String type;
    private long userID;
    private long time;
    private int api;

    public Statistic(String type, long userID, long time, int api) {
        this.type = type;
        this.userID = userID;
        this.time = time;
        this.api = api;
    }

    public Statistic(JSONObject json){
        this.type = json.getString(JSONKeys.KEY_TYPE);
        this.userID = json.getLong(JSONKeys.KEY_USER_ID);
        this.time = json.getLong(JSONKeys.KEY_TIME);
        this.api = json.getInt(JSONKeys.KEY_API);
    }

    public String getType() {
        return type;
    }

    public long getTime() {
        return time;
    }

    public int getApi() {
        return api;
    }

    public long getUserID() {
        return userID;
    }

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

    public static JSONArray makeJSONArray(ArrayList<Statistic> statistics) {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < statistics.size(); i++) {
            jsonArray.put(statistics.get(i).getJSONObject());
        }
        return jsonArray;
    }

    public static JSONArray makeJSONArray(Statistic[] statistics) {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < statistics.length; i++) {
            jsonArray.put(statistics[i].getJSONObject());
        }
        return jsonArray;
    }

}
