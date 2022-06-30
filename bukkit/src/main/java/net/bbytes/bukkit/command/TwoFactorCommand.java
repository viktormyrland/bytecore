package net.bbytes.bukkit.command;

import net.bbytes.bukkit.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TwoFactorCommand implements CommandExecutor, TabCompleter{

	ExecutorService exec = Executors.newCachedThreadPool();
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(args.length == 0) {
			sender.sendMessage("§b§lTwo-Factor Authentication");
			sender.sendMessage(" §7- §b/2fa new §7- Generate a new 2FA code");
			if(sender.hasPermission("bbytes.admin")) {
				sender.sendMessage(" ");
				sender.sendMessage("§c§lAdmin Commands:");
				sender.sendMessage(" §7- §c/2fa new <player> §7- Generate a new 2FA code for a player");
				sender.sendMessage(" §7- §c/2fa remove <player> §7- Remove 2FA from a player ");
				sender.sendMessage(" §7- §c/2fa check [player] §7- See the current 2FA code for a player");
			}
		}else if(args[0].equalsIgnoreCase("new")) {
			
			if(args.length > 2 && sender.hasPermission("bbytes.admin")) {
				if(getOfflinePlayer(args[1]) == null) {
					sender.sendMessage("§7Could not find player '§c" + args[0] + "§7'");
					return true;
				}
				sender.sendMessage("§aA new 2FA code has been generated for §b" + Bukkit.getOfflinePlayer(args[1]).getName() + "§a.");
				
				String secret = Main.getInstance().getTfau().generateBase32Secret();
				
				if(Main.getInstance().getServer().getVersion().contains("1.12.2")) {
//					Main.getInstance().getWrapper().sendChatComponent((Player) sender,  "{\"text\":\"Click for QR Code\",\"color\":\"yellow\",\"underlined\":\"true\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"" + Main.getInstance().getTfau().qrImageUrl("Blockbytes", secret) +"\"}}");

			        
			        Main.getInstance().getTwoFactorUtils().setNewSecret(Bukkit.getOfflinePlayer(args[1]).getUniqueId(), secret);
				}
				
				
			}else {
				sender.sendMessage("§aA new 2FA code has been generated for you. Open Google Authenticator or Authy and scan the QR code linked below.");
				
				String secret = Main.getInstance().getTfau().generateBase32Secret();

//				Main.getInstance().getWrapper().sendChatComponent((Player) sender,  "{\"text\":\"Click for QR Code\",\"color\":\"yellow\",\"underlined\":\"true\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"" + Main.getInstance().getTfau().qrImageUrl("Blockbytes", secret) +"\"}}");
		        
		        Main.getInstance().getTwoFactorUtils().setNewSecret(((Player) sender).getUniqueId(), secret);
	        
			}
		}else if(args[0].equalsIgnoreCase("remove") && sender.hasPermission("bbytes.admin")) {
			UUID user;
			String player;
			
			if(args.length == 1) {
				sender.sendMessage("§cPlease specify a user");
				return true;
			}else if(getOfflinePlayer(args[1]) == null){
				sender.sendMessage("§cUser not found");
				return true;
			}else {
				user = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
				player = Bukkit.getOfflinePlayer(args[1]).getName();
			}
			
			exec.execute(() ->{
				String secret = Main.getInstance().getTwoFactorUtils().getSecretFromUUID(user);
				if(secret == null) {
					sender.sendMessage("§cUser does not have 2FA");
				}else {
				
					Main.getInstance().getTwoFactorUtils().remove2fa(user);
					
					sender.sendMessage("§a2FA has been removed from §b" + player + "§a.");
				}
			});
			
			
			
		}else if(args[0].equalsIgnoreCase("check") && sender.hasPermission("bbytes.admin")) {
			
			UUID user;
			
			if(args.length == 1) {
				user = ((Player) sender).getUniqueId();
			}else if(getOfflinePlayer(args[1]) == null){
				sender.sendMessage("§cUser not found");
				return true;
			}else {
				user = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
			}
			exec.execute(() ->{
				try {
					String secret = Main.getInstance().getTwoFactorUtils().getSecretFromUUID(user);
					if(secret == null) {
						sender.sendMessage("§cUser does not have 2FA");
					}else {
						String code = Main.getInstance().getTfau().generateCurrentNumber(secret);
						
						sender.sendMessage("§b§l" + code);
					}
				} catch (GeneralSecurityException e) {e.printStackTrace();
					sender.sendMessage("§cError in generating code");
				}
			});
			
			
		}else {
			sender.sendMessage("§cInvalid Subcommand");
		}
		
		
		return true;
	}
	
	public OfflinePlayer getOfflinePlayer(String name) {
        for(OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            if(player.getName().toLowerCase().equals(name.toLowerCase())) return player;
        }
        return null;
    }

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> list = new ArrayList<String>();
		if(args.length == 1) {
			list.add("new");
			if(sender.hasPermission("bbytes.admin")) {
				list.add("remove");
				list.add("check");
			}
		}else if(args.length == 2 && sender.hasPermission("bbytes.admin")) {
			for(Player all : Bukkit.getOnlinePlayers())
				list.add(all.getName());
		}
		
		List<String> returnList = new ArrayList<String>();
		for(String str : list) {
			if(str.toLowerCase().startsWith(args[args.length-1].toLowerCase()))
				returnList.add(str);
		}
		//Collections.sort(returnList, String.CASE_INSENSITIVE_ORDER);
		
		return returnList;
	}
	
	/*
	 @Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> list = new ArrayList<String>();
		if(args.length == 1) {
			list.add("new");
			
		}
		
		List<String> returnList = new ArrayList<String>();
		for(String str : list) {
			if(str.toLowerCase().startsWith(args[args.length-1].toLowerCase()))
				returnList.add(str);
		}
		//Collections.sort(returnList, String.CASE_INSENSITIVE_ORDER);
		
		return returnList;
	}
	 
	 */
	
	

}
