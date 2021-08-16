package org.fabric_python.mod.container;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.registry.Registry;
import org.fabric_python.mod.PythonProxy;
import org.fabric_python.mod.TaskWorker;

import java.util.*;

public class MoveItemsInShulkerBox implements TaskWorker {
    @Override
    public void onTask(MinecraftClient client, Map<String, String> info) {
        String itemName = info.get("name");
        if (itemName == null) {
            Map<String, String> res = new HashMap<>();
            res.put("error", "must provide the item name");
            PythonProxy.outbox.sendMsg(info.get("sid"), res);

            return;
        }

        ClientPlayerEntity player = client.player;

        if (player == null) {
            Map<String, String> res = new HashMap<>();
            res.put("error", "cannot find the player object");
            PythonProxy.outbox.sendMsg(info.get("sid"), res);

            return;
        }

        if(client.interactionManager == null) {
            Map<String, String> res = new HashMap<>();
            res.put("error", "cannot find the interaction manager object");
            PythonProxy.outbox.sendMsg(info.get("sid"), res);

            return;
        }

        if (player.currentScreenHandler instanceof ShulkerBoxScreenHandler) {
            ShulkerBoxScreenHandler handler = (ShulkerBoxScreenHandler) player.currentScreenHandler;

            /* first 27 slots = shulker box */

            for (int i = 0; i < 27; i++) {
                Slot slot = handler.getSlot(i);
                ItemStack itemStack = slot.getStack();

                if (Registry.ITEM.getId(itemStack.getItem()).toString().contains(itemName) && itemStack.getCount() >= 1) {
                    client.interactionManager.clickSlot(player.currentScreenHandler.syncId, i, 0, SlotActionType.QUICK_MOVE, player);
                }
            }

            Map<String, String> res = new HashMap<>();
            res.put("res", "success");
            PythonProxy.outbox.sendMsg(info.get("sid"), res);
        } else {
            Map<String, String> res = new HashMap<>();
            res.put("error", "unsupported chests");
            PythonProxy.outbox.sendMsg(info.get("sid"), res);
        }
    }
}

