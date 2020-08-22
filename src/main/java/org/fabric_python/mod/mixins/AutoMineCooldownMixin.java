package org.fabric_python.mod.mixins;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.fabric_python.mod.PythonProxy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class AutoMineCooldownMixin {
    @Shadow
    private int blockBreakingCooldown;

    @Inject(at = @At("HEAD"), method = "updateBlockBreakingProgress (Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z", cancellable = true)
    protected void updateBlockBreakingProgress(BlockPos blockPos_1, Direction direction_1, CallbackInfoReturnable<Boolean> info){
        boolean currentAutoMine = PythonProxy.globalMap != null && PythonProxy.globalMap.getOrDefault("automine", "0").equals("1");
        if(currentAutoMine){
            blockBreakingCooldown = 0;
        }
    }
}
