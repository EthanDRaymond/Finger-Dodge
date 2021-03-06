package com.edr.fingerdodge.stat;

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

    /**
     * Creates a new game statistic with the given values.
     *
     * @param type          Not needed anymore
     * @param id            The user ID
     * @param time          The time the statistic was logged
     * @param api           The app's API
     * @param duration      The duration the game lasted (milliseconds)
     * @param beatHighScore Whether this game beat the user's high score
     */
    public GameStatistic(@SuppressWarnings("UnusedParameters") String type, long id, long time, int api, long duration, boolean beatHighScore) {
        super(TYPE, id, time, api);
        this.duration = duration;
        this.beatHighScore = beatHighScore;
    }

    /**
     * Creates a new game statistic with the given JSON code.
     *
     * @param json the raw json code
     * @throws JSONException thrown if there is a problem with the JSON code
     */
    public GameStatistic(JSONObject json) throws JSONException {
        super(json);
        duration = json.getLong(JSONKeys.KEY_DURATION);
        beatHighScore = json.getBoolean(JSONKeys.KEY_BEAT_HIGH_SCORE);
    }

    /**
     * Returns the duration of the game.
     */
    private long getDuration() {
        return duration;
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

    /**
     * Returns true if the user beat their own high score in the game, false if the user did not
     * beat their high score.
     */
    private boolean isBeatHighScore() {
        return beatHighScore;
    }
}
