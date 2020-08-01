package org.fabric_python.mod;

import java.util.HashMap;
import java.util.Map;

public class Outbox {
    public Map<String,  Map<String, String>> box;

    Outbox(){
        box = new HashMap<>();
    }

    public void sendMsg(String sid,  Map<String, String>msg){
        box.put(sid, msg);
    }

    public Map<String, String> getMsg(String sid){
        Map<String, String> emptyMap = new HashMap<String, String>();
        Map<String, String> result = box.getOrDefault(sid, emptyMap);
        box.remove(sid);
        return result;
    }
}
