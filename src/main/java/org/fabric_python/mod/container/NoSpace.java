package org.fabric_python.mod.container;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.screen.slot.Slot;
import org.fabric_python.mod.PythonProxy;
import org.fabric_python.mod.TaskWorker;

import java.util.HashMap;
import java.util.Map;

public class NoSpace implements TaskWorker {
    @Override
    public void onTask(MinecraftClient client, Map<String, String> info) {
        ClientPlayerEntity player = client.player;

        if(player == null){
            Map<String, String> res = new HashMap<>();
            res.put("error", "cannot find the player");
            PythonProxy.outbox.sendMsg(info.get("sid"), res);

            return;
        }

        for (int i = 9; i <= 44; i++) {
            Slot cur_slot = player.playerScreenHandler.getSlot(i);
            if (!cur_slot.hasStack() || cur_slot.getStack().getCount() == 0) {
                Map<String, String> res = new HashMap<>();
                res.put("res", "0");
                PythonProxy.outbox.sendMsg(info.get("sid"), res);
                return;
            }
        }

        Map<String, String> res = new HashMap<>();
        res.put("res", "1");
        PythonProxy.outbox.sendMsg(info.get("sid"), res);
    }
}
