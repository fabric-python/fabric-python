package org.fabric_python.mod.player;

import net.minecraft.client.MinecraftClient;
import org.fabric_python.mod.PythonProxy;
import org.fabric_python.mod.TaskWorker;

import java.util.HashMap;
import java.util.Map;

public class Hungry implements TaskWorker {
    @Override
    public void onTask(MinecraftClient client, Map<String, String> info) {
        assert client.player != null;

        Map<String, String> res = new HashMap<>();
        res.put("res", String.valueOf(client.player.getHungerManager().getFoodLevel()));
        PythonProxy.outbox.sendMsg(info.get("sid"), res);
    }
}
