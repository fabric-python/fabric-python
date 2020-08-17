package org.fabric_python.mod.player;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.chunk.ChunkCache;
import org.fabric_python.mod.PythonProxy;
import org.fabric_python.mod.TaskWorker;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Move implements TaskWorker {
    @Override
    public void onTask(MinecraftClient client, Map<String, String> info) {
        int dest_x = Integer.parseInt(info.getOrDefault("x", String.valueOf(0)));
        int dest_y = Integer.parseInt(info.getOrDefault("y", String.valueOf(0)));
        int dest_z = Integer.parseInt(info.getOrDefault("z", String.valueOf(0)));

        PathNodeMaker nodeMaker = new LandPathNodeMaker();
        nodeMaker.setCanEnterOpenDoors(true);
        PathNodeNavigator pathNodeNavigator = new PathNodeNavigator(nodeMaker, MathHelper.floor(100 * 16.0D)); // as in the case of a ghast

        assert client.player != null;
        ClientPlayerEntity player = client.player;
        VillagerEntity fakeVillager = new VillagerEntity(EntityType.VILLAGER, player.world);
        Vec3d pos = player.getPos();

        fakeVillager.setBoundingBox(player.getBoundingBox());
        fakeVillager.setPos(pos.x, pos.y, pos.z);
        fakeVillager.yaw = player.yaw;
        fakeVillager.noClip = player.noClip;
        fakeVillager.setOnGround(player.isOnGround());
        fakeVillager.stepHeight = player.stepHeight;
        fakeVillager.setMovementSpeed(player.getMovementSpeed());
        fakeVillager.setForwardSpeed(player.forwardSpeed);
        fakeVillager.setSidewaysSpeed(player.sidewaysSpeed);
        fakeVillager.setUpwardSpeed(player.upwardSpeed);

        Vec3d dest = new Vec3d(dest_x, dest_y, dest_z);
        if (player.getPos().distanceTo(dest) <= 4) {
            Vec3d delta = dest.subtract(client.player.getPos());

            client.player.setJumping(true);
            client.player.setYaw(changeAngle(client.player.yaw, (float) (MathHelper.atan2(delta.z, delta.x) * 57.2957763671875D) - 90.0F));
            client.player.move(MovementType.PLAYER, delta);
            client.player.setJumping(false);
        }else{
            int rangeOfViewableWorld = 100 + 16;
            BlockPos playerBlockPos = client.player.getBlockPos();
            ChunkCache viewableWorld = new ChunkCache(client.player.world, playerBlockPos.add(-rangeOfViewableWorld, -rangeOfViewableWorld, -rangeOfViewableWorld), playerBlockPos.add(rangeOfViewableWorld, rangeOfViewableWorld, rangeOfViewableWorld));

            Set<BlockPos> target = ImmutableSet.of(new BlockPos(dest_x, dest_y, dest_z));

            Path path = pathNodeNavigator.findPathToAny(viewableWorld, fakeVillager, target, 100, 1, 1);

            assert path != null;

            Vec3d cur_dest = client.player.getPos();
            while(path.getCurrentNodeIndex() < path.getLength()) {
                Vec3i next_i = path.getCurrentPosition();
                Vec3d next = new Vec3d(next_i.getX(), next_i.getY(), next_i.getZ());
                if(next.distanceTo(client.player.getPos()) < 10){
                    cur_dest = next;
                    path.setCurrentNodeIndex(path.getCurrentNodeIndex() + 1);
                }else{
                    Vec3d delta = cur_dest.subtract(client.player.getPos());

                    client.player.setJumping(true);
                    player.setYaw(changeAngle(client.player.yaw, (float) (MathHelper.atan2(delta.z, delta.x) * 57.2957763671875D) - 90.0F));
                    client.player.move(MovementType.PLAYER, delta);
                    client.player.setJumping(false);

                    if(cur_dest.distanceTo(player.getPos()) > 4){
                        break;
                    }
                }
            }
        }

        Map<String, String> res = new HashMap<>();
        res.put("res", String.valueOf(player.getPos().distanceTo(dest)));
        PythonProxy.outbox.sendMsg(info.get("sid"), res);
    }

    float changeAngle(float float_1, float float_2) {
        float float_4 = MathHelper.wrapDegrees(float_2 - float_1);
        if (float_4 > (float) 90.0) {
            float_4 = (float) 90.0;
        }

        if (float_4 < -(float) 90.0) {
            float_4 = -(float) 90.0;
        }

        float float_5 = float_1 + float_4;
        if (float_5 < 0.0F) {
            float_5 += 360.0F;
        } else if (float_5 > 360.0F) {
            float_5 -= 360.0F;
        }

        return float_5;
    }
}
