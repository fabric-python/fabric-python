package org.fabric_python.mod.player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.fabric_python.mod.TaskWorker;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class Attack implements TaskWorker {
    @Override
    public void onTask(MinecraftClient client, Map<String, String> info) {
        double x = Double.parseDouble(info.getOrDefault("x", String.valueOf(0.0)));
        double y = Double.parseDouble(info.getOrDefault("y", String.valueOf(0.0)));
        double z = Double.parseDouble(info.getOrDefault("z", String.valueOf(0.0)));

        ClientPlayerEntity player = client.player;

        Vec3d box_1 = new Vec3d(x - 2, y - 1, z - 2);
        Vec3d box_2 = new Vec3d(x + 2, y + 1, z + 2);

        Box box = new Box(box_1, box_2);

        assert player != null;
        Predicate<Entity> isTouchingWater = Entity::isTouchingWater;
        List<WitherSkeletonEntity> target_wither_skeleton = player.getEntityWorld().getEntities(EntityType.WITHER_SKELETON, box, isTouchingWater);
        List<MagmaCubeEntity> target_magma_cube = player.getEntityWorld().getEntities(EntityType.MAGMA_CUBE, box, p -> true);

        if(target_wither_skeleton.size() != 0){
            assert client.interactionManager != null;
            client.interactionManager.attackEntity(player, target_wither_skeleton.get(0));
        }else if(target_magma_cube.size() != 0){
            assert client.interactionManager != null;
            client.interactionManager.attackEntity(player, target_magma_cube.get(0));
        }
    }
}
