package org.fabric_python.mod.player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.fabric_python.mod.PythonProxy;
import org.fabric_python.mod.TaskWorker;

import java.util.HashMap;
import java.util.Map;

public class AttackBlock implements TaskWorker {
    @Override
    public void onTask(MinecraftClient client, Map<String, String> info) {
        int x = Integer.parseInt(info.getOrDefault("x", String.valueOf(0)));
        int y = Integer.parseInt(info.getOrDefault("y", String.valueOf(0)));
        int z = Integer.parseInt(info.getOrDefault("z", String.valueOf(0)));

        ClientPlayerEntity player = client.player;
        assert player != null;

        assert client.interactionManager != null;
        client.interactionManager.attackBlock(new BlockPos(x, y, z), Direction.UP);

        Map<String, String> res = new HashMap<>();
        res.put("res", "done");
        PythonProxy.outbox.sendMsg(info.get("sid"), res);
    }
}

