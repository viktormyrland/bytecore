package net.bbytes.bukkit.inventory.guis;

import net.bbytes.bukkit.Main;
import net.bbytes.bukkit.inventory.GUIItem;
import net.bbytes.bukkit.message.Message;
import net.bbytes.bukkit.project.Project;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ProjectEditInventory extends BaseInventory {



    @Override
    public Inventory getInventory(Player player, Object... options){
        setPlayer(player);
        String projectID = (String) options[0];
        boolean confirmDelete = false;
        if(options.length > 1) confirmDelete = true;
        Project project = Main.getInstance().getProjectManager().getProject(projectID);

        Inventory inv = createInventory(3, getMessage(Message.EDIT_PROJECT));

        ItemStack item = itemStackUtils.getItemStack(
                project.getDisplayItem(),
                "§6" + project.getName(),
                new String[]{
                        "§8" + project.getShortID(),
                        "§8§l» §3" + getMessage(Message.WORD_PROJECT_LEADER) + ": §b" + project.getProjectManager(),
                        "§8§l» §3" + getMessage(Message.WORD_WORLDS) + ": §b" + project.getWorlds().size(),
                        "",
                        "§2§l» §a" + getMessage(Message.CLICK_EDIT_ICON)
                });

        item = setNBT(item, "projectID", projectID);
        item = applyGUIItem(item, GUIItem.PROJECT_PLACEHOLDER);

        setItem(inv, item, 1, 5);

        setItem(inv, itemStackUtils.getItemStack(GUIItem.PROJECT_EDIT_NAME, getMessage(Message.EDIT_NAME), new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                "\\" + getMessage(Message.EDIT_PROJECT_DISPLAYNAME_INFO),
                "",
                "§8» §7" + getMessage(Message.CURRENT_NAME) + ": §b" + project.getName(),
                "",
                "§2§l» §a" + getMessage(Message.EDIT_PROJECT_DISPLAYNAME)
        }), 2, 2);

        setItem(inv, itemStackUtils.getItemStack(GUIItem.PROJECT_EDIT_LEAD, getMessage(Message.EDIT_PROJECT_LEAD), new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                "\\" + getMessage(Message.EDIT_PROJECT_LEAD),
                "",
                "§8» §7" + getMessage(Message.CURRENT_LEADER) + ": §b" + project.getProjectManager(),
                "",
                "§2§l» §a" + getMessage(Message.EDIT_PROJECT_LEAD)
        }), 2, 3);

        setItem(inv, itemStackUtils.getItemStack(GUIItem.PROJECT_EDIT_ACCESS, getMessage(Message.EDIT_PROJECT_ACCESS), new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                "\\" + getMessage(Message.EDIT_PROJECT_ACCESS_INFO),
                "",
                "§2§l» §a" + getMessage(Message.EDIT_PROJECT_ACCESS)
        }), 2, 4);


        if(!confirmDelete)
            setItem(inv, itemStackUtils.getItemStack(GUIItem.PROJECT_DELETE, getMessage(Message.EDIT_PROJECT_DELETE), new String[]{
                    Message.FORMAT_DIVIDER.getRaw(),
                    "\\" + getMessage(Message.EDIT_PROJECT_DELETE_INFO),
                    "",
                    "§4§l» §c" + getMessage(Message.EDIT_PROJECT_DELETE)
            }), 2, 8);
        else
            setItem(inv, itemStackUtils.getItemStack(GUIItem.PROJECT_CONFIRM_DELETE, getMessage(Message.EDIT_PROJECT_DELETE_CONFIRM), new String[]{
                    Message.FORMAT_DIVIDER.getRaw(),
                    "\\" + getMessage(Message.EDIT_PROJECT_DELETE_INFO),
                    "",
                    "§4§l» §c§n" + getMessage(Message.EDIT_PROJECT_DELETE_CONFIRM_INFO),
            }), 2, 8);


        setItem(inv, itemStackUtils.getItemStack(GUIItem.GENERIC_GO_BACK, getMessage(Message.GO_BACK)), 3, 1);

        addBorder(inv);
        return inv;
    }


}
