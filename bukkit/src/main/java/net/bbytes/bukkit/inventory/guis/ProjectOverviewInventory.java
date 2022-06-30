package net.bbytes.bukkit.inventory.guis;

import net.bbytes.bukkit.Main;
import net.bbytes.bukkit.inventory.GUIItem;
import net.bbytes.bukkit.message.Message;
import net.bbytes.bukkit.project.Project;
import net.bbytes.bukkit.world.ConfigurableWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ProjectOverviewInventory extends BaseInventory {


    @Override
    public Inventory getInventory(Player p, Object... options){
        setPlayer(p);

        UUID projectID = UUID.fromString((String) options[0]);
        int page = (int) options[1];
        Project project = Main.getInstance().getProjectManager().getProject(projectID);

        if(!project.canAccess(p.getUniqueId())){
            p.sendMessage(getMessage(Message.NO_ACCESS_PROJECT));
            p.sendTitle("§c" + getMessage(Message.NO_ACCESS), "", 4, 40, 4);
            p.closeInventory();
            return null;
        }

        Inventory inv = createInventory(5, Message.PROJECT_OVERVIEW.get(p));

        ItemStack item = itemStackUtils.getItemStack(
                project.getDisplayItem(),
                "§6" + project.getName(),
                new String[]{
                        Message.FORMAT_DIVIDER.getRaw(),
                        "§8§l» §3" + getMessage(Message.WORD_PROJECT_LEADER)+": §b" + project.getProjectManager(),
                        "§8§l» §3" + getMessage(Message.WORD_WORLDS)+": §b" + project.getWorlds().size(),
                });

        item = setNBT(item, "projectID", projectID.toString());
        item = applyGUIItem(item, GUIItem.PROJECT_PLACEHOLDER);

        setItem(inv, item, 1, 5);

        setItem(inv, itemStackUtils.getItemStack(GUIItem.PROJECT_NEW_WORLD, getMessage(Message.NEW_WORLD), new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                "\\" + getMessage(Message.NEW_WORLD_INFO),
                "",
                "§2§l» §a" + getMessage(Message.CLICK_NEW_WORLD)
        }), 1, 7);

        if(p.hasPermission("bbytes.admin"))
            setItem(inv, itemStackUtils.getItemStack(GUIItem.PROJECT_EDIT, getMessage(Message.EDIT_PROJECT), new String[]{
                    Message.FORMAT_DIVIDER.getRaw(),
                    "§8[§c" + getMessage(Message.INFO_ADMIN_ONLY) + "§8]",
                    "\\" + getMessage(Message.EDIT_PROJECT_INFO),
                    "",
                    "§2§l» §a" + getMessage(Message.CLICK_EDIT_PROJECT)
            }), 1, 3);




        int pages = ((project.getWorlds().size()-1)/21)+1;

        if(project.getWorlds().size() == 0)
            setItem(inv, itemStackUtils.getItemStack(GUIItem.GENERIC_EMPTY, getMessage(Message.EMPTY), new String[]{
                    Message.FORMAT_DIVIDER.getRaw(),
                    getMessage(Message.EMPTY_WORLDS)
            }), 3, 5);

        for(int i = 0; i < 21; i++){
            if(i+((page-1)*21) >= project.getWorlds().size()) break;
            ConfigurableWorld world = project.getWorlds().get(((page-1)*21) + i);
            if(world == null) break;

            item = itemStackUtils.getItemStack(world.getDisplayItem(), "§6" + world.getDisplayname(), new String[]{
                    Message.FORMAT_DIVIDER.getRaw(),
                    "§2§l» §a" + getMessage(Message.CLICK_TRAVEL),
                    "§2§l» §a" + getMessage(Message.CLICK_SHIFT_EDIT)
            });

            item = applyGUIItem(item, GUIItem.WORLD_PLACEHOLDER);
            item = setNBT(item, "worldID", world.getFileWorldName());

            setItem(inv, item, (i/7)+2, (i%7)+2);

        }

        //  Worlds Paging

        item = itemStackUtils.getItemStack(
                GUIItem.GENERIC_PAGES.getItem(),
                getMessage(Message.PAGES).replace("{page}", "" + page).replace("{pages}", "" + pages));

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


        setItem(inv, itemStackUtils.getItemStack(GUIItem.GENERIC_GO_BACK, getMessage(Message.GO_BACK)), 5, 1);

        addBorder(inv);

        return inv;
    }


}
