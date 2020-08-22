package org.fabric_python.mod.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;

public class AutoAttackPredicate {
    public static boolean shouldAutoAttack(Entity entity){
        if(entity.getType().getSpawnGroup() == SpawnGroup.MONSTER) {
            return !(entity instanceof EndermanEntity) && !(entity instanceof ZombifiedPiglinEntity);
        }
        return false;
    }
}
