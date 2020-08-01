package org.fabric_python.mod;

import net.minecraft.client.MinecraftClient;

import java.util.Map;

public interface TaskWorker {
    void onTask(MinecraftClient client, Map<String, String> info);
}
