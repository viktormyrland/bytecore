package net.bbytes.bukkit.inventory.guis;

import net.bbytes.bukkit.inventory.GUIItem;
import net.bbytes.bukkit.message.Message;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class NewWorldInventory extends BaseInventory {



    @Override
    public Inventory getInventory(Player player, Object... options){
        setPlayer(player);
        String projectID = getMessage(Message.WORD_NONE);
        if(options.length > 0) projectID = (String) options[0];

        Inventory inv = createInventory(5, getMessage(Message.NEW_WORLD));

        ItemStack item = itemStackUtils.getItemStack(GUIItem.PROJECT_NEW_WORLD.getItem(), getMessage(Message.NEW_WORLD));

        item = setNBT(item, "projectID", projectID);

        setItem(inv, item, 1, 5);


        setItem(inv, itemStackUtils.getItemStack(GUIItem.NEW_WORLD_VOID, getMessage(Message.NEW_WORLD_VOID), new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                "\\" + getMessage(Message.NEW_WORLD_VOID_INFO),
                "",
                "§2§l» §a" + getMessage(Message.NEW_WORLD_CREATE)
        }), 3, 2);

        setItem(inv, itemStackUtils.getItemStack(GUIItem.NEW_WORLD_FLAT, getMessage(Message.NEW_WORLD_FLAT), new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                "\\" + getMessage(Message.NEW_WORLD_FLAT_INFO),
                "",
                "§2§l» §a" + getMessage(Message.NEW_WORLD_CREATE)
        }), 3, 4);

        setItem(inv, itemStackUtils.getItemStack(GUIItem.NEW_WORLD_NORMAL, getMessage(Message.NEW_WORLD_NORMAL), new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                "\\" + getMessage(Message.NEW_WORLD_NORMAL_INFO),
                "",
                "§2§l» §a" + getMessage(Message.NEW_WORLD_CREATE)
        }), 3, 6);

        setItem(inv, itemStackUtils.getItemStack(GUIItem.NEW_WORLD_UPLOAD, getMessage(Message.NEW_WORLD_UPLOAD), new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                "\\" + getMessage(Message.NEW_WORLD_UPLOAD_INFO),
                "",
                "§2§l» §a" + getMessage(Message.NEW_WORLD_IMPORT_CREATE)
        }), 3, 8);

        if(player.hasPermission("bbytes.admin"))
            setItem(inv, itemStackUtils.getItemStack(GUIItem.NEW_WORLD_IMPORT, getMessage(Message.NEW_WORLD_IMPORT), new String[]{
                    Message.FORMAT_DIVIDER.getRaw(),
                    "§8[§c" + getMessage(Message.INFO_ADMIN_ONLY) + "§8]",
                    "\\" + getMessage(Message.NEW_WORLD_IMPORT_INFO),
                    "",
                    "§2§l» §a" + getMessage(Message.NEW_WORLD_UPLOAD_CREATE)
            }), 5, 5);

        setItem(inv, itemStackUtils.getItemStack(GUIItem.GENERIC_GO_BACK, getMessage(Message.GO_BACK)), 5, 1);

        addBorder(inv);

        return inv;
    }


}
