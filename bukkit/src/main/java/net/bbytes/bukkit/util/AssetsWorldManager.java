package net.bbytes.bukkit.util;

import net.bbytes.bukkit.Main;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class AssetsWorldManager implements Listener{
	
	public final String WORLDNAME = "Assets";
	private HashMap<UUID, PlayerState> playersInAssetsWorld = new HashMap<UUID, PlayerState>();
	
	public void giveHotbarInventory(Player p) {
		p.getInventory().clear();
		
		ItemStack item = Main.getInstance().getItemStackUtils().getItemStack(Material.COMPASS, "§aAssets World Warp");
		p.getInventory().setItem(7, item);
		
		item = Main.getInstance().getItemStackUtils().getItemStack(Material.RED_DYE, "§cLeave");
		p.getInventory().setItem(8, item);
		
		
		
	}
	
	public Inventory getCompassGUI() {
		return getCompassGUI(1);
	}
	
	@SuppressWarnings("deprecation")
	public Inventory getCompassGUI(int page) {
		Inventory inv = Bukkit.createInventory(null, 54, "§2Assets GUI");
//
//		Essentials es = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
//		String world = WORLDNAME;
//
//		List<String> warps = new ArrayList<String>();
//
//		/*
//		for(String warp : es.getWarps().getList()) {
//			try {
//				if(es.getWarps().getWarp(warp).getWorld().getName().equalsIgnoreCase(world))
//					warps.add(warp);
//			}catch(Exception ex) {	}
//		}
//		*/
//		for(String warp : es.getWarps().getList()) {
//			if(Main.getInstance().worldLoaderUtils.getWorldOfUnloadedWarp(warp).equalsIgnoreCase(world))
//				warps.add(warp);
//		}
//
//
//		Collections.sort(warps, String.CASE_INSENSITIVE_ORDER);
//
//
//
//		ItemStack paneBorder = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)15);
//
//		for(int i = 0; i < 9; i++) {
//			inv.setItem(i, paneBorder);
//		}
//		for(int i = 45; i < 54; i++) {
//			inv.setItem(i, paneBorder);
//		}
//
//		if(warps.size() == 0) {
//			ItemStack item = new ItemStack(Material.BEDROCK);
//			ItemMeta meta = item.getItemMeta();
//			meta.setDisplayName("§cNo Warps defined.");
//			item.setItemMeta(meta);
//			inv.setItem(22, item);
//
//		}
//
//		ItemStack item1 = new ItemStack(Material.CHEST);
//		ItemMeta meta1 = item1.getItemMeta();
//		meta1.setDisplayName("§6Warps");
//		List<String> lore1 = new ArrayList<String>();
//
//		lore1.add("§7This is a list of warps in the assets world");
//		lore1.add("§7Click on an enderpearl to warp.");
//		meta1.setLore(lore1);
//		item1.setItemMeta(meta1);
//		inv.setItem(4, item1);
//
//
//
//		int i = 1;
//		boolean nextPage = false;
//		for(int warpNr = (page-1)*28; warpNr < warps.size(); warpNr++) {
//
//			String warpName = warps.get(warpNr);
//
//			Material mat = Material.getMaterial(Main.getInstance().getConfig().getInt("warp." + warpName));
//
//			if(mat == Material.AIR ||  mat == null) {
//				mat = Material.getMaterial(Main.getInstance().getConfig().getString("warp." + warpName));
//				if(mat == Material.AIR ||  mat == null) {
//					mat = Material.ENDER_PEARL;
//				}
//			}
//
//
//
//			ItemStack item = new ItemStack(mat);
//			ItemMeta meta = item.getItemMeta();
//			meta.setDisplayName("§a" + warpName);
//			List<String> lore = new ArrayList<String>();
//			Location loc = null;
//			try {
//				loc = es.getWarps().getWarp(warpName);
//			}catch(Exception ex) {
//				loc = Main.getInstance().worldLoaderUtils.getLocationOfUnloadedWarp(warpName);
//			}
//
//			lore.add("§6Pos: §a" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ());
//			lore.add(" ");
//			lore.add("§a» §7Click to Teleport!");
//
//			meta.setLore(lore);
//			item.setItemMeta(meta);
//
//
//			int math1 = (int) (i-1) / 7;
//
//			int math2 = i + 9 + (math1 * 2);
//
//			inv.setItem(math2, item);
//			i++;
//
//			if(i > (page-1)*28 + 28) {
//				nextPage = true;
//				break;
//			}
//
//		}
//
//		/*
//		 * Paging
//		 */
//
//		ItemStack item = new ItemStack(Material.PAPER);
//		ItemMeta meta = item.getItemMeta();
//		meta.setDisplayName("§ePage: " + page);
//		item.setItemMeta(meta);
//		inv.setItem(49, item);
//
//		if(nextPage) {
//			item = new ItemStack(Material.ARROW);
//			meta = item.getItemMeta();
//			meta.setDisplayName("§eNext Page");
//			item.setItemMeta(meta);
//			inv.setItem(51, item);
//		}
//		if(page > 1) {
//			item = new ItemStack(Material.ARROW);
//			meta = item.getItemMeta();
//			meta.setDisplayName("§ePrevious Page");
//			item.setItemMeta(meta);
//			inv.setItem(47, item);
//		}
//

		
		return inv;
		
	}
	
	
	public void joinAssetsWorld(Player p) {
		if(isInAssetsWorld(p))
			p.sendMessage("§cYou are already in the assets world.");
		else {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mvtp " + p.getName() + " " + WORLDNAME);
		}
		
	}

	public void leaveAssetsWorld(Player p) {
		p.teleport(playersInAssetsWorld.get(p.getUniqueId()).location);
	}
	
	public boolean isInAssetsWorld(Player p) {
		if(playersInAssetsWorld.containsKey(p.getUniqueId()))
			return true;
		return false;
	}
	


	public HashMap<UUID, PlayerState> getPlayersInAssetsWorld() {
		return playersInAssetsWorld;
	}
	
	@EventHandler
	public static void onPlayerTemplateWorldEnter(PlayerTeleportEvent e) {
		
		if(Bukkit.getWorld(e.getTo().getWorld().getName()) != null && !e.getFrom().getWorld().getName().equalsIgnoreCase(e.getTo().getWorld().getName())) {
			if(e.getTo().getWorld().getName().equalsIgnoreCase(Main.getInstance().getAssetsWorldManager().WORLDNAME)) {
				e.getPlayer().sendMessage(Main.getInstance().PREFIX + "§aJoined the assets world");
				Main.getInstance().getAssetsWorldManager().playersInAssetsWorld.put(e.getPlayer().getUniqueId(), new PlayerState(e.getPlayer().getInventory().getContents(), e.getPlayer().getLocation()));
				
				Main.getInstance().getAssetsWorldManager().giveHotbarInventory(e.getPlayer());
			}
		}
	}
	
	@EventHandler
	public static void onPlayerTemplateWorldExit(PlayerTeleportEvent e) {
		
		if(Bukkit.getWorld(e.getTo().getWorld().getName()) != null && !e.getFrom().getWorld().getName().equalsIgnoreCase(e.getTo().getWorld().getName())) {
			if(e.getFrom().getWorld().getName().equalsIgnoreCase(Main.getInstance().getAssetsWorldManager().WORLDNAME)) {
				e.getPlayer().getInventory().setContents(Main.getInstance().getAssetsWorldManager().playersInAssetsWorld.get(e.getPlayer().getUniqueId()).inventory);
				e.getPlayer().sendMessage(Main.getInstance().PREFIX + "§aLeft the assets world");
				
				Main.getInstance().getAssetsWorldManager().playersInAssetsWorld.remove(e.getPlayer().getUniqueId());
			}
		}
	}
	
	@EventHandler
	public void onAssetsInventoryCreativeEvent(InventoryCreativeEvent e) {
		if(Main.getInstance().getAssetsWorldManager().isInAssetsWorld((Player)e.getWhoClicked())) {
			if(e.getCurrentItem() != null) if(e.getCurrentItem().getItemMeta() != null) if(e.getCurrentItem().getItemMeta().hasDisplayName()) {
				
				if(e.getCurrentItem().getItemMeta().getDisplayName().equals("§aAssets World Warp")) {
					e.setCancelled(true);
					//e.getWhoClicked().openInventory(Main.getInstance().getAssetsWorldManager().getCompassGUI(0));
				}else if(e.getCurrentItem().getItemMeta().getDisplayName().equals("§cLeave")) {
					e.setCancelled(true);
					//Main.getInstance().getAssetsWorldManager().leaveAssetsWorld((Player)e.getWhoClicked());;
				
				}
				//e.setCancelled(true);
			}
			if(e.getCursor() != null) if(e.getCursor().getItemMeta() != null) if(e.getCursor().getItemMeta().hasDisplayName()) {
				
				if(e.getCursor().getItemMeta().getDisplayName().equals("§aAssets World Warp")) {
					e.setCancelled(true);
					//e.getWhoClicked().openInventory(Main.getInstance().getAssetsWorldManager().getCompassGUI(0));
				}else if(e.getCursor().getItemMeta().getDisplayName().equals("§cLeave")) {
					e.setCancelled(true);
					//Main.getInstance().getAssetsWorldManager().leaveAssetsWorld((Player)e.getWhoClicked());;
				
				}
				//e.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void onAssetsInventoryClick(InventoryClickEvent e) {
		if(Main.getInstance().getAssetsWorldManager().isInAssetsWorld((Player)e.getWhoClicked())) {
			
			if(e.getCurrentItem() != null) if(e.getCurrentItem().getItemMeta() != null) if(e.getCurrentItem().getItemMeta().hasDisplayName()) {
				if(e.getCurrentItem().getItemMeta().getDisplayName().equals("§aAssets World Warp")) {
					e.setCancelled(true);
					((Player)e.getWhoClicked()).updateInventory();
					//e.getWhoClicked().openInventory(Main.getInstance().getAssetsWorldManager().getCompassGUI(0));
				}else if(e.getCurrentItem().getItemMeta().getDisplayName().equals("§cLeave")) {
					e.setCancelled(true);
					((Player)e.getWhoClicked()).updateInventory();
					//Main.getInstance().getAssetsWorldManager().leaveAssetsWorld((Player)e.getWhoClicked());;
				}
				
			//	((Player)e.getWhoClicked()).updateInventory();
				
			}
			
			if(e.getCursor() != null) if(e.getCursor().getItemMeta() != null) if(e.getCursor().getItemMeta().hasDisplayName()) {
				
				if(e.getCursor().getItemMeta().getDisplayName().equals("§aAssets World Warp")) {
					e.setCancelled(true);
					((Player)e.getWhoClicked()).updateInventory();
					//e.getWhoClicked().openInventory(Main.getInstance().getAssetsWorldManager().getCompassGUI(0));
				}else if(e.getCursor().getItemMeta().getDisplayName().equals("§cLeave")) {
					e.setCancelled(true);
					((Player)e.getWhoClicked()).updateInventory();
					//Main.getInstance().getAssetsWorldManager().leaveAssetsWorld((Player)e.getWhoClicked());;
				
				}
				//e.setCancelled(true);
			}
			
			
			if(e.getView().getTitle().equals("§2Assets GUI")) {
				e.setCancelled(true);
				if(e.getCurrentItem() != null) if(e.getCurrentItem().getItemMeta() != null) if(e.getCurrentItem().getItemMeta().hasDisplayName()) {						
						
					if(e.getCurrentItem().getType() != Material.CHEST && e.getCurrentItem().getType() != Material.BLACK_STAINED_GLASS_PANE && e.getCurrentItem().getItemMeta().getDisplayName().startsWith("§a")) {
						
						String warpName = e.getCurrentItem().getItemMeta().getDisplayName().replaceAll("§a", "");
						e.getWhoClicked().closeInventory();
						Bukkit.dispatchCommand(e.getWhoClicked(), "warp " + warpName);
						//Bukkit.dispatchCommand(e.getWhoClicked(), "warp " + warpName);
					}else if(e.getCurrentItem().getItemMeta().getDisplayName().equals("§eNext Page")) {
						e.getWhoClicked().openInventory(Main.getInstance().getAssetsWorldManager().getCompassGUI(Integer.parseInt(e.getInventory().getItem(49).getItemMeta().getDisplayName().replace("§ePage: ", "")) + 1));
					}else if(e.getCurrentItem().getItemMeta().getDisplayName().equals("§ePrevious Page")) {
						e.getWhoClicked().openInventory(Main.getInstance().getAssetsWorldManager().getCompassGUI(Integer.parseInt(e.getInventory().getItem(49).getItemMeta().getDisplayName().replace("§ePage: ", "")) - 1));
					}
						
					
				}
				((Player)e.getWhoClicked()).updateInventory();
			}
				
		}
	}
	
	@EventHandler
	public void onAssetsItemInteract(PlayerInteractEvent e) {
		if(Main.getInstance().getAssetsWorldManager().isInAssetsWorld(e.getPlayer())) {
			if(e.getItem() != null) if(e.getItem().getItemMeta() != null) if(e.getItem().getItemMeta().hasDisplayName()) {
				if(e.getItem().getItemMeta().getDisplayName().equals("§aAssets World Warp")) {
					Main.getInstance().getLuckPerms().getUserManager().getUser(e.getPlayer().getUniqueId()).data().add(Node.builder("worldedit.navigation.jumpto.tool").value(false).build());
					Main.getInstance().getLuckPerms().getUserManager().getUser(e.getPlayer().getUniqueId()).data().add(Node.builder("worldedit.navigation.thru.tool").value(false).build());
					Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
						Main.getInstance().getLuckPerms().getUserManager().getUser(e.getPlayer().getUniqueId()).data().remove(Node.builder("worldedit.navigation.jumpto.tool").build());
						Main.getInstance().getLuckPerms().getUserManager().getUser(e.getPlayer().getUniqueId()).data().remove(Node.builder("worldedit.navigation.thru.tool").build());
					}, 10);
					e.setCancelled(true);
					e.getPlayer().openInventory(Main.getInstance().getAssetsWorldManager().getCompassGUI());
				}else if(e.getItem().getItemMeta().getDisplayName().equals("§cLeave")) {
					e.setCancelled(true);
					Main.getInstance().getAssetsWorldManager().leaveAssetsWorld(e.getPlayer());;
				}
				
			}
			
				
		}
	}
	
	@EventHandler
	public void onPlayerQuitInAssetsWorld(PlayerQuitEvent e) {
		if(e.getPlayer().getLocation().getWorld().getName().equalsIgnoreCase(WORLDNAME)) {
			restoreState(e.getPlayer());
			
		}
	}
	
	public void restoreState(Player p) {
		p.getInventory().setContents(Main.getInstance().getAssetsWorldManager().playersInAssetsWorld.get(p.getUniqueId()).inventory);
		p.teleport(Main.getInstance().getAssetsWorldManager().playersInAssetsWorld.get(p.getUniqueId()).location);
		Main.getInstance().getAssetsWorldManager().playersInAssetsWorld.remove(p.getUniqueId());
	}

}

class PlayerState {
	
	PlayerState(ItemStack[] inv, Location loc){
		this.inventory = inv;
		this.location = loc;
	}
	public ItemStack[] inventory = null;
	public Location location = null;

}
