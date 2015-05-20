package com.edr.fingerdodge.stat;

import android.app.Activity;

import com.edr.fingerdodge.json.JSONKeys;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Ethan Raymond
 */
public class ActivityOpenStatistic extends Statistic {

    public static final String TYPE = "activity-open";

    private String activityName;

    public ActivityOpenStatistic(long time, Activity activity) {
        super(TYPE, time);
        this.activityName = activity.getLocalClassName();
    }

    public ActivityOpenStatistic(JSONObject json) throws JSONException {
        super(TYPE, json.getLong(JSONKeys.KEY_TIME));
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
