package com.edr.fingerdodge.stat;

import com.edr.fingerdodge.game.Game;
import com.edr.fingerdodge.json.JSONKeys;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Ethan Raymond
 */
public class GameStatistic extends Statistic {

    public static final String TYPE = "game";

    private long duration;
    private boolean beatHighScore;

    public GameStatistic(long time, Game game) {
        super(TYPE, time);
        duration = (long) (game.getScore() * 1000);
        beatHighScore = game.getScore() >= game.getHighScore();
    }

    public GameStatistic(JSONObject json) throws JSONException {
        super(TYPE, json.getLong(JSONKeys.KEY_TIME));
        duration = json.getLong(JSONKeys.KEY_DURATION);
        duration = json.getLong(JSONKeys.KEY_BEAT_HIGH_SCORE);
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
