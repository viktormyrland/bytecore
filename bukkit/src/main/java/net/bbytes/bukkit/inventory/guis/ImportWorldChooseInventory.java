package net.bbytes.bukkit.inventory.guis;

import net.bbytes.bukkit.Main;
import net.bbytes.bukkit.inventory.GUIItem;
import net.bbytes.bukkit.message.Message;
import net.bbytes.bukkit.world.ConfigurableWorld;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ImportWorldChooseInventory extends BaseInventory {



    @Override
    public Inventory getInventory(Player player, Object... options){
        setPlayer(player);

        String projectID = (String) options[0];
        int page  = (int) options[1];

        Inventory inv = createInventory(6, getMessage(Message.NEW_WORLD_IMPORT));

        ItemStack item = itemStackUtils.getItemStack(GUIItem.NEW_WORLD_IMPORT, getMessage(Message.NEW_WORLD_IMPORT), new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                "\\" + getMessage(Message.NEW_WORLD_IMPORT_INFO),
        });

        item = setNBT(item, "projectID", projectID);
        item = applyGUIItem(item, GUIItem.IMPORT_INFO_IMPORT);

        setItem(inv, item, 1, 5);

        List<ItemStack> itemStackList = new ArrayList<>();

        List<String> worlds = new ArrayList<>();

        for(File worldFile : Objects.requireNonNull(Bukkit.getWorldContainer().listFiles())){
            worlds.add(worldFile.getName());
        }



        Collections.sort(worlds);


        for(int i = (page-1)*28; i < worlds.size(); i++){
            File world = new File(Bukkit.getWorldContainer(), worlds.get(i));
            if(Bukkit.getWorld(world.getName()) != null) continue;
            if(ConfigurableWorld.getWorld(world.getName()) != null) continue;
            if(Main.getInstance().getWorldManager().getRecycleBin().getWorld(world.getName()) != null) continue;
            item = itemStackUtils.getItemStack(GUIItem.IMPORT_SELECT_PLACEHOLDER.getItem(), world.getName(), new String[]{
                    Message.FORMAT_DIVIDER.getRaw(),
                    "§8§l» §a" + getMessage(Message.WORLD_NAME) + ": §b" + world.getName(),
                    "",
                    "§2§l» §a" + getMessage(Message.CLICK_IMPORT)
            });
            item = setNBT(item, "worldID", world.getName());
            itemStackList.add(item);
        }

        if(itemStackList.size() == 0)
            setItem(inv, itemStackUtils.getItemStack(GUIItem.GENERIC_EMPTY, getMessage(Message.EMPTY), new String[]{
                    Message.FORMAT_DIVIDER.getRaw(),
                    "\\" + getMessage(Message.EMPTY_WORLDS)
            }), 3, 5);
        else
            for(int i = 0; i < 28; i++){
                if(i >= itemStackList.size()) break;
                setItem(inv, itemStackList.get(i), (i/7)+2, (i%7)+2);
            }


        setItem(inv, itemStackUtils.getItemStack(GUIItem.GENERIC_GO_BACK, getMessage(Message.GO_BACK)), 6, 1);

        //  Project Paging

        int pages = ((itemStackList.size()-1)/28)+1 + (page-1);

        item = itemStackUtils.getItemStack(
                GUIItem.GENERIC_PAGES.getItem(),
                Message.PAGES.get(player).replace("{page}", "" + page).replace("{pages}", "" + pages));

        item = setNBT(item, "page", page);
        item.setAmount(Math.min(page, 64));

        setItem(inv, item, 6, 5);

        if(page < pages){
            item = itemStackUtils.getItemStack(GUIItem.GENERIC_PAGE_NEXT.getItem(), Message.PAGE_NEXT.get(player));
            item.setAmount(Math.min(page+1, 64));
            setItem(inv, item, 6, 7);
        }

        if(page > 1){
            item = itemStackUtils.getItemStack(GUIItem.GENERIC_PAGE_PREV.getItem(), Message.PAGE_PREVIOUS.get(player));
            item.setAmount(Math.min(page-1, 64));
            setItem(inv, item, 6, 3);
        }

        addBorder(inv);
        return inv;
    }


}
