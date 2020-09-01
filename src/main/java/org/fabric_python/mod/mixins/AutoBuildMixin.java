package org.fabric_python.mod.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityPose;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.light.ChunkLightingView;
import net.minecraft.world.chunk.light.LightingProvider;
import org.fabric_python.mod.PythonProxy;
import org.fabric_python.mod.block.MayInteract;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.sqrt;

@Mixin(ClientPlayerEntity.class)
public abstract class AutoBuildMixin {
    @Inject(at = @At("RETURN"), method = "sendMovementPackets ()V")
    private void sendMovementPackets(CallbackInfo info) {
        if (PythonProxy.globalMap == null) {
            return;
        }

        int autobuild = Integer.parseInt(PythonProxy.globalMap.getOrDefault("autobuild", "0"));
        if (autobuild == 0) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if(client == null){
            return;
        }

        if(client.interactionManager != null && client.interactionManager.isBreakingBlock()){
            return;
        }

        ClientWorld world = client.world;
        if(world == null){
            return;
        }

        ClientPlayerEntity player = client.player;
        if(player == null){
            return;
        }

        double coolDownNow = Instant.now().getEpochSecond() * 1000 + Instant.now().getNano() / 1000.0 / 1000;
        double coolDownLast = Double.parseDouble(PythonProxy.globalMap.getOrDefault("autobuild_cooldown", "0.0"));

        if(coolDownNow - coolDownLast <= 250) {
            return;
        }

        PythonProxy.globalMap.put("autobuild_cooldown", String.valueOf(coolDownNow));

        boolean mainHandReady = false;

        ItemStack mainHandItemStack = player.getMainHandStack();
        Item mainHandItem = mainHandItemStack.getItem();

        if(mainHandItemStack.getCount() != 0 && mainHandItem instanceof BlockItem){
            mainHandReady = true;
        }

        if(!mainHandReady){
            return;
        }

        // require the 9 blocks near player's foot have 3 that are of this type
        int blockFound = 0;
        BlockPos playerBlockPos = player.getBlockPos();

        for(int i = -1; i <= 1; i++){
            for(int k = -1; k <= 1; k++){
                BlockPos testBlockPos = playerBlockPos.add(i, -1, k);
                BlockState testBlockState = world.getBlockState(testBlockPos);

                if(Item.BLOCK_ITEMS.containsKey(testBlockState.getBlock()) && Item.BLOCK_ITEMS.get(testBlockState.getBlock()) == mainHandItem){
                    blockFound ++;
                }
            }
        }

        if(blockFound <= 3){
            return;
        }

        Vec3d playerEyePosStanding = new Vec3d(player.getX(), player.getY() + player.getEyeHeight(EntityPose.STANDING), player.getZ());
        Vec3d playerEyePosCrouching = new Vec3d(player.getX(), player.getY() + player.getEyeHeight(EntityPose.CROUCHING), player.getZ());

        boolean pos1 = Boolean.parseBoolean(PythonProxy.globalMap.getOrDefault("pos1", "False"));
        boolean pos2 = Boolean.parseBoolean(PythonProxy.globalMap.getOrDefault("pos2", "False"));

        int pos1X = Integer.parseInt(PythonProxy.globalMap.getOrDefault("pos1_x", "0"));
        int pos1Y = Integer.parseInt(PythonProxy.globalMap.getOrDefault("pos1_y", "0"));
        int pos1Z = Integer.parseInt(PythonProxy.globalMap.getOrDefault("pos1_z", "0"));

        int pos2X = Integer.parseInt(PythonProxy.globalMap.getOrDefault("pos2_x", "0"));
        int pos2Y = Integer.parseInt(PythonProxy.globalMap.getOrDefault("pos2_y", "0"));
        int pos2Z = Integer.parseInt(PythonProxy.globalMap.getOrDefault("pos2_z", "0"));

        boolean posBoxExists = pos1 && pos2;

        int box1X = Math.min(pos1X, pos2X);
        int box1Y = Math.min(pos1Y, pos2Y);
        int box1Z = Math.min(pos1Z, pos2Z);

        int box2X = Math.max(pos1X, pos2X);
        int box2Y = Math.max(pos1Y, pos2Y);
        int box2Z = Math.max(pos1Z, pos2Z);

        Box posBox = new Box(box1X - 0.5, box1Y - 0.5, box1Z - 0.5, box2X + 0.5, box2Y + 0.5, box2Z + 0.5);

        boolean found = false;
        BlockPos foundBaseBlockPos = null;
        BlockPos foundBlockPos = null;
        BlockState foundBlockState = null;
        Direction foundDirection = Direction.EAST;

        for(int i = -6; i <= 6; i++){
            int j_bound = (int) sqrt(36 - i * i);
            for(int j = -j_bound; j <= j_bound; j++){
                int k_bound = (int) sqrt(36 - i * i - j * j);
                for(int k = -k_bound; k <= k_bound; k++) {
                    BlockPos blockPos = playerBlockPos.add(new Vec3i(i, j, k));

                    if(posBoxExists && !posBox.contains(new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()))){
                        continue;
                    }

                    if(playerEyePosStanding.distanceTo(new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ())) >= 5.9){
                        continue;
                    }

                    if(playerEyePosCrouching.distanceTo(new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ())) >= 5.9){
                        continue;
                    }

                    if(player.getPos().distanceTo(new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5)) >= 7.9){
                        continue;
                    }

                    BlockState blockState = world.getBlockState(blockPos);
                    if(Item.BLOCK_ITEMS.get(blockState.getBlock()) != mainHandItem){
                        continue;
                    }

                    // west
                    Vec3d pos_west = new Vec3d(blockPos.getX() - 1, blockPos.getY(), blockPos.getZ());
                    BlockState blockState_west = world.getBlockState(blockPos.add(-1, 0, 0));
                    if(blockState_west.isAir() && (!posBoxExists || posBox.contains(pos_west))){
                        found = true;
                        foundBaseBlockPos = blockPos;
                        foundBlockPos = blockPos.add(-1, 0, 0);
                        foundBlockState = blockState_west;
                        foundDirection = Direction.WEST;

                        break;
                    }

                    // east
                    Vec3d pos_east = new Vec3d(blockPos.getX() + 1, blockPos.getY(), blockPos.getZ());
                    BlockState blockState_east = world.getBlockState(blockPos.add(1, 0, 0));
                    if(blockState_east.isAir() && (!posBoxExists || posBox.contains(pos_east))){
                        found = true;
                        foundBaseBlockPos = blockPos;
                        foundBlockPos = blockPos.add(1, 0, 0);
                        foundBlockState = blockState_east;
                        foundDirection = Direction.EAST;

                        break;
                    }

                    // south
                    Vec3d pos_south = new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ() + 1);
                    BlockState blockState_south = world.getBlockState(blockPos.add(0, 0, 1));
                    if(blockState_south.isAir() && (!posBoxExists || posBox.contains(pos_south))){
                        found = true;
                        foundBaseBlockPos = blockPos;
                        foundBlockPos = blockPos.add(0, 0, 1);
                        foundBlockState = blockState_south;
                        foundDirection = Direction.SOUTH;

                        break;
                    }

                    // north
                    Vec3d pos_north = new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ() + 1);
                    BlockState blockState_north = world.getBlockState(blockPos.add(0, 0, -1));
                    if(blockState_north.isAir() && (!posBoxExists || posBox.contains(pos_north))){
                        found = true;
                        foundBaseBlockPos = blockPos;
                        foundBlockPos = blockPos.add(0, 0, -1);
                        foundBlockState = blockState_north;
                        foundDirection = Direction.NORTH;

                        break;
                    }
                }
            }
        }

        boolean blockMayInteract = false;
        if(found && foundBlockState != null && MayInteract.blockMayInteract(foundBlockState.getBlock())){
            blockMayInteract = true;
        }

        if(found) {
            if(client.interactionManager != null) {
                if(blockMayInteract) {
                    PythonProxy.globalMap.put("autobuild_cancel_interaction", "True");
                    player.networkHandler.sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
                }

                client.interactionManager.interactBlock(player, client.world, Hand.MAIN_HAND, new BlockHitResult(new Vec3d(foundBlockPos.getX() + 0.5, foundBlockPos.getY(), foundBlockPos.getZ() + 0.5), foundDirection, foundBaseBlockPos, false));

                if(blockMayInteract) {
                    player.networkHandler.sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
                    PythonProxy.globalMap.put("autobuild_cancel_interaction", "False");
                }
            }
        }
    }
}
