package org.fabric_python.mod.player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.fabric_python.mod.PythonProxy;
import org.fabric_python.mod.TaskWorker;

import java.util.HashMap;
import java.util.Map;

public class SendChatMessage implements TaskWorker {
    @Override
    public void onTask(MinecraftClient client, Map<String, String> info) {
        ClientPlayerEntity player = client.player;
        if(player == null){
            Map<String, String> res = new HashMap<>();
            res.put("error", "cannot find the player");
            PythonProxy.outbox.sendMsg(info.get("sid"), res);

            return;
        }

        String message = info.get("message");
        if(message != null) {
            player.sendChatMessage(message);
        }

        Map<String, String> res = new HashMap<>();
        res.put("res", "sent");
        PythonProxy.outbox.sendMsg(info.get("sid"), res);
    }
}
