package com.edr.fingerdodge.stat;

import com.edr.fingerdodge.json.JSONObject;
import com.edr.fingerdodge.net.Packet;

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
