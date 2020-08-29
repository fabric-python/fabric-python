package org.fabric_python.mod.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.TorchBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.fabric_python.mod.PythonProxy;
import org.fabric_python.mod.TaskWorker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;

public class FindSafeMineBlock implements TaskWorker {
    @Override
    public void onTask(MinecraftClient client, Map<String, String> info) {
        boolean found = false;
        BlockPos foundPos = BlockPos.ORIGIN;
        BlockPos foundStandingPlace = BlockPos.ORIGIN;

        ClientPlayerEntity player = client.player;
        if (player == null) {
            Map<String, String> res = new HashMap<>();
            res.put("error", "cannot find the player");
            PythonProxy.outbox.sendMsg(info.get("sid"), res);

            return;
        }

        World world = client.world;
        if (world == null) {
            Map<String, String> res = new HashMap<>();
            res.put("error", "cannot find the world");
            PythonProxy.outbox.sendMsg(info.get("sid"), res);

            return;
        }

        ArrayList<BlockPos> nearbyAirGoodList = SafeMineProvider.CreateGoodAirMap(client, player, world, player.getBlockPos(), 12, OptionalInt.empty(), OptionalInt.empty(), OptionalInt.of(-1), OptionalInt.of(2), OptionalInt.empty(), OptionalInt.empty());

        int y_lower_limit = 0;
        int y_upper_limit = 1;

        for (int range = 1; range <= 20; range++) {
            for (int x = -range; x <= range; x++) {
                int z_limit = (int) Math.sqrt(range * range - x * x);
                for (int z = -z_limit; z <= z_limit; z++) {
                    int y_limit = (int) Math.sqrt(range * range - x * x - z * z);
                    int y_new_upper_limit = Math.min(y_upper_limit, y_limit);
                    for (int y = y_new_upper_limit; y >= y_lower_limit; y--) {
                        if (x * x + y * y + z * z <= (range - 1) * (range - 1)) {
                            continue;
                        }

                        BlockPos blockPos = player.getBlockPos().add(x, y, z);
                        BlockState blockState = world.getBlockState(blockPos);
                        if (!blockState.isOpaqueFullCube(world, blockPos)) {
                            continue;
                        }

                        BlockPos blockPos_up = blockPos.add(0, 1, 0);
                        BlockState blockState_up = world.getBlockState(blockPos_up);
                        if(blockState_up.getBlock() instanceof FallingBlock){
                            BlockPos blockPos_up_up = blockPos_up.add(0, 1, 0);
                            BlockState blockState_up_up = world.getBlockState(blockPos_up_up);

                            if (!blockState_up_up.isOpaqueFullCube(world, blockPos_up_up) && !nearbyAirGoodList.contains(blockPos_up_up)) {
                                continue;
                            }
                        }

                        ArrayList<BlockPos> checkList = new ArrayList<>();
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

                        // check 0, 1, 0
                        BlockPos blockPos_5 = blockPos.add(0, 1, 0);
                        BlockState blockState_5 = world.getBlockState(blockPos_5);
                        if (!blockState_5.isOpaqueFullCube(world, blockPos_5)) {
                            checkList.add(blockPos_5);
                        }

                        // check 0, 0, 1
                        BlockPos blockPos_6 = blockPos.add(0, 0, 1);
                        BlockState blockState_6 = world.getBlockState(blockPos_6);
                        if (!blockState_6.isOpaqueFullCube(world, blockPos_6)) {
                            checkList.add(blockPos_6);
                        }

                        boolean shouldMine = true;
                        for (BlockPos blockPosToCheck : checkList) {
                            if (!nearbyAirGoodList.contains((blockPosToCheck))) {
                                shouldMine = false;
                                break;
                            }
                        }

                        if (!shouldMine) {
                            continue;
                        }

                        ArrayList<BlockPos> standingList = new ArrayList<>();

                        // check -1, 0, 0
                        BlockPos blockStandingPos_1 = new BlockPos(blockPos.getX() - 1, player.getBlockPos().getY(), blockPos.getZ());
                        BlockState blockStandingState_1 = world.getBlockState(blockStandingPos_1);
                        BlockState blockStandingState_1_up = world.getBlockState(blockStandingPos_1.add(0, 1, 0));
                        BlockState blockStandingState_1_down = world.getBlockState(blockStandingPos_1.add(0, -1, 0));
                        if ((blockStandingState_1.isAir() || blockStandingState_1.getBlock() instanceof TorchBlock) && blockStandingState_1_up.isAir()
                                && blockStandingState_1_down.isOpaqueFullCube(world, blockStandingPos_1.add(0, -1, 0))) {
                            standingList.add(blockStandingPos_1);
                        }

                        // check 0, 0, -1
                        BlockPos blockStandingPos_2 = new BlockPos(blockPos.getX(), player.getBlockPos().getY(), blockPos.getZ() - 1);
                        BlockState blockStandingState_2 = world.getBlockState(blockStandingPos_2);
                        BlockState blockStandingState_2_up = world.getBlockState(blockStandingPos_2.add(0, 1, 0));
                        BlockState blockStandingState_2_down = world.getBlockState(blockStandingPos_2.add(0, -1, 0));
                        if ((blockStandingState_2.isAir() || blockStandingState_2.getBlock() instanceof TorchBlock) && blockStandingState_2_up.isAir()
                                && blockStandingState_2_down.isOpaqueFullCube(world, blockStandingPos_2.add(0, -1, 0))) {
                            standingList.add(blockStandingPos_2);
                        }

                        // check 1, 0, 0
                        BlockPos blockStandingPos_3 = new BlockPos(blockPos.getX() + 1, player.getBlockPos().getY(), blockPos.getZ());
                        BlockState blockStandingState_3 = world.getBlockState(blockStandingPos_3);
                        BlockState blockStandingState_3_up = world.getBlockState(blockStandingPos_3.add(0, 1, 0));
                        BlockState blockStandingState_3_down = world.getBlockState(blockStandingPos_3.add(0, -1, 0));
                        if ((blockStandingState_3.isAir() || blockStandingState_3.getBlock() instanceof TorchBlock) && blockStandingState_3_up.isAir()
                                && blockStandingState_3_down.isOpaqueFullCube(world, blockStandingPos_3.add(0, -1, 0))) {
                            standingList.add(blockStandingPos_3);
                        }

                        // check 0, 0, 1
                        BlockPos blockStandingPos_4 = new BlockPos(blockPos.getX(), player.getBlockPos().getY(), blockPos.getZ() + 1);
                        BlockState blockStandingState_4 = world.getBlockState(blockStandingPos_4);
                        BlockState blockStandingState_4_up = world.getBlockState(blockStandingPos_4.add(0, 1, 0));
                        BlockState blockStandingState_4_down = world.getBlockState(blockStandingPos_2.add(0, -1, 0));
                        if ((blockStandingState_4.isAir() || blockStandingState_4.getBlock() instanceof TorchBlock) && blockStandingState_4_up.isAir()
                                && blockStandingState_4_down.isOpaqueFullCube(world, blockStandingPos_4.add(0, -1, 0))) {
                            standingList.add(blockStandingPos_4);
                        }


                        double nearbyStandingPlaceDistance = 0;
                        BlockPos nearbyStandingPlace = null;

                        for (BlockPos standingBlockPos : standingList) {
                            if (nearbyStandingPlace == null) {
                                nearbyStandingPlace = standingBlockPos;
                                nearbyStandingPlaceDistance = player.getPos().distanceTo(new Vec3d(standingBlockPos.getX(), standingBlockPos.getY(), standingBlockPos.getZ()));
                            } else {
                                double distance = player.getPos().distanceTo(new Vec3d(standingBlockPos.getX(), standingBlockPos.getY(), standingBlockPos.getZ()));
                                if (distance < nearbyStandingPlaceDistance) {
                                    nearbyStandingPlaceDistance = distance;
                                    nearbyStandingPlace = standingBlockPos;
                                }
                            }
                        }

                        if (nearbyStandingPlace != null) {
                            found = true;
                            foundPos = blockPos;
                            foundStandingPlace = nearbyStandingPlace;
                            break;
                        }
                    }

                    if (found) {
                        break;
                    }
                }

                if (found) {
                    break;
                }
            }

            if (found) {
                break;
            }
        }


        Map<String, String> res = new HashMap<>();
        res.put("x", String.valueOf(foundPos.getX()));
        res.put("y", String.valueOf(foundPos.getY()));
        res.put("z", String.valueOf(foundPos.getZ()));

        res.put("x_standing", String.valueOf(foundStandingPlace.getX()));
        res.put("y_standing", String.valueOf(foundStandingPlace.getY()));
        res.put("z_standing", String.valueOf(foundStandingPlace.getZ()));

        res.put("block_type", Registry.BLOCK.getId(world.getBlockState(foundPos).getBlock()).toString());

        PythonProxy.outbox.sendMsg(info.get("sid"), res);
    }
}

