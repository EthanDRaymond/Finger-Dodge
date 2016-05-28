package com.edr.fingerdodge.stat;

import com.edr.fingerdodge.net.Packet;

import org.json.JSONObject;

/**
 * @author Ethan Raymond
 */
public class StatisticPacket extends Packet {

    private static final String TITLE = "StatisticPacket";

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")

    public StatisticPacket(int api, long userID, JSONObject content) {
        super(TITLE, api, userID, content);
    }

}
