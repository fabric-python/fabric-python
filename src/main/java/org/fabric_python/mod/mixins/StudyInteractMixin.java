package org.fabric_python.mod.mixins;

import net.minecraft.client.MinecraftClient;
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
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import org.fabric_python.mod.PythonProxy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.time.Instant;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class StudyInteractMixin {
    @Inject(at = @At("HEAD"), method = "interactEntity (Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;", cancellable = true)
    protected void interactEntity(PlayerEntity playerEntity_1, Entity entity_1, Hand hand_1, CallbackInfoReturnable<ActionResult> info) {
        if (entity_1 instanceof AbstractDonkeyEntity || entity_1 instanceof BoatEntity || entity_1 instanceof HorseEntity || entity_1 instanceof MinecartEntity || entity_1 instanceof PigEntity || entity_1 instanceof SkeletonHorseEntity || entity_1 instanceof StriderEntity || entity_1 instanceof ZombieHorseEntity) {
            isStudyOn(info);
        }
    }

    @Inject(at = @At("HEAD"), method = "interactEntityAtLocation (Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/hit/EntityHitResult;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;", cancellable = true)
    protected void interactEntityAtLocation(PlayerEntity playerEntity_1, Entity entity_1, EntityHitResult entityHitResult_1, Hand hand_1, CallbackInfoReturnable<ActionResult> info) {
        if (entity_1 instanceof AbstractDonkeyEntity || entity_1 instanceof BoatEntity || entity_1 instanceof HorseEntity || entity_1 instanceof MinecartEntity || entity_1 instanceof PigEntity || entity_1 instanceof SkeletonHorseEntity || entity_1 instanceof StriderEntity || entity_1 instanceof ZombieHorseEntity) {
            isStudyOn(info);
        }
    }

    private void isStudyOn(CallbackInfoReturnable<ActionResult> info) {
        if (PythonProxy.globalMap == null) {
            return;
        }

        boolean currentStudy = Boolean.parseBoolean(PythonProxy.globalMap.getOrDefault("study_locked", "False"));
        if (!currentStudy) {
            return;
        }

        long currentStudyUntil = Long.parseLong(PythonProxy.globalMap.getOrDefault("study_locked_until", "0"));
        long now = Instant.now().getEpochSecond();

        if (now >= currentStudyUntil) {
            PythonProxy.globalMap.put("study_locked", "False");
            if (MinecraftClient.getInstance().player != null) {
                MinecraftClient.getInstance().player.sendMessage(Text.method_30163("Study mode ends"), false);
            }
            return;
        }

        info.setReturnValue(ActionResult.PASS);
    }
}
