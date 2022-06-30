package net.bbytes.bukkit.inventory.guis;

import net.bbytes.bukkit.inventory.GUIItem;
import net.bbytes.bukkit.message.Message;
import net.bbytes.bukkit.world.ConfigurableWorldType;
import net.bbytes.bukkit.world.ImportWorldInfo;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ImportWorldInventory extends BaseInventory{

    @Override
    public Inventory getInventory(Player player, Object... options){
        setPlayer(player);
        ImportWorldInfo info = (ImportWorldInfo) options[0];


        Inventory inv = createInventory(6, getMessage(Message.NEW_WORLD_IMPORT));

        ItemStack item = null;

        item = itemStackUtils.getItemStack((info.isUpload() ? GUIItem.IMPORT_INFO_UPLOAD : GUIItem.IMPORT_INFO_IMPORT), getMessage(Message.IMPORT_ITEM_TITLE), new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                "\\" + getMessage(Message.IMPORT_INFO),
        });

        item = setNBT(item, "info.displayName", info.getDisplayname());
        item = setNBT(item, "info.worldID", info.getWorldID());
        item = setNBT(item, "info.configurableWorldType", info.getConfigurableWorldType().name());
        item = setNBT(item, "info.environment", info.getEnvironment().name());
        item = setNBT(item, "info.projectID", (info.getProject() != null ? info.getProject().getUUID().toString() : "none"));
        item = setNBT(item, "info.upload", info.isUpload());

        setItem(inv, item, 1, 5);

        setItem(inv, itemStackUtils.getItemStack(GUIItem.GENERIC_GO_BACK.getItem(), getMessage(Message.CANCEL)), 6, 1);

        setItem(inv, itemStackUtils.getItemStack(GUIItem.IMPORT_CREATE, getMessage(Message.IMPORT_CREATE), new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                "\\" + getMessage(Message.IMPORT_CREATE_INFO),
                "",
                "§2§l» §a" + getMessage(Message.CLICK_CREATE_WORLD),
        }), 6, 9);

        String[] lore = new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                "\\" + getMessage(Message.IMPORT_TYPE_INFO),
                "",
                "§2§l» §a" + getMessage(Message.CLICK_SELECT)
        };

        setItem(inv, itemStackUtils.getItemStack(GUIItem.IMPORT_TYPE_VOID, getMessage(Message.NEW_WORLD_VOID), lore), 2, 2);
        setItem(inv, itemStackUtils.getItemStack(GUIItem.IMPORT_TYPE_NORMAL, getMessage(Message.NEW_WORLD_NORMAL), lore), 2, 3);
        setItem(inv, itemStackUtils.getItemStack(GUIItem.IMPORT_TYPE_FLAT, getMessage(Message.NEW_WORLD_FLAT), lore), 2, 4);

        setItem(inv, itemStackUtils.getItemStack(
                (info.getConfigurableWorldType() == ConfigurableWorldType.VOID ? GUIItem.IMPORT_ENABLED : GUIItem.IMPORT_DISABLED),
                (info.getConfigurableWorldType() == ConfigurableWorldType.VOID ? getMessage(Message.WORD_ENABLED) : getMessage(Message.WORD_DISABLED))), 3, 2);

        setItem(inv, itemStackUtils.getItemStack(
                (info.getConfigurableWorldType() == ConfigurableWorldType.NORMAL ? GUIItem.IMPORT_ENABLED : GUIItem.IMPORT_DISABLED),
                (info.getConfigurableWorldType() == ConfigurableWorldType.NORMAL ? getMessage(Message.WORD_ENABLED) : getMessage(Message.WORD_DISABLED))), 3, 3);

        setItem(inv, itemStackUtils.getItemStack(
                (info.getConfigurableWorldType() == ConfigurableWorldType.FLAT ? GUIItem.IMPORT_ENABLED : GUIItem.IMPORT_DISABLED),
                (info.getConfigurableWorldType() == ConfigurableWorldType.FLAT ? getMessage(Message.WORD_ENABLED) : getMessage(Message.WORD_DISABLED))), 3, 4);

        lore = new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                "\\"+ getMessage(Message.IMPORT_ENVIROMENT_INFO),
                "",
                "§2§l» §a" + getMessage(Message.CLICK_SELECT)
        };

        setItem(inv, itemStackUtils.getItemStack(GUIItem.IMPORT_ENV_OVERWORLD, lore), 2, 6);
        setItem(inv, itemStackUtils.getItemStack(GUIItem.IMPORT_ENV_NETHER, lore), 2, 7);
        setItem(inv, itemStackUtils.getItemStack(GUIItem.IMPORT_ENV_THE_END, lore), 2, 8);

        setItem(inv, itemStackUtils.getItemStack(
                (info.getEnvironment() == World.Environment.NORMAL ? GUIItem.IMPORT_ENABLED : GUIItem.IMPORT_DISABLED),
                (info.getEnvironment() == World.Environment.NORMAL ? getMessage(Message.WORD_ENABLED) : getMessage(Message.WORD_DISABLED))), 3, 6);

        setItem(inv, itemStackUtils.getItemStack(
                (info.getEnvironment() == World.Environment.NETHER ? GUIItem.IMPORT_ENABLED : GUIItem.IMPORT_DISABLED),
                (info.getEnvironment() == World.Environment.NETHER ? getMessage(Message.WORD_ENABLED) : getMessage(Message.WORD_DISABLED))), 3, 7);
        setItem(inv, itemStackUtils.getItemStack(
                (info.getEnvironment() == World.Environment.THE_END ? GUIItem.IMPORT_ENABLED : GUIItem.IMPORT_DISABLED),
                (info.getEnvironment() == World.Environment.THE_END ? getMessage(Message.WORD_ENABLED) : getMessage(Message.WORD_DISABLED))), 3, 8);




        item = itemStackUtils.getItemStack((info.getProject() != null ? info.getProject().getDisplayItem() : new ItemStack(Material.BOOKSHELF)), getMessage(Message.IMPORT_SELECT_PROJECT), new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                "\\" + getMessage(Message.IMPORT_PROJECT_INFO),
                "§8§l» §7" +getMessage(Message.WORD_PROJECT) + ": §b" + (info.getProject() != null ? info.getProject().getName() : "none"),
                "",
                "§2§l» §a" + getMessage(Message.CLICK_SELECT)
        });

        item = applyGUIItem(item, GUIItem.PROJECT_PLACEHOLDER);


        setItem(inv, item, 5, 4);

        setItem(inv, itemStackUtils.getItemStack(GUIItem.WORLD_EDIT_NAME, getMessage(Message.EDIT_NAME), new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                "\\" + getMessage(Message.WORLD_EDIT_NAME_INFO),
                "",
                "§8» §7" + getMessage(Message.CURRENT_NAME) + ": §b" + info.getDisplayname(),
                "",
                "§2§l» §a" + getMessage(Message.WORLD_EDIT_NAME)
        }), 5, 6);





        addBorder(inv);
        return inv;
    }
}
