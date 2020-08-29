package org.fabric_python.mod.animal;

import java.util.HashMap;

public class PigFeedHistory {
    public static HashMap<String, Long> FeedHistory;

    public static Long get(String uuid) {
        if(FeedHistory == null){
            FeedHistory = new HashMap<>();
        }

        return FeedHistory.getOrDefault(uuid, (long) 0);
    }

    public static void set(String uuid, long time) {
        if(FeedHistory == null){
            FeedHistory = new HashMap<>();
        }
        FeedHistory.put(uuid, time);
    }
}
