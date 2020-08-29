package org.fabric_python.mod.mixins;

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
        if(getType() == EntityType.PLAYER) {
            boolean isNVon = PythonProxy.globalMap != null && PythonProxy.globalMap.getOrDefault("night_vision", "0").equals("1");

            if (isNVon) {
                if (effect == StatusEffects.NIGHT_VISION) {
                    info.setReturnValue(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 600));
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "hasStatusEffect (Lnet/minecraft/entity/effect/StatusEffect;)Z", cancellable = true)
    protected void hasStatusEffect(StatusEffect effect, CallbackInfoReturnable<Boolean> info) {
        if(getType() == EntityType.PLAYER) {
            boolean isNVon = PythonProxy.globalMap != null && PythonProxy.globalMap.getOrDefault("night_vision", "0").equals("1");

            if (isNVon) {
                if (effect == StatusEffects.NIGHT_VISION) {
                    info.setReturnValue(true);
                }
            }
        }
    }
}
