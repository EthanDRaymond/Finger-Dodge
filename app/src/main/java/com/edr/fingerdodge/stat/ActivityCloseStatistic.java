package com.edr.fingerdodge.stat;

import android.app.Activity;

import com.edr.fingerdodge.json.JSONKeys;

import org.json.JSONObject;

/**
 * @author Ethan Raymond
 */
public class ActivityCloseStatistic extends Statistic {

    private static final String TYPE = "activity-close";

    private String activityName;

    public ActivityCloseStatistic(long time, Activity activity) {
        super(TYPE, time);
        this.activityName = activity.getLocalClassName();
    }

    public String getActivityName() {
        return activityName;
    }

    @Override
    public JSONObject getJSONObject() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JSONKeys.KEY_TYPE, getJSONObject());
            jsonObject.put(JSONKeys.KEY_TIME, getTime());
            jsonObject.put(JSONKeys.KEY_ACTIVITY_NAME, getActivityName());
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
