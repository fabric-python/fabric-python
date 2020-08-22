package org.fabric_python.mod.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.util.math.MathHelper;
import org.fabric_python.mod.PythonProxy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(VehicleMoveC2SPacket.class)
public class EasyRowMixin {
    @Shadow
    protected double x;
    @Shadow
    protected double y;
    @Shadow
    protected double z;
    @Shadow
    protected float pitch;


    @Inject(at = @At("HEAD"), method = "write (Lnet/minecraft/network/PacketByteBuf;)V", cancellable = true)
    protected void write(PacketByteBuf packetByteBuf_1, CallbackInfo info) throws IOException {
        if (PythonProxy.globalMap == null) {
            return;
        }

        int easyRow = Integer.parseInt(PythonProxy.globalMap.getOrDefault("easyrow", "1"));
        if (easyRow == 0) {
            return;
        }

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null){
            return;
        }

        if(player.isRiding() && player.getRootVehicle() instanceof BoatEntity){
            BoatEntity boat = (BoatEntity) player.getRootVehicle();
            boat.updatePositionAndAngles(x, y, z, player.yaw, pitch);

            packetByteBuf_1.writeDouble(this.x);
            packetByteBuf_1.writeDouble(this.y);
            packetByteBuf_1.writeDouble(this.z);
            packetByteBuf_1.writeFloat(player.yaw);
            packetByteBuf_1.writeFloat(this.pitch);
            info.cancel();
        }
    }
}

