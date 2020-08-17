package org.fabric_python.mod.mixins;

import net.minecraft.block.*;
import org.fabric_python.mod.PythonProxy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class BlockOpaqueMixin {
    @Shadow
    protected abstract Block getBlock();

    @Inject(at = @At("HEAD"), method = "isOpaque ()Z", cancellable = true)
    protected void renderBlocking(CallbackInfoReturnable<Boolean> info) {
        boolean isXrayOn = PythonProxy.globalMap != null && PythonProxy.globalMap.getOrDefault("xray", "0").equals("1");
        if(isXrayOn) {
            info.setReturnValue(false);
        }
    }
}
