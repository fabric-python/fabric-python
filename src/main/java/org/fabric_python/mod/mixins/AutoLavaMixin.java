package org.fabric_python.mod.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidDrainable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;
import org.fabric_python.mod.PythonProxy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Instant;

@Mixin(ClientPlayerEntity.class)
public abstract class AutoLavaMixin {
    @Inject(at = @At("RETURN"), method = "sendMovementPackets ()V")
    private void sendMovementPackets(CallbackInfo info) {
        if (PythonProxy.globalMap == null) {
            return;
        }

        int autolava = Integer.parseInt(PythonProxy.globalMap.getOrDefault("autolava", "0"));
        if (autolava == 0) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return;
        }

        if (client.interactionManager != null && client.interactionManager.isBreakingBlock()) {
            return;
        }

        ClientWorld world = client.world;
        if (world == null) {
            return;
        }

        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }

        double coolDownNow = Instant.now().getEpochSecond() * 1000 + Instant.now().getNano() / 1000.0 / 1000;
        double coolDownLast = Double.parseDouble(PythonProxy.globalMap.getOrDefault("autoplace_cooldown", "0.0"));

        if (coolDownNow - coolDownLast <= 250) {
            return;
        }

        boolean mainHandReady = false;

        ItemStack mainHandItemStack = player.getMainHandStack();
        Item mainHandItem = mainHandItemStack.getItem();

        if (mainHandItemStack.getCount() != 0 && Registry.ITEM.getId(mainHandItem).toString().equals("minecraft:bucket")) {
            mainHandReady = true;
        }

        if (!mainHandReady) {
            return;
        }

        boolean hasEmptySpace = false;

        for (int i = 9; i <= 44; i++) {
            Slot cur_slot = player.playerScreenHandler.getSlot(i);
            if (!cur_slot.hasStack() || cur_slot.getStack().getCount() == 0) {
                hasEmptySpace = true;
                break;
            }
        }

        if (!hasEmptySpace) {
            return;
        }

        if (client.world == null) {
            return;
        }

        boolean found = false;
        BlockHitResult hitResult_1 = rayTrace(client.world, player);

        if (hitResult_1.getType() == HitResult.Type.MISS || hitResult_1.getType() != HitResult.Type.BLOCK) {
            return;
        }

        BlockPos blockPos_1 = hitResult_1.getBlockPos();
        Direction direction_1 = hitResult_1.getSide();

        BlockState blockState_1 = client.world.getBlockState(blockPos_1);

        if (!(blockState_1.getBlock() instanceof FluidDrainable)) {
            return;
        }

        PythonProxy.globalMap.put("autoplace_cooldown", String.valueOf(coolDownNow));

        if (client.interactionManager != null) {
            client.interactionManager.interactItem(player, client.world, Hand.MAIN_HAND);
        }

        player.sendMessage(Text.of("Collected"), true);
    }

    protected BlockHitResult rayTrace(World world_1, PlayerEntity playerEntity_1) {
        float float_1 = playerEntity_1.pitch;
        float float_2 = playerEntity_1.yaw;
        Vec3d vec3d_1 = playerEntity_1.getCameraPosVec(1.0F);
        float float_3 = MathHelper.cos(-float_2 * 0.017453292F - 3.1415927F);
        float float_4 = MathHelper.sin(-float_2 * 0.017453292F - 3.1415927F);
        float float_5 = -MathHelper.cos(-float_1 * 0.017453292F);
        float float_6 = MathHelper.sin(-float_1 * 0.017453292F);
        float float_7 = float_4 * float_5;
        float float_9 = float_3 * float_5;
        Vec3d vec3d_2 = vec3d_1.add((double)float_7 * 5.0D, (double)float_6 * 5.0D, (double)float_9 * 5.0D);
        return world_1.rayTrace(new RayTraceContext(vec3d_1, vec3d_2, RayTraceContext.ShapeType.OUTLINE, RayTraceContext.FluidHandling.SOURCE_ONLY, playerEntity_1));
    }
}
