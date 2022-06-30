package net.bbytes.bukkit.inventory.guis;

import net.bbytes.bukkit.Main;
import net.bbytes.bukkit.inventory.GUIItem;
import net.bbytes.bukkit.message.Message;
import net.bbytes.bukkit.world.ConfigurableWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TransferWorldInventory extends BaseInventory {



    @Override
    public Inventory getInventory(Player player, Object... options){
        setPlayer(player);
        String worldID = (String) options[0];
        ConfigurableWorld configurableWorld = ConfigurableWorld.getWorld(worldID);

        Inventory inv = createInventory(5, getMessage(Message.WORLD_EDIT_TRANSFER));

        ItemStack item = itemStackUtils.getItemStack(configurableWorld.getDisplayItem(), configurableWorld.getDisplayname(), new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                "§8§l» §7" + getMessage(Message.WORD_WORLD) + ": §b" + configurableWorld.getDisplayname(),
                "§8§l» §7" + getMessage(Message.WORD_PROJECT) + ": §b" + (configurableWorld.getProject() != null ? configurableWorld.getProject().getName() : "none"),
                "",
                "\\" + getMessage(Message.TRANSFER_INFO)
        });

        item = applyGUIItem(item, GUIItem.WORLD_PLACEHOLDER);
        item = setNBT(item, "worldID", worldID);

        setItem(inv, item, 1, 5);

        if(Main.getInstance().getSubversion() == 17){
            setItem(inv, itemStackUtils.getItemStack(GUIItem.WORLD_TRANSFER_BUILD18, getMessage(Message.TRANSFER_15), new String[]{
                    Message.FORMAT_DIVIDER.getRaw(),
                    "\\" + getMessage(Message.TRANSFER_18_INFO),
                    "",
                    "§2§l» §a" + getMessage(Message.TRANSFER_18)
            }), 3, 4);

//            setItem(inv, itemStackUtils.getItemStack(GUIItem.WORLD_TRANSFER_BUILD16, getMessage(Message.TRANSFER_16), new String[]{
//                    Message.FORMAT_DIVIDER.getRaw(),
//                    "\\" + getMessage(Message.TRANSFER_16_INFO),
//                    "",
//                    "§2§l» §a" + getMessage(Message.TRANSFER_16)
//            }), 3, 6);
        }
//        else{
//            setItem(inv, itemStackUtils.getItemStack(GUIItem.WORLD_TRANSFER_BUILD16, getMessage(Message.TRANSFER_16), new String[]{
//                    Message.FORMAT_DIVIDER.getRaw(),
//                    "\\" + getMessage(Message.TRANSFER_16_INFO),
//                    "",
//                    "§2§l» §a" + getMessage(Message.TRANSFER_16)
//            }), 3, 5);
//        }


        setItem(inv, itemStackUtils.getItemStack(GUIItem.GENERIC_GO_BACK, getMessage(Message.GO_BACK)), 5, 1);

        addBorder(inv);

        return inv;
    }


}
