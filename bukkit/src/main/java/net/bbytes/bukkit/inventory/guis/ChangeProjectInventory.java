package net.bbytes.bukkit.inventory.guis;

import net.bbytes.bukkit.Main;
import net.bbytes.bukkit.inventory.GUIItem;
import net.bbytes.bukkit.message.Message;
import net.bbytes.bukkit.project.Project;
import net.bbytes.bukkit.world.ConfigurableWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ChangeProjectInventory extends BaseInventory {



    @Override
    public Inventory getInventory(Player p, Object... options){
        setPlayer(p);

        String worldID = (String) options[0];
        int page = (int) options[1];
        Inventory inv = createInventory(6, getMessage(Message.CHANGE_PROJECT));

        ConfigurableWorld world = ConfigurableWorld.getWorld(worldID);


        ItemStack item = itemStackUtils.getItemStack(world.getDisplayItem(), world.getDisplayname(), new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                "§8» §7" + getMessage(Message.WORD_PROJECT) + ": §b" + (world.getProject() != null ? world.getProject().getName() : "none"),
                "",
                "\\" + getMessage(Message.CHANGE_PROJECT_INFO)
        });
        item = setNBT(item, "worldID", worldID);
        item = applyGUIItem(item, GUIItem.WORLD_PLACEHOLDER);
        setItem(inv, item, 1, 5);

        setItem(inv, itemStackUtils.getItemStack(GUIItem.GENERIC_GO_BACK, getMessage(Message.GO_BACK)), 6, 1);

        if(world.getProject() != null)
            setItem(inv, itemStackUtils.getItemStack(GUIItem.WORLD_MOVE_UNASSIGN, new String[]{
                    Message.FORMAT_DIVIDER.getRaw(),
                    "\\" + getMessage(Message.CHANGE_PROJECT_UNASSIGN),
                    "",
                    "§2§l» §a" + getMessage(Message.CLICK_UNASSIGN)
            }), 1, 7);



        List<ItemStack> itemStackList = new ArrayList<>();

        for(int i = (page-1)*28; i < Main.getInstance().getProjectManager().getProjectList().size(); i++){
            Project project = Main.getInstance().getProjectManager().getProjectList().get(i);
            item = itemStackUtils.getItemStack(project.getDisplayItem(), "§6" + project.getName(), new String[]{
                    Message.FORMAT_DIVIDER.getRaw(),
                    "§8§l» §7" + Message.WORD_PROJECT_LEADER.get(p)+ ": §b" + project.getProjectManager(),
                    "§8§l» §7" + getMessage(Message.WORD_WORLDS) + ": §b" + project.getWorlds().size(),
                    "",
                    "§2§l» §a" + getMessage(Message.CLICK_CHANGE_PROJECT)
            });

            item = applyGUIItem(item, GUIItem.PROJECT_PLACEHOLDER);
            item = setNBT(item, "projectID", project.getUUID().toString());
            itemStackList.add(item);
        }

        for(int i = 0; i < 28; i++){
            if(i >= itemStackList.size())break;
            setItem(inv, itemStackList.get(i), (i/7)+2, (i%7)+2);

        }

        //  Project Paging
        int pages = ((itemStackList.size()-1)/28)+1 +(page-1);

        item = itemStackUtils.getItemStack(
                GUIItem.GENERIC_PAGES.getItem(),
                Message.PAGES.get(p).replace("{page}", "" + page).replace("{pages}", "" + pages));

        item = setNBT(item, "page", page);
        item.setAmount(Math.min(page, 64));

        setItem(inv, item, 6, 5);

        if(page < pages){
            item = itemStackUtils.getItemStack(GUIItem.GENERIC_PAGE_NEXT.getItem(), Message.PAGE_NEXT.get(p));
            item.setAmount(Math.min(page+1, 64));
            setItem(inv, item, 6, 7);
        }

        if(page > 1){
            item = itemStackUtils.getItemStack(GUIItem.GENERIC_PAGE_PREV.getItem(), Message.PAGE_PREVIOUS.get(p));
            item.setAmount(Math.min(page-1, 64));
            setItem(inv, item, 6, 3);
        }



        addBorder(inv, 1);
        addBorder(inv, 6);
        return inv;
    }


}
