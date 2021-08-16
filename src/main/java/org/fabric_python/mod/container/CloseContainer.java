package org.fabric_python.mod.container;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import org.fabric_python.mod.PythonProxy;
import org.fabric_python.mod.TaskWorker;

import java.util.*;

public class CloseContainer implements TaskWorker {
    @Override
    public void onTask(MinecraftClient client, Map<String, String> info) {
        ClientPlayerEntity player = client.player;

        if (player == null) {
            Map<String, String> res = new HashMap<>();
            res.put("res", "cannot find the player object");
            PythonProxy.outbox.sendMsg(info.get("sid"), res);

            return;
        }
        player.currentScreenHandler.close(player);

        if(client.currentScreen instanceof GenericContainerScreen){
            client.currentScreen.onClose();
        }

        if(client.currentScreen instanceof ShulkerBoxScreen){
            client.currentScreen.onClose();
        }

        assert client.interactionManager != null;
        client.interactionManager.stopUsingItem(player);

        Map<String, String> res = new HashMap<>();
        res.put("res", "done");
        PythonProxy.outbox.sendMsg(info.get("sid"), res);
    }
}