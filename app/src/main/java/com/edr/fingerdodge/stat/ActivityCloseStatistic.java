package com.edr.fingerdodge.stat;

import com.edr.fingerdodge.json.JSONException;
import com.edr.fingerdodge.json.JSONKeys;
import com.edr.fingerdodge.json.JSONObject;

/**
 * @author Ethan Raymond
 */
public class ActivityCloseStatistic extends Statistic {

    public static final String TYPE = "activity-close";

    private String activityName;

    public ActivityCloseStatistic(String type, long id, long time, int api, String activityName){
        super(type, id, time, api);
        this.activityName = activityName;
    }

    public ActivityCloseStatistic(JSONObject json) throws JSONException {
        super(json);
        this.activityName = json.getString(JSONKeys.KEY_ACTIVITY_NAME);
    }

    public String getActivityName() {
        return activityName;
    }

    @Override
    public JSONObject getJSONObject() {
        try {
            JSONObject jsonObject = super.getJSONObject();
            jsonObject.put(JSONKeys.KEY_ACTIVITY_NAME, getActivityName());
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
