package org.fabric_python.mod.db;

import net.minecraft.nbt.CompoundTag;

import java.io.Serializable;

public class ChestEntry implements Serializable {
    Integer slot;
    String itemName;
    Integer num;
    CompoundTag tags;

    public ChestEntry(Integer slot, String itemName, Integer num, CompoundTag tags){
        this.slot = slot;
        this.itemName = itemName;
        this.num = num;
        this.tags = tags;
    }
}
