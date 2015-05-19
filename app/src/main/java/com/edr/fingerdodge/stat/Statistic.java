package com.edr.fingerdodge.stat;

import com.edr.fingerdodge.json.JSONKeys;

import org.json.JSONObject;

/**
 * @author Ethan Raymond
 */
public class Statistic {

    private String type;
    private long time;

    public Statistic(String type, long time) {
        this.type = type;
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public long getTime() {
        return time;
    }

    public JSONObject getJSONObject() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JSONKeys.KEY_TYPE, getType());
            jsonObject.put(JSONKeys.KEY_TIME, getTime());
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
