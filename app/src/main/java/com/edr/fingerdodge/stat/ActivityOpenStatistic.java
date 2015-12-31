package com.edr.fingerdodge.stat;

import com.edr.fingerdodge.json.JSONException;
import com.edr.fingerdodge.json.JSONKeys;
import com.edr.fingerdodge.json.JSONObject;

/**
 * @author Ethan Raymond
 */
public class ActivityOpenStatistic extends Statistic {

    public static final String TYPE = "activity-open";

    private String activityName;

    public ActivityOpenStatistic(String type, long id, long time, int api, String activityName) {
        super(type, id, time, api);
        this.activityName = activityName;
    }

    public ActivityOpenStatistic(JSONObject json) throws JSONException {
        super(json);
        this.activityName = json.getString(JSONKeys.KEY_ACTIVITY_NAME);
    }

    private String getActivityName() {
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
