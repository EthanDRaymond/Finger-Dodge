package com.edr.fingerdodge.stat;

import com.edr.fingerdodge.json.JSONArray;
import com.edr.fingerdodge.json.JSONKeys;
import com.edr.fingerdodge.json.JSONObject;
import com.edr.fingerdodge.net.Packet;

import java.util.ArrayList;

/**
 * @author Ethan Raymond
 */
public class StatisticPacket extends Packet {

    public static final String TITLE = "StatisticPacket";

    private ArrayList<Statistic> statistics;

    public StatisticPacket(int api, long userID, JSONObject content){
        super(TITLE, api, userID, content);
    }

    public StatisticPacket(String json){
        super(json);
        setStatisticsFromContent();
    }

    private void setStatisticsFromContent(){
        statistics = new ArrayList<Statistic>();
        JSONArray array = getContent().getJSONArray(JSONKeys.KEY_STATISTICS);
        for (int i = 0; i < array.length(); i++) {
            addStatistic(array.getJSONObject(i));
        }
    }

    private void addStatistic(JSONObject jsonObject){
        String type = jsonObject.getString(JSONKeys.KEY_TYPE);
        if (type.equals(ActivityOpenStatistic.TYPE)){
            statistics.add(new ActivityOpenStatistic(jsonObject));
        } else if (type.equals(ActivityCloseStatistic.TYPE)){
            statistics.add(new ActivityCloseStatistic(jsonObject));
        } else if (type.equals(GameStatistic.TYPE)){
            statistics.add(new GameStatistic(jsonObject));
        }
    }

    public ArrayList<Statistic> getStatistics() {
        return statistics;
    }
}
