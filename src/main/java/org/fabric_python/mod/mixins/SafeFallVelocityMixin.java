package org.fabric_python.mod.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.Vec3d;
import org.fabric_python.mod.PythonProxy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class SafeFallVelocityMixin {
    @Shadow
    protected Vec3d velocity;

    @Shadow
    protected abstract EntityType<?> getType();

    @Inject(at = @At("HEAD"), method = "setVelocity (Lnet/minecraft/util/math/Vec3d;)V", cancellable = true)
    protected void setVelocity(Vec3d vec3d_1, CallbackInfo info){
        if(getType() == EntityType.PLAYER){
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

            vec3d_1 = vec3d_1.multiply(9.9 / vec3d_1.length());
            this.velocity = vec3d_1;
        }
    }
}
