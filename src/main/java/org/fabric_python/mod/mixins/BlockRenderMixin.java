package org.fabric_python.mod.mixins;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.*;
import net.minecraft.world.BlockView;
import org.fabric_python.mod.PythonProxy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.class)
public abstract class BlockRenderMixin {
    @Inject(at = @At("HEAD"), method = "getVisualShape (Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;", cancellable = true)
    protected void renderBlocking1(BlockState blockState_1, BlockView blockView_1, BlockPos blockPos_1, ShapeContext shapeContext_1, CallbackInfoReturnable<VoxelShape> info) {
        boolean isXrayOn = PythonProxy.globalMap != null && PythonProxy.globalMap.getOrDefault("xray", "0").equals("1");
        if(isXrayOn && PythonProxy.noRenderList != null && PythonProxy.noRenderList.contains(Registry.BLOCK.getId(blockState_1.getBlock()).getPath())) {
            info.setReturnValue(VoxelShapes.empty());
        }
    }

    @Inject(at = @At("HEAD"), method = "getOutlineShape (Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;", cancellable = true)
    protected void renderBlocking2(BlockState blockState_1, BlockView blockView_1, BlockPos blockPos_1, ShapeContext shapeContext_1, CallbackInfoReturnable<VoxelShape> info) {
        boolean isXrayOn = PythonProxy.globalMap != null && PythonProxy.globalMap.getOrDefault("xray", "0").equals("1");
        if(isXrayOn && PythonProxy.noRenderList != null && PythonProxy.noRenderList.contains(Registry.BLOCK.getId(blockState_1.getBlock()).getPath())) {
            info.setReturnValue(VoxelShapes.fullCube());
        }
    }

    @Inject(at = @At("HEAD"), method = "getCollisionShape (Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;", cancellable = true)
    protected void renderBlocking3(BlockState blockState_1, BlockView blockView_1, BlockPos blockPos_1, ShapeContext shapeContext_1, CallbackInfoReturnable<VoxelShape> info) {
        boolean isXrayOn = PythonProxy.globalMap != null && PythonProxy.globalMap.getOrDefault("xray", "0").equals("1");
        if(isXrayOn && PythonProxy.noRenderList != null && PythonProxy.noRenderList.contains(Registry.BLOCK.getId(blockState_1.getBlock()).getPath())) {
            info.setReturnValue(VoxelShapes.fullCube());
        }
    }

    @Inject(at = @At("HEAD"), method = "getRenderType (Lnet/minecraft/block/BlockState;)Lnet/minecraft/block/BlockRenderType;", cancellable = true)
    protected void renderBlocking4(BlockState blockState_1, CallbackInfoReturnable<BlockRenderType> info) {
        boolean isXrayOn = PythonProxy.globalMap != null && PythonProxy.globalMap.getOrDefault("xray", "0").equals("1");
        if(isXrayOn && PythonProxy.noRenderList != null && PythonProxy.noRenderList.contains(Registry.BLOCK.getId(blockState_1.getBlock()).getPath())) {
            info.setReturnValue(BlockRenderType.INVISIBLE);
        }
    }
}

