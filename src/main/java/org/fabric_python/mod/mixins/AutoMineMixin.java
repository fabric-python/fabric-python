package org.fabric_python.mod.mixins;

import net.minecraft.client.MinecraftClient;
import org.fabric_python.mod.PythonProxy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class AutoMineMixin {
    @Inject(at = @At("HEAD"), method = "handleBlockBreaking (Z)V", cancellable = true)
    protected void handleBlockBreaking(boolean pressed, CallbackInfo info){
        boolean currentAutoMine = PythonProxy.globalMap != null && PythonProxy.globalMap.getOrDefault("automine", "0").equals("1");
        if(currentAutoMine){
            info.cancel();
        }
    }
}
