package net.bbytes.bukkit.inventory.guis;

import net.bbytes.bukkit.Main;
import net.bbytes.bukkit.inventory.GUIItem;
import net.bbytes.bukkit.message.Message;
import net.bbytes.bukkit.project.Project;
import net.bbytes.bukkit.user.User;
import net.bbytes.bukkit.world.ConfigurableWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ProjectListInventory extends BaseInventory {



    @Override
    public Inventory getInventory(Player p, Object... options){
        setPlayer(p);
        int page = (int) options[0];
        Inventory inv = createInventory(6, Message.PROJECTS_TITLE.get(p));

        setItem(inv, itemStackUtils.getItemStack(GUIItem.PLUGIN_INFO, new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                "\\" + Message.PROJECTS_PLUGIN_INFO.get(p),
                "",
                "§2§6» §eMade by Viktoracri"
        }), 1, 5);

        setItem(inv, itemStackUtils.getItemStack(GUIItem.PROJECTS_RECYCLE_BIN, Message.PROJECTS_RECYCLE_BIN.get(p), new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                "\\" + Message.PROJECTS_RECYCLE_BIN_INFO.get(p),
                "",
                "§2§l» §a" + Message.CLICK_TO_OPEN.get(p)
        }), 1, 9);

        setItem(inv, itemStackUtils.getItemStack(GUIItem.PROJECTS_PLOT_WORLD, Message.PROJECTS_PLOT_WORLD.get(p), new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                "\\" + Message.PROJECTS_PLOT_WORLD_INFO.get(p),
                "",
                "§2§l» §a" + Message.PROJECTS_PLOT_WORLD_GO.get(p)
        }), 1, 3);

        setItem(inv, itemStackUtils.getItemStack(GUIItem.PROJECTS_SPAWN, new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                "\\" + Message.PROJECTS_SPAWN.get(p),
                "",
                "§2§l» §a" + Message.PROJECTS_SPAWN_GO.get(p)
        }), 1, 1);

        if(p.hasPermission("bbytes.admin"))
            setItem(inv, itemStackUtils.getItemStack(GUIItem.PROJECTS_NEW_PROJECT, Message.PROJECTS_NEW.get(p), new String[]{
                    Message.FORMAT_DIVIDER.getRaw(),
                    "§8[§c" + Message.INFO_ADMIN_ONLY.get(p) + "§8]",
                    "\\" + Message.PROJECTS_NEW_INFO.get(p),
                    "",
                    "§2§l» §a" + Message.PROJECTS_NEW_CREATE.get(p)
            }), 1, 7);

        setItem(inv, itemStackUtils.getItemStack(GUIItem.PROJECTS_UNCATEGORIZED_WORLDS, Message.PROJECTS_UNCATEGORIZED_WORLDS.get(p), new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                "\\" + Message.PROJECTS_UNCATEGORIZED_WORLDS_INFO.get(p),
                "",
                "§2§l» §a" + Message.CLICK_TO_BROWSE.get(p)
        }), 5, 9);


        //  Project List




        List<ItemStack> itemStackList = new ArrayList<>();

        for(int i = (page-1)*21; i < Main.getInstance().getProjectManager().getProjectList().size(); i++){
            Project project = Main.getInstance().getProjectManager().getProjectList().get(i);
            if(!project.canAccess(p.getUniqueId()))continue;
            ItemStack item = itemStackUtils.getItemStack(project.getDisplayItem(), "§6" + project.getName(), new String[]{
                    Message.FORMAT_DIVIDER.getRaw(),
                    "§8§l» §7" + Message.WORD_PROJECT_LEADER.get(p)+ ": §b" + project.getProjectManager(),
                    "§8§l» §7" + Message.WORD_WORLDS.get(p)+": §b" + project.getWorlds().size(),
                    "",
                    "§2§l» §a"+ Message.CLICK_EXPLORE_PROJECT.get(p)
            });

            item = applyGUIItem(item, GUIItem.PROJECT_PLACEHOLDER);
            item = setNBT(item, "projectID", project.getUUID().toString());
            itemStackList.add(item);
        }

        if(itemStackList.size() == 0)
            setItem(inv, itemStackUtils.getItemStack(GUIItem.GENERIC_EMPTY, new String[]{
                    Message.FORMAT_DIVIDER.getRaw(),
                    "\\" + Message.EMPTY_PROJECTS.get(p)
            }), 3, 5);
        else
            for(int i = 0; i < 21; i++){
                if(i >= itemStackList.size())break;
                setItem(inv, itemStackList.get(i), (i/7)+2, (i%7)+2);

            }

        //  Project Paging

        int pages = ((itemStackList.size()-1)/21)+1 + (page-1);

        ItemStack item = itemStackUtils.getItemStack(
                GUIItem.GENERIC_PAGES.getItem(),
                Message.PAGES.get(p).replace("{page}", "" + page).replace("{pages}", "" + pages));

        item = setNBT(item, "page", page);
        item.setAmount(Math.min(page, 64));

        setItem(inv, item, 5, 5);

        if(page < pages){
            item = itemStackUtils.getItemStack(GUIItem.GENERIC_PAGE_NEXT.getItem(), Message.PAGE_NEXT.get(p));
            item.setAmount(Math.min(page+1, 64));
            setItem(inv, item, 5, 7);
        }

        if(page > 1){
            item = itemStackUtils.getItemStack(GUIItem.GENERIC_PAGE_PREV.getItem(), Message.PAGE_PREVIOUS.get(p));
            item.setAmount(Math.min(page-1, 64));
            setItem(inv, item, 5, 3);
        }



        User user = Main.getInstance().getUserManager().getUser(p);



        itemStackList = new ArrayList<>();

        for(ConfigurableWorld configurableWorld : user.getRecentWorlds()){
            if(configurableWorld.isRecycled())continue;
            if(configurableWorld.getProject() != null) if(!configurableWorld.getProject().canAccess(p.getUniqueId()))continue;
            item = itemStackUtils.getItemStack(configurableWorld.getDisplayItem(), "§6" + configurableWorld.getDisplayname(), new String[]{
                    Message.FORMAT_DIVIDER.getRaw(),
                    "§8§l» §7" + Message.WORD_PROJECT.get(p) + ": §b" + (configurableWorld.getProject() != null ? configurableWorld.getProject().getName() : Message.WORD_NONE.get(p)),
                    "",
                    "§2§l» §a" + Message.CLICK_TRAVEL.get(p),
                    "§2§l» §a"+ Message.CLICK_SHIFT_EDIT.get(p)
            });

            item = applyGUIItem(item, GUIItem.WORLD_PLACEHOLDER);
            item = setNBT(item, "worldID", configurableWorld.getFileWorldName());
            itemStackList.add(item);

            if(itemStackList.size() >= 7)break;
        }

        if(itemStackList.size() == 0)
            setItem(inv, itemStackUtils.getItemStack(GUIItem.GENERIC_EMPTY, "§c"+Message.EMPTY.get(p), new String[]{
                    Message.FORMAT_DIVIDER.getRaw(),
                    "" + Message.EMPTY_NO_RECENT_WORLDS.get(p)
            }), 6, 5);
        else
            for(int i = 0; i < 7; i++){
                if(i >= itemStackList.size()) break;
                setItem(inv, itemStackList.get(i), 6, i+2);
            }


        addBorder(inv, 1);
        addBorder(inv, 5);


        return inv;
    }


}
