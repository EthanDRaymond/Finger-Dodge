package com.edr.fingerdodge.stat;

import com.edr.fingerdodge.game.Game;
import com.edr.fingerdodge.json.JSONKeys;

import org.json.JSONObject;

/**
 * @author Ethan Raymond
 */
public class GameStatistic extends Statistic {

    private static final String TYPE = "game";

    private long duration;
    private boolean beatHighScore;

    public GameStatistic(long time, Game game) {
        super(TYPE, time);
        duration = (long) (game.getScore() * 1000);
        beatHighScore = game.getScore() >= game.getHighScore();
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
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JSONKeys.KEY_TYPE, getType());
            jsonObject.put(JSONKeys.KEY_TIME, getTime());
            jsonObject.put(JSONKeys.KEY_DURATION, getDuration());
            jsonObject.put(JSONKeys.KEY_BEAT_HIGH_SCORE, isBeatHighScore());
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
