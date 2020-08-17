package org.fabric_python.mod.container;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.registry.Registry;
import org.fabric_python.mod.PythonProxy;
import org.fabric_python.mod.TaskWorker;
import org.fabric_python.mod.db.ChestEntry;
import org.fabric_python.mod.db.DBManager;

import java.sql.SQLException;
import java.util.*;

public class ChestCache implements TaskWorker {
    @Override
    public void onTask(MinecraftClient client, Map<String, String> info) {
        String groupName = info.getOrDefault("group", "default");
        int x = Integer.parseInt(info.getOrDefault("x", String.valueOf(0)));
        int y = Integer.parseInt(info.getOrDefault("y", String.valueOf(0)));
        int z = Integer.parseInt(info.getOrDefault("z", String.valueOf(0)));

        ClientPlayerEntity player = client.player;

        if (player == null) {
            Map<String, String> res = new HashMap<>();
            res.put("res", "cannot find the player object");
            PythonProxy.outbox.sendMsg(info.get("sid"), res);

            return;
        }
        if (player.currentScreenHandler instanceof GenericContainerScreenHandler) {
            GenericContainerScreenHandler handler = (GenericContainerScreenHandler) player.currentScreenHandler;

            /* first 54 slots */
            List<ChestEntry> list = new LinkedList<>();

            for (int i = 0; i < 54; i++) {
                Slot slot = handler.getSlot(i);
                ItemStack itemStack = slot.getStack();

                if (itemStack.getCount() >= 1) {
                    list.add(new ChestEntry(i, Registry.ITEM.getId(itemStack.getItem()).toString(), itemStack.getCount(), itemStack.getTag()));
                }
            }

            DBManager db = new DBManager();
            try {
                db.loadDatabase(client);
                db.updateChest(groupName, x, y, z, list);
                db.close();
            } catch (SQLException e) {
                Map<String, String> res = new HashMap<>();
                res.put("res", e.getSQLState() + e.getMessage() + Arrays.toString(e.getStackTrace()));
                PythonProxy.outbox.sendMsg(info.get("sid"), res);
                return;
            }

            Map<String, String> res = new HashMap<>();
            res.put("res", "updated");
            PythonProxy.outbox.sendMsg(info.get("sid"), res);
        } else {
            Map<String, String> res = new HashMap<>();
            res.put("res", "unsupported chests");
            PythonProxy.outbox.sendMsg(info.get("sid"), res);
        }
    }
}

