package org.fabric_python.mod.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
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
import org.fabric_python.mod.mob.AutoAttackPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Instant;
import java.util.*;
import java.util.function.Predicate;

import static java.lang.Math.sqrt;

@Mixin(ClientPlayerEntity.class)
public abstract class AutoAttackMixin {
    @Inject(at = @At("RETURN"), method = "sendMovementPackets ()V")
    private void sendMovementPackets(CallbackInfo info) {
        if (PythonProxy.globalMap == null) {
            return;
        }

        int autoattack = Integer.parseInt(PythonProxy.globalMap.getOrDefault("autoattack", "0"));
        if (autoattack == 0) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if(client == null){
            return;
        }

        if(client.interactionManager == null){
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

        if(player.getMainHandStack() == null){
            return;
        }

        if(!Registry.ITEM.getId(player.getMainHandStack().getItem()).toString().contains("sword")){
            return;
        }

        double coolDownNow = Instant.now().getEpochSecond() * 1000 + Instant.now().getNano() / 1000.0 / 1000;
        double coolDownLast = Double.parseDouble(PythonProxy.globalMap.getOrDefault("autoattack_cooldown", "0.0"));

        if(coolDownNow - coolDownLast <= 625) {
            return;
        }

        PythonProxy.globalMap.put("autoattack_cooldown", String.valueOf(coolDownNow));

        BlockPos blockPos = player.getBlockPos();
        int x = blockPos.getX();
        int y = blockPos.getY();
        int z = blockPos.getZ();

        Vec3d box_1 = new Vec3d(x - 6, y - 6, z - 6);
        Vec3d box_2 = new Vec3d(x + 6, y + 6, z + 6);

        Box box = new Box(box_1, box_2);

        Predicate<Entity> predicate = p -> (AutoAttackPredicate.shouldAutoAttack(p)) && (p.distanceTo(player) <= 6);
        List<Entity> target = player.getEntityWorld().getEntities(player, box, predicate);

        if(target.size() != 0){
            client.interactionManager.attackEntity(player, target.get(0));
            player.sendMessage(Text.method_30163("Auto attacked"), true);
        }
    }
}
