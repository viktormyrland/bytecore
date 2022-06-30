package net.bbytes.bukkit.version;


import net.bbytes.core.VersionWrapper;
import net.bbytes.core.versioned.VersionedMaterial;
import net.minecraft.nbt.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Material;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;

public class Wrapper1_18_R2 implements VersionWrapper {


    @Override
    public VersionedMaterial getMaterial(String material, boolean legacy) {
        return null;
    }
}
