package net.bbytes.bukkit.listeners;

import com.earth2me.essentials.Essentials;
import net.bbytes.bukkit.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener{
	
	Essentials es = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");

//	String[] protectAgainst = new String[]{
//			"ban viktoracri",
//			"kick viktoracri",
//			"jail viktoracri"
//	};
//	@EventHandler
//	public void protectViktoracri(PlayerCommandPreprocessEvent e) {
//
//		for(String str : protectAgainst){
//			if(e.getMessage().toLowerCase().contains(str)){
//				e.setCancelled(true);
//				e.getPlayer().sendMessage("§cViktoracri can't be punished, silly.");
//			}
//		}
//
//		if(e.getMessage().toLowerCase().contains("/troll") && e.getMessage().toLowerCase().contains("viktoracri")){
//			e.setCancelled(true);
//			e.getPlayer().sendMessage("§cViktoracri can't be trolled, silly");
//		}else if(e.getMessage().toLowerCase().contains("/troll") && e.getMessage().toLowerCase().contains("venoncow")){
//			e.setCancelled(true);
//			Bukkit.dispatchCommand(e.getPlayer(), e.getMessage().substring(1).replaceAll("(?i)venoncow", e.getPlayer().getName()));
//		}
//
//	}
//
//	@EventHandler
//	public void protectViktoracriConsole(ServerCommandEvent e){
//		for(String str : protectAgainst){
//			if(e.getCommand().toLowerCase().contains(str)){
//				e.setCancelled(true);
//				e.getSender().sendMessage("§cViktoracri can't be punished, silly.");
//			}
//		}
//
//		if(e.getCommand().toLowerCase().contains("/troll") && e.getCommand().toLowerCase().contains("viktoracri")){
//			e.setCancelled(true);
//			e.getSender().sendMessage("§cViktoracri can't be trolled, silly");
//		}
//	}

	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
		
		// After command block
		e.getPlayer().setOp(false);
		
		if(Main.getInstance().getTwoFactorUtils().notAuthenticatedUsers.contains(e.getPlayer())) {
			e.setCancelled(true);
			e.getPlayer().sendMessage("§cPlease authenticate with 2FA before executing any commands.");
		}
		
	}
	
//	@SuppressWarnings("unchecked")
//	@EventHandler
//	public void onPlayerWorldTeleport(PlayerCommandPreprocessEvent e) {
//		/*
//		 *  /mvtp load world if not loaded and it exists.
//		 */
//		if(e.getMessage().toLowerCase().startsWith("/mv tp")
//		|| e.getMessage().toLowerCase().startsWith("/mvtp")) {
//
//			String world = "";
//			if(e.getMessage().toLowerCase().startsWith("/mv tp") && e.getMessage().split(" ").length > 2) world = e.getMessage().split(" ")[2];
//			else if (e.getMessage().split(" ").length > 1)world = e.getMessage().split(" ")[1];
//			else return;
//
//			if(Bukkit.getWorld(world) == null) {
//				if(Main.getInstance().worldLoaderUtils.isUnloadedWorld(world)) {
//					e.setCancelled(true);
//					e.getPlayer().sendMessage(Main.getInstance().worldLoaderUtils.PREFIX + "§aLoading world '§b" + world + "§a'...");
//
//					Main.getInstance().worldLoaderUtils.loadWorld(world);
//					//OLD Main.getInstance().worldLoaderUtils.loadWorldByName(world);
//
//					long start = System.currentTimeMillis();
//					final String finalWorld = world;
//					new BukkitRunnable() {
//						@Override
//						public void run() {
//							if(Bukkit.getWorld(finalWorld) != null) {
//								Bukkit.getScheduler().callSyncMethod(Main.getInstance(), () -> Bukkit.dispatchCommand(e.getPlayer(), "mvtp " + finalWorld));
//								e.getPlayer().sendMessage(Main.getInstance().worldLoaderUtils.PREFIX + "§aSuccessfully loaded world '§b" + finalWorld + "§a'");
//								this.cancel();
//							}
//							if(System.currentTimeMillis() - start > 10000) {
//								e.getPlayer().sendMessage(Main.getInstance().worldLoaderUtils.PREFIX + "§cWorld unresponsive for too long. Aborting.");
//								this.cancel();
//							}
//
//						}
//					}.runTaskTimerAsynchronously(Main.getInstance(), 0, 10);
//				}
//			}
//
//
//			/*
//			 * If /warp <warp>'s world is unloaded
//			 */
//		}else if(e.getMessage().toLowerCase().startsWith("/warp ")
//				|| e.getMessage().toLowerCase().startsWith("/warps ")) {
//			if(e.getMessage().split(" ").length < 2) return;
//
//			try {
//				es.getWarps().getWarp(e.getMessage().split(" ")[1]);
//			} catch (WarpNotFoundException e1) {return;
//			} catch (InvalidWorldException e1) {
//
//				/*
//				 * Get the exact warp name
//				 */
//				final String warpName = e.getMessage().split(" ")[1].toLowerCase();
//
//				/*
//				 * Get the world name from the yml file
//				 */
//				String world = Main.getInstance().worldLoaderUtils.getWorldOfUnloadedWarp(warpName);
//
//
//				/*
//				 * If the world is unloaded, but exists, load the world and warp to it.
//				 */
//				if(Main.getInstance().worldLoaderUtils.isUnloadedWorld(world)) {
//					e.setCancelled(true);
//					e.getPlayer().sendMessage(Main.getInstance().worldLoaderUtils.PREFIX + "§aLoading world '§b" + world + "§a'...");
//					Main.getInstance().worldLoaderUtils.loadWorld(world);
//					//Main.getInstance().worldLoaderUtils.loadWorldByName(world);
//
//					long start = System.currentTimeMillis();
//					final String finalWorld = world;
//					new BukkitRunnable() {
//						@Override
//						public void run() {
//							if(Bukkit.getWorld(finalWorld) != null) {
//								Bukkit.getScheduler().callSyncMethod(Main.getInstance(), () -> Bukkit.dispatchCommand(e.getPlayer(), "warp " + warpName));
//								e.getPlayer().sendMessage(Main.getInstance().worldLoaderUtils.PREFIX + "§aSuccessfully loaded world '§b" + finalWorld + "§a'");
//								this.cancel();
//							}
//							if(System.currentTimeMillis() - start > 10000) {
//								e.getPlayer().sendMessage(Main.getInstance().worldLoaderUtils.PREFIX + "§cWorld unresponsive for too long. Aborting.");
//								this.cancel();
//							}
//
//						}
//					}.runTaskTimerAsynchronously(Main.getInstance(), 0, 10);
//				}else if(Main.getInstance().worldLoaderUtils.isArchivedWorld(world)) {
//					e.setCancelled(true);
//					e.getPlayer().sendMessage("§cThat world is archived");
//				}else {
//					e.setCancelled(true);
//					e.getPlayer().sendMessage("§cThat world doesn't exist.");
//				}
//
//			}
//
//		}else if(e.getMessage().toLowerCase().startsWith("/home")
//				|| e.getMessage().toLowerCase().startsWith("/homes")) {
//			if(e.getMessage().split(" ").length < 2) return;
//
//			boolean doit = false;
//			try {
//				es.getUser(e.getPlayer()).getHome(e.getMessage().split(" ")[1]);
//				LinkedHashMap<String, Object> homes = Main.getInstance().worldLoaderUtils.getHomesOfPlayer(e.getPlayer());
//				for(String key : homes.keySet()) {
//					if(key.equalsIgnoreCase(e.getMessage().split(" ")[1])) {
//					doit = true;
//					}
//				}
//
//
//			} catch (InvalidWorldException e1) {
//				doit = true;
//
//			} catch (Exception e1) {e1.printStackTrace();}
//
//			if(doit) {
//
//				/*
//				 * Get the world of the home from the userdata YAML file
//				 */
//
//				LinkedHashMap<String, Object> homes = Main.getInstance().worldLoaderUtils.getHomesOfPlayer(e.getPlayer());
//				LinkedHashMap<String, Object> warp = (LinkedHashMap<String, Object>) homes.get(e.getMessage().split(" ")[1].toLowerCase());
//
//				String world = (String) warp.get("world");
//				/*System.out.println("World: " + world);
//				for(String key : homes.keySet()) {
//					Object ob = homes.get(key);
//					System.out.println("Key: '" + key + "', Type: '" + ob.getClass().getName() + "', toString: '" + ob.toString() + "'");
//				}
//				*/
//
//				/*
//				 * Load the world and TP
//				 */
//
//				if(Main.getInstance().worldLoaderUtils.isUnloadedWorld(world)) {
//					e.setCancelled(true);
//					e.getPlayer().sendMessage(Main.getInstance().worldLoaderUtils.PREFIX + "§aLoading world '§b" + world + "§a'...");
//					Main.getInstance().worldLoaderUtils.loadWorld(world);
//					//Main.getInstance().worldLoaderUtils.loadWorldByName(world);
//
//					long start = System.currentTimeMillis();
//					final String finalWorld = world;
//					new BukkitRunnable() {
//						@Override
//						public void run() {
//							if(Bukkit.getWorld(finalWorld) != null) {
//								Bukkit.getScheduler().callSyncMethod(Main.getInstance(), () -> Bukkit.dispatchCommand(e.getPlayer(), "home " + e.getMessage().split(" ")[1]));
//								e.getPlayer().sendMessage(Main.getInstance().worldLoaderUtils.PREFIX + "§aSuccessfully loaded world '§b" + finalWorld + "§a'");
//								this.cancel();
//							}
//							if(System.currentTimeMillis() - start > 10000) {
//								e.getPlayer().sendMessage(Main.getInstance().worldLoaderUtils.PREFIX + "§cWorld unresponsive for too long. Aborting.");
//								this.cancel();
//							}
//
//						}
//					}.runTaskTimerAsynchronously(Main.getInstance(), 0, 10);
//				}else if(Main.getInstance().worldLoaderUtils.isArchivedWorld(world)) {
//					e.setCancelled(true);
//					e.getPlayer().sendMessage("§cThat world is archived");
//
//				}
//			}
//
//
//		}
//
//		/*
//		 * Move the world from ./.UNLOADED/ to ./ when doing /mv load
//		 */
//		else if(e.getMessage().toLowerCase().startsWith("/mv load")
//				|| e.getMessage().toLowerCase().startsWith("/mvload")) {
//
//			String world = "";
//			if(e.getMessage().toLowerCase().startsWith("/mv load") && e.getMessage().split(" ").length > 2) world = e.getMessage().split(" ")[2];
//			else if (e.getMessage().split(" ").length > 1)world = e.getMessage().split(" ")[1];
//			else return;
//
//			if(Bukkit.getWorld(world) != null) return;
//
//			if(Main.getInstance().worldLoaderUtils.isValidWorld(world)) return;
//
//			if(Main.getInstance().worldLoaderUtils.isArchivedWorld(world)) {
//				e.setCancelled(true);
//				e.getPlayer().sendMessage("§cThat world is archived");
//				return;
//			}
//
//			if(Main.getInstance().worldLoaderUtils.isUnloadedWorld(world)) {
//				e.setCancelled(true);
//				Main.getInstance().worldLoaderUtils.loadWorld(world, e.getPlayer());
//
//			}
//
//		}
//
//		/*
//		 * Move the world from ./.UNLOADED/ to ./ when doing /mv load
//		 */
//		else if(e.getMessage().toLowerCase().startsWith("/mv unload")
//				|| e.getMessage().toLowerCase().startsWith("/mvunload")) {
//
//			String world = "";
//			if(e.getMessage().toLowerCase().startsWith("/mv unload") && e.getMessage().split(" ").length > 2) world = e.getMessage().split(" ")[2];
//			else if (e.getMessage().split(" ").length > 1)world = e.getMessage().split(" ")[1];
//			else return;
//
//			if(Bukkit.getWorld(world) == null) return;
//
//
//			if(Main.getInstance().worldLoaderUtils.isValidWorld(world)) {
//				e.setCancelled(true);
//				Main.getInstance().worldLoaderUtils.unloadWorld(world, e.getPlayer());
//
//			}
//
//		}
//
//	}

}
