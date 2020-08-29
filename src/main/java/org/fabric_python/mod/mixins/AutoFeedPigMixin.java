package org.fabric_python.mod.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.light.ChunkLightingView;
import net.minecraft.world.chunk.light.LightingProvider;
import org.fabric_python.mod.PythonProxy;
import org.fabric_python.mod.animal.PigFeedHistory;
import org.fabric_python.mod.block.MayInteract;
import org.fabric_python.mod.mob.AutoAttackPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Instant;
import java.util.*;
import java.util.function.Predicate;

import static java.lang.Math.sqrt;

@Mixin(ClientPlayerEntity.class)
public abstract class AutoFeedPigMixin {
    public boolean FeedingPredicate(ClientPlayerEntity player, PigEntity entity){
        if(entity.isBaby() || entity.isInLove()){
            return false;
        }

        if(entity.distanceTo(player) > 6) {
            return false;
        }

        long now = Instant.now().getEpochSecond();
        long lastFeed = PigFeedHistory.get(entity.getUuidAsString());

        return (now - lastFeed) > 320;
    }

    @Inject(at = @At("RETURN"), method = "sendMovementPackets ()V")
    private void sendMovementPackets(CallbackInfo info) {
        if (PythonProxy.globalMap == null) {
            return;
        }

        int autofeedpig = Integer.parseInt(PythonProxy.globalMap.getOrDefault("autofeedpig", "0"));
        if (autofeedpig == 0) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if(client == null){
            return;
        }

        if(client.interactionManager != null && client.interactionManager.isBreakingBlock()){
            return;
        }

        ClientWorld world = client.world;
        if(world == null){
            return;
        }

        ClientPlayerEntity player = client.player;
        if(player == null){
            return;
        }

        ArrayList<String> acceptedFood = new ArrayList<>();
        acceptedFood.add("minecraft:potato");
        acceptedFood.add("minecraft:carrot");
        acceptedFood.add("minecraft:beetroot");

        boolean mainHandReady = false;
        ItemStack mainHandItemStack = player.getMainHandStack();
        Item mainHandItem = mainHandItemStack.getItem();

        if(mainHandItemStack.getCount() != 0 && acceptedFood.contains(Registry.ITEM.getId(mainHandItem).toString())){
            mainHandReady = true;
        }

        if(!mainHandReady){
            for (int i = 9; i <= 44; i++) {
                Slot cur_slot = player.playerScreenHandler.getSlot(i);
                if (cur_slot.hasStack() && acceptedFood.contains(Registry.ITEM.getId(cur_slot.getStack().getItem()).toString()) && cur_slot.getStack().getCount() >= 1) {
                    if (i != 36) {
                        client.interactionManager.clickSlot(player.playerScreenHandler.syncId, i, 0, SlotActionType.SWAP, player);
                    }
                    player.inventory.selectedSlot = 0;
                    mainHandReady = true;
                    break;
                }
            }
        }

        if (!mainHandReady) {
            return;
        }

        BlockPos blockPos = player.getBlockPos();
        int x = blockPos.getX();
        int y = blockPos.getY();
        int z = blockPos.getZ();

        Vec3d box_1 = new Vec3d(x - 6, y - 6, z - 6);
        Vec3d box_2 = new Vec3d(x + 6, y + 6, z + 6);

        Box box = new Box(box_1, box_2);

        TargetPredicate predicate = new TargetPredicate();
        predicate.setPredicate(p -> (p instanceof PigEntity) && (FeedingPredicate(player, (PigEntity)p)));

        PigEntity target = player.getEntityWorld().getClosestEntity(PigEntity.class, predicate, player, player.getX(), player.getY(), player.getZ(), box);
        if(target == null){
            return;
        }

        double coolDownNow = Instant.now().getEpochSecond() * 1000 + Instant.now().getNano() / 1000.0 / 1000;
        double coolDownLast = Double.parseDouble(PythonProxy.globalMap.getOrDefault("autofeed_cooldown", "0.0"));

        if(coolDownNow - coolDownLast <= 250) {
            return;
        }

        PythonProxy.globalMap.put("autofeed_cooldown", String.valueOf(coolDownNow));

        client.interactionManager.interactEntity(player, target, Hand.MAIN_HAND);
        PigFeedHistory.set(target.getUuidAsString(), Instant.now().getEpochSecond());
    }
}
