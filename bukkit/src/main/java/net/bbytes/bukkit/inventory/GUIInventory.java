package net.bbytes.bukkit.inventory;

import net.bbytes.bukkit.Main;
import net.bbytes.bukkit.inventory.guis.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;

public enum GUIInventory {

    PROJECT_OVERVIEW(new ProjectOverviewInventory()),
    PROJECT_LIST(new ProjectListInventory()),
    PROJECT_EDIT(new ProjectEditInventory()),
    WORLDS_UNCATEGORIZED(new UncategorizedWorldsInventory()),
    ACCESS_EDIT(new AccessEditInventory()),
    WORLD_EDIT(new WorldEditInventory()),
    NEW_WORLD(new NewWorldInventory()),
    RECYCLE_BIN(new RecycleBinInventory()),
    WARP_PROJECTS(new WarpMenuProjectsInventory()),
    WARP_WORLDS_IN_PROJECT(new WarpMenuWorldsInventory()),
    WARPS_IN_WORLD(new WarpMenuWarpsInventory()),
    WARPS_UNCATEGORIZED(new UncategorizedWarpsInventory()),
    CHANGE_PROJECT(new ChangeProjectInventory()),
    IMPORT_WORLD(new ImportWorldInventory()),
    IMPORT_WORLD_CHANGE_PROJECT(new ImportWorldChangeProjectInventory()),
    IMPORT_WORLD_CHOOSE_WORLD(new ImportWorldChooseInventory()),
    TRANSFER_WORLD_TO_1_16(new TransferWorldInventory()),

    NOTHING(null);

    private int id;
    private final BaseInventory inv;

    GUIInventory(BaseInventory inv){
        this.inv = inv;
    }

    public int getID(){
        return id;
    }

    public BaseInventory getInventory(){
        return inv;
    }

    public void display(Player player, Object... options){
        Inventory inv = this.getInventory().getInventory(player, options);
        inv = Main.getInstance().getInventoryManager().applyGUIInventory(inv, this);

        if(player.getOpenInventory().getTopInventory().getSize() != 5)
            if(Main.getInstance().getInventoryManager().getGUIInventory(player.getOpenInventory().getTopInventory()) != this) {
                player.openInventory(inv);
                return;
            }

        Main.getInstance().getInventoryManager().updateInventory(player, inv);
    }

    public void refresh(Player player, Object... options){
        this.inv.refresh(player, this, options);
    }
    public void setID(int id){
        this.id = id;
        if(this.inv != null)this.inv.setID(id);
    }

    public static GUIInventory get(String name){
        if(name != null) return GUIInventory.valueOf(name);
        return null;
    }
}
