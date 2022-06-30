package net.bbytes.bukkit.command;

import net.bbytes.bukkit.Main;
import net.bbytes.bukkit.message.Message;
import net.bbytes.bukkit.redis.RedisMessageReceiver;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WhitelistCommand implements CommandExecutor,TabCompleter, RedisMessageReceiver {
	
	public static ArrayList<String> loadedWhitelist = new ArrayList<String>();

	
	final int PER_PAGE = 30;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		
		if(!sender.hasPermission("bbytes.admin")) {
			sender.sendMessage(Message.NO_PERMISSION.get(sender));
			return true;
		}
		
		if(args.length == 0) {
			sendHelp(sender);
		}else if(args[0].equalsIgnoreCase("add")) {
			if(args.length < 2) sender.sendMessage("§cUsage: §7/whitelist add <user>");
			else {
				ExecutorService executor = Executors.newSingleThreadExecutor();
				executor.execute(() -> {
					
					UUID uuid = Main.getInstance().getUTNUtils().getUUIDFromName_Sync(args[1]);
					if(uuid == null) {
						sender.sendMessage("§cThere is no Minecraft user with that username.");
						return;
					}
					
					
					ResultSet rs = Main.getInstance().getMySQLManager().mysql.getMySQL().query("SELECT * FROM Whitelist WHERE UUID ='" + uuid.toString() + "';");
					
					try {
						if(rs.next()) {
							sender.sendMessage("§bUser is already whitelisted");
							return;
						}
						rs.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					
					Main.getInstance().getMySQLManager().mysql.update("INSERT INTO Whitelist (UUID) VALUES ('" + uuid.toString() + "');");
					
					sender.sendMessage("§bAdded " + Main.getInstance().getUTNUtils().getFixedName_Sync(args[1]) + " to the whitelist.");
					
					loadedWhitelist.add(uuid.toString());
					Main.getInstance().getRedisManager().publishMessage("WHITELIST", "reloadWhitelist");
				});
				executor.shutdown();
			}
		}else if(args[0].equalsIgnoreCase("remove")) {
			if(args.length < 2) sender.sendMessage("§cUsage: §7/whitelist remove <user>");
			else {
				ExecutorService executor = Executors.newSingleThreadExecutor();
				executor.execute(() -> {
					
					UUID uuid = Main.getInstance().getUTNUtils().getUUIDFromName_Sync(args[1]);
					if(uuid == null) {
						sender.sendMessage("§cThere is no Minecraft user with that username.");
						return;
					}
					
					
					ResultSet rs = Main.getInstance().getMySQLManager().mysql.getMySQL().query("SELECT * FROM Whitelist WHERE UUID ='" + uuid.toString() + "';");
					
					try {
						if(!rs.next()) {
							sender.sendMessage("§cUser is not whitelisted");
							return;
						}
						rs.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					
					Main.getInstance().getMySQLManager().mysql.update("DELETE FROM Whitelist WHERE UUID='" + uuid.toString() + "';");
					
					sender.sendMessage("§bRemoved " + Main.getInstance().getUTNUtils().getFixedName_Sync(args[1]) + " from the whitelist.");
					
					loadedWhitelist.remove(uuid.toString());
//					Main.getInstance().getBungeeMessager().forwardBungeeMessage("UpdateWhitelist", null);
					Main.getInstance().getRedisManager().publishMessage("WHITELIST", "reloadWhitelist");
					
				});
				executor.shutdown();
			}
			
		}else if(args[0].equalsIgnoreCase("list")) {
			ExecutorService executor = Executors.newSingleThreadExecutor();
			executor.execute(() -> {
				
				// Get the page number
				
				int page = 0;
				if(args.length > 1) {
					try {
						page = Integer.parseInt(args[1]);
					}catch(NumberFormatException ex) {
						page = 0;
					}
				}
				if(page < 1)page = 1;
				
				// Load the list of UUIDs
				
				loadMemoryWhitelist_Sync();
				/*
				ResultSet rs = Main.getInstance().getMySQLManager().mysql.getMySQL().query("SELECT * FROM Whitelist;");
				List<String> uuids = new ArrayList<String>();
				try {
					while(rs.next()) {
						uuids.add(rs.getString("UUID"));
					}
					rs.close();
				} catch (SQLException e) {e.printStackTrace();}
				*/
				ArrayList<String> uuids = loadedWhitelist;
				
				// Correct the page number if it's too high
				if(page > ((int)((uuids.size()-1)/PER_PAGE) + 1)) page = ((int)((uuids.size()-1)/PER_PAGE) + 1);
				
				// Notify if the whitelist is empty
				if(uuids.size() == 0) {
					sender.sendMessage("§cThe whitelist is empty.");
					return;
				}
				
				// Create a set of all usernames
				List<String> users = new ArrayList<String>();
				
				for(String uuid : uuids) {
					users.add(Main.getInstance().getUTNUtils().getNameFromUUID_Sync(UUID.fromString(uuid)));
				}
				
				// Sort the usernames
				Collections.sort(users, String.CASE_INSENSITIVE_ORDER);
				
				// Create the final visible string
				String str = "";
				for(int i = (page-1)*PER_PAGE; i < (page-1)*PER_PAGE+PER_PAGE; i++) {
					if(users.size() == i) break;
					
					/*
					if(uuids.size() - 1 == (page-1)*20 + i) {
						str = str.substring(0, str.length() - 1) + "and §f" + MojangAPI.getCurrentName(UUID.fromString(uuids.get(i)));
					}
					else str += 
					*/
					str += "§f" + users.get(i) + "§7, ";
				}
				
				
				str = str.substring(0, str.length() - 4);
				
				sender.sendMessage("§6§lList of whitelisted players");
				if(str.contains(", ")) {
					int index = str.lastIndexOf(", ");
					StringBuilder builder = new StringBuilder();
					builder.append(str.substring(0, index));
					builder.append(" and ");
					builder.append(str.substring(index + 2));
					sender.sendMessage(builder.toString());
				}else {
					sender.sendMessage(str);
				}
				
				
				
				
				
				sender.sendMessage("§6--- §eShowing page §6" + page + " §eof §6" + ((int)((uuids.size()-1)/PER_PAGE) + 1)+ " ---");
				
				
				
			});
			executor.shutdown();
		}else
			sendHelp(sender);
		
		
		
		return true;
	}
	
	void sendHelp(CommandSender sender) {
		sender.sendMessage("§f§lWhitelist Management");
		sender.sendMessage(" §7- §b/whitelist add <user> §7- Add a user to the whitelist");
		sender.sendMessage(" §7- §b/whitelist remove <user> §7- Remove a user from the whitelist");
		sender.sendMessage(" §7- §b/whitelist list [page] §7- List all users in the whitelist");
	}
	
	public static void loadMemoryWhitelist() {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.execute(WhitelistCommand::loadMemoryWhitelist_Sync);
		executor.shutdown();
	}
	
	private static void loadMemoryWhitelist_Sync() {
		ResultSet rs = Main.getInstance().getMySQLManager().mysql.getMySQL().query("SELECT * FROM Whitelist;");

			try {
				loadedWhitelist.clear();
				while(rs.next()) {
					loadedWhitelist.add(rs.getString("UUID"));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				

	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> list = new ArrayList<String>();
		if(sender.hasPermission("bbytes.admin")) {
			if(args.length == 1) {
				list.add("add");
				list.add("remove");			
				list.add("list");
			}
		}
		
		List<String> returnList = new ArrayList<String>();
		for(String str : list) {
			if(str.toLowerCase().startsWith(args[args.length-1].toLowerCase()))
				returnList.add(str);
		}
		//Collections.sort(returnList, String.CASE_INSENSITIVE_ORDER);
		
		return returnList;
	}


	@Override
	public void onRedisMessageReceived(String channel, String message) {
		if(channel.equals("WHITELIST")){
			if(message.equals("reloadWhitelist")){
				loadMemoryWhitelist();
			}
		}
	}
}
