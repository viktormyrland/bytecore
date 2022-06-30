package net.bbytes.bukkit.listeners;

import net.bbytes.bukkit.Main;
import net.bbytes.bukkit.command.PrefixCommand;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.potion.PotionEffectType;

import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayerChatListener implements Listener{
	ExecutorService exec = Executors.newCachedThreadPool();
	
	@EventHandler (priority = EventPriority.MONITOR)
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		
//		for(Player all : Bukkit.getOnlinePlayers())
//			if(!all.hasPermission("honeyfrost.allowchat"))
//				if(e.getMessage().toLowerCase().contains(all.getName().toLowerCase())) {
////					Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
////						e.getPlayer().sendMessage("§8[§6§lH§e§lF§8] §cPlayer " + all.getName() + " is silenced, and cannot send or read chat messages.");
////						}, 5);
//
//					continue;
//				}
		
//		if(!e.getPlayer().hasPermission("honeyfrost.allowchat")) {
//			e.getPlayer().sendMessage("§cYou are silenced and cannot send or receive chat messages.");
//			e.setCancelled(true);
//			return;
//		}
		
		if(e.isCancelled())
			return;

		if(Main.getInstance().getTwoFactorUtils().notAuthenticatedUsers.contains(e.getPlayer())) {
			try {
				if(!Main.getInstance().getTfau().generateCurrentNumber(Main.getInstance().getTwoFactorUtils().getSecretFromUUID(e.getPlayer().getUniqueId())).equals(e.getMessage().replaceAll(" ", ""))) {
					e.getPlayer().sendMessage("§cInvalid code.");
				}else {
					Main.getInstance().getTwoFactorUtils().authenticate(e.getPlayer());
					e.getPlayer().sendMessage("§3[2FA] §3Authenticated with 2FA");
					for(Player all : Bukkit.getOnlinePlayers()) {
						if(all.getUniqueId() != e.getPlayer().getUniqueId())
							all.sendMessage("§3[2FA] §b" + e.getPlayer().getName() + " §3authenticated with 2FA");
					}
					e.getPlayer().removePotionEffect(PotionEffectType.BLINDNESS);
					
					// Just to keep Dinner happy
					Bukkit.getScheduler().runTaskLater(Main.getInstance(), () ->e.getPlayer().removePotionEffect(PotionEffectType.BLINDNESS), 20);
				}
				
				e.setCancelled(true);
				return;
			} catch (GeneralSecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void chatFormat(AsyncPlayerChatEvent e){

		String suffix = Main.getInstance().getLuckPerms().getUserManager().getUser(e.getPlayer().getUniqueId()).getCachedData().getMetaData().getSuffix();
		if(suffix == null) suffix = "";
		else {
			suffix = " §8[§7" + suffix + "§8]";
		}
		suffix = suffix.replaceAll("&", "§");


		String prefix = PrefixCommand.getPrefix(Objects.requireNonNull(Main.getInstance().getLuckPerms().getUserManager().getUser(e.getPlayer().getUniqueId())).getCachedData().getMetaData().getMetaValue("prefix"));

		if(prefix == null || prefix.isEmpty()) prefix = "";
		else prefix = "§8«" + prefix + "§8» ";
		prefix = prefix.replaceAll("&", "§");


		e.setMessage(e.getMessage().replaceAll("&", "§"));
		e.setMessage(e.getMessage().replaceAll("%", "%%"));

		String msg = prefix + "§7" + e.getPlayer().getDisplayName().replaceAll("&", "§") + suffix + "§7: §f" + e.getMessage();


		e.setFormat(msg);

		Main.getInstance().getBungeeMessager().sendPlayerChatMessage(msg, "OTHERS");


		Set<Player> set = new HashSet<Player>();
		for(Player p : e.getRecipients()) {
			set.add(p);
		}

		for(Player p : set) {
			if(Main.getInstance().getTwoFactorUtils().notAuthenticatedUsers.contains(p)) {
				e.getRecipients().remove(p);
			}
		}
	}
	
//	@EventHandler
//	public void onManagerScream(AsyncPlayerChatEvent e) {
//
//		if(!e.getPlayer().hasPermission("bbytes.admin"))
//			return;
//
//		String shout = "§4§l";
//		if(e.getPlayer().getName().equals("VenonCow"))
//			shout = "§3§l";
//
//		StringBuilder newMessage = new StringBuilder();
//		boolean previousUppercase = false;
//		String[] msg = e.getMessage().split(" ");
//		for(String m : msg) {
//			if((isAllUpper(m) && m.length() > 1) || (isAllUpper(m) && previousUppercase)) {
//				newMessage.append(shout).append(m).append("§r ");
//				previousUppercase = true;
//			}else {
//				newMessage.append(m).append(" ");
//				previousUppercase = false;
//			}
//		}
//
//		e.setMessage(newMessage.substring(0, newMessage.length() -1));
//	}


	@EventHandler(priority = EventPriority.MONITOR)
	public void globalChatSend(AsyncPlayerChatEvent e){
		if(e.isCancelled())return;

		Main.getInstance().getRedisManager().publishMessage("CHAT_MESSAGE",
				Main.getInstance().CLIENTNAME + ";" + e.getFormat());

	}



	private boolean isAllUpper(String s) {
		boolean containsLetters = false;
		for(char c : s.toCharArray()) {
			if(!containsLetters) if(Character.isLetter(c))
				containsLetters = true;
	    	
	       if(Character.isLetter(c) && Character.isLowerCase(c)) {
	           return false;
	        }
	    }
		return containsLetters;
	}
}
