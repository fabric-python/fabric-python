package org.fabric_python.mod.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.mob.ZombieHorseEntity;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.fabric_python.mod.PythonProxy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;

@Mixin(PlayerMoveC2SPacket.class)
public class SafeFallMixin {
    @Inject(at = @At("HEAD"), method = "write (Lnet/minecraft/network/PacketByteBuf;)V", cancellable = true)
    protected void write(PacketByteBuf packetByteBuf_1, CallbackInfo info) throws IOException {
        if (PythonProxy.globalMap == null) {
            return;
        }

        int safeFall = Integer.parseInt(PythonProxy.globalMap.getOrDefault("safefall", "0"));
        if (safeFall == 0) {
            return;
        }

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null){
            return;
        }

        packetByteBuf_1.writeByte(1);
        info.cancel();
    }
}
