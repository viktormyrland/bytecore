package net.bbytes.bukkit.inventory;

import net.bbytes.bukkit.Main;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ItemStackUtils {

	public ItemStack getItemStack(Material mat, String displayname) {
		return getItemStack(new ItemStack(mat), displayname, new ArrayList<>());
	}

	public ItemStack getItemStack(ItemStack mat, String displayname) {
		return getItemStack(mat, displayname, new ArrayList<>());
	}

	public ItemStack getItemStack(Material mat, int subid, String displayname) {
		return getItemStack(new ItemStack(mat, 1, (short)subid), displayname, new ArrayList<>());
	}

	public ItemStack getItemStack(Material mat, int subid) {
		return getItemStack(new ItemStack(mat, 1, (short)subid), null, new ArrayList<>());
	}

	public ItemStack getItemStack(Material mat, int subid, String displayname, List<String> lore) {
		return getItemStack(new ItemStack(mat, 1, (short)subid), displayname, lore);
	}
	public ItemStack getItemStack(Material mat, String displayname, List<String> lore) {
		return getItemStack(new ItemStack(mat), displayname, lore);
	}

	public ItemStack getItemStack(Material mat, String displayname, String lore) {
		return getItemStack(new ItemStack(mat), displayname, Collections.singletonList(lore));
	}

	public ItemStack getItemStack(Material mat, MaterialColor color) {
		return getItemStack(new ItemStack(mat, 1, (mat == Material.INK_SAC ? color.getDyeID() : color.getBlockID())), null, new ArrayList<>());
	}

	public ItemStack getItemStack(Material mat, MaterialColor color, String displayname) {
		return getItemStack(new ItemStack(mat, 1, color.getBlockID()), displayname, new ArrayList<>());
	}

	public ItemStack getItemStack(Material mat, MaterialColor color, String displayname, String lore) {
		return getItemStack(new ItemStack(mat, 1, color.getBlockID()), displayname, Collections.singletonList(lore));
	}

	public ItemStack getItemStack(Material mat, MaterialColor color, String displayname, List<String> lore) {
		return getItemStack(new ItemStack(mat, 1, color.getBlockID()), displayname, lore);
	}


	public ItemStack getItemStack(Material mat, int subid, String displayname, String lore) {
		return getItemStack(new ItemStack(mat, 1, (short) subid), displayname, Collections.singletonList(lore));
	}

	public ItemStack getItemStack(ItemStack item, String[] lore) {
		return getItemStack(item, item.getItemMeta().getDisplayName(), Arrays.asList(lore));
	}

	public ItemStack getItemStack(GUIItem item, String[] lore) {
		return getItemStack(item.getItem(), lore);
	}
	public ItemStack getItemStack(GUIItem item, String displayname) {
		return getItemStack(item.getItem(), displayname, new String[]{});
	}

	public ItemStack getItemStack(GUIItem item, String displayname, String[] lore) {
		return getItemStack(item.getItem(), displayname, lore);
	}

	public ItemStack getSkull(String displayname, String owner) {
		return getSkull(displayname, owner, new ArrayList<>());
	}

	public ItemStack getSkull(String displayname, String owner, String lore) {
		return getSkull(displayname, owner, Collections.singletonList(lore));
	}

	public ItemStack getItemStack(ItemStack item, String displayname, String[] lore) {
		return getItemStack(item, displayname, Arrays.asList(lore));
	}

	public void changeColor(ItemStack item, MaterialColor color){
		changeColor(item, color.getBlockID());
	}
	public void changeColor(ItemStack item, int i){

		item.setDurability((short)i);
	}

	public ItemStack getSkull(String displayname, String owner, List<String> lore) {

		ItemStack it = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
		SkullMeta meta = (SkullMeta) it.getItemMeta();
		meta.setDisplayName(displayname);
		meta.setOwner(owner);
		meta.setLore(lore);
		it.setItemMeta(meta);

		return it;

	}

	public ItemStack getItemStack(ItemStack item, String displayname, List<String> lore) {
		ItemStack it = item.clone();

		ItemMeta meta = it.getItemMeta();
		if(displayname != null){
			meta.setDisplayName((displayname.startsWith("§") ? displayname : "§6" + displayname));
		}

		for(int i = 0; i < lore.size(); i++)
			if(!lore.get(i).replaceAll(" ", "").startsWith("§") && lore.get(i).length() > 0)
				lore.set(i, "§7" + lore.get(i));



		meta.setLore(lore);
		it.setItemMeta(meta);

		return it;
	}

	public String serializeItemStack(ItemStack item){
		return item.getType().name() + "|" + item.getAmount() + "|" + item.getDurability() + "|";
	}

	public ItemStack deserializeItemStack(String str, boolean legacy){
//		ItemStack item = new ItemStack(Main.getInstance().getWrapper().getMaterial(str.split("\\|")[0], legacy));
		Material mat = Material.getMaterial(str.split("\\|")[0]);
		if(mat == null) mat = Material.getMaterial(str.split("\\|")[0], true);
		if(mat == null) mat = Material.OAK_LOG;

		ItemStack item = new ItemStack(mat);
		item.setAmount(Integer.parseInt(str.split("\\|")[1]));
		return item;
	}

	public ItemStack deserializeItemStack(String str){
		return deserializeItemStack(str, false);
	}
	
	
}
