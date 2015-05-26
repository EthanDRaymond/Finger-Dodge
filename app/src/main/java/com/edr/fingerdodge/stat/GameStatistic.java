package com.edr.fingerdodge.stat;

import com.edr.fingerdodge.json.JSONException;
import com.edr.fingerdodge.json.JSONKeys;
import com.edr.fingerdodge.json.JSONObject;

/**
 * @author Ethan Raymond
 */
public class GameStatistic extends Statistic {

    public static final String TYPE = "game";

    private long duration;
    private boolean beatHighScore;

    public GameStatistic(String type, long id, long time, int api, long duration, boolean beatHighScore) {
        super(TYPE, id, time, api);
        this.duration = duration;
        this.beatHighScore = beatHighScore;
    }

    public GameStatistic(JSONObject json) throws JSONException {
        super(json);
        duration = json.getLong(JSONKeys.KEY_DURATION);
        beatHighScore = json.getBoolean(JSONKeys.KEY_BEAT_HIGH_SCORE);
    }

    public long getDuration() {
        return duration;
    }

    public boolean isBeatHighScore() {
        return beatHighScore;
    }

    @Override
    public JSONObject getJSONObject() {
        try {
            JSONObject jsonObject = super.getJSONObject();
            jsonObject.put(JSONKeys.KEY_DURATION, getDuration());
            jsonObject.put(JSONKeys.KEY_BEAT_HIGH_SCORE, isBeatHighScore());
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
