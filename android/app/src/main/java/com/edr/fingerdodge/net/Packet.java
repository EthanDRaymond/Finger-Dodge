package com.edr.fingerdodge.net;

import com.edr.fingerdodge.json.JSONKeys;
import com.edr.fingerdodge.json.JSONObject;

/**
 * @author Ethan Raymond
 */
public class Packet {

    private String title;
    private int api;
    private long userID;
    private JSONObject content;

    protected Packet(String title, int api, long userID, JSONObject content) {
        this.title = title;
        this.api = api;
        this.userID = userID;
        this.content = content;
    }

    private int getApi() {
        return api;
    }

    private JSONObject getContent() {
        return content;
    }

    public JSONObject getJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSONKeys.KEY_TITLE, getTitle());
        jsonObject.put(JSONKeys.KEY_API, getApi());
        jsonObject.put(JSONKeys.KEY_USER_ID, getUserID());
        jsonObject.put(JSONKeys.KEY_CONTENT, getContent());
        return jsonObject;
    }

    private String getTitle() {
        return title;
    }

    private long getUserID() {
        return userID;
    }

}
