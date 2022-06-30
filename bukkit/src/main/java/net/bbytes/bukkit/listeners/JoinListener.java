package net.bbytes.bukkit.listeners;

import com.earth2me.essentials.Essentials;
import net.bbytes.bukkit.Main;
import net.bbytes.bukkit.command.MaintenanceCommand;
import net.bbytes.bukkit.command.WhitelistCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JoinListener implements Listener{
	ExecutorService exec = Executors.newCachedThreadPool();
	Essentials es = (Essentials)Bukkit.getPluginManager().getPlugin("Essentials");

	@EventHandler
	public void addUserObject(PlayerJoinEvent e){
		Main.getInstance().getUserManager().addUser(e.getPlayer());
	}




	@EventHandler
	public void maintenanceEnforce(PlayerLoginEvent e){
		if(MaintenanceCommand.maintenanceEnabled){
			String[] allowedusers = new String[]{
					"Viktoracri"
			};

			if(!Arrays.asList(allowedusers).contains(e.getPlayer().getName())){
				e.disallow(Result.KICK_OTHER, "\n\n§c§The Blockbytes server is currently under maintenance");
			}
		}
	}



	@EventHandler
	public void authenticateUser(PlayerJoinEvent e) {
		
//		if(!e.getPlayer().hasPermission("honeyfrost.allowchat")) {
//			Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
//				Bukkit.broadcastMessage("§8[§6§lH§e§lF§8] §cPlayer " + e.getPlayer().getName() + " is silenced, and cannot send or read chat messages.");
//				}, 20);
//
//		}
		Main.getInstance().getTwoFactorUtils().notAuthenticatedUsers.add(e.getPlayer());
		
		exec.execute(() -> {
			
			/*
			 * If you don't have 2fa enabled
			 */
			if(Main.getInstance().getTwoFactorUtils().getSecretFromUUID(e.getPlayer().getUniqueId()) == null) {
				Main.getInstance().getTwoFactorUtils().notAuthenticatedUsers.remove(e.getPlayer());
				Bukkit.getScheduler().runTask(Main.getInstance(), () -> {e.getPlayer().removePotionEffect(PotionEffectType.BLINDNESS);});
//				Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {e.getPlayer().sendMessage("§3[2FA] §c§lWARNING: §cYou do not have 2FA enabled.");}, 30);
			}else {
				/*
				 * If you have 2fa enabled
				 */
					
					/*
					 * If there is over 24 hours since you last logged in.
					 */
				if(System.currentTimeMillis() - Main.getInstance().getTwoFactorUtils().getLastAuthenticatedFromUUID(e.getPlayer().getUniqueId()) > Main.getInstance().getTwoFactorUtils().HOURS *60*60*1000) {
					Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {e.getPlayer().sendMessage("§3[2FA] §3There has been over 72 hours since you last logged on. Please enter your 2FA code.");}, 30);
					Bukkit.getScheduler().runTask(Main.getInstance(), () -> {e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100000, 100000));}); 
					
					/*
					 * Announce to all other players
					 */
					Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
						for(Player all : Bukkit.getOnlinePlayers()) {
							if(all.getUniqueId() != e.getPlayer().getUniqueId())
								all.sendMessage("§3[2FA] §b" + e.getPlayer().getName() + " §3needs to authenticate with 2FA.");
						}
					}, 20);
					return;
				}
					/*
					 * If the IP has changed
					 */
				if(!Main.getInstance().getTwoFactorUtils().getLastIPFromUUID(e.getPlayer().getUniqueId()).equals(e.getPlayer().getAddress().getHostString())) {
					
					Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {e.getPlayer().sendMessage("§3[2FA] §3Your IP address has changed since you last logged on. Please enter your 2FA code.");}, 30);
					Bukkit.getScheduler().runTask(Main.getInstance(), () -> {e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100000, 100000));}); 
					
					/*
					 * Announce to all other players
					 */
					
					Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
					
						for(Player all : Bukkit.getOnlinePlayers()) {
							if(all.getUniqueId() != e.getPlayer().getUniqueId())
								all.sendMessage("§3[2FA] §b" + e.getPlayer().getName() + " §3needs to authenticate with 2FA. Reason: IP Changed");
						}
					}, 20);
					return;
				}
				
				/*
				 * If the IP is the same and there is under 24 hours since they last logged in.
				 */
				Main.getInstance().getTwoFactorUtils().notAuthenticatedUsers.remove(e.getPlayer());
				Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {e.getPlayer().sendMessage("§3[2FA] §3Already authenticated with 2FA.");}, 30);
				Bukkit.getScheduler().runTask(Main.getInstance(), () -> {e.getPlayer().removePotionEffect(PotionEffectType.BLINDNESS);});
				Main.getInstance().getTwoFactorUtils().registerActivity(e.getPlayer());
			}
		});
		

	}
	
	@EventHandler
	public void whitelistEnforce(PlayerLoginEvent e) {

		boolean exists = false;
		for(String uuid : WhitelistCommand.loadedWhitelist)
			if(uuid.equals(e.getPlayer().getUniqueId().toString())){
				exists = true;
				break;
			}

		if(!exists)
			e.disallow(Result.KICK_WHITELIST, "\n\n§c§lYou are not whitelisted on the Blockbytes server.");
	}


	@EventHandler
	public void loadWorld(PlayerLoginEvent e) {
		if(e.getPlayer().getLocation().getWorld() == null){
			System.out.println("Player tried to join unloaded world " + e.getPlayer().getWorld().getName());
		}
	}

	
//	@EventHandler
//	public void f3PermUpdate(PlayerJoinEvent e) {
//		Main.getInstance().getF3NPerm().update(e.getPlayer());
//	}

	@EventHandler
	public void announce(PlayerJoinEvent e) {
		e.setJoinMessage("§8[§a§l+§8] §a" + e.getPlayer().getName());
	}
	
	
	

	
	

}
