package org.fabric_python.mod.container;

import net.minecraft.client.MinecraftClient;
import org.fabric_python.mod.TaskWorker;

import java.util.Map;

public class FindItems implements TaskWorker {
    @Override
    public void onTask(MinecraftClient client, Map<String, String> info) {
        String groupName = info.getOrDefault("group", "default");
        String itemName = info.getOrDefault("item", "default");
        String tagConstraint = info.getOrDefault("tag", "");
    }
}
