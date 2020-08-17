package org.fabric_python.mod.raytracing;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Box;
import org.fabric_python.mod.TaskWorker;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VoidTexture implements TaskWorker {
    static Set<String> voided_texture = new HashSet<>();
    static Box bounding_box = null;

    @Override
    public void onTask(MinecraftClient client, Map<String, String> info) {

    }
}

