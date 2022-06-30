package net.bbytes.bukkit.inventory.guis;

import net.bbytes.bukkit.Main;
import net.bbytes.bukkit.inventory.GUIItem;
import net.bbytes.bukkit.message.Message;
import net.bbytes.bukkit.project.Project;
import net.bbytes.bukkit.user.User;
import net.bbytes.bukkit.world.RecycledConfigurableWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecycleBinInventory extends BaseInventory {

    final String format = "EEEEE d MMM yy HH:mm";



    @Override
    public Inventory getInventory(Player p, Object... options){
        setPlayer(p);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, User.getUser(p).getLanguage().getLocale());

        int page = (int) options[0];

        Inventory inv = createInventory(6, Message.PROJECTS_RECYCLE_BIN.get(p));

        setItem(inv, itemStackUtils.getItemStack(GUIItem.PROJECTS_RECYCLE_BIN, Message.PROJECTS_RECYCLE_BIN.get(p), new String[]{
                Message.FORMAT_DIVIDER.getRaw(),
                "\\" + Message.PROJECTS_RECYCLE_BIN_INFO.get(p),
                "",
                "§2§l» §a" + Message.CLICK_TO_OPEN.get(p)
        }), 1, 5);

        List<RecycledConfigurableWorld> worldList = Main.getInstance().getWorldManager().getRecycleBin().getRecycledWorldsList();

        List<ItemStack> itemStackList = new ArrayList<>();

        for(int i = (page-1)*28; i < worldList.size(); i++){
            RecycledConfigurableWorld world = worldList.get(i);
            if(world == null) break;

            String project = "none";
            if(world.getProject() != null){
                Project project1 = world.getProject();
                project = project1.getName();

                if(!project1.canAccess(p.getUniqueId()))
                    continue;
            }


            ItemStack item = itemStackUtils.getItemStack(world.getDisplayItem(), "§6" + world.getDisplayname(), new String[]{
                    "§8" + world.getFileWorldName(),
                    "§8» §7" +getMessage(Message.DELETED) + ": §b" + simpleDateFormat.format(new Date(world.getRecycled())),
                    "§8» §7" + getMessage(Message.DELETED_BY) + ": §b" + world.getRecycledBy(),
                    "§8» §7" + getMessage(Message.WORD_PROJECT) + ": §b" + project,
                    "",
                    "§2§l» §a" + getMessage(Message.CLICK_RESTORE),
            });

            if(p.hasPermission("bbytes.admin")){
                item = itemStackUtils.getItemStack(world.getDisplayItem(), "§6" + world.getDisplayname(), new String[]{
                        "§8" + world.getFileWorldName(),
                        "§8» §7" +getMessage(Message.DELETED) + ": §b" + simpleDateFormat.format(new Date(world.getRecycled())),
                        "§8» §7" + getMessage(Message.DELETED_BY) + ": §b" + world.getRecycledBy(),
                        "§8» §7" + getMessage(Message.WORD_PROJECT) + ": §b" + project,
                        "",
                        "§2§l» §a" + getMessage(Message.CLICK_RESTORE),
                        "§4§l» §c" + getMessage(Message.CLICK_SHIFT_DELETE) + " §8[§c" + getMessage(Message.INFO_ADMIN_ONLY) + "§8]"
                });
            }


            item = applyGUIItem(item, GUIItem.WORLD_PLACEHOLDER);
            item = setNBT(item, "recycledWorldID", world.getFileWorldName());
            itemStackList.add(item);
        }


        if(itemStackList.size() == 0)
            setItem(inv, itemStackUtils.getItemStack(GUIItem.GENERIC_EMPTY, getMessage(Message.EMPTY), new String[]{
                    Message.FORMAT_DIVIDER.getRaw(),
                    getMessage(Message.EMPTY_WORLDS)
            }), 3, 5);

       else
           for(int i = 0; i < 28; i++){
            if(i >= itemStackList.size())break;
            setItem(inv, itemStackList.get(i), (i/7)+2, (i%7)+2);

        }


        int pages = ((itemStackList.size()-1)/28)+1 + (page-1);

        //  Worlds Paging

        ItemStack item = itemStackUtils.getItemStack(
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


        setItem(inv, itemStackUtils.getItemStack(GUIItem.GENERIC_GO_BACK, getMessage(Message.GO_BACK)), 6, 1);

        addBorder(inv);

        return inv;
    }


}
