package net.bbytes.bukkit.inventory.guis;

import net.bbytes.bukkit.Main;
import net.bbytes.bukkit.inventory.GUIItem;
import net.bbytes.bukkit.message.Message;
import net.bbytes.bukkit.project.Project;
import net.bbytes.bukkit.user.User;
import net.bbytes.bukkit.warp.Warp;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class WarpMenuProjectsInventory extends BaseInventory {


    DecimalFormat decimalFormat = new DecimalFormat("0.00");

    @Override
    public Inventory getInventory(Player p, Object... options){
        setPlayer(p);
        int page = (int) options[0];
        Inventory inv = createInventory(6, getMessage(Message.WARP_MENU_TITLE));


        setItem(inv, itemStackUtils.getItemStack(GUIItem.WARP_INFO, getMessage(Message.WARP_MENU_TITLE), new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                "",
                "\\" + getMessage(Message.WARP_MENU_INFO)
        }), 1, 5);



        List<ItemStack> itemStackList = new ArrayList<>();

        for(int i = (page-1)*21; i < Main.getInstance().getProjectManager().getProjectList().size(); i++){
            Project project = Main.getInstance().getProjectManager().getProjectList().get(i);
            if(!project.canAccess(p.getUniqueId()))continue;
            if(project.getWarpsInProject().size() == 0)continue;
            ItemStack item = itemStackUtils.getItemStack(project.getDisplayItem(), "§6" + project.getName(), new String[]{
                    Message.FORMAT_DIVIDER.getRaw(),
                    "§8§l» §7" + Message.WORD_PROJECT_LEADER.get(p)+ ": §b" + project.getProjectManager(),
                    "§8§l» §7" + getMessage(Message.WORD_WARPS) + ": §b" + project.getWarpsInProject().size(),
                    "",
                    "§2§l» §a" + getMessage(Message.CLICK_BROWSE_WARPS)
            });

            item = applyGUIItem(item, GUIItem.PROJECT_PLACEHOLDER);
            item = setNBT(item, "projectID", project.getUUID().toString());
            itemStackList.add(item);
        }

        List<Warp> uncategorizedWarps = Main.getInstance().getWarpManager().getUncategorizedWarps();
        if(uncategorizedWarps.size() > 0){
            setItem(inv, itemStackUtils.getItemStack(GUIItem.WARP_UNCATEGORIZED, getMessage(Message.WARP_UNCATEGORIZED), new String[]{
                    Message.FORMAT_DIVIDER.getRaw(),
                    "§8§l» §7" + getMessage(Message.WORD_WARPS) + ": §b" + uncategorizedWarps.size(),
                    "",
                    "\\" + getMessage(Message.WARP_UNCATEGORIZED_INFO),
                    "",
                    "§2§l» §a" + getMessage(Message.CLICK_BROWSE_WARPS)
            }), 1, 8);
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

        //  Project Paging
        int pages = ((itemStackList.size()-1)/21)+1 +(page-1);

        ItemStack item = itemStackUtils.getItemStack(
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

        /*
         Recent Warps
         */
        User user = Main.getInstance().getUserManager().getUser(p);


        itemStackList = new ArrayList<>();

        for(Warp warp : user.getRecentWarps()){
            if(warp.getConfigurableWorld() != null) if(warp.getConfigurableWorld().getProject() != null) if(!warp.getConfigurableWorld().getProject().canAccess(p.getUniqueId()))continue;

            String project = null;
            if(warp.getConfigurableWorld() != null) if(warp.getConfigurableWorld().getProject() != null){
                project = warp.getConfigurableWorld().getProject().getName();
            }

            item = itemStackUtils.getItemStack(warp.getDisplayItem(), "§6" + warp.getName(), new String[]{
                    Message.FORMAT_DIVIDER.getRaw(),
                    "§8» §7" + getMessage(Message.WORD_PROJECT) + ": §b" + (project != null ? project : "none"),
                    "§8» §7" + getMessage(Message.WORD_WORLD) +": §b" + (warp.getConfigurableWorld() != null ? warp.getConfigurableWorld().getDisplayname() : warp.getWorld()),
                    "§8» §7" + getMessage(Message.WORD_LOCATION) +": §b" +
                            decimalFormat.format(warp.getLocation().getX()) + " " +
                            decimalFormat.format(warp.getLocation().getY()) + " " +
                            decimalFormat.format(warp.getLocation().getZ()) + "",
                    "",
                    "§2§l» §a" + getMessage(Message.CLICK_WARP),
            });

            item = applyGUIItem(item, GUIItem.WARP_PLACEHOLDER);
            item = setNBT(item, "warp", warp.getName());
            itemStackList.add(item);

            if(itemStackList.size() >= 7)break;
        }

        if(itemStackList.size() == 0)
            setItem(inv, itemStackUtils.getItemStack(GUIItem.GENERIC_EMPTY, "§c"+Message.EMPTY.get(p), new String[]{
                    Message.FORMAT_DIVIDER.getRaw(),
                    "" + Message.EMPTY_NO_RECENT_WARPS.get(p)
            }), 6, 5);
        else
            for(int i = 0; i < 7; i++){
                if(i >= itemStackList.size()) break;
                setItem(inv, itemStackList.get(i), 6, i+2);
            }

        addBorder(inv, 1);
        addBorder(inv, 5);
        return inv;
    }


}
