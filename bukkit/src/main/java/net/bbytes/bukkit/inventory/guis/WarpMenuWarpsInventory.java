package net.bbytes.bukkit.inventory.guis;

import net.bbytes.bukkit.inventory.GUIItem;
import net.bbytes.bukkit.message.Message;
import net.bbytes.bukkit.warp.Warp;
import net.bbytes.bukkit.world.ConfigurableWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class WarpMenuWarpsInventory extends BaseInventory {


    DecimalFormat decimalFormat = new DecimalFormat("0.00");

    @Override
    public Inventory getInventory(Player p, Object... options){
        setPlayer(p);
        String worldID = (String) options[0];
        int page = (int) options[1];
        Inventory inv = createInventory(6, getMessage(Message.WARP_MENU_TITLE));

        ConfigurableWorld world = ConfigurableWorld.getWorld(worldID);

        ItemStack item = itemStackUtils.getItemStack(world.getDisplayItem(), world.getDisplayname(), new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                "§8» §7" + getMessage(Message.WORD_PROJECT) + ": §b" + world.getProject().getName(),
                "§8» §7" + getMessage(Message.WORD_WARPS) + ": §b" + world.getWarpsInWorld().size(),
                "",
                "\\" + getMessage(Message.WARP_WORLD_INFO)
        });
        item = setNBT(item, "worldID", worldID);
        item = applyGUIItem(item, GUIItem.WORLD_PLACEHOLDER);
        setItem(inv, item, 1, 5);


        setItem(inv, itemStackUtils.getItemStack(GUIItem.GENERIC_GO_BACK, getMessage(Message.GO_BACK)), 6, 1);


        List<ItemStack> itemStackList = new ArrayList<>();

        for(int i = (page-1)*28; i < world.getWarpsInWorld().size(); i++){
            Warp warp = world.getWarpsInWorld().get(i);
            item = itemStackUtils.getItemStack(warp.getDisplayItem(), "§6" + warp.getName(), new String[]{
                    Message.FORMAT_DIVIDER.getRaw(),
                    "§8» §7"+ getMessage(Message.WORD_NAME) + ": §b" + warp.getName(),
                    "§8» §7"+ getMessage(Message.WORD_LOCATION) + ": §b"
                            + decimalFormat.format(warp.getLocation().getX()) + " "
                            + decimalFormat.format(warp.getLocation().getY()) + " "
                            + decimalFormat.format(warp.getLocation().getZ()) + "",
                    "",
                    "§2§l» §a"+ getMessage(Message.CLICK_WARP),
            });

            item = applyGUIItem(item, GUIItem.WARP_PLACEHOLDER);
            item = setNBT(item, "warp", warp.getName());
            itemStackList.add(item);

        }

        if(itemStackList.size() == 0)
            return null;

        for(int i = 0; i < 28; i++){
            if(i >= itemStackList.size()) break;
            setItem(inv, itemStackList.get(i), (i/7)+2, (i%7)+2);
        }

        int pages = ((itemStackList.size()-1)/28)+1 + (page-1);

        //  Project Paging

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


        addBorder(inv);
        return inv;
    }


}
