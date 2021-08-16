package org.fabric_python.mod.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityPose;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
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
import java.util.*;

import static java.lang.Math.sqrt;

@Mixin(ClientPlayerEntity.class)
public abstract class AutoPlantMixin {
    @Inject(at = @At("RETURN"), method = "sendMovementPackets ()V")
    private void sendMovementPackets(CallbackInfo info) {
        if (PythonProxy.globalMap == null) {
            return;
        }

        int autoplant = Integer.parseInt(PythonProxy.globalMap.getOrDefault("autoplant", "0"));
        if (autoplant == 0) {
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

        LightingProvider lightingProvider = client.world.getLightingProvider();
        if(lightingProvider.hasUpdates()){
            return;
        }

        ChunkLightingView view = lightingProvider.get(LightType.BLOCK);

        ClientPlayerEntity player = client.player;
        if(player == null){
            return;
        }

        double coolDownNow = Instant.now().getEpochSecond() * 1000 + Instant.now().getNano() / 1000.0 / 1000;
        double coolDownLast = Double.parseDouble(PythonProxy.globalMap.getOrDefault("autoplace_cooldown", "0.0"));

        if(coolDownNow - coolDownLast <= 250) {
            return;
        }

        boolean mainHandReady = false;

        ItemStack mainHandItemStack = player.getMainHandStack();
        Item mainHandItem = mainHandItemStack.getItem();

        if(mainHandItemStack.getCount() != 0 && Registry.ITEM.getId(mainHandItem).toString().equals("minecraft:spruce_sapling")){
            mainHandReady = true;
        }

        if(!mainHandReady){
            return;
        }

        BlockPos playerBlockPos = player.getBlockPos();
        Vec3d playerEyePosStanding = new Vec3d(player.getX(), player.getY() + player.getEyeHeight(EntityPose.STANDING), player.getZ());
        Vec3d playerEyePosCrouching = new Vec3d(player.getX(), player.getY() + player.getEyeHeight(EntityPose.CROUCHING), player.getZ());

        LinkedList<BlockPos> potentialBlockPos = new LinkedList<>();

        for(int i = -5; i <= 5; i++){
            int j_bound = (int) sqrt(36 - i * i);
            for(int j = -j_bound; j <= j_bound; j++){
                int k_bound = (int) sqrt(36 - i * i - j * j);
                for(int k = -k_bound; k <= k_bound; k++) {
                    BlockPos blockPos = playerBlockPos.add(new Vec3i(i, j, k));

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

                    if(!Registry.BLOCK.getId(blockState.getBlock()).getPath().equals("podzol")){
                        continue;
                    }

                    BlockState blockState_up = world.getBlockState(blockPos.add(0, 1, 0));

                    if(!blockState_up.isAir()){
                        continue;
                    }

                    potentialBlockPos.add(blockPos);
                }
            }
        }

        if(client.world == null){
            return;
        }

        if(potentialBlockPos.size() > 0) {
            double last_plant_time = Double.parseDouble(PythonProxy.globalMap.getOrDefault("last_plant_time", "0.0"));
            int last_plant_x = Integer.parseInt(PythonProxy.globalMap.getOrDefault("last_plant_x", "0"));
            int last_plant_y = Integer.parseInt(PythonProxy.globalMap.getOrDefault("last_plant_y", "0"));
            int last_plant_z = Integer.parseInt(PythonProxy.globalMap.getOrDefault("last_plant_z", "0"));

            BlockPos foundBlockPos = BlockPos.ORIGIN;

            if((coolDownNow - last_plant_time) > 5.0) {
                foundBlockPos = potentialBlockPos.getFirst();
            }else{
                Vec3i last_plant = new Vec3i(last_plant_x, last_plant_y, last_plant_z);

                foundBlockPos = potentialBlockPos.getFirst();
                double distance = foundBlockPos.getSquaredDistance(last_plant);

                potentialBlockPos.removeFirst();

                for(BlockPos pos : potentialBlockPos) {
                    double cur_distance = pos.getSquaredDistance(last_plant);

                    if(cur_distance < distance) {
                        foundBlockPos = pos;
                        distance = cur_distance;
                    }
                }
            }

            PythonProxy.globalMap.put("autoplace_cooldown", String.valueOf(coolDownNow));

            if(client.interactionManager != null) {
                PythonProxy.globalMap.put("autoplace_cancel_interaction", "True");

                client.interactionManager.interactBlock(player, client.world, Hand.MAIN_HAND, new BlockHitResult(new Vec3d(foundBlockPos.getX() + 0.5, foundBlockPos.getY() + 1, foundBlockPos.getZ() + 0.5), Direction.UP, foundBlockPos, false));
                PythonProxy.globalMap.put("autoplace_cooldown", String.valueOf(coolDownNow));

                PythonProxy.globalMap.put("last_plant_time", String.valueOf(coolDownNow));
                PythonProxy.globalMap.put("last_plant_x", String.valueOf(foundBlockPos.getX()));
                PythonProxy.globalMap.put("last_plant_y", String.valueOf(foundBlockPos.getY()));
                PythonProxy.globalMap.put("last_plant_z", String.valueOf(foundBlockPos.getZ()));

                player.sendMessage(Text.of("Planting..."), true);

                if(player.getMainHandStack().getCount() == 0) {
                    for (int i = 9; i <= 44; i++) {
                        Slot cur_slot = player.playerScreenHandler.getSlot(i);
                        if (cur_slot.hasStack() && cur_slot.getStack().getItem() == Registry.ITEM.get(new Identifier("spruce_sapling")) && cur_slot.getStack().getCount() >= 1) {
                            if (i != 36) {
                                assert client.interactionManager != null;
                                client.interactionManager.clickSlot(player.playerScreenHandler.syncId, i, 0, SlotActionType.SWAP, player);
                            }
                            player.inventory.selectedSlot = 0;

                            break;
                        }
                    }
                }
            }
        }else{
            player.sendMessage(Text.of("Go ahead"), true);
        }
    }
}
