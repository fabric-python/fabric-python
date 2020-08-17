package org.fabric_python.mod.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.text.Text;
import org.fabric_python.mod.PythonProxy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Instant;

@Mixin(KeyboardInput.class)
public abstract class StudyPlayerNoRidingMixin extends Input {
    @Inject(at = @At("HEAD"), method = "tick(Z)V", cancellable = true)
    protected void tick(CallbackInfo info){
        if(isStudyOn()){
            this.pressingForward = false;
            this.pressingBack = false;
            this.pressingLeft = false;
            this.pressingRight = false;
            this.movementForward = 0.0F;
            this.movementSideways = 0.0F;
            this.jumping = false;
            this.sneaking = false;

            info.cancel();
        }
    }

    private boolean isStudyOn() {
        if(PythonProxy.globalMap == null){
            return false;
        }

        boolean currentStudy = Boolean.parseBoolean(PythonProxy.globalMap.getOrDefault("study_locked", "False"));
        if(!currentStudy) {
            return false;
        }

        long currentStudyUntil = Long.parseLong(PythonProxy.globalMap.getOrDefault("study_locked_until", "0"));
        long now = Instant.now().getEpochSecond();

        if (now >= currentStudyUntil) {
            PythonProxy.globalMap.put("study_locked", "False");
            if(MinecraftClient.getInstance().player !=null){
                MinecraftClient.getInstance().player.sendMessage(Text.method_30163("Study mode ends"), false);
            }
            return false;
        }

        return true;
    }
}
