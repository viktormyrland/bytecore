package net.bbytes.bukkit.command;

import net.bbytes.bukkit.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

public class WorkCommand implements CommandExecutor,TabCompleter{
	
	final int LISTSIZE = 7;

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
//
//
//		if(!sender.hasPermission("honeyfrost.user")) {
//			sender.sendMessage("§cNo Permission");
//			return true;
//		}
//
//
//
//		if(args.length == 0) {
//			sendHelpList(sender);
//		}else if(args[0].equalsIgnoreCase("start")){
//			if(!(sender instanceof Player)) {
//				sender.sendMessage("§cThou art not a player");
//				return true;
//			}
//
//			if(Main.getInstance().getTtu().isPlayerWorking(((Player) sender).getUniqueId())){
//				sender.sendMessage("§cYou are already in a work session.");
//				return true;
//			}
//
//
//
//			/*
//			if(args.length < 2) {
//				sender.sendMessage("§cUsage: §7/work start <ID>");
//			}else {
//				int i = 0;
//				try {
//					i = Integer.parseInt(args[1]);
//				}catch(NumberFormatException ex) {
//					sender.sendMessage("§cPlease specify a valid ID.");
//					return true;
//				}
//
//				final int i2 = i;
//
//				Main.getInstance().getMySQLManager().mysql.query("SELECT * FROM TT_Projects WHERE ID='" + i + "'", new Consumer<ResultSet>() {
//
//					@Override
//					public void accept(ResultSet rs) {
//						try {
//							if(!rs.next()) {
//								sender.sendMessage("§cThere is no project with that ID.");
//							}else if(!rs.getBoolean("Active")){
//								sender.sendMessage("§cThis project is no longer active.");
//							}else {
//
//								Main.getInstance().getTtu().newWorkSession((Player) sender, i2);
//								sender.sendMessage("§a§lWork session started");
//								sender.sendMessage("§aProject: '§b" + rs.getString("Name") + "§a' with project ID §b" + i2);
//
//							}
//						} catch (SQLException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//
//					}
//
//				});
//
//
//
//
//			}*/
//		}else if(args[0].equalsIgnoreCase("stop")){
//
//			Player p = (Player) sender;
//
//			if(!Main.getInstance().getTtu().isPlayerWorking(p.getUniqueId())){
//				sender.sendMessage("§cYou are not in a work session.");
//			}else {
//				Main.getInstance().getTtu().endWorkSession(p.getUniqueId());
//			}
//
//		}else if(args[0].equalsIgnoreCase("list")){
//
//			int page = 1;
//			if(args.length > 1) {
//				try {
//					page = Integer.parseInt(args[1]);
//				}catch(NumberFormatException ex) {
//					sender.sendMessage("§cPlease specify a valid number.");
//					return true;
//				}
//
//			}
//
//			if(page < 1) page = 1;
//
//			final int finalPage = page;
//
//			Main.getInstance().getMySQLManager().mysql.query("SELECT * FROM TT_Projects ORDER BY TimeCreated DESC", new Consumer<ResultSet>() {
//
//				@Override
//				public void accept(ResultSet rs) {
//
//					try {
//
//
//						int size = 0;
//						while(rs.next()) {
//							size++;
//						}
//						rs.beforeFirst();
//
//
//						int pages = ((size-1)/LISTSIZE)+1;
//
//						int page = finalPage;
//						if(page > pages)page = pages;
//
//
//						for(int i = 0; i < (page-1)*LISTSIZE; i++) {
//							rs.next();
//						}
//
//						if(size == 0) {
//							sender.sendMessage("§cThere are no projects");
//						}else {
//							sender.sendMessage("§6§lList of projects");
//							for(int i = 0; i < LISTSIZE; i++) {
//								if(!rs.next())break;
//								if(rs.getBoolean("Active")) sender.sendMessage(" §7- §a" + rs.getString("Name") + " §7(ID: " + rs.getString("ID") + ")");
//								else sender.sendMessage(" §7- §c" + rs.getString("Name") + " §7(ID: " + rs.getString("ID") + ")");
//
//							}
//							sender.sendMessage("§6--- §eShowing page §6" + page + " §eof §6" + pages + " ---");
//						}
//
//
//
//
//					} catch (SQLException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//
//
//
//				}
//
//			});
//
//
//
//		}else if(args[0].equalsIgnoreCase("create") && sender.hasPermission("honeyfrost.admin")) {
//			if(args.length < 2) {
//				sender.sendMessage("§cUsage: §7/work create <name>");
//			}else {
//				String name = "";
//				for(int i = 1; i < args.length; i++) {
//					name += args[i] + " ";
//				}
//				final String finalName = name.substring(0, name.length() - 1);
//
//				Main.getInstance().getMySQLManager().mysql.insert("INSERT INTO TT_Projects (Name, Active, TimeCreated) VALUES ('"+finalName + "',TRUE,'"+System.currentTimeMillis() + "');", new Consumer<Integer>() {
//
//					@Override
//					public void accept(Integer i) {
//						sender.sendMessage("§aNew project created with name '§b" + finalName + "§a' and ID §b" + i +"§a.");
//					}
//
//				});
//			}
//
//
//		}else if(args[0].equalsIgnoreCase("toggle") && sender.hasPermission("honeyfrost.admin")) {
//			if(args.length < 2) {
//				sender.sendMessage("§cUsage: §7/work toggle <Project ID>");
//			}else {
//				int i = 0;
//				try {
//					i = Integer.parseInt(args[1]);
//				}catch(NumberFormatException ex) {
//					sender.sendMessage("§cPlease specify a valid ID.");
//					return true;
//				}
//
//				final int i2 = i;
//				Main.getInstance().getMySQLManager().mysql.query("SELECT Active,Name FROM TT_Projects WHERE ID='" + i2 +"';", new Consumer<ResultSet>() {
//
//					@Override
//					public void accept(ResultSet rs) {
//						try {
//							if(!rs.next()) {
//								sender.sendMessage("§cThere is no project with that ID.");
//							}else {
//								boolean active = rs.getBoolean("Active");
//								Main.getInstance().getMySQLManager().mysql.update("UPDATE TT_Projects SET Active=" +!active +" WHERE ID='" + i2 +"';");
//
//								if(active) {
//									sender.sendMessage("§aYou marked the project '§b" + rs.getString("Name") +"§a' with ID §b" + i2 + " §aas §cnot active");
//								}else {
//									sender.sendMessage("§aYou marked the project '§b" + rs.getString("Name") +"§a' with ID §b" + i2 + " §aas §aactive");
//								}
//
//								for(WorkSession ws : Main.getInstance().getTtu().currentSessions) {
//									if(ws.getProjectID().toString().equalsIgnoreCase(i2) {
//										Main.getInstance().getTtu().endWorkSession(ws.getUUID());
//									}
//								}
//
//								for(AfkWorkPlayer awp : Main.getInstance().getTtu().currentAFKPlayersInSession) {
//									if(awp.projectID == i2) {
//										Main.getInstance().getTtu().currentAFKPlayersInSession.remove(awp);
//									}
//								}
//
//
//							}
//						} catch (SQLException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//
//					}
//
//				});
//			}
//		}else if(args[0].equalsIgnoreCase("stats")){
//
//			OfflinePlayer target = null;
//			int projectID = -1;
//
//			if(args.length == 1) {			//	/work stats
//				if(sender instanceof Player)
//					sendGeneralStats((Player) sender, sender);
//				else sendHelpList(sender);
//			}else if(args.length == 2) {		// 		/work stats <project> OR /work stats <player>
//				try {
//					projectID = Integer.parseInt(args[1]);
//				}catch(NumberFormatException ex) {
//					if(sender.hasPermission("honeyfrost.admin")) {
//						if(Bukkit.getOfflinePlayer(args[1]) != null) {
//							target = Bukkit.getOfflinePlayer(args[1]);
//						}else {
//							sender.sendMessage("§cCould not find player '§7" + args[1] + "§c'");
//							return true;
//						}
//
//					}else {
//						sender.sendMessage("§cNot a valid number");
//						return true;
//					}
//				}
//
//				if(projectID != -1 && sender instanceof Player)
//					sendProjectStats((Player) sender, sender, projectID);
//				else if(target != null)
//					sendGeneralStats(target, sender);
//			}else if(args.length == 3 && sender.hasPermission("honeyfrost.admin")) {		// /work stats <player> <projectID>
//				if(Bukkit.getOfflinePlayer(args[1]) != null) {
//					target = Bukkit.getOfflinePlayer(args[1]);
//				}else {
//					sender.sendMessage("§cCould not find player '§7" + args[1] + "§c'");
//					return true;
//				}
//
//				try {
//					projectID = Integer.parseInt(args[2]);
//				}catch(NumberFormatException ex) {
//					sender.sendMessage("§cNot a valid number");
//					return true;
//				}
//
//				sendProjectStats(target, sender, projectID);
//
//			}else
//				sendHelpList(sender);
//		}
//		else {
//			sender.sendMessage("§cInvalid subcommand");
//		}
//
//
//
		return true;
	}
	
	public void sendGeneralStats(OfflinePlayer player, CommandSender sender) {
		Executors.newCachedThreadPool().execute(() -> {
			ResultSet rs = Main.getInstance().getMySQLManager().mysql.getMySQL().query("SELECT StartSession,EndSession FROM TT_Sessions WHERE UUID='" + player.getUniqueId().toString()+ "';");
			
			try {
				
				
				int sessionSize = 0;
				long workTime = 0;
				while(rs.next()) {
					workTime += rs.getLong("EndSession")-rs.getLong("StartSession");
					sessionSize++;
				}
				
				if(sessionSize == 0) {
					sender.sendMessage("§cPlayer has not registered any work. *sad venon noises*");
					return;
				}
				
				
				long averageSessionLength = workTime / sessionSize;
				
				
				sender.sendMessage("§8----- §e§lGeneral work statistics for §b§l" + player.getName() + " §8-----");
				sender.sendMessage(" §8- §eAmount of Sessions§8: §a" + sessionSize);
				sender.sendMessage(" §8- §eTotal Work Time§8: §a" + (int)(workTime/1000/60/60) + " §ehours, §a" + (	workTime/1000/60) % 60 + " §eminutes and §a" + (workTime/1000) % 60 + " §eseconds");
				sender.sendMessage(" §8- §eAverage session§8: §a" + (int)( averageSessionLength/1000/60/60) + " §ehours, §a" + ( averageSessionLength/1000/60) % 60 + " §eminutes and §a" + ( averageSessionLength/1000) % 60 + " §eseconds");
				
				
			} catch (SQLException e) {e.printStackTrace();}
		});
	}
	
	public void sendProjectStats(OfflinePlayer player, CommandSender sender, UUID projectID) {
//		Executors.newCachedThreadPool().execute(() -> {
//			ResultSet rs = Main.getInstance().getMySQLManager().mysql.getMySQL().query("SELECT StartSession,EndSession FROM TT_Sessions WHERE UUID='" + player.getUniqueId().toString()+ "' AND ProjectID=" + projectID + ";");
//
//			try {
//
//				if(!project.next()) {
//					sender.sendMessage("§cThere is no project with that ID");
//					return;
//				}
//				String projectName = project.getString("Name");
//
//				int sessionSize = 0;
//				long workTime = 0;
//				while(rs.next()) {
//					workTime += rs.getLong("EndSession")-rs.getLong("StartSession");
//					sessionSize++;
//				}
//
//				if(sessionSize == 0) {
//					sender.sendMessage("§cPlayer has not registered any work on that project. *sad venon noises*");
//					return;
//				}
//
//
//				long averageSessionLength = workTime / sessionSize;
//
//
//				sender.sendMessage("§8----- §6§lProject statistics for §b§l" + player.getName() + " §8-----");
//				sender.sendMessage(" §8- §6Project Name§8: §a" + projectName);
//				sender.sendMessage(" §8- §6Amount of Sessions§8: §a" + sessionSize);
//				sender.sendMessage(" §8- §6Total Work Time§8: §a" + (int)(workTime/1000/60/60) + " §6hours, §a" + (	workTime/1000/60) % 60 + " §6minutes and §a" + (workTime/1000) % 60 + " §6seconds");
//				sender.sendMessage(" §8- §6Average session§8: §a" + (int)( averageSessionLength/1000/60/60) + " §6hours, §a" + ( averageSessionLength/1000/60) % 60 + " §6minutes and §a" + ( averageSessionLength/1000) % 60 + " §6seconds");
//
//
//			} catch (SQLException e) {e.printStackTrace();}
//		});
	}

	
	public void sendHelpList(CommandSender sender) {
		sender.sendMessage("§6§lWork Time Tracker");
		sender.sendMessage(" §7- §a/work start §7- Start working on a project");
		sender.sendMessage(" §7- §a/work stop §7- Stop working");
		sender.sendMessage(" §7- §a/work stats §7- Show statistics");
		sender.sendMessage(" §7- §a/work generalstats §7- Show statistics");
		
		if(sender.hasPermission("bbytes.admin")) {
			sender.sendMessage(" ");
			sender.sendMessage("§c§lAdmin Commands:");
			sender.sendMessage(" §7- §c/work stats <player> §7- Show statistics for a player");
			sender.sendMessage(" §7- §c/work generalstats <player> §7- Show statistics for a player");
		}
		
		
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> list = new ArrayList<String>();
		if(args.length == 1) {
			list.add("start");
			list.add("stop");
			list.add("stats");
			list.add("generalstats");
		}else if(args.length == 2 && sender.hasPermission("bbytes.admin")) {
			for(Player all : Bukkit.getOnlinePlayers())
				list.add(all.getName());
		}
		List<String> returnList = new ArrayList<String>();
		for(String str : list) {
			if(str.toLowerCase().startsWith(args[args.length-1].toLowerCase()))
				returnList.add(str);
		}
		Collections.sort(returnList, String.CASE_INSENSITIVE_ORDER);
		
		return returnList;
	}

}
