package org.fabric_python.mod.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import org.fabric_python.mod.PythonProxy;
import org.fabric_python.mod.animal.CowFeedHistory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Instant;
import java.util.ArrayList;

@Mixin(ClientPlayerEntity.class)
public abstract class AutoFeedCowMixin {
    public boolean FeedingPredicate(ClientPlayerEntity player, CowEntity entity){
        if(entity.isBaby() || entity.isInLove()){
            return false;
        }

        if(entity.distanceTo(player) > 6) {
            return false;
        }

        long now = Instant.now().getEpochSecond();
        long lastFeed = CowFeedHistory.get(entity.getUuidAsString());

        return (now - lastFeed) > 320;
    }

    @Inject(at = @At("RETURN"), method = "sendMovementPackets ()V")
    private void sendMovementPackets(CallbackInfo info) {
        if (PythonProxy.globalMap == null) {
            return;
        }

        int autofeedcow = Integer.parseInt(PythonProxy.globalMap.getOrDefault("autofeedcow", "0"));
        if (autofeedcow == 0) {
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
        acceptedFood.add("minecraft:wheat");

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
        predicate.setPredicate(p -> (p instanceof CowEntity) && (FeedingPredicate(player, (CowEntity)p)));

        CowEntity target = player.getEntityWorld().getClosestEntity(CowEntity.class, predicate, player, player.getX(), player.getY(), player.getZ(), box);
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
        CowFeedHistory.set(target.getUuidAsString(), Instant.now().getEpochSecond());
    }
}
