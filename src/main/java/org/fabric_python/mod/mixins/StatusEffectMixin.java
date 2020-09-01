package org.fabric_python.mod.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.world.World;
import org.fabric_python.mod.PythonProxy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class StatusEffectMixin extends Entity  {
    public StatusEffectMixin(EntityType<?> entityType_1, World world_1) {
        super(entityType_1, world_1);
    }

    @Inject(at = @At("HEAD"), method = "getStatusEffect (Lnet/minecraft/entity/effect/StatusEffect;)Lnet/minecraft/entity/effect/StatusEffectInstance;", cancellable = true)
    protected void getStatusEffect(StatusEffect effect, CallbackInfoReturnable<StatusEffectInstance> info) {
        if(getType() == EntityType.PLAYER && PythonProxy.globalMap != null ) {
            boolean isNVon = PythonProxy.globalMap.getOrDefault("night_vision", "0").equals("1");
            int speed = Integer.parseInt(PythonProxy.globalMap.getOrDefault("speed", "2"));
            int haste = Integer.parseInt(PythonProxy.globalMap.getOrDefault("haste", "1"));
            int jumpboost = Integer.parseInt(PythonProxy.globalMap.getOrDefault("jumpboost", "2"));
            int leviation = Integer.parseInt(PythonProxy.globalMap.getOrDefault("leviation", "0"));

            if (isNVon) {
                if (effect == StatusEffects.NIGHT_VISION) {
                    info.setReturnValue(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 600));
                }
            }

            if(speed > 0){
                if(effect == StatusEffects.SPEED){
                    ClientPlayerEntity player = MinecraftClient.getInstance().player;
                    StatusEffectInstance instance = new StatusEffectInstance(StatusEffects.SPEED, 600, speed - 1);

                    if(player != null) {
                        instance.getEffectType().onRemoved(player, player.getAttributes(), instance.getAmplifier());
                        instance.getEffectType().onApplied(player, player.getAttributes(), instance.getAmplifier());
                    }

                    info.setReturnValue(instance);
                }
            }

            if(haste > 0){
                if(effect == StatusEffects.HASTE){
                    ClientPlayerEntity player = MinecraftClient.getInstance().player;
                    StatusEffectInstance instance = new StatusEffectInstance(StatusEffects.HASTE, 600, haste - 1);

                    if(player != null) {
                        instance.getEffectType().onRemoved(player, player.getAttributes(), instance.getAmplifier());
                        instance.getEffectType().onApplied(player, player.getAttributes(), instance.getAmplifier());
                    }

                    info.setReturnValue(instance);
                }
            }

            if(jumpboost > 0){
                if(effect == StatusEffects.JUMP_BOOST){
                    info.setReturnValue(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 600, jumpboost - 1));
                }
            }

            if(leviation > 0){
                if(effect == StatusEffects.LEVITATION){
                    info.setReturnValue(new StatusEffectInstance(StatusEffects.LEVITATION, 600, leviation - 1));
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "hasStatusEffect (Lnet/minecraft/entity/effect/StatusEffect;)Z", cancellable = true)
    protected void hasStatusEffect(StatusEffect effect, CallbackInfoReturnable<Boolean> info) {
        if(getType() == EntityType.PLAYER && PythonProxy.globalMap != null) {
            boolean isNVon = PythonProxy.globalMap.getOrDefault("night_vision", "0").equals("1");
            int speed = Integer.parseInt(PythonProxy.globalMap.getOrDefault("speed", "2"));
            int haste = Integer.parseInt(PythonProxy.globalMap.getOrDefault("haste", "1"));
            int jumpboost = Integer.parseInt(PythonProxy.globalMap.getOrDefault("jumpboost", "1"));
            int leviation = Integer.parseInt(PythonProxy.globalMap.getOrDefault("leviation", "0"));

            if (isNVon) {
                if (effect == StatusEffects.NIGHT_VISION) {
                    info.setReturnValue(true);
                }
            }

            if(speed > 0){
                if(effect == StatusEffects.SPEED){
                    info.setReturnValue(true);
                }
            }

            if(haste > 0){
                if(effect == StatusEffects.HASTE){
                    info.setReturnValue(true);
                }
            }

            if(jumpboost > 0){
                if(effect == StatusEffects.JUMP_BOOST){
                    info.setReturnValue(true);
                }
            }

            if(leviation > 0){
                if(effect == StatusEffects.LEVITATION){
                    info.setReturnValue(true);
                }
            }
        }
    }
}
