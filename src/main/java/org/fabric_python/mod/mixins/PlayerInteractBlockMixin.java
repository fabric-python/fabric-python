package org.fabric_python.mod.mixins;

import net.minecraft.entity.player.PlayerEntity;
import org.fabric_python.mod.PythonProxy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerInteractBlockMixin {
    @Inject(at = @At("HEAD"), method = "shouldCancelInteraction ()Z", cancellable = true)
    protected void shouldCancelInteraction(CallbackInfoReturnable<Boolean> info) {
        if (PythonProxy.globalMap == null) {
            return;
        }

        boolean autotorch_cancel_interaction = Boolean.parseBoolean(PythonProxy.globalMap.getOrDefault("autotorch_cancel_interaction", "False"));
        if (autotorch_cancel_interaction) {
            info.setReturnValue(true);
        }
    }
}
