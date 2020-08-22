package org.fabric_python.mod.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.fabric_python.mod.PythonProxy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class BlockTranslucentMixin {
    @Inject(at = @At("HEAD"), method = "isTranslucent (Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Z", cancellable = true)
    protected void renderBlocking(BlockState blockState_1, BlockView _blockView_1, BlockPos _blockPos_1, CallbackInfoReturnable<Boolean> info) {
        boolean isXrayOn = PythonProxy.globalMap != null && PythonProxy.globalMap.getOrDefault("xray", "0").equals("1");
        if(isXrayOn) {
            info.setReturnValue(true);
        }
    }
}
