package net.bbytes.bukkit.inventory;

import de.tr7zw.changeme.nbtapi.NBTItem;
import net.bbytes.bukkit.Main;
import net.bbytes.bukkit.project.Project;
import net.bbytes.bukkit.world.ConfigurableWorldType;
import net.bbytes.bukkit.world.ImportWorldInfo;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class InventoryChangedRunnable implements Runnable {
    @Override
    public void run() {

        for(Player player : Bukkit.getOnlinePlayers()){
            if(Main.getInstance().getInventoryManager().getGUIInventory(player.getOpenInventory().getTopInventory()) != null){
                Inventory inv = player.getOpenInventory().getTopInventory();
                GUIInventory guiInventory = Main.getInstance().getInventoryManager().getGUIInventory(player.getOpenInventory().getTopInventory());

                if(guiInventory != null)
                    switch(guiInventory){
                        case PROJECT_LIST:
                        case WARP_PROJECTS: {
                            int page = getPage(inv, 5, 5);
                            guiInventory.refresh(player, page);
                            break;
                        }
                        case PROJECT_OVERVIEW:
                        case WARP_WORLDS_IN_PROJECT: {
                            int page = getPage(inv, 5, 5);
                            String  projectID = new NBTItem(inv.getItem(4)).getString("projectID");
                            guiInventory.refresh(player, projectID, page);
                            break;
                        }
                        case RECYCLE_BIN:
                        case WARPS_UNCATEGORIZED:
                        case WORLDS_UNCATEGORIZED: {
                            int page = getPage(inv, 6, 5);
                            guiInventory.refresh(player, page);
                            break;
                        }
                        case ACCESS_EDIT:{
                            int page = getPage(inv, 6, 5);
                            String projectID = new NBTItem(inv.getItem(4)).getString("projectID");
                            guiInventory.refresh(player, projectID, page);
                            break;
                        }
                        case WORLD_EDIT:{
                            String worldID = (String) Main.getInstance().getInventoryManager().getNBT(inv.getItem(4), "worldID");
                            boolean confirm = Main.getInstance().getInventoryManager().getGUIItem(inv.getItem(35)) == GUIItem.WORLD_CONFIRM_DELETE;
                            if(confirm)
                                guiInventory.refresh(player, worldID, true);
                            else guiInventory.refresh(player, worldID);
                            break;
                        }
                        case PROJECT_EDIT: {
                            String projectID = new NBTItem(inv.getItem(4)).getString("projectID");
                            boolean confirm = Main.getInstance().getInventoryManager().getGUIItem(inv.getItem(16)) == GUIItem.PROJECT_CONFIRM_DELETE;
                            if (confirm)
                                guiInventory.refresh(player, projectID, true);
                            else guiInventory.refresh(player, projectID);
                            break;
                        }
                        case WARPS_IN_WORLD:
                        case CHANGE_PROJECT: {
                            int page = getPage(inv, 6, 5);
                            String worldID = (String) Main.getInstance().getInventoryManager().getNBT(inv.getItem(4), "worldID");
                            guiInventory.refresh(player, worldID, page);
                            break;
                        }
                        case IMPORT_WORLD_CHANGE_PROJECT:{
                            ImportWorldInfo info = getImportWorldInfo(inv);
                            int page = getPage(inv, 6, 5);
                            guiInventory.refresh(player, info, page);
                        }


                    }

            }
        }

    }

    public int getPage(Inventory inv, int row, int column){
        ItemStack item = inv.getItem((row-1)*9+column-1);
        return (int) new NBTItem(item).getInteger("page");
    }

    public ImportWorldInfo getImportWorldInfo(Inventory inv){
        ImportWorldInfo importWorldInfo = new ImportWorldInfo();

        importWorldInfo.setUpload( (byte)Main.getInstance().getInventoryManager().getNBT(inv.getItem(4), "info.upload") == 0x01);
        importWorldInfo.setDisplayname((String) Main.getInstance().getInventoryManager().getNBT(inv.getItem(4), "info.displayName"));
        importWorldInfo.setWorldID((String) Main.getInstance().getInventoryManager().getNBT(inv.getItem(4), "info.worldID"));
        importWorldInfo.setProject(Project.getProject((String) Main.getInstance().getInventoryManager().getNBT(inv.getItem(4), "info.projectID")));
        importWorldInfo.setConfigurableWorldType(ConfigurableWorldType.valueOf((String) Main.getInstance().getInventoryManager().getNBT(inv.getItem(4), "info.configurableWorldType")));
        importWorldInfo.setEnvironment(World.Environment.valueOf((String) Main.getInstance().getInventoryManager().getNBT(inv.getItem(4), "info.environment")));

        return importWorldInfo;
    }
}
