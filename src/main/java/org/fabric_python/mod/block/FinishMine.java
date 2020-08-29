package org.fabric_python.mod.block;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.fabric_python.mod.PythonProxy;
import org.fabric_python.mod.TaskWorker;

import java.util.HashMap;
import java.util.Map;

public class FinishMine implements TaskWorker {
    @Override
    public void onTask(MinecraftClient client, Map<String, String> info) {
        int x = Integer.parseInt(info.getOrDefault("x", String.valueOf(0)));
        int y = Integer.parseInt(info.getOrDefault("y", String.valueOf(0)));
        int z = Integer.parseInt(info.getOrDefault("z", String.valueOf(0)));

        ClientPlayerEntity player = client.player;
        if(player == null){
            Map<String, String> res = new HashMap<>();
            res.put("error", "cannot find the player");
            PythonProxy.outbox.sendMsg(info.get("sid"), res);

            return;
        }

        ClientPlayerInteractionManager interactionManager = client.interactionManager;
        if(interactionManager == null){
            Map<String, String> res = new HashMap<>();
            res.put("error", "cannot find the interaction manager");
            PythonProxy.outbox.sendMsg(info.get("sid"), res);

            return;
        }

        World world = client.world;
        if(world == null){
            Map<String, String> res = new HashMap<>();
            res.put("error", "cannot find the world");
            PythonProxy.outbox.sendMsg(info.get("sid"), res);

            return;
        }

        BlockPos blockPos = new BlockPos(x, y, z);
        int attempt = 500;
        while(attempt > 0 && interactionManager.isBreakingBlock()) {
            interactionManager.updateBlockBreakingProgress(blockPos, Direction.UP);
            attempt --;
        }

        if(interactionManager.isBreakingBlock()){
            Map<String, String> res = new HashMap<>();
            res.put("res", "failed");
            PythonProxy.outbox.sendMsg(info.get("sid"), res);
        }else{
            Map<String, String> res = new HashMap<>();
            res.put("res", "done");
            PythonProxy.outbox.sendMsg(info.get("sid"), res);
        }
    }
}
