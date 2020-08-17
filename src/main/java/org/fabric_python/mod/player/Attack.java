package org.fabric_python.mod.player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.fabric_python.mod.PythonProxy;
import org.fabric_python.mod.TaskWorker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

        Optional<EntityType<?>> type = EntityType.get(info.getOrDefault("type", "null"));

        if(!type.isPresent()){
            Map<String, String> res = new HashMap<>();
            res.put("res", "type not found");
            PythonProxy.outbox.sendMsg(info.get("sid"), res);
        }
        
        assert player != null;
        Predicate<Entity> predicate = p -> type.isPresent() && p.getType()==type.get();
        List<Entity> target = player.getEntityWorld().getEntities(player, box, predicate);

        if(target.size() != 0){
            assert client.interactionManager != null;
            client.interactionManager.attackEntity(player, target.get(0));
        }

        Map<String, String> res = new HashMap<>();
        res.put("res", "attacked");
        PythonProxy.outbox.sendMsg(info.get("sid"), res);
    }
}
