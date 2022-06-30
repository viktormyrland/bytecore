package net.bbytes.bukkit.listeners;

import de.tr7zw.changeme.nbtapi.NBTItem;
import net.bbytes.bukkit.Main;
import net.bbytes.bukkit.inventory.GUIInventory;
import net.bbytes.bukkit.inventory.GUIItem;
import net.bbytes.bukkit.message.Message;
import net.bbytes.bukkit.project.Project;
import net.bbytes.bukkit.user.User;
import net.bbytes.bukkit.util.BookUtil;
import net.bbytes.bukkit.warp.Warp;
import net.bbytes.bukkit.world.ConfigurableWorld;
import net.bbytes.bukkit.world.ConfigurableWorldType;
import net.bbytes.bukkit.world.ImportWorldInfo;
import net.bbytes.bukkit.world.RecycledConfigurableWorld;
import net.wesjd.anvilgui.AnvilGUI;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class InventoryListener implements Listener {

    @EventHandler
    public void onMenuItemDrag(InventoryDragEvent e) {
        GUIInventory view = Main.getInstance().getInventoryManager().getGUIInventory(e.getView().getTopInventory());
        if(view != null) {
            for(int i : e.getRawSlots())
                if(i < e.getView().getTopInventory().getSize()){
                    e.setCancelled(true);
                    break;
                }
        }

    }
    @EventHandler
    public void onMenuItemClick(InventoryClickEvent e) {
        if(e.getClickedInventory() == null) return;
        GUIInventory inv = Main.getInstance().getInventoryManager().getGUIInventory(e.getView().getTopInventory());

//        		System.out.println("---------");
//		if(e.getCurrentItem() != null)
//			System.out.println("e.getCurrentItem: " + e.getCurrentItem().getType().toString());
//		else System.out.println("e.getCurrentItem: null");
//
//		if(e.getCursor()!= null)
//			System.out.println("e.getCursor: " + e.getCursor().getType().toString());
//		else System.out.println("e.getCursor: null");
//
//		System.out.println("e.getAction: " + e.getAction().name());
//		System.out.println("e.getSlotType: " + e.getSlotType().name());
//		System.out.println("e.getClick: " + e.getClick().name());
//		if(e.getClickedInventory() != null) System.out.println("e.getClickedInventory: " + e.getClickedInventory().getTitle());
//		else System.out.println("e.getClickedInventory: null");
//		System.out.println("e.getSlot: " + e.getSlot());
//		System.out.println("e.getRawSlot: " + e.getRawSlot());

        if(e.getCurrentItem() != null)if(e.getCurrentItem().getType() == Material.AIR && inv != null && e.getSlotType() == InventoryType.SlotType.CONTAINER)
            if(e.getRawSlot() < e.getView().getTopInventory().getSize())
                e.setCancelled(true);

        if(e.getCurrentItem() != null)if(e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY && inv != null)
            e.setCancelled(true);

        if(e.getCurrentItem() != null) if(e.getCurrentItem().getType() != Material.AIR){
            GUIItem item = Main.getInstance().getInventoryManager().getGUIItem(e.getCurrentItem());



            if(inv == null) {
                if(item != null) if(item.getMoveable() == GUIItem.Moveable.FALSE) e.setCancelled(true);
                return;
            }


            if(item != null) {

                if(item.getMoveable() == GUIItem.Moveable.FALSE) e.setCancelled(true);

				/*
						CLICK GUI ITEM START
				 */
                Player player = (Player)e.getWhoClicked();

                e.setCancelled(true);

                if(e.getAction() == InventoryAction.NOTHING)
                    return;

				switch(item){
				    /*
				        Project List
				     */
                    case PROJECTS_NEW_PROJECT:{
                        Main.getInstance().getProjectManager().newProject().setProjectManager(player.getName());
                        GUIInventory.PROJECT_LIST.display(player, getPage(e.getClickedInventory(), 5, 5));


                        break;
                    }

                    case PROJECTS_SPAWN: {
                        player.teleport(Main.getInstance().getSpawnLocation());

                        break;
                    }
                    case PROJECTS_PLOT_WORLD: {
                        if(Bukkit.getWorld("plotworld") != null)
                            player.teleport(Bukkit.getWorld("plotworld").getSpawnLocation());

                        break;
                    }
                    case PROJECT_PLACEHOLDER:{
                        switch (inv) {
                            case PROJECT_LIST: {
                                String projectID = new NBTItem(e.getCurrentItem()).getString("projectID");
                                GUIInventory.PROJECT_OVERVIEW.display(player, projectID, 1);
                                break;
                            }
                            case PROJECT_EDIT: {
                                String projectID = new NBTItem(e.getCurrentItem()).getString("projectID");
                                Project project = Main.getInstance().getProjectManager().getProject(projectID);

                                if (e.getCursor() != null) if (e.getCursor().getType() != Material.AIR) {
                                    project.setDisplayItem(new ItemStack(e.getCursor().getType(), e.getCursor().getAmount(), e.getCursor().getDurability()));
                                    project.saveProject();
                                    e.setCursor(new ItemStack(e.getCurrentItem().getType(), e.getCurrentItem().getAmount(), e.getCurrentItem().getDurability()));
                                    GUIInventory.PROJECT_EDIT.display(player, projectID);
                                }

                                break;
                            }
                            case WARP_PROJECTS: {
                                String projectID = new NBTItem(e.getCurrentItem()).getString("projectID");
                                GUIInventory.WARP_WORLDS_IN_PROJECT.display(player, projectID, 1);
                                break;
                            }
                            case CHANGE_PROJECT: {
                                String projectID = new NBTItem(e.getCurrentItem()).getString("projectID");
                                String worldID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "worldID");

                                ConfigurableWorld world = ConfigurableWorld.getWorld(worldID);
                                Project project = Project.getProject(projectID);

                                world.changeProject(project);

                                GUIInventory.PROJECT_OVERVIEW.display(player, projectID, 1);

                                break;
                            }
                            case IMPORT_WORLD:{
                                ImportWorldInfo info = getImportWorldInfo(e.getClickedInventory());
                                GUIInventory.IMPORT_WORLD_CHANGE_PROJECT.display(player, info, 1);
                                break;
                            }
                            case IMPORT_WORLD_CHANGE_PROJECT:{
                                ImportWorldInfo info = getImportWorldInfo(e.getClickedInventory());
                                String projectID = new NBTItem(e.getCurrentItem()).getString("projectID");
                                Project project = Project.getProject(projectID);
                                info.setProject(project);
                                GUIInventory.IMPORT_WORLD.display(player, info);
                                break;
                            }
                        }
                        break;
                    }
                    case PROJECTS_RECYCLE_BIN:{
                        GUIInventory.RECYCLE_BIN.display(player, 1);
                        break;
                    }
                    case PROJECTS_UNCATEGORIZED_WORLDS:{
                        if(inv == GUIInventory.PROJECT_LIST){
                            GUIInventory.WORLDS_UNCATEGORIZED.display(player, 1);
                        }
                        break;
                    }

                    /*
                        Project Overview
                     */
                    case PROJECT_EDIT:{
                        String projectID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "projectID");
                        GUIInventory.PROJECT_EDIT.display(player, projectID);
                        break;
                    }
                    case PROJECT_NEW_WORLD:{
                        if(inv == GUIInventory.PROJECT_OVERVIEW){
                            String projectID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "projectID");
                            GUIInventory.NEW_WORLD.display(player, projectID);
                        }else if (inv == GUIInventory.WORLDS_UNCATEGORIZED){
                            GUIInventory.NEW_WORLD.display(player);
                        }
                        break;
                    }


                    /*
                        Project Edit
                     */
                    case PROJECT_DELETE:{
                        String projectID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "projectID");
                        GUIInventory.PROJECT_EDIT.display(player, projectID, true);

                        break;
                    }
                    case PROJECT_CONFIRM_DELETE:{
                        String projectID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "projectID");
                        Project project = Main.getInstance().getProjectManager().getProject(projectID);
                        Main.getInstance().getProjectManager().deleteProject(project);
                        GUIInventory.PROJECT_LIST.display(player, 1);

                        break;
                    }
                    case PROJECT_EDIT_NAME:{
                        String projectID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "projectID");
                        final String finalizedID = projectID;
                        Project project = Main.getInstance().getProjectManager().getProject(projectID);
                        new AnvilGUI.Builder()
                                .onClose(
                                        player1 -> GUIInventory.PROJECT_EDIT.display(player, finalizedID)
                                )
                                .onComplete((player1, text) -> {
                                       project.setName(text);
                                       project.saveProject();
                                       return AnvilGUI.Response.close();
                                })
                                .text(project.getName())
                                .plugin(Main.getInstance())
                                .open(player);
                        break;

                    }

                    case PROJECT_EDIT_LEAD:{
                        String projectID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "projectID");
                        final String finalizedID = projectID;
                        Project project = Main.getInstance().getProjectManager().getProject(projectID);
                        new AnvilGUI.Builder()
                                .onClose(
                                        player1 -> GUIInventory.PROJECT_EDIT.display(player, finalizedID)
                                )
                                .onComplete((player1, text) -> {
                                    project.setProjectManager(text);
                                    project.saveProject();
                                    return AnvilGUI.Response.close();
                                })
                                .text(project.getProjectManager())
                                .plugin(Main.getInstance())
                                .open(player);
                        break;

                    }

                    case PROJECT_EDIT_ACCESS:{
                        if(inv == GUIInventory.PROJECT_EDIT){
                            String projectID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "projectID");
                            GUIInventory.ACCESS_EDIT.display(player, projectID, 1);
                        }
                        break;
                    }

                    /*
                        New World
                     */

                    case NEW_WORLD_VOID:{
                        String projectID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "projectID");
                        Project project = Main.getInstance().getProjectManager().getProject(projectID);
                        if(project != null){
                            Main.getInstance().getWorldManager().createNewWorld(player, ConfigurableWorldType.VOID, project);
                            GUIInventory.PROJECT_OVERVIEW.display(player, projectID, 1);
                        }else{
                            Main.getInstance().getWorldManager().createNewWorld(player, ConfigurableWorldType.VOID);
                            GUIInventory.WORLDS_UNCATEGORIZED.display(player, 1);
                        }

                        break;
                    }
                    case NEW_WORLD_FLAT:{
                        String projectID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "projectID");
                        Project project = Main.getInstance().getProjectManager().getProject(projectID);

                        if(project != null){
                            Main.getInstance().getWorldManager().createNewWorld(player, ConfigurableWorldType.FLAT, project);
                            GUIInventory.PROJECT_OVERVIEW.display(player, projectID, 1);
                        }else{
                            Main.getInstance().getWorldManager().createNewWorld(player, ConfigurableWorldType.FLAT);
                            GUIInventory.WORLDS_UNCATEGORIZED.display(player, 1);
                        }


                        break;
                    }
                    case NEW_WORLD_NORMAL:{
                        String projectID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "projectID");
                        Project project = Main.getInstance().getProjectManager().getProject(projectID);
                        if(project != null){
                            Main.getInstance().getWorldManager().createNewWorld(player, ConfigurableWorldType.NORMAL, project);
                            GUIInventory.PROJECT_OVERVIEW.display(player, projectID, 1);
                        }else{
                            Main.getInstance().getWorldManager().createNewWorld(player, ConfigurableWorldType.NORMAL);
                            GUIInventory.WORLDS_UNCATEGORIZED.display(player, 1);
                        }

                        break;
                    }case NEW_WORLD_IMPORT:{
                        String projectID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "projectID");

                        GUIInventory.IMPORT_WORLD_CHOOSE_WORLD.display(player, projectID, 1);
                        break;
                    }
                    case NEW_WORLD_UPLOAD:{
                        String projectID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "projectID");
                        Project project = Main.getInstance().getProjectManager().getProject(projectID);
                        User u = User.getUser(player);
                        if(project != null){
                            u.setProjectToUpload(project);
                        }else{
                            u.setProjectToUpload(null);
                        }



                        BookUtil.openUploadBook(player);

                        break;
                    }

                    /*
                        World Edit
                     */

                    case WORLD_EDIT_NAME:{
                        switch(inv){
                            case WORLD_EDIT:{
                                String worldID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "worldID");
                                ConfigurableWorld world = Main.getInstance().getWorldManager().getWorld(worldID);
                                new AnvilGUI.Builder()
                                        .onClose(
                                                player1 -> GUIInventory.WORLD_EDIT.display(player, worldID)
                                        )
                                        .onComplete((player1, text) -> {
                                            world.setDisplayname(text);
                                            return AnvilGUI.Response.close();
                                        })
                                        .text(world.getDisplayname())
                                        .plugin(Main.getInstance())
                                        .open(player);
                                break;
                            }
                            case IMPORT_WORLD:{
                                ImportWorldInfo info = getImportWorldInfo(e.getClickedInventory());
                                new AnvilGUI.Builder()
                                        .onClose(
                                                player1 -> GUIInventory.IMPORT_WORLD.display(player, info)
                                        )
                                        .onComplete((player1, text) -> {
                                            info.setDisplayname(text);
                                            return AnvilGUI.Response.close();
                                        })
                                        .text(info.getDisplayname())
                                        .plugin(Main.getInstance())
                                        .open(player);

                                break;
                            }
                        }

                        break;

                    }
                    case WORLD_DOWNLOAD:{
                        String worldID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "worldID");
                        ConfigurableWorld world = Main.getInstance().getWorldManager().getWorld(worldID);

                        world.downloadWorld(player);
                        player.closeInventory();

                        break;
                    }
                    case WORLD_DELETE:{
                        String worldID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "worldID");

                        GUIInventory.WORLD_EDIT.display(player, worldID, true);
                        break;
                    }
                    case WORLD_CONFIRM_DELETE:{
                        String worldID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "worldID");
                        ConfigurableWorld world = ConfigurableWorld.getWorld(worldID);
                        Project project = world.getProject();
                        world.recycleWorld(player);

                        if(project != null) GUIInventory.PROJECT_OVERVIEW.display(player, project.getUUID().toString(), 1);
                        else GUIInventory.WORLDS_UNCATEGORIZED.display(player, 1);
                        player.sendMessage(Message.WORLD_RECYCLED.getWithPrefix(player).replace("{world}", world.getDisplayname()));
                        break;
                    }
                    case WORLD_CLONE:{
                        String worldID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "worldID");
                        ConfigurableWorld world = ConfigurableWorld.getWorld(worldID);
                        world.cloneWorld();

                        if(world.getProject() != null)
                            GUIInventory.PROJECT_OVERVIEW.display(player, world.getProjectID().toString(), 1);
                        else player.closeInventory();

                        break;
                    }
                    case WORLD_TRANSFER:{
                        String worldID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "worldID");
//                        GUIInventory.TRANSFER_WORLD_TO_1_16.display(player, worldID);
                        ConfigurableWorld world = ConfigurableWorld.getWorld(worldID);
                        world.transferWorld(player, "BUILD18");

                        break;
                    }
                    case WORLD_MOVE_PROJECT:{
                        String worldID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "worldID");
                        GUIInventory.CHANGE_PROJECT.display(player, worldID, 1);
                        break;
                    }

//                    case WORLD_TRANSFER_BUILD15:{
//                        String worldID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "worldID");
//                        ConfigurableWorld world = ConfigurableWorld.getWorld(worldID);
//                        world.transferWorld(player, "BUILD15");
//                        break;
//                    }
//                    case WORLD_TRANSFER_BUILD16:{
//                        String worldID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "worldID");
//                        ConfigurableWorld world = ConfigurableWorld.getWorld(worldID);
//                        world.transferWorld(player, "BUILD16");
//                        break;
//                    }

                    /*
                        World Move Project
                     */
                    case WORLD_MOVE_UNASSIGN:{
                        switch(inv){
                            case CHANGE_PROJECT:{
                                String worldID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "worldID");
                                ConfigurableWorld world = ConfigurableWorld.getWorld(worldID);
                                world.unassignFromProject();
                                GUIInventory.WORLDS_UNCATEGORIZED.display(player, 1);
                                break;
                            }
                            case IMPORT_WORLD_CHANGE_PROJECT:{
                                ImportWorldInfo info = getImportWorldInfo(e.getClickedInventory());
                                info.setProject(null);
                                GUIInventory.IMPORT_WORLD.display(player, info);
                                break;
                            }
                        }

                        break;
                    }



                    /*
                        World Properties
                     */

                    case WORLD_PROPERTY_TIMELOCK:{
                        String worldID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "worldID");
                        ConfigurableWorld world = Main.getInstance().getWorldManager().getWorld(worldID);
                        world.getWorldProperties().setTimeLock(!world.getWorldProperties().isTimeLock());

                        GUIInventory.WORLD_EDIT.display(player, worldID);
                        break;
                    }
                    case WORLD_PROPERTY_WEATHERLOCK:{
                        String worldID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "worldID");
                        ConfigurableWorld world = Main.getInstance().getWorldManager().getWorld(worldID);
                        world.getWorldProperties().setWeatherLock(!world.getWorldProperties().isWeatherLock());

                        GUIInventory.WORLD_EDIT.display(player, worldID);
                        break;
                    }
                    case WORLD_PROPERTY_MOB_SPAWN:{
                        String worldID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "worldID");
                        ConfigurableWorld world = Main.getInstance().getWorldManager().getWorld(worldID);
                        world.getWorldProperties().setMobSpawn(!world.getWorldProperties().isMobSpawn());

                        GUIInventory.WORLD_EDIT.display(player, worldID);
                        break;
                    }
                    case WORLD_PROPERTY_MOB_GRIEFING:{
                        String worldID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "worldID");
                        ConfigurableWorld world = Main.getInstance().getWorldManager().getWorld(worldID);
                        world.getWorldProperties().setMobGriefing(!world.getWorldProperties().isMobGriefing());

                        GUIInventory.WORLD_EDIT.display(player, worldID);
                        break;
                    }
                    case WORLD_PROPERTY_TNT_EXPLODE:{
                        String worldID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "worldID");
                        ConfigurableWorld world = Main.getInstance().getWorldManager().getWorld(worldID);
                        world.getWorldProperties().setTntExplode(!world.getWorldProperties().isTntExplode());

                        GUIInventory.WORLD_EDIT.display(player, worldID);
                        break;
                    }
                    case WORLD_PROPERTY_FIRE_SPREAD:{
                        String worldID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "worldID");
                        ConfigurableWorld world = Main.getInstance().getWorldManager().getWorld(worldID);
                        world.getWorldProperties().setFireSpread(!world.getWorldProperties().isFireSpread());

                        GUIInventory.WORLD_EDIT.display(player, worldID);
                        break;
                    }
                    case WORLD_PROPERTY_PLANT_GROWTH:{
                        String worldID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "worldID");
                        ConfigurableWorld world = Main.getInstance().getWorldManager().getWorld(worldID);
                        world.getWorldProperties().setGrowth(!world.getWorldProperties().isGrowth());

                        GUIInventory.WORLD_EDIT.display(player, worldID);
                        break;
                    }
                    case WORLD_PROPERTY_ICE_SNOW_MELT:{
                        String worldID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "worldID");
                        ConfigurableWorld world = Main.getInstance().getWorldManager().getWorld(worldID);
                        world.getWorldProperties().setIceSnowMelting(!world.getWorldProperties().isIceSnowMelting());

                        GUIInventory.WORLD_EDIT.display(player, worldID);
                        break;
                    }
                    case WORLD_PROPERTY_LIQUID_FLOW:{
                        String worldID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "worldID");
                        ConfigurableWorld world = Main.getInstance().getWorldManager().getWorld(worldID);
                        world.getWorldProperties().setLiquidFlow(!world.getWorldProperties().isLiquidFlow());

                        GUIInventory.WORLD_EDIT.display(player, worldID);
                        break;
                    }
                    case WORLD_PROPERTY_LEAF_DECAY:{
                        String worldID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "worldID");
                        ConfigurableWorld world = Main.getInstance().getWorldManager().getWorld(worldID);
                        world.getWorldProperties().setLeafDecay(!world.getWorldProperties().isLeafDecay());

                        GUIInventory.WORLD_EDIT.display(player, worldID);
                        break;
                    }
                    case WORLD_PROPERTY_SNOW_FORMING:{
                        String worldID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "worldID");
                        ConfigurableWorld world = Main.getInstance().getWorldManager().getWorld(worldID);
                        world.getWorldProperties().setSnowFall(!world.getWorldProperties().isSnowFall());

                        GUIInventory.WORLD_EDIT.display(player, worldID);
                        break;
                    }
                    case WORLD_PROPERTY_BLOCK_GRAVITY:{
                        String worldID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "worldID");
                        ConfigurableWorld world = Main.getInstance().getWorldManager().getWorld(worldID);
                        world.getWorldProperties().setBlockGravity(!world.getWorldProperties().isBlockGravity());

                        GUIInventory.WORLD_EDIT.display(player, worldID);
                        break;
                    }
                    case WORLD_PLACEHOLDER:{
                        switch(inv){
                            case WORLDS_UNCATEGORIZED:
                            case PROJECT_LIST:
                            case PROJECT_OVERVIEW:{
                                    if(!e.getClick().isShiftClick()){
                                    String worldID = new NBTItem(e.getCurrentItem()).getString("worldID");
                                    Main.getInstance().getWorldManager().getWorld(worldID).enterWorld(player);
                                }else{
                                    String worldID = new NBTItem(e.getCurrentItem()).getString("worldID");
                                    GUIInventory.WORLD_EDIT.display(player, worldID);
                                }
                                break;
                            }
                            case WORLD_EDIT:{
                                String worldID = new NBTItem(e.getCurrentItem()).getString("worldID");
                                ConfigurableWorld world = Main.getInstance().getWorldManager().getWorld(worldID);

                                if(e.getCursor() != null) if(e.getCursor().getType() != Material.AIR){
                                    world.setDisplayItem(new ItemStack(e.getCursor().getType(), e.getCursor().getAmount(), e.getCursor().getDurability()));
                                    e.setCursor(new ItemStack(e.getCurrentItem().getType(), e.getCurrentItem().getAmount(), e.getCurrentItem().getDurability()));
                                    GUIInventory.WORLD_EDIT.display(player, worldID);
                                }
                                break;
                            }
                            case RECYCLE_BIN:{
                                if(player.hasPermission("bbytes.admin") && e.getClick().isShiftClick()){
                                    String recycledWorldID = new NBTItem(e.getCurrentItem()).getString("recycledWorldID");
                                    RecycledConfigurableWorld world = Main.getInstance().getWorldManager().getRecycleBin().getWorld(recycledWorldID);
                                    Main.getInstance().getWorldManager().getRecycleBin().getRecycledWorldsList().remove(world);
                                    File file = new File(Bukkit.getWorldContainer(), world.getFileWorldName());;
                                    if(file.isDirectory()) {
                                        try {
                                            FileUtils.deleteDirectory(file);
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                    player.sendMessage(Message.WORLD_DELETE_FORCE.getWithPrefix(player).replace("{world}", world.getDisplayname()));

                                    break;
                                }
                                String recycledWorldID = new NBTItem(e.getCurrentItem()).getString("recycledWorldID");
                                RecycledConfigurableWorld world = Main.getInstance().getWorldManager().getRecycleBin().getWorld(recycledWorldID);
                                world.restore();
                                if(world.getProjectID() != null) if(Project.getProject(world.getProjectID().toString()) != null){
                                    GUIInventory.PROJECT_OVERVIEW.display(player, world.getProjectID().toString(), 1);
                                    break;
                                }

                                GUIInventory.PROJECT_LIST.display(player, 1);

                                break;
                            }
                            case WARP_WORLDS_IN_PROJECT:{
                                String worldID = new NBTItem(e.getCurrentItem()).getString("worldID");
                                GUIInventory.WARPS_IN_WORLD.display(player, worldID, 1);
                                break;
                            }

                        }
                        break;
                    }



                    /*
                         ACCESS
                     */

                    case ACCESS_NEW:{
                        String projectID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "projectID");
                        Project project = Project.getProject(projectID);

                        new AnvilGUI.Builder()
                                .onComplete((player1, text) -> {
                                    player.closeInventory();
                                    AtomicBoolean isLoaded = new AtomicBoolean(false);
                                    ExecutorService ex2 = Executors.newCachedThreadPool();
                                    ex2.execute(() -> {
                                        int i = 1;
                                        while(!isLoaded.get()){
                                            String msg = "§6Loading" + new String(new char[i]).replace("\0", ".");
                                            player.sendTitle(msg, "", 0, 8, 0);
                                            try {
                                                Thread.sleep(180);
                                            } catch (InterruptedException e1) {
                                                e1.printStackTrace();
                                            }

                                            i++;
                                            if(i > 3) i = 1;
                                        }
                                    });
                                    ex2.shutdown();
                                    UUID uuid = Main.getInstance().getUTNUtils().getUUIDFromName_Sync(text);
                                    isLoaded.set(true);
                                    if(uuid == null){
                                        player.sendMessage("§cError: §4Player not found");
                                        GUIInventory.ACCESS_EDIT.display(player, projectID, -1);
                                        return AnvilGUI.Response.close();
                                    }

                                    project.addMember(uuid);
                                    GUIInventory.ACCESS_EDIT.display(player, projectID, -1);
                                    return AnvilGUI.Response.close();
                                })
                                .text("\u0000")
                                .plugin(Main.getInstance())
                                .open(player);
                        break;
                    }
                    case ACCESS_PLACEHOLDER:{
                        UUID uuid = UUID.fromString(new NBTItem(e.getCurrentItem()).getString("uuid"));
                        Project project = Project.getProject(new NBTItem(e.getClickedInventory().getItem(4)).getString( "projectID"));
                        project.removeMember(uuid);

                        GUIInventory.ACCESS_EDIT.display(player, project.getUUID().toString(), getPage(e.getClickedInventory(), 6, 5));
                        break;
                    }

                    /*
                        Warp
                     */

                    case WARP_PLACEHOLDER:{

                        String warpName = new NBTItem(e.getCurrentItem()).getString("warp");
                        Warp warp = Warp.getWarp(warpName);
                        if(warp != null){
                            warp.goTo(player);
                            player.closeInventory();
                        }

                        break;
                    }

                    case WARP_UNCATEGORIZED:{
                        if(inv.equals(GUIInventory.WARP_PROJECTS)){
                            GUIInventory.WARPS_UNCATEGORIZED.display(player, 1);

                        }
                        break;
                    }

                    case IMPORT_SELECT_PLACEHOLDER:{
                        String projectID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "projectID");
                        String worldID = new NBTItem(e.getCurrentItem()).getString("worldID");


                        Project project = Project.getProject(projectID);
                        ImportWorldInfo info = new ImportWorldInfo();
                        info.setProject(project);
                        info.setDisplayname(worldID);
                        info.setWorldID(worldID);
                        GUIInventory.IMPORT_WORLD.display((player), info);
                        break;
                    }

                    /*
                        Import World Settings
                     */
                    case IMPORT_DISABLED:
                    case IMPORT_ENABLED:{
                        InventoryClickEvent event = new InventoryClickEvent(e.getView(), e.getSlotType(), e.getRawSlot()-9, e.getClick(), e.getAction());
                        this.onMenuItemClick(event);
                        return;
                    }
                    case IMPORT_ENV_OVERWORLD:{
                        ImportWorldInfo info = getImportWorldInfo(e.getClickedInventory());
                        info.setEnvironment(World.Environment.NORMAL);
                        inv.display(player, info);
                        break;
                    }
                    case IMPORT_ENV_NETHER:{
                        ImportWorldInfo info = getImportWorldInfo(e.getClickedInventory());
                        info.setEnvironment(World.Environment.NETHER);
                        inv.display(player, info);
                        break;
                    }
                    case IMPORT_ENV_THE_END:{

                        ImportWorldInfo info = getImportWorldInfo(e.getClickedInventory());
                        info.setEnvironment(World.Environment.THE_END);
                        inv.display(player, info);
                        break;
                    }

                    case IMPORT_TYPE_NORMAL:{
                        ImportWorldInfo info = getImportWorldInfo(e.getClickedInventory());
                        info.setConfigurableWorldType(ConfigurableWorldType.NORMAL);
                        inv.display(player, info);
                        break;
                    }
                    case IMPORT_TYPE_FLAT:{
                        ImportWorldInfo info = getImportWorldInfo(e.getClickedInventory());
                        info.setConfigurableWorldType(ConfigurableWorldType.FLAT);
                        inv.display(player, info);
                        break;
                    }
                    case IMPORT_TYPE_VOID:{
                        ImportWorldInfo info = getImportWorldInfo(e.getClickedInventory());
                        info.setConfigurableWorldType(ConfigurableWorldType.VOID);
                        inv.display(player, info);
                        break;
                    }
                    case IMPORT_CREATE:{
                        ImportWorldInfo info = getImportWorldInfo(e.getClickedInventory());
                        if(ConfigurableWorld.getWorld(info.getWorldID()) != null){
                            player.sendMessage("§cError: §4World already imported.");
                            player.closeInventory();
                            break;
                        }
                        if(info.getProject() != null) if(!info.getProject().canAccess(player.getUniqueId())){
                                player.sendMessage("§cError: §4You do not have access to this project");
                                player.sendTitle("§cNo Access", "", 4, 40, 4);
                                player.closeInventory();
                                break;
                            }
                        player.closeInventory();
                        info.createWorldOpenMenu(player);

                        break;
                    }


                    case GENERIC_PAGE_NEXT:{
                        switch(inv){
                            case PROJECT_LIST:{
                                int page = getPage(e.getClickedInventory(), 5, 5);
                                GUIInventory.PROJECT_LIST.display(player, page + 1);
                                break;
                            }
                            case PROJECT_OVERVIEW:{
                                int page = getPage(e.getClickedInventory(), 5, 5);
                                String projectID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "projectID");
                                GUIInventory.PROJECT_OVERVIEW.display(player, projectID, page + 1);
                                break;
                            }
                            case ACCESS_EDIT:{
                                int page = getPage(e.getClickedInventory(), 6, 5);
                                String projectID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "projectID");
                                GUIInventory.ACCESS_EDIT.display(player, projectID, page + 1);
                                break;
                            }
                            case WARP_PROJECTS:{
                                int page = getPage(e.getClickedInventory(), 5, 5);
                                inv.display(player, page + 1);
                                break;
                            }
                            case WARP_WORLDS_IN_PROJECT:{
                                int page = getPage(e.getClickedInventory(), 5, 5);
                                String projectID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "projectID");
                                inv.display(player, projectID, page + 1);
                                break;
                            }
                            case WARPS_IN_WORLD:{
                                String worldID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "worldID");
                                int page = getPage(e.getClickedInventory(), 6, 5);
                                inv.display(player, worldID, page + 1);
                                break;
                            }
                            case WORLDS_UNCATEGORIZED:
                            case WARPS_UNCATEGORIZED:{
                                int page = getPage(e.getClickedInventory(), 6, 5);
                                inv.display(player, page + 1);
                                break;
                            }
                            case CHANGE_PROJECT:{
                                int page = getPage(e.getClickedInventory(), 6, 5);
                                String worldID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "worldID");
                                inv.display(player, worldID, page + 1);
                                break;
                            }
                            case IMPORT_WORLD_CHANGE_PROJECT:{
                                int page = getPage(e.getClickedInventory(), 6, 5);
                                ImportWorldInfo info = getImportWorldInfo(e.getClickedInventory());
                                inv.display(player, info, page + 1);
                                break;
                            }
                            case IMPORT_WORLD_CHOOSE_WORLD:{
                                int page = getPage(e.getClickedInventory(), 6, 5);
                                String projectID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "projectID");
                                inv.display(player, projectID, page+1);
                                break;
                            }
                        }
                        break;
                    }
                    case GENERIC_PAGE_PREV:{
                        switch(inv){
                            case PROJECT_LIST:{
                                int page = getPage(e.getClickedInventory(), 5, 5);
                                GUIInventory.PROJECT_LIST.display(player, page - 1);
                                break;
                            }
                            case PROJECT_OVERVIEW:{
                                int page = getPage(e.getClickedInventory(), 5, 5);
                                String projectID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "projectID");
                                GUIInventory.PROJECT_OVERVIEW.display(player, projectID, page - 1);
                                break;
                            }
                            case ACCESS_EDIT:{
                                int page = getPage(e.getClickedInventory(), 6, 5);
                                String projectID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "projectID");
                                GUIInventory.ACCESS_EDIT.display(player, projectID, page - 1);
                                break;
                            }
                            case WARP_PROJECTS:{
                                int page = getPage(e.getClickedInventory(), 5, 5);
                                inv.display(player, page - 1);
                                break;
                            }
                            case WARP_WORLDS_IN_PROJECT:{
                                int page = getPage(e.getClickedInventory(), 5, 5);
                                String projectID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "projectID");
                                inv.display(player, projectID, page - 1);
                                break;
                            }
                            case WARPS_IN_WORLD:{
                                String worldID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "worldID");
                                int page = getPage(e.getClickedInventory(), 6, 5);
                                inv.display(player, worldID, page - 1);
                                break;
                            }
                            case WARPS_UNCATEGORIZED:
                            case WORLDS_UNCATEGORIZED: {
                                int page = getPage(e.getClickedInventory(), 6, 5);
                                inv.display(player, page -1);
                                break;
                            }
                            case CHANGE_PROJECT:{
                                int page = getPage(e.getClickedInventory(), 6, 5);
                                String worldID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "worldID");
                                inv.display(player, worldID, page - 1);
                                break;
                            }
                            case IMPORT_WORLD_CHANGE_PROJECT:{
                                int page = getPage(e.getClickedInventory(), 6, 5);
                                ImportWorldInfo info = getImportWorldInfo(e.getClickedInventory());
                                inv.display(player, info, page - 1);
                                break;
                            }
                            case IMPORT_WORLD_CHOOSE_WORLD:{
                                int page = getPage(e.getClickedInventory(), 6, 5);
                                String projectID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "projectID");
                                inv.display(player, projectID, page-1);
                                break;
                            }
                        }
                        break;
                    }
                    case GENERIC_GO_BACK:{
                        switch(inv){
                            case PROJECT_OVERVIEW:
                            case RECYCLE_BIN:
                            case WORLDS_UNCATEGORIZED: {
                               GUIInventory.PROJECT_LIST.display(player,1);
                               break;
                            }
                            case PROJECT_EDIT:
                            case NEW_WORLD: {
                                String projectID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "projectID");
                                try{
                                    UUID.fromString(projectID);
                                    GUIInventory.PROJECT_OVERVIEW.display(player, projectID, 1);
                                }catch(IllegalArgumentException ex){
                                    GUIInventory.WORLDS_UNCATEGORIZED.display(player, 1);
                                }

                                break;
                            }
                            case WORLD_EDIT:{
                                String worldID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "worldID");
                                Project project = Main.getInstance().getWorldManager().getWorld(worldID).getProject();
                                if(project != null)  GUIInventory.PROJECT_OVERVIEW.display(player, project.getUUID().toString(), 1);
                                else GUIInventory.WORLDS_UNCATEGORIZED.display(player, 1);
                                break;
                            }
                            case ACCESS_EDIT:{
                                String projectID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "projectID");
                                GUIInventory.PROJECT_EDIT.display(player, projectID);
                                break;
                            }
                            case WARP_WORLDS_IN_PROJECT:{
                                String projectID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "projectID");
                                GUIInventory.WARP_PROJECTS.display(player, 1);
                                break;
                            }
                            case WARPS_IN_WORLD:{
                                String worldID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "worldID");
                                Project project = ConfigurableWorld.getWorld(worldID).getProject();
                                if(project != null) GUIInventory.WARP_WORLDS_IN_PROJECT.display(player, project.getUUID().toString(), 1);
                                else GUIInventory.WARPS_UNCATEGORIZED.display(player, 1);
                                break;
                            }

                            case WARPS_UNCATEGORIZED:{
                                GUIInventory.WARP_PROJECTS.display(player, 1);
                                break;
                            }
                            case CHANGE_PROJECT:
                            case TRANSFER_WORLD_TO_1_16: {
                                String worldID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "worldID");
                                GUIInventory.WORLD_EDIT.display(player, worldID);
                                break;
                            }
                            case IMPORT_WORLD_CHANGE_PROJECT:{
                                ImportWorldInfo info = getImportWorldInfo(e.getClickedInventory());
                                GUIInventory.IMPORT_WORLD.display(player, info);
                                break;
                            }
                            case IMPORT_WORLD:{
                                player.closeInventory();
                                break;
                            }
                            case IMPORT_WORLD_CHOOSE_WORLD:{
                                String projectID = new NBTItem(e.getClickedInventory().getItem(4)).getString( "projectID");
                                GUIInventory.NEW_WORLD.display(player, projectID);
                                break;
                            }

                        }


                        break;
                    }


                }







//                System.out.println("Player '" + player.getName() + "' clicked item '" + item + "' in inventory '" + inv + "'");

				/*
						CLICK GUI ITEM END
				 */

            }
        }


    }

    @EventHandler
    public void onMenuClose(InventoryCloseEvent e){
        GUIInventory inv = Main.getInstance().getInventoryManager().getGUIInventory(e.getView().getTopInventory());

        if(inv != null){
            switch(inv){
                case IMPORT_WORLD_CHANGE_PROJECT:
                case IMPORT_WORLD:{
                    Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                        if(e.getPlayer().getOpenInventory().getTopInventory().getSize() == 5){
                            ImportWorldInfo info = getImportWorldInfo(e.getView().getTopInventory());
                            info.cancelImport((Player) e.getPlayer());
                        }
                    }, 2);

                    break;
                }
            }
        }
    }

    public int getPage(Inventory inv, int row, int column){
        ItemStack item = inv.getItem((row-1)*9+column-1);
        return new NBTItem(item).getInteger("page");
    }

    public ImportWorldInfo getImportWorldInfo(Inventory inv){
        ImportWorldInfo importWorldInfo = new ImportWorldInfo();

        ItemStack item = inv.getItem(4);

        assert item != null;
        importWorldInfo.setUpload(new NBTItem(item).getByte( "info.upload") == 0x01);
        importWorldInfo.setDisplayname(new NBTItem(item).getString( "info.displayName"));
        importWorldInfo.setWorldID(new NBTItem(item).getString("info.worldID"));
        importWorldInfo.setProject(Project.getProject(new NBTItem(item).getString( "info.projectID")));
        importWorldInfo.setConfigurableWorldType(ConfigurableWorldType.valueOf(new NBTItem(item).getString("info.configurableWorldType")));
        importWorldInfo.setEnvironment(World.Environment.valueOf(new NBTItem(item).getString( "info.environment")));

        return importWorldInfo;
    }
}
