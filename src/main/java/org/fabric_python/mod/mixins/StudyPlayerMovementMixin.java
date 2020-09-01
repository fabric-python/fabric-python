package org.fabric_python.mod.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.fabric_python.mod.PythonProxy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.time.Instant;

@Mixin(ClientPlayerEntity.class)
public abstract class StudyPlayerMovementMixin {
    @Inject(at = @At("HEAD"), method = "canMoveVoluntarily ()Z", cancellable = true)
    protected void canMoveVoluntarily(CallbackInfoReturnable<Boolean> info){
        isStudyOn(info);
    }

    @Inject(at = @At("HEAD"), method = "startRiding (Lnet/minecraft/entity/Entity;Z)Z", cancellable = true)
    protected void startRiding(Entity entity_1, boolean boolean_1, CallbackInfoReturnable<Boolean> info){
        isStudyOn(info);
    }

    private void isStudyOn(CallbackInfoReturnable<Boolean> info) {
        if(PythonProxy.globalMap == null){
            return;
        }

        boolean currentStudy = Boolean.parseBoolean(PythonProxy.globalMap.getOrDefault("study_locked", "False"));
        if(!currentStudy) {
            return;
        }

        long currentStudyUntil = Long.parseLong(PythonProxy.globalMap.getOrDefault("study_locked_until", "0"));
        long now = Instant.now().getEpochSecond();

        if (now >= currentStudyUntil) {
            PythonProxy.globalMap.put("study_locked", "False");
            if(MinecraftClient.getInstance().player !=null){
                MinecraftClient.getInstance().player.sendMessage(Text.of("Study mode ends"), false);
            }
            return;
        }

        info.setReturnValue(false);
    }
}
