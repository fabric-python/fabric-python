package org.fabric_python.mod.player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.fabric_python.mod.PythonProxy;
import org.fabric_python.mod.TaskWorker;

import java.util.HashMap;
import java.util.Map;

public class Rowing implements TaskWorker {
    @Override
    public void onTask(MinecraftClient client, Map<String, String> info) {
        int dest_x = Integer.parseInt(info.getOrDefault("x", String.valueOf(0)));
        int dest_y = Integer.parseInt(info.getOrDefault("y", String.valueOf(0)));
        int dest_z = Integer.parseInt(info.getOrDefault("z", String.valueOf(0)));

        Vec3d dest = new Vec3d(dest_x, dest_y, dest_z);

        assert client.player != null;
        ClientPlayerEntity player = client.player;
        if(player.getVehicle() != null && player.getVehicle() instanceof BoatEntity){
            BoatEntity boat = (BoatEntity) player.getVehicle();
            Vec3d pos = boat.getPos();

            Vec3d delta = new Vec3d(dest_x - pos.x, dest_y - pos.y, dest_z - pos.z);
            delta = delta.multiply(20 / delta.length());

            boat.setYaw(changeAngle(boat.yaw, (float) (MathHelper.atan2(delta.z, delta.x) * 57.2957763671875D) - 90.0F));

            boat.move(MovementType.SELF, delta);
            boat.updatePassengerPosition(player);
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
