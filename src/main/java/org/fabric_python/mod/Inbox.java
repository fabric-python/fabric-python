package org.fabric_python.mod;

import net.minecraft.client.MinecraftClient;

import java.util.*;

public class Inbox {
    public Map<String, TaskWorker> workers;
    public Map<String, LinkedList<Map<String, String>>> queues;

    Inbox(){
        workers = new HashMap<>();
        queues = new HashMap<>();
    }

    void addWorker(String name, TaskWorker worker){
        workers.put(name, worker);
        queues.put(name, new LinkedList<>());
    }

    void put(String name, Map<String, String> info){
        queues.get(name).add(info);
    }

    void run(MinecraftClient client){
        if(!client.isRunning()){
            return;
        }

        for (Map.Entry<String, LinkedList<Map<String, String>>> entry : queues.entrySet()) {
            LinkedList<Map<String, String>> list = entry.getValue();

            while (!list.isEmpty()) {
                Map<String, String> info = list.remove();
                try {
                    workers.get(entry.getKey()).onTask(client, info);
                }catch(NullPointerException e){
                    if(!info.get("sid").equals("")) {
                        Map<String, String> res = new HashMap<String, String>();
                        res.put("res", e.getMessage() + Arrays.toString(e.getStackTrace()));
                        PythonProxy.outbox.sendMsg(info.get("sid"), res);
                    }
                };
            }
        }
    }
}