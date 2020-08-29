package org.fabric_python.mod.block;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.OptionalInt;

public class SafeMineProvider {
    public static ArrayList<BlockPos> CreateGoodAirMap(MinecraftClient client, ClientPlayerEntity player, World world, BlockPos start, int rangeLimit, OptionalInt x_lower_bound, OptionalInt x_upper_bound, OptionalInt y_lower_bound, OptionalInt y_upper_bound, OptionalInt z_lower_bound, OptionalInt z_upper_bound) {
        // create the nearby "air" blocks list
        ArrayList<BlockPos> nearbyAirList = new ArrayList<>();
        nearbyAirList.add(start);

        int x_new_lower_limit;
        if(x_lower_bound.isPresent()){
            x_new_lower_limit = Math.max(x_lower_bound.getAsInt(), -rangeLimit);
        }else{
            x_new_lower_limit = -rangeLimit;
        }

        int x_new_upper_limit;
        if(x_upper_bound.isPresent()){
            x_new_upper_limit = Math.min(x_upper_bound.getAsInt(), rangeLimit);
        }else{
            x_new_upper_limit = rangeLimit;
        }

        for (int x = x_new_lower_limit; x <= x_new_upper_limit; x++) {
            int y_limit = (int) Math.sqrt(rangeLimit * rangeLimit - x * x);

            int y_new_lower_limit;
            if(y_lower_bound.isPresent()){
                y_new_lower_limit = Math.max(y_lower_bound.getAsInt(), -y_limit);
            }else{
                y_new_lower_limit = -y_limit;
            }

            int y_new_upper_limit;
            if(y_upper_bound.isPresent()){
                y_new_upper_limit = Math.min(y_upper_bound.getAsInt(), y_limit);
            }else{
                y_new_upper_limit = y_limit;
            }

            for (int y = y_new_lower_limit; y <= y_new_upper_limit; y++) {
                int z_limit = (int) Math.sqrt(rangeLimit * rangeLimit - x * x - y * y);

                int z_new_lower_limit;
                if(z_lower_bound.isPresent()){
                    z_new_lower_limit = Math.max(z_lower_bound.getAsInt(), -z_limit);
                }else{
                    z_new_lower_limit = -z_limit;
                }

                int z_new_upper_limit;
                if(z_upper_bound.isPresent()){
                    z_new_upper_limit = Math.min(z_upper_bound.getAsInt(), z_limit);
                }else{
                    z_new_upper_limit = z_limit;
                }

                for (int z = z_new_lower_limit; z <= z_new_upper_limit; z++) {
                    BlockPos thisBlockPos = player.getBlockPos().add(x, y, z);
                    BlockState thisBlockState = world.getBlockState(thisBlockPos);

                    if (!(thisBlockState.getCollisionShape(world, thisBlockPos) == VoxelShapes.empty())) {
                        continue;
                    }

                    nearbyAirList.add(thisBlockPos);
                }
            }
        }

        ArrayList<BlockPos> nearbyAirGoodList = new ArrayList<>();
        propogate(player.getBlockPos(), nearbyAirList, nearbyAirGoodList);

        return nearbyAirGoodList;
    }

    protected static void propogate(BlockPos start, ArrayList<BlockPos> nearbyAirList, ArrayList<BlockPos> nearbyAirGoodList) {
        if (nearbyAirList.contains(start)) {
            nearbyAirList.remove(start);
            nearbyAirGoodList.add(start);
        }

        // check -1, 0, 0
        BlockPos blockPos_1 = start.add(-1, 0, 0);
        if (nearbyAirList.contains(blockPos_1)) {
            propogate(blockPos_1, nearbyAirList, nearbyAirGoodList);
        }

        // check 0, -1, 0
        BlockPos blockPos_2 = start.add(0, -1, 0);
        if (nearbyAirList.contains(blockPos_2)) {
            propogate(blockPos_2, nearbyAirList, nearbyAirGoodList);
        }

        // check 0, 0, -1
        BlockPos blockPos_3 = start.add(0, 0, -1);
        if (nearbyAirList.contains(blockPos_3)) {
            propogate(blockPos_3, nearbyAirList, nearbyAirGoodList);
        }

        // check 1, 0, 0
        BlockPos blockPos_4 = start.add(1, 0, 0);
        if (nearbyAirList.contains(blockPos_4)) {
            propogate(blockPos_4, nearbyAirList, nearbyAirGoodList);
        }

        // check 0, 1, 0
        BlockPos blockPos_5 = start.add(0, 1, 0);
        if (nearbyAirList.contains(blockPos_5)) {
            propogate(blockPos_5, nearbyAirList, nearbyAirGoodList);
        }

        // check 0, 0, 1
        BlockPos blockPos_6 = start.add(0, 0, 1);
        if (nearbyAirList.contains(blockPos_6)) {
            propogate(blockPos_6, nearbyAirList, nearbyAirGoodList);
        }
    }
}
