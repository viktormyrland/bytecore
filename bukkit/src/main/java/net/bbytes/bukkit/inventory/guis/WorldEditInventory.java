package net.bbytes.bukkit.inventory.guis;

import net.bbytes.bukkit.Main;
import net.bbytes.bukkit.inventory.GUIItem;
import net.bbytes.bukkit.message.Message;
import net.bbytes.bukkit.project.Project;
import net.bbytes.bukkit.world.ConfigurableWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class WorldEditInventory extends BaseInventory {



    @Override
    public Inventory getInventory(Player player, Object... options){
        setPlayer(player);

        String worldID = (String) options[0];
        boolean confirm = false;
        if(options.length > 1) confirm = true;

        ConfigurableWorld world = Main.getInstance().getWorldManager().getWorld(worldID);
        if(world == null) return null;

        if(world.getProject() != null) if(!world.getProject().canAccess(player.getUniqueId())){
            player.sendMessage(getMessage(Message.NO_ACCESS_WORLD));
            player.sendTitle(getMessage(Message.NO_ACCESS), "", 4, 40, 4);
            player.closeInventory();
            return null;
        }


        Inventory inv = createInventory(4, getMessage(Message.WORLD_EDIT));

        Project project = null;
        if(world.getProjectID() != null) if(Main.getInstance().getProjectManager().getProject(world.getProjectID()) != null)
            project = Main.getInstance().getProjectManager().getProject(world.getProjectID());

        ItemStack item = itemStackUtils.getItemStack(world.getDisplayItem(), "§6" + world.getDisplayname(), new String[]{
                        "§8" + (isUUIDFormat(world.getFileWorldName()) ? world.getFileWorldName().substring(0, world.getFileWorldName().indexOf("-")) : world.getFileWorldName()),
                        "§8» §7" + getMessage(Message.WORD_PROJECT) + ": §b" + (project != null ? project.getName() : getMessage(Message.WORD_NONE)),
                        "§8» §7" + getMessage(Message.WORLD_NAME) + ": §b" + world.getDisplayname(),
                        "",
                        "§2§l» §a" + getMessage(Message.CLICK_EDIT_ICON)

                });

        item = applyGUIItem(item, GUIItem.WORLD_PLACEHOLDER);
        item = setNBT(item, "worldID", world.getFileWorldName());

        setItem(inv, item, 1, 5);

        setItem(inv, itemStackUtils.getItemStack(GUIItem.WORLD_DOWNLOAD, getMessage(Message.WORLD_EDIT_DOWNLOAD), new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                "\\" + getMessage(Message.WORLD_EDIT_DOWNLOAD_INFO),
                "",
                "§2§l» §a" + getMessage(Message.WORLD_EDIT_DOWNLOAD)
        }), 1, 7);

        if(Main.getInstance().getSubversion() == 17)
            setItem(inv, itemStackUtils.getItemStack(GUIItem.WORLD_TRANSFER, getMessage(Message.WORLD_EDIT_TRANSFER), new String[]{
                    Message.FORMAT_DIVIDER.getRaw(),
                    "\\" + getMessage(Message.WORLD_EDIT_TRANSFER_INFO),
                    "",
                    "§2§l» §a" + getMessage(Message.WORLD_EDIT_TRANSFER)
            }), 1, 3);

        setItem(inv, itemStackUtils.getItemStack(GUIItem.WORLD_CLONE, getMessage(Message.WORLD_EDIT_CLONE), new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                "\\" + getMessage(Message.WORLD_EDIT_CLONE_INFO),
                "",
                "§2§l» §a" + getMessage(Message.WORLD_EDIT_CLONE)
        }), 1, 9);

        if(player.hasPermission("bbytes.admin"))
            setItem(inv, itemStackUtils.getItemStack(GUIItem.WORLD_MOVE_PROJECT, getMessage(Message.CHANGE_PROJECT), new String[]{
                    Message.FORMAT_DIVIDER.getRaw(),
                    "§8[§c" + getMessage(Message.INFO_ADMIN_ONLY) + "§8]",
                    "\\" + getMessage(Message.WORLD_EDIT_CHANGE_PROJECT_INFO),
                    "",
                    "§2§l» §a" + getMessage(Message.CHANGE_PROJECT)
            }), 4, 7);

        if(!confirm)
            setItem(inv, itemStackUtils.getItemStack(GUIItem.WORLD_DELETE, "§4" + getMessage(Message.WORLD_EDIT_DELETE), new String[]{
                    Message.FORMAT_DIVIDER.getRaw(),
                    "\\" + getMessage(Message.WORLD_EDIT_DELETE_INFO),
                    "",
                    "§4§l» §c" + getMessage(Message.WORLD_EDIT_DELETE)
            }), 4, 9);
        else setItem(inv, itemStackUtils.getItemStack(GUIItem.WORLD_CONFIRM_DELETE, "§4" + getMessage(Message.WORLD_EDIT_CONFIRM_DELETE), new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                "\\" + getMessage(Message.WORLD_EDIT_DELETE_INFO),
                "",
                "§4§l» §c§n" + getMessage(Message.WORLD_EDIT_CONFIRM_DELETE)
        }), 4, 9);

        setItem(inv, itemStackUtils.getItemStack(GUIItem.GENERIC_GO_BACK, getMessage(Message.GO_BACK)), 4, 1);

        setItem(inv, itemStackUtils.getItemStack(GUIItem.WORLD_EDIT_NAME, getMessage(Message.WORLD_NAME), new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                "\\" + getMessage(Message.WORLD_EDIT_NAME_INFO),
                "",
                "§8» §7" + getMessage(Message.CURRENT_NAME) + ": §6" + world.getDisplayname(),
                "",
                "§2§l» §a" + getMessage(Message.WORLD_EDIT_NAME)

        }), 1, 1);


        /*
            World Properties
         */

        String disabled = "§c" + getMessage(Message.WORLD_EDIT_DISABLED) + "§7";
        String enabled = "§a"  + getMessage(Message.WORLD_EDIT_ENABLED) + "§7";

        item = itemStackUtils.getItemStack(GUIItem.WORLD_PROPERTY_TIMELOCK, getMessage(Message.PROPERTY_TIMELOCK), new String[]{
                    Message.FORMAT_DIVIDER.getRaw(),
                    getMessage(Message.WORLD_EDIT_STATUS) + ": "+ (world.getWorldProperties().isTimeLock() ? enabled : disabled),
                    "",
                    "\\" + getMessage(Message.PROPERTY_TIMELOCK_INFO),
                    "",
                    "§2§l» §a" + getMessage(Message.CLICK_TOGGLE)
            });
        if(world.getWorldProperties().isTimeLock())
            addGlow(item);
        setItem(inv, item, 2, 2);

        item = itemStackUtils.getItemStack(GUIItem.WORLD_PROPERTY_WEATHERLOCK, getMessage(Message.PROPERTY_WEATHERLOCK), new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                getMessage(Message.WORLD_EDIT_STATUS) + ": "+ (world.getWorldProperties().isWeatherLock() ? enabled : disabled),
                "",
                "\\" + getMessage(Message.PROPERTY_WEATHERLOCK_INFO),
                "",
                "§2§l» §a" + getMessage(Message.CLICK_TOGGLE)
        });
        if(world.getWorldProperties().isWeatherLock())
            addGlow(item);
        setItem(inv, item, 3, 2);

        item = itemStackUtils.getItemStack(GUIItem.WORLD_PROPERTY_MOB_SPAWN, getMessage(Message.PROPERTY_MOBSPAWN), new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                getMessage(Message.WORLD_EDIT_STATUS)+ ": " + (world.getWorldProperties().isMobSpawn() ? enabled : disabled),
                "",
                "\\" + getMessage(Message.PROPERTY_MOBSPAWN_INFO),
                "",
                "§2§l» §a" + getMessage(Message.CLICK_TOGGLE)
        });
        if(world.getWorldProperties().isMobSpawn())
            addGlow(item);
        setItem(inv, item, 2, 4);

        item = itemStackUtils.getItemStack(GUIItem.WORLD_PROPERTY_FIRE_SPREAD, getMessage(Message.PROPERTY_FIRE_SPREAD), new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                getMessage(Message.WORLD_EDIT_STATUS) + ": "+ (world.getWorldProperties().isFireSpread() ? enabled : disabled),
                "",
                "\\" + getMessage(Message.PROPERTY_FIRE_SPREAD_INFO),
                "",
                "§2§l» §a" + getMessage(Message.CLICK_TOGGLE)
        });
        if(world.getWorldProperties().isFireSpread())
            addGlow(item);
        setItem(inv, item, 2, 5);

        item = itemStackUtils.getItemStack(GUIItem.WORLD_PROPERTY_PLANT_GROWTH, getMessage(Message.PROPERTY_PLANT_GROWTH), new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                getMessage(Message.WORLD_EDIT_STATUS) + ": "+ (world.getWorldProperties().isGrowth() ? enabled : disabled),
                "",
                "\\" + getMessage(Message.PROPERTY_PLANT_GROWTH_INFO),
                "",
                "§2§l» §a" + getMessage(Message.CLICK_TOGGLE)
        });
        if(world.getWorldProperties().isGrowth())
            addGlow(item);
        setItem(inv, item, 2, 6);

        item = itemStackUtils.getItemStack(GUIItem.WORLD_PROPERTY_LIQUID_FLOW, getMessage(Message.PROPERTY_LIQUID_FLOW), new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                getMessage(Message.WORLD_EDIT_STATUS) + ": "+ (world.getWorldProperties().isLiquidFlow() ? enabled : disabled),
                "",
                "\\" + getMessage(Message.PROPERTY_LIQUID_FLOW_INFO),
                "",
                "§2§l» §a" + getMessage(Message.CLICK_TOGGLE)
        });
        if(world.getWorldProperties().isLiquidFlow())
            addGlow(item);
        setItem(inv, item, 2, 7);

        item = itemStackUtils.getItemStack(GUIItem.WORLD_PROPERTY_SNOW_FORMING, getMessage(Message.PROPERTY_SNOW_FORMING), new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                getMessage(Message.WORLD_EDIT_STATUS) + ": "+ (world.getWorldProperties().isSnowFall() ? enabled : disabled),
                "",
                "\\" + getMessage(Message.PROPERTY_SNOW_FORMING_INFO),
                "",
                "§2§l» §a" + getMessage(Message.CLICK_TOGGLE)
        });
        if(world.getWorldProperties().isSnowFall())
            addGlow(item);
        setItem(inv, item, 2, 8);

        item = itemStackUtils.getItemStack(GUIItem.WORLD_PROPERTY_BLOCK_GRAVITY, getMessage(Message.PROPERTY_BLOCK_GRAVITY), new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                getMessage(Message.WORLD_EDIT_STATUS) + ": "+ (world.getWorldProperties().isBlockGravity() ? enabled : disabled),
                "",
                "\\" + getMessage(Message.PROPERTY_BLOCK_GRAVITY_INFO),
                "",
                "§2§l» §a" + getMessage(Message.CLICK_TOGGLE)
        });
        if(world.getWorldProperties().isBlockGravity())
            addGlow(item);
        setItem(inv, item, 3, 4);

        item = itemStackUtils.getItemStack(GUIItem.WORLD_PROPERTY_TNT_EXPLODE, getMessage(Message.PROPERTY_TNT_EXPLODE), new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                getMessage(Message.WORLD_EDIT_STATUS)+ ": " + (world.getWorldProperties().isTntExplode() ? enabled : disabled),
                "",
                "\\" + getMessage(Message.PROPERTY_TNT_EXPLODE_INFO),
                "",
                "§2§l» §a" + getMessage(Message.CLICK_TOGGLE)
        });
        if(world.getWorldProperties().isTntExplode())
            addGlow(item);
        setItem(inv, item, 3, 5);

        item = itemStackUtils.getItemStack(GUIItem.WORLD_PROPERTY_ICE_SNOW_MELT, getMessage(Message.PROPERTY_ICE_SNOW_MELT), new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                getMessage(Message.WORLD_EDIT_STATUS) + ": "+ (world.getWorldProperties().isIceSnowMelting() ? enabled : disabled),
                "",
                "\\" + getMessage(Message.PROPERTY_ICE_SNOW_MELT_INFO),
                "",
                "§2§l» §a" + getMessage(Message.CLICK_TOGGLE)
        });
        if(world.getWorldProperties().isIceSnowMelting())
            addGlow(item);
        setItem(inv, item, 3, 6);

        item = itemStackUtils.getItemStack(GUIItem.WORLD_PROPERTY_MOB_GRIEFING, getMessage(Message.PROPERTY_MOB_GRIEFING), new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                getMessage(Message.WORLD_EDIT_STATUS) + ": "+ (world.getWorldProperties().isMobGriefing() ? enabled : disabled),
                "",
                "\\" + getMessage(Message.PROPERTY_MOB_GRIEFING_INFO),
                "",
                "§2§l» §a" + getMessage(Message.CLICK_TOGGLE)
        });
        if(world.getWorldProperties().isMobGriefing())
            addGlow(item);
        setItem(inv, item, 3, 7);

        item = itemStackUtils.getItemStack(GUIItem.WORLD_PROPERTY_LEAF_DECAY, getMessage(Message.PROPERTY_LEAF_DECAY), new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                getMessage(Message.WORLD_EDIT_STATUS) + ": " + (world.getWorldProperties().isLeafDecay() ? enabled : disabled),
                "",
                "\\" + getMessage(Message.PROPERTY_LEAF_DECAY_INFO),
                "",
                "§2§l» §a" + getMessage(Message.CLICK_TOGGLE)
        });
        if(world.getWorldProperties().isLeafDecay())
            addGlow(item);
        setItem(inv, item, 3, 8);


        addBorder(inv);
        return inv;
    }


}
