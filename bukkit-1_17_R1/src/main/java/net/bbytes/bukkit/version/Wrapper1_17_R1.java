package net.bbytes.bukkit.version;


import net.bbytes.core.VersionWrapper;
import net.bbytes.core.versioned.VersionedMaterial;
import net.minecraft.nbt.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Wrapper1_17_R1 implements VersionWrapper {


    @Override
    public VersionedMaterial getMaterial(String material, boolean legacy) {
        return null;
    }
}
