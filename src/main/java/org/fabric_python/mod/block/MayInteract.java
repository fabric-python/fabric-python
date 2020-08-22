package org.fabric_python.mod.block;

import net.minecraft.block.*;

public class MayInteract {
    public static boolean blockMayInteract(Block block){
        if(block instanceof AbstractButtonBlock){
            return true;
        }

        if(block instanceof AbstractChestBlock){
            return true;
        }

        if(block instanceof AbstractFurnaceBlock){
            return true;
        }

        if(block instanceof AbstractSignBlock){
            return true;
        }

        if(block instanceof AnvilBlock){
            return true;
        }

        if(block instanceof BarrelBlock){
            return true;
        }

        if(block instanceof BeaconBlock){
            return true;
        }

        if(block instanceof BedBlock){
            return true;
        }

        if(block instanceof BeehiveBlock){
            return true;
        }

        if(block instanceof BellBlock){
            return true;
        }

        if(block instanceof BrewingStandBlock){
            return true;
        }

        if(block instanceof CakeBlock){
            return true;
        }

        if(block instanceof CampfireBlock){
            return true;
        }

        if(block instanceof CartographyTableBlock){
            return true;
        }

        if(block instanceof CauldronBlock){
            return true;
        }

        if(block instanceof CommandBlock){
            return true;
        }

        if(block instanceof ComparatorBlock){
            return true;
        }

        if(block instanceof ComposterBlock){
            return true;
        }

        if(block instanceof CraftingTableBlock){
            return true;
        }

        if(block instanceof DaylightDetectorBlock){
            return true;
        }

        if(block instanceof DispenserBlock){
            return true;
        }

        if(block instanceof DoorBlock){
            return true;
        }

        if(block instanceof DragonEggBlock){
            return true;
        }

        if(block instanceof EnchantingTableBlock){
            return true;
        }

        if(block instanceof FenceBlock){
            return true;
        }

        if(block instanceof FenceGateBlock){
            return true;
        }

        if(block instanceof FlowerPotBlock){
            return true;
        }

        if(block instanceof GrindstoneBlock){
            return true;
        }

        if(block instanceof HopperBlock){
            return true;
        }

        if(block instanceof JigsawBlock){
            return true;
        }

        if(block instanceof JukeboxBlock){
            return true;
        }

        if(block instanceof LecternBlock){
            return true;
        }

        if(block instanceof LeverBlock){
            return true;
        }

        if(block instanceof LoomBlock){
            return true;
        }

        if(block instanceof NoteBlock){
            return true;
        }

        if(block instanceof PistonExtensionBlock){
            return true;
        }

        if(block instanceof RedstoneOreBlock){
            return true;
        }

        if(block instanceof RedstoneWireBlock){
            return true;
        }

        if(block instanceof RepeaterBlock){
            return true;
        }

        if(block instanceof RespawnAnchorBlock){
            return true;
        }

        if(block instanceof ShulkerBoxBlock){
            return true;
        }

        if(block instanceof StairsBlock){
            return true;
        }

        if(block instanceof StonecutterBlock){
            return true;
        }

        if(block instanceof StructureBlock){
            return true;
        }

        if(block instanceof SweetBerryBushBlock){
            return true;
        }

        if(block instanceof TntBlock){
            return true;
        }

        if(block instanceof TrapdoorBlock){
            return true;
        }

        return false;
    }
}
