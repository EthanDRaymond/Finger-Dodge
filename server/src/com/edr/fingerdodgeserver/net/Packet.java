package com.edr.fingerdodgeserver.net;

import com.edr.fingerdodgeserver.json.JSONException;
import com.edr.fingerdodgeserver.json.JSONKeys;
import com.edr.fingerdodgeserver.json.JSONObject;

/**
 * @author Ethan Raymond
 */
public class Packet {

    protected String title;
    protected int api;
    protected long userID;
    protected JSONObject content;

    public Packet(String title, int api, int userID, JSONObject content){
        this.title = title;
        this.api = api;
        this.userID = userID;
        this.content = content;
    }

    public Packet(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        this.title = jsonObject.getString(JSONKeys.KEY_TITLE);
        this.api = jsonObject.getInt(JSONKeys.KEY_API);
        this.userID = jsonObject.getLong(JSONKeys.KEY_USER_ID);
        this.content = jsonObject.getJSONObject(JSONKeys.KEY_CONTENT);
    }

    public String getTitle() {
        return title;
    }

    public int getApi() {
        return api;
    }

    public long getUserID() {
        return userID;
    }

    public JSONObject getContent() {
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

}
