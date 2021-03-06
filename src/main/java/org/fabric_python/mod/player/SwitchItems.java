package org.fabric_python.mod.player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.fabric_python.mod.PythonProxy;
import org.fabric_python.mod.TaskWorker;

import java.util.HashMap;
import java.util.Map;

public class SwitchItems implements TaskWorker {
    @Override
    public void onTask(MinecraftClient client, Map<String, String> info) {
        ClientPlayerEntity player = client.player;

        if(player == null){
            Map<String, String> res = new HashMap<>();
            res.put("res", "failure");
            PythonProxy.outbox.sendMsg(info.get("sid"), res);

            return;
        }

        if(info.getOrDefault("hand", "main").equals("main")) {
            if (player.getMainHandStack() != null && Registry.ITEM.get(new Identifier(info.get("name"))) == player.getMainHandStack().getItem() && player.getMainHandStack().getCount() >= 1) {
                Map<String, String> res = new HashMap<>();
                res.put("res", "already in the main hand");
                PythonProxy.outbox.sendMsg(info.get("sid"), res);

                return;
            }
        }else{
            if (player.getOffHandStack() != null && Registry.ITEM.get(new Identifier(info.get("name"))) == player.getOffHandStack().getItem() && player.getOffHandStack().getCount() >= 1) {
                Map<String, String> res = new HashMap<>();
                res.put("res", "already in the off hand");
                PythonProxy.outbox.sendMsg(info.get("sid"), res);

                return;
            }
        }

        /* skip slot 0: crafting result */
        /* skip slot 1, 2, 3, 4: the 2x2 crafting tables */
        /* skip slot 5, 6, 7, 8: armors */

        /* available slots:
         * first row    :   9, 10, 11, 12, 13, 14, 15, 16, 17
         * second row   :   18, 19, 20, 21, 22, 23, 24, 25, 26
         * third row    :   27, 28, 29, 30, 31, 32, 33, 34, 35
         * player 1-9   :   36, 37, 38, 39, 40, 41, 42, 43, 44
         * offline      :   45
         */
        if(info.getOrDefault("hand", "main").equals("main")) {
            for (int i = 9; i <= 44; i++) {
                Slot cur_slot = player.playerScreenHandler.getSlot(i);
                if (cur_slot.hasStack() && cur_slot.getStack().getItem() == Registry.ITEM.get(new Identifier(info.get("name"))) && cur_slot.getStack().getCount() >= 1) {
                    if (i != 36) {
                        assert client.interactionManager != null;
                        client.interactionManager.clickSlot(player.playerScreenHandler.syncId, i, 0, SlotActionType.SWAP, player);
                    }
                    player.inventory.selectedSlot = 0;

                    Map<String, String> res = new HashMap<>();
                    res.put("res", "success");
                    PythonProxy.outbox.sendMsg(info.get("sid"), res);
                    return;
                }
            }
        }else{
            for (int i = 9; i <= 44; i++) {
                Slot cur_slot = player.playerScreenHandler.getSlot(i);
                if (cur_slot.hasStack() && cur_slot.getStack().getItem() == Registry.ITEM.get(new Identifier(info.get("name"))) && cur_slot.getStack().getCount() >= 1) {
                    client.interactionManager.clickSlot(player.playerScreenHandler.syncId, i, 40, SlotActionType.SWAP, player);

                    Map<String, String> res = new HashMap<>();
                    res.put("res", "success");
                    PythonProxy.outbox.sendMsg(info.get("sid"), res);
                    return;
                }
            }
        }

        Map<String, String> res = new HashMap<>();
        res.put("res", "failure");
        PythonProxy.outbox.sendMsg(info.get("sid"), res);
    }
}
