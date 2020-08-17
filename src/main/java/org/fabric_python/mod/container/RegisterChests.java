package org.fabric_python.mod.container;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.fabric_python.mod.PythonProxy;
import org.fabric_python.mod.TaskWorker;
import org.fabric_python.mod.db.ChestEntry;
import org.fabric_python.mod.db.DBManager;

import java.sql.SQLException;
import java.util.*;

import static net.minecraft.block.HorizontalFacingBlock.FACING;

public class RegisterChests implements TaskWorker {
    @Override
    public void onTask(MinecraftClient client, Map<String, String> info) {
        String groupName = info.getOrDefault("group", "default");
        int x = Integer.parseInt(info.getOrDefault("x", String.valueOf(0)));
        int y = Integer.parseInt(info.getOrDefault("y", String.valueOf(0)));
        int z = Integer.parseInt(info.getOrDefault("z", String.valueOf(0)));

        ClientPlayerEntity player = client.player;

        if(player == null) {
            Map<String, String> res = new HashMap<>();
            res.put("res", "cannot find the player object");
            PythonProxy.outbox.sendMsg(info.get("sid"), res);

            return;
        }

        World world = player.world;

        BlockPos start = new BlockPos(x, y, z);
        BlockState blockState = world.getBlockState(start);

        if (!(blockState.getBlock() instanceof ChestBlock)) {
            Map<String, String> res = new HashMap<>();
            res.put("res", "not a chest block");
            PythonProxy.outbox.sendMsg(info.get("sid"), res);

            return;
        }

        Direction dir = blockState.get(FACING);

        int x_delta = 0;
        int z_delta = 0;

        if (dir == Direction.EAST || dir == Direction.WEST) {
            BlockPos east = new BlockPos(x + 1, y, z);
            if (world.getBlockState(east).getBlock() instanceof ChestBlock) {
                x_delta = 1;
            } else {
                x_delta = -1;
            }
        } else {
            BlockPos south = new BlockPos(x, y, z + 1);
            if (world.getBlockState(south).getBlock() instanceof ChestBlock) {
                z_delta = 1;
            } else {
                z_delta = -1;
            }
        }

        int height = 1;
        while (true) {
            BlockPos up = new BlockPos(x, y + height, z);

            if (world.getBlockState(up).getBlock() instanceof ChestBlock) {
                height += 1;
            } else {
                break;
            }
        }

        LinkedList<BlockPos> list = new LinkedList<>();

        if (x_delta != 0) {
            boolean breakFlag = false;
            for (int i = x; !breakFlag; i += x_delta) {
                for (int j = y; j < y + height; j++) {
                    BlockPos thisBlock = new BlockPos(i, j, z);

                    if (world.getBlockState(thisBlock).getBlock() instanceof ChestBlock) {
                        list.add(new BlockPos(i, j, z));
                    } else {
                        breakFlag = true;
                        break;
                    }
                }
            }
        }else{
            boolean breakFlag = false;
            for (int i = z; !breakFlag; i += z_delta) {
                for (int j = y; j < y + height; j++) {
                    BlockPos thisBlock = new BlockPos(x, j, i);

                    if (world.getBlockState(thisBlock).getBlock() instanceof ChestBlock) {
                        list.add(new BlockPos(x, j, i));
                    } else {
                        breakFlag = true;
                        break;
                    }
                }
            }
        }


        DBManager db = new DBManager();
        try {
            db.loadDatabase(client);

            LinkedList<ChestEntry> emptyList = new LinkedList<>();
            String emptyHash = db.computeHash(emptyList);
            for (BlockPos blockPos : list) {
                db.initHash(groupName, blockPos.getX(), blockPos.getY(), blockPos.getZ(), emptyHash);
            }
            db.close();
        } catch (SQLException e) {
            Map<String, String> res = new HashMap<>();
            res.put("res", e.getSQLState() + e.getMessage() + Arrays.toString(e.getStackTrace()));
            PythonProxy.outbox.sendMsg(info.get("sid"), res);
            return;
        }

        Map<String, String> res = new HashMap<>();
        res.put("res", String.valueOf(list.size()));
        PythonProxy.outbox.sendMsg(info.get("sid"), res);
    }
}

