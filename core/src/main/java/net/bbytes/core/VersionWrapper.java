package net.bbytes.core;

import net.bbytes.core.versioned.VersionedItemStack;
import net.bbytes.core.versioned.VersionedMaterial;
import net.bbytes.core.versioned.VersionedPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface  VersionWrapper {
    public  VersionedMaterial getMaterial(String material, boolean legacy);

}
