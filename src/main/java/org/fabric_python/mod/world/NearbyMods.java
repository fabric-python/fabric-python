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
import java.util.function.Predicate;

public class NearbyMods implements TaskWorker {
    @Override
    public void onTask(MinecraftClient client, Map<String, String> info) {
        ClientPlayerEntity player = client.player;
        assert player != null;

        Vec3d playerPos = player.getPos();

        Vec3d box1 = new Vec3d(playerPos.x - 5, playerPos.y - 5, playerPos.z - 5);
        Vec3d box2 = new Vec3d(playerPos.x + 5, playerPos.y + 5, playerPos.z + 5);

        Predicate<Entity> isTouchingWater = Entity::isTouchingWater;
        List<WitherSkeletonEntity> list_wither_skeleton = player.getEntityWorld().getEntities(EntityType.WITHER_SKELETON, new Box(box1, box2), isTouchingWater);
        List<MagmaCubeEntity> list_magma_cube = player.getEntityWorld().getEntities(EntityType.MAGMA_CUBE, new Box(box1, box2), p -> true);

        Map<String, String> res = new HashMap<>();
        res.put("count", String.valueOf(list_wither_skeleton.size() + list_magma_cube.size()));
        PythonProxy.outbox.sendMsg(info.get("sid"), res);
    }
}
