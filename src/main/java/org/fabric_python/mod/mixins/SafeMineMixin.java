package org.fabric_python.mod.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.fabric_python.mod.PythonProxy;
import org.fabric_python.mod.block.SafeMineProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.OptionalInt;

@Mixin(ClientPlayerInteractionManager.class)
public class SafeMineMixin {
    @Inject(at = @At("HEAD"), method = "updateBlockBreakingProgress (Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z", cancellable = true)
    protected void updateBlockBreakingProgress(BlockPos blockPos, Direction _direction_1, CallbackInfoReturnable<Boolean> info) {
        if (PythonProxy.globalMap == null) {
            return;
        }

        int safeMine = Integer.parseInt(PythonProxy.globalMap.getOrDefault("safemine", "0"));
        if (safeMine == 0) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        World world = client.world;
        ClientPlayerEntity player = client.player;

        if (world == null) {
            return;
        }

        if (player == null) {
            return;
        }

        ArrayList<BlockPos> nearbyAirGoodList = SafeMineProvider.CreateGoodAirMap(client, player, world, player.getBlockPos(), 8, OptionalInt.empty(), OptionalInt.empty(), OptionalInt.empty(), OptionalInt.empty(), OptionalInt.empty(), OptionalInt.empty());

        ArrayList<BlockPos> checkList = new ArrayList<>();

        boolean willFall = false;
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.isOpaqueFullCube(world, blockPos)) {
            BlockPos blockPos_up = blockPos.add(0, 1, 0);
            BlockState blockState_up = world.getBlockState(blockPos_up);
            if(blockState_up.getBlock() instanceof FallingBlock){
                BlockPos blockPos_up_up = blockPos_up.add(0, 1, 0);
                BlockState blockState_up_up = world.getBlockState(blockPos_up_up);

                if (!blockState_up_up.isOpaqueFullCube(world, blockPos_up_up) && !nearbyAirGoodList.contains(blockPos_up_up)) {
                    willFall = true;
                }
            }

            // check -1, 0, 0
            BlockPos blockPos_1 = blockPos.add(-1, 0, 0);
            BlockState blockState_1 = world.getBlockState(blockPos_1);
            if (!blockState_1.isOpaqueFullCube(world, blockPos_1)) {
                checkList.add(blockPos_1);
            }

            // check 0, -1, 0
            BlockPos blockPos_2 = blockPos.add(0, -1, 0);
            BlockState blockState_2 = world.getBlockState(blockPos_2);
            if (!blockState_2.isOpaqueFullCube(world, blockPos_2)) {
                checkList.add(blockPos_2);
            }

            // check 0, 0, -1
            BlockPos blockPos_3 = blockPos.add(0, 0, -1);
            BlockState blockState_3 = world.getBlockState(blockPos_3);
            if (!blockState_3.isOpaqueFullCube(world, blockPos_3)) {
                checkList.add(blockPos_3);
            }

            // check 1, 0, 0
            BlockPos blockPos_4 = blockPos.add(1, 0, 0);
            BlockState blockState_4 = world.getBlockState(blockPos_4);
            if (!blockState_4.isOpaqueFullCube(world, blockPos_4)) {
                checkList.add(blockPos_4);
            }

            // check 0, -1, 0
            BlockPos blockPos_5 = blockPos.add(0, 1, 0);
            BlockState blockState_5 = world.getBlockState(blockPos_5);
            if (!blockState_5.isOpaqueFullCube(world, blockPos_5)) {
                checkList.add(blockPos_5);
            }

            // check 0, 0, -1
            BlockPos blockPos_6 = blockPos.add(0, 0, 1);
            BlockState blockState_6 = world.getBlockState(blockPos_6);
            if (!blockState_6.isOpaqueFullCube(world, blockPos_6)) {
                checkList.add(blockPos_6);
            }
        }

        if(willFall){
            player.sendMessage(Text.of("Unsafe mining"), true);
            info.setReturnValue(false);
        }

        if (checkList.isEmpty()) {
            return;
        }


        boolean shouldMine = true;
        for (BlockPos blockPosToCheck : checkList) {
            if (!nearbyAirGoodList.contains((blockPosToCheck))) {
                shouldMine = false;
                break;
            }
        }

        if (!shouldMine) {
            player.sendMessage(Text.of("Unsafe mining"), true);
            info.setReturnValue(false);
        }
    }
}
