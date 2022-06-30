package net.bbytes.bukkit.inventory.guis;

import net.bbytes.bukkit.Main;
import net.bbytes.bukkit.inventory.GUIItem;
import net.bbytes.bukkit.message.Message;
import net.bbytes.bukkit.project.Project;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class AccessEditInventory extends BaseInventory {



    @Override
    public Inventory getInventory(Player p, Object... options){
        setPlayer(p);

        String projectID = (String) options[0];
        int page = (int) options[1];

        Project project = Project.getProject(projectID);

        Inventory inv = createInventory(6, Message.EDIT_PROJECT_ACCESS.get(p));

        ItemStack item = itemStackUtils.getItemStack(
                GUIItem.PROJECT_EDIT_ACCESS,
                new String[]{
                        Message.FORMAT_DIVIDER.getRaw(),
                        "\\" + getMessage(Message.EDIT_PROJECT_ACCESS_INFO),
                        "",
                        "§8§l» §3" + getMessage(Message.WORD_PROJECT) + ": §b" + project.getName(),
                        "§8§l» §3" + getMessage(Message.WORD_WORLDS) + ": §b" + project.getWorlds().size(),
                });
        item = setNBT(item, "projectID", projectID);
        setItem(inv, item, 1, 5);

        setItem(inv, itemStackUtils.getItemStack(GUIItem.GENERIC_GO_BACK, getMessage(Message.GO_BACK)), 6, 1);

        setItem(inv, itemStackUtils.getItemStack(GUIItem.ACCESS_NEW, getMessage(Message.ACCESS_NEW), new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                "\\" + getMessage(Message.ACCESS_NEW_INFO),
                "",
                "§2§l» §a" + getMessage(Message.CLICK_ADD),
        }), 1, 7);

        int pages = ((project.getMemberList().size()-1)/28) + 1;
        if(page == -1) page = pages;

        if(project.getMemberList().size() == 0)
            setItem(inv, itemStackUtils.getItemStack(GUIItem.GENERIC_EMPTY, getMessage(Message.EMPTY), new String[]{
                    Message.FORMAT_DIVIDER.getRaw(),
                    getMessage(Message.EMPTY_ACCESS)
            }), 3, 5);

        for(int i = 0; i < 28; i++){
            if(i+((page-1)*28) >= project.getMemberList().size()) break;
            UUID uuid = project.getMemberList().get(((page-1)*28) + i);
            if(uuid == null) break;

            item = itemStackUtils.getItemStack(new ItemStack(Material.PAPER), "§6" + Main.getInstance().getUTNUtils().getCachedUsername(uuid), new String[]{
                    Message.FORMAT_DIVIDER.getRaw(),
                    "§4§l» §c" + getMessage(Message.CLICK_REMOVE),
            });

            item = applyGUIItem(item, GUIItem.ACCESS_PLACEHOLDER);
            item = setNBT(item, "uuid", uuid.toString());

            setItem(inv, item, (i/7)+2, (i%7)+2);
        }

        //  Paging

        item = itemStackUtils.getItemStack(
                GUIItem.GENERIC_PAGES.getItem(),
                getMessage(Message.PAGES).replace("{page}", "" + page).replace("{pages}", "" + pages));

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
