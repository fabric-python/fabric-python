package org.fabric_python.mod.player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Hand;

import org.fabric_python.mod.PythonProxy;
import org.fabric_python.mod.TaskWorker;

import java.util.HashMap;
import java.util.Map;

public class UseItem implements TaskWorker {
    @Override
    public void onTask(MinecraftClient client, Map<String, String> info) {
        assert client.interactionManager != null;
        assert client.player != null;
        client.interactionManager.interactItem(client.player, client.player.getEntityWorld(), Hand.MAIN_HAND);

        Map<String, String> res = new HashMap<>();
        res.put("res", "done");
        PythonProxy.outbox.sendMsg(info.get("sid"), res);
    }
}
