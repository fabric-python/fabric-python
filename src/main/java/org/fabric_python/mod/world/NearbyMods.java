package org.fabric_python.mod.world;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.fabric_python.mod.PythonProxy;
import org.fabric_python.mod.TaskWorker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class NearbyMods implements TaskWorker {
    @Override
    public void onTask(MinecraftClient client, Map<String, String> info) {
        ClientPlayerEntity player = client.player;
        assert player != null;

        Vec3d playerPos = player.getPos();

        Vec3d box_1 = new Vec3d(playerPos.x - 2, playerPos.y - 1, playerPos.z - 2);
        Vec3d box_2 = new Vec3d(playerPos.x + 2, playerPos.y + 1, playerPos.z + 2);

        Box box = new Box(box_1, box_2);

        Optional<EntityType<?>> type = EntityType.get(info.getOrDefault("type", "null"));

        if(!type.isPresent()){
            Map<String, String> res = new HashMap<>();
            res.put("res", "type not found");
            PythonProxy.outbox.sendMsg(info.get("sid"), res);
            return;
        }

        Predicate<Entity> predicate = p -> true;
        List<Entity> target = player.getEntityWorld().getEntities(client.player, box, predicate);

        Map<String, String> res = new HashMap<>();
        res.put("count", String.valueOf(target.size()));
        PythonProxy.outbox.sendMsg(info.get("sid"), res);
    }
}
