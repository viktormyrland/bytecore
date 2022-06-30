package net.bbytes.bukkit.inventory.guis;

import net.bbytes.bukkit.Main;
import net.bbytes.bukkit.inventory.GUIItem;
import net.bbytes.bukkit.message.Message;
import net.bbytes.bukkit.world.ConfigurableWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class UncategorizedWorldsInventory extends BaseInventory {


    DecimalFormat decimalFormat = new DecimalFormat("0.00");

    @Override
    public Inventory getInventory(Player p, Object... options){
        setPlayer(p);
        int page = (int) options[0];
        Inventory inv = createInventory(6, getMessage(Message.PROJECTS_UNCATEGORIZED_WORLDS));

        List<ConfigurableWorld> uncategorizedWorlds = Main.getInstance().getWorldManager().getUncategorizedWorlds();

        setItem(inv, itemStackUtils.getItemStack(GUIItem.PROJECTS_UNCATEGORIZED_WORLDS, getMessage(Message.PROJECTS_UNCATEGORIZED_WORLDS), new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                "§8§l» §7" + getMessage(Message.WORD_WORLDS) + ": §b" + uncategorizedWorlds.size(),
                "",
                "\\" + getMessage(Message.PROJECTS_UNCATEGORIZED_WORLDS_INFO),
        }), 1, 5);


        setItem(inv, itemStackUtils.getItemStack(GUIItem.GENERIC_GO_BACK, getMessage(Message.GO_BACK)), 6, 1);

        setItem(inv, itemStackUtils.getItemStack(GUIItem.PROJECT_NEW_WORLD, getMessage(Message.NEW_WORLD), new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                "\\" + getMessage(Message.NEW_WORLD_INFO),
                "",
                "§2§l» §a" + getMessage(Message.NEW_WORLD)
        }), 1, 7);

        List<ItemStack> itemStackList = new ArrayList<>();

        for(int i = (page-1)*28; i < uncategorizedWorlds.size(); i++){
            ConfigurableWorld world = uncategorizedWorlds.get(i);

            ItemStack item = itemStackUtils.getItemStack(world.getDisplayItem(), "§6" + world.getDisplayname(), new String[]{
                    Message.FORMAT_DIVIDER.getRaw(),
                    "§2§l» §a" + getMessage(Message.CLICK_TRAVEL),
                    "§2§l» §a" + getMessage(Message.CLICK_SHIFT_EDIT)
            });

            item = applyGUIItem(item, GUIItem.WORLD_PLACEHOLDER);
            item = setNBT(item, "worldID", world.getFileWorldName());
            itemStackList.add(item);
        }

        if(itemStackList.size() == 0){
            setItem(inv, itemStackUtils.getItemStack(GUIItem.GENERIC_EMPTY, getMessage(Message.EMPTY), new String[]{
                    Message.FORMAT_DIVIDER.getRaw(),
                    getMessage(Message.EMPTY_WORLDS)
            }), 3, 5);
        }else
            for(int i = 0; i < 28; i++){
                if(i >= itemStackList.size()) break;
                setItem(inv, itemStackList.get(i), (i/7)+2, (i%7)+2);
            }

        int pages = ((itemStackList.size()-1)/28)+1 + (page-1);

        //  Project Paging

        ItemStack item = itemStackUtils.getItemStack(
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


        addBorder(inv);
        return inv;
    }


}
