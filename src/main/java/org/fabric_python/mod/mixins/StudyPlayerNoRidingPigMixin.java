package org.fabric_python.mod.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.text.Text;
import org.fabric_python.mod.PythonProxy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.time.Instant;

@Mixin(PigEntity.class)
public class StudyPlayerNoRidingPigMixin {
    @Inject(at = @At("HEAD"), method = "canBeControlledByRider ()Z", cancellable = true)
    protected void canBeControlledByRider(CallbackInfoReturnable<Boolean> info){
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
                MinecraftClient.getInstance().player.sendMessage(Text.method_30163("Study mode ends"), false);
            }
            return;
        }

        info.setReturnValue(false);
    }
}
