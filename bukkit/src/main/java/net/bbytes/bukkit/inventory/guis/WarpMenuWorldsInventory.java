package net.bbytes.bukkit.inventory.guis;

import net.bbytes.bukkit.inventory.GUIItem;
import net.bbytes.bukkit.message.Message;
import net.bbytes.bukkit.project.Project;
import net.bbytes.bukkit.world.ConfigurableWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class WarpMenuWorldsInventory extends BaseInventory {



    @Override
    public Inventory getInventory(Player p, Object... options){
        setPlayer(p);
        String projectID = (String) options[0];
        int page = (int) options[1];

        Project project = Project.getProject(projectID);
        Inventory inv = createInventory(5, getMessage(Message.WARP_MENU_TITLE));

        ItemStack item = itemStackUtils.getItemStack(project.getDisplayItem(), project.getName(), new String[]{
                        "§8" + project.getShortID(),
                        "§8» §7"+ getMessage(Message.WORD_PROJECT) + ": §b" + project.getName(),
                        "§8» §7"+ getMessage(Message.WORD_WARPS) + ": §b" + project.getWarpsInProject().size(),
                        "",
                        "\\" + getMessage(Message.WARP_PROJECT_INFO)
                });
        item = setNBT(item, "projectID", projectID);

        item = applyGUIItem(item, GUIItem.PROJECT_PLACEHOLDER);
        setItem(inv, item, 1, 5);


        setItem(inv, itemStackUtils.getItemStack(GUIItem.GENERIC_GO_BACK, getMessage(Message.GO_BACK)), 5, 1);


        List<ItemStack> itemStackList = new ArrayList<>();

        for(int i = (page-1)*21; i < project.getWorlds().size(); i++){
            ConfigurableWorld world = project.getWorlds().get(i);
            if(world.getWarpsInWorld().size() == 0)continue;
            item = itemStackUtils.getItemStack(world.getDisplayItem(), world.getDisplayname(), new String[]{
                    Message.FORMAT_DIVIDER.getRaw(),
                    "§8§l» §7"+ getMessage(Message.WORD_WARPS) + ": §b" + world.getWarpsInWorld().size(),
                    "",
                    "§2§l» §a"+ getMessage(Message.CLICK_BROWSE_WARPS)
            });

            item = applyGUIItem(item, GUIItem.WORLD_PLACEHOLDER);
            item = setNBT(item, "worldID", world.getFileWorldName());
            itemStackList.add(item);
        }

        if(itemStackList.size() == 0)
            setItem(inv, itemStackUtils.getItemStack(GUIItem.GENERIC_EMPTY, getMessage(Message.EMPTY), new String[]{
                    Message.FORMAT_DIVIDER.getRaw(),
                    getMessage(Message.EMPTY_WARPS)
            }), 3, 5);
        else
            for(int i = 0; i < 21; i++){
                if(i >= itemStackList.size())break;
                setItem(inv, itemStackList.get(i), (i/7)+2, (i%7)+2);

            }

        int pages = ((itemStackList.size()-1)/21)+1 + (page-1);

        //  Project Paging

        item = itemStackUtils.getItemStack(
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


        addBorder(inv, 1);
        addBorder(inv, 5);
        return inv;
    }


}
