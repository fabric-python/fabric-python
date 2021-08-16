package org.fabric_python.mod.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.fabric_python.mod.PythonProxy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class SneakingMixin {
    @Inject(at = @At("HEAD"), method = "adjustMovementForSneaking (Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/entity/MovementType;)Lnet/minecraft/util/math/Vec3d;", cancellable = true)
    protected void adjustMovementForSneaking(Vec3d vec3d_1, MovementType movementType_1, CallbackInfoReturnable<Vec3d> info) {
        if (PythonProxy.globalMap == null) {
            return;
        }

        int sneaking = Integer.parseInt(PythonProxy.globalMap.getOrDefault("sneaking", "0"));
        if (sneaking == 0) {
            return;
        }

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return;
        }

        if (player.isOnGround()) {
            double double_1 = vec3d_1.x;
            double double_2 = vec3d_1.z;
            double var7 = 0.05D;


            while (double_1 != 0.0D && player.world.isSpaceEmpty(player, player.getBoundingBox().offset(double_1, (double) (-player.stepHeight), 0.0D))) {
                if (double_1 < 0.05D && double_1 >= -0.05D) {
                    double_1 = 0.0D;
                } else if (double_1 > 0.0D) {
                    double_1 -= 0.05D;
                } else {
                    double_1 += 0.05D;
                }
            }

            while (double_2 != 0.0D && player.world.isSpaceEmpty(player, player.getBoundingBox().offset(0.0D, (double) (-player.stepHeight), double_2))) {
                if (double_2 < 0.05D && double_2 >= -0.05D) {
                    double_2 = 0.0D;
                } else if (double_2 > 0.0D) {
                    double_2 -= 0.05D;
                } else {
                    double_2 += 0.05D;
                }
            }

            while (double_1 != 0.0D && double_2 != 0.0D && player.world.isSpaceEmpty(player, player.getBoundingBox().offset(double_1, (double) (-player.stepHeight), double_2))) {
                if (double_1 < 0.05D && double_1 >= -0.05D) {
                    double_1 = 0.0D;
                } else if (double_1 > 0.0D) {
                    double_1 -= 0.05D;
                } else {
                    double_1 += 0.05D;
                }

                if (double_2 < 0.05D && double_2 >= -0.05D) {
                    double_2 = 0.0D;
                } else if (double_2 > 0.0D) {
                    double_2 -= 0.05D;
                } else {
                    double_2 += 0.05D;
                }
            }

            vec3d_1 = new Vec3d(double_1, vec3d_1.y, double_2);
            info.setReturnValue(vec3d_1);
        } else {
            info.setReturnValue(vec3d_1);
        }
    }
}
