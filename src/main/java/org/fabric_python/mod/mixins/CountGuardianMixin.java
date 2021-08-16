package org.fabric_python.mod.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.network.packet.s2c.play.MobSpawnS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;
import org.fabric_python.mod.PythonProxy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Enumeration;

@Mixin(ClientPlayNetworkHandler.class)
public class CountGuardianMixin {
    @Inject(at = @At("RETURN"), method = "onMobSpawn (Lnet/minecraft/network/packet/s2c/play/MobSpawnS2CPacket;)V")
    protected void onMobSpawn(MobSpawnS2CPacket mobSpawnS2CPacket_1, CallbackInfo info)  {
        if (PythonProxy.globalMap == null) {
            return;
        }

        int countguardian = Integer.parseInt(PythonProxy.globalMap.getOrDefault("countguardian", "0"));
        if (countguardian == 0) {
            return;
        }

        if (Registry.ENTITY_TYPE.get(mobSpawnS2CPacket_1.getEntityTypeId()) == EntityType.GUARDIAN) {
            MinecraftClient client = MinecraftClient.getInstance();
            if(client.player == null) {
                return;
            }

            synchronized (PythonProxy.guardianCount) {
                long time = Instant.now().getEpochSecond();
                Integer cur = PythonProxy.guardianCount.getOrDefault(time, 0);
                PythonProxy.guardianCount.put(time, cur + 1);

                int last_guardian_update = Integer.parseInt(PythonProxy.globalMap.getOrDefault("last_guardian_update", "0"));
                if(time - last_guardian_update >= 1) {
                    PythonProxy.globalMap.put("last_guardian_update", String.valueOf(time));

                    long total_entity = 0;
                    long total_epoch = 0;

                    Enumeration<Long> keys = PythonProxy.guardianCount.keys();

                    ArrayList<Long> list = new ArrayList<>();

                    while(keys.hasMoreElements()) {
                        list.add(keys.nextElement());
                    }

                    for(Long epoch : list){
                        if(time - epoch <= 60) {
                            total_entity += PythonProxy.guardianCount.getOrDefault(epoch, 0);
                            total_epoch += 1;
                        } else {
                            PythonProxy.guardianCount.remove(epoch);
                        }
                    }

                    client.player.sendMessage(Text.of(String.format("Guardian: %.2f per second", total_entity * 1.0 / total_epoch)), true);
                }
            }
        }
    }
}
