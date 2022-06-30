package net.bbytes.bukkit.inventory.guis;

import de.tr7zw.changeme.nbtapi.NBTItem;
import net.bbytes.bukkit.Main;
import net.bbytes.bukkit.inventory.GUIInventory;
import net.bbytes.bukkit.inventory.GUIItem;
import net.bbytes.bukkit.inventory.ItemStackUtils;
import net.bbytes.bukkit.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BaseInventory {

    private int id = -1;
    public ItemStackUtils itemStackUtils;

    private MessageGetter messageGetter;

    public BaseInventory(){
         itemStackUtils = Main.getInstance().getItemStackUtils();
    }


    public void display(Player player){
        display(player, null, new Object[]{});
    };
    public void display(Player player, Object... options){
        Inventory inv = getInventory(player, options);
        if(inv == null)return;
        Main.getInstance().getInventoryManager().updateInventory(player, inv);
    };

    public void refresh(Player player, GUIInventory inventory, Object... options){
        setPlayer(player);
        Inventory inv = getInventory(player, options);
        if(inv == null)return;
        Main.getInstance().getInventoryManager().updateItemsInInventory(player, inv, inventory);
    }
    public Inventory getInventory(Player player, Object... options){return null;};

    public void addBorder(Inventory inv){
        for(int i = 0; i < 9; i++){
            if(inv.getItem(i) == null)
                inv.setItem(i, GUIItem.GENERIC_BORDER.getItem());
        }

        for(int i = inv.getSize()-9; i < inv.getSize(); i++){
            if(inv.getItem(i) == null)
                inv.setItem(i, GUIItem.GENERIC_BORDER.getItem());
        }
    }

    public void addBorder(Inventory inv, int row){
        for(int i = (row-1)*9; i < row*9; i++){
            if(inv.getItem(i) == null)
                inv.setItem(i, GUIItem.GENERIC_BORDER.getItem());
        }
    }

    public void fillBorder(Inventory inv){
        for(int i = 0; i < inv.getSize(); i++){
            if(inv.getItem(i) == null)
                inv.setItem(i, GUIItem.GENERIC_BORDER.getItem());
        }
    }

    public void setItem(Inventory inv, GUIItem item, int row, int column){
        setItem(inv, item.getItem(), row, column);
    }
    public void setItem(Inventory inv, ItemStack item, int row, int column){
        ItemMeta meta = item.getItemMeta();
        if(item.hasItemMeta()) if(item.getItemMeta().hasLore()){
            List<String> newLore = new ArrayList<String>();
            for(String line : item.getItemMeta().getLore()){
                if(!line.startsWith("§7\\")) newLore.add(line);
                else{
                    String line2 = line.substring(3);
                    StringBuilder builder = new StringBuilder();
                    for(String word : line2.split(" ")){
                        builder.append(word).append(" ");
                        if(builder.length() > 30){
                            newLore.add("§7" + builder.substring(0, builder.length() - 1));
                            builder = new StringBuilder();
                        }
                    }
                    if(builder.length() > 0) newLore.add("§7" + builder.substring(0, builder.length() - 1));

                }
            }

            meta.setLore(newLore);
        }

        if(item.getType().equals(Material.POTION)
                || item.getType().name().endsWith("SWORD")
                || item.getType().name().endsWith("AXE"))
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        item.setItemMeta(meta);
        inv.setItem((row-1)*9+column-1, item);

    }

    public Inventory createInventory(int rows, String title) {
        //return Bukkit.createInventory(null, rows*9, Main.getInstance().getHiddenStringUtils().encodeString(id + "") + title);
        return Bukkit.createInventory(null, rows*9, title);
    }

    public void setID(int id){
        this.id = id;
    }

    public ItemStack applyGUIItem(ItemStack item, GUIItem guiItem){
        return Main.getInstance().getInventoryManager().applyGUIItemTags(item, guiItem);
    }

    public ItemStack setNBT(ItemStack item, String key, Object value){
        NBTItem nbtItem = new NBTItem(item);
        if(value instanceof Integer) nbtItem.setInteger(key, (int)value);
        else if(value instanceof Double) nbtItem.setDouble(key, (double)value);
        else if(value instanceof Byte) nbtItem.setByte(key, (byte)value);
        else if(value instanceof Float) nbtItem.setFloat(key, (float)value);
        else if(value instanceof Long) nbtItem.setLong(key, (long)value);
        else if(value instanceof Boolean) nbtItem.setBoolean(key, (boolean)value);
        else if(value instanceof Short) nbtItem.setShort(key, (short)value);
        else if(value instanceof UUID) nbtItem.setUUID(key, (UUID)value);
        else if(value instanceof String) nbtItem.setString(key, (String)value);
        else nbtItem.setObject(key, value);

        return nbtItem.getItem();

    }

    public boolean isUUIDFormat(String uuid){
        try{
            UUID.fromString(uuid);
            return true;
        }catch(IllegalArgumentException e){
            return false;
        }
    }

    public static ItemStack addGlow(ItemStack item){
        item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        return item;
    }

    public void setPlayer(Player player){
        this.messageGetter = new MessageGetter(player);
    }

    public String getMessage(Message message){
        return this.messageGetter.get(message);
    }


    public class MessageGetter{
        private Player player;

        public MessageGetter(Player player) {
            this.player = player;
        }

        public String get(Message message){
            return message.get(player);
        }
    }


}

