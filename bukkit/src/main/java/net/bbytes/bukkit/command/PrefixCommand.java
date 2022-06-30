package net.bbytes.bukkit.command;

import net.bbytes.bukkit.Main;
import net.bbytes.bukkit.message.Message;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.MetaNode;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class PrefixCommand implements CommandExecutor, TabCompleter{

	private static Map<String, String> cachedPrefixes = new HashMap<>();

	public static void updateCache(){
		Main.getInstance().getMySQLManager().mysql.query("SELECT * FROM Prefixes;", (rs) -> {
			cachedPrefixes.clear();
			while(true){
				try {
					if (!rs.next()) break;

					cachedPrefixes.put(rs.getString("ID").toUpperCase(), rs.getString("Prefix"));


				} catch (SQLException throwables) {
					throwables.printStackTrace();
				}

			}
		});
	}

	public static String getPrefix(String prefix) {
		return cachedPrefixes.getOrDefault(prefix, prefix);
	}


	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(sender instanceof Player)
			if(!sender.hasPermission("blockbytes.prefix.set")) {
				sender.sendMessage(Message.NO_PERMISSION.get(sender));
				return true;
			}

		//prefix <user> <prefix>
		if(args.length < 2) {
			sender.sendMessage("§cUsage: §7/prefix <user> <prefix | clear>");
			StringBuilder prefixList = new StringBuilder("§cPrefixes: §7");
			for(String key : cachedPrefixes.keySet())
				prefixList.append(key).append(", ");


			sender.sendMessage(prefixList.substring(0, prefixList.toString().length()-2));

			
		}else if(Bukkit.getOfflinePlayer(args[0]) == null) {
			sender.sendMessage("§7Could not find player '§c" + args[0] + "§7'");
		}else if(args[1].equalsIgnoreCase("clear")){

			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);

			LuckPermsProvider.get().getUserManager().loadUser(offlinePlayer.getUniqueId());

			CompletableFuture<User> userFuture = LuckPermsProvider.get().getUserManager().loadUser(offlinePlayer.getUniqueId());

			userFuture.thenAcceptAsync(user -> {
//				user.data().clear(
//						MetaNode.builder().key("prefix").build().getContexts()
//				);

				user.data().clear(node -> {
					return node.getKey().startsWith("meta.prefix.");
				});

				LuckPermsProvider.get().getUserManager().saveUser(user);
			});

			sender.sendMessage("§b" + offlinePlayer.getName() + "'s §aprefix has been cleared");
			if(offlinePlayer.isOnline() && offlinePlayer != sender) offlinePlayer.getPlayer().sendMessage("§aYour prefix was cleared");


		}else{
			if(!cachedPrefixes.containsKey(args[1].toUpperCase())){
				sender.sendMessage("§7Invalid prefix: '§c" + args[1] + "§7'");
				return true;
			}

			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);

			LuckPermsProvider.get().getUserManager().loadUser(offlinePlayer.getUniqueId());

			CompletableFuture<User> userFuture = LuckPermsProvider.get().getUserManager().loadUser(offlinePlayer.getUniqueId());

			userFuture.thenAcceptAsync(user -> {
				user.data().clear(node -> node.getKey().startsWith("meta.prefix."));
				user.data().add(MetaNode.builder("prefix", args[1].toUpperCase()).build());
				LuckPermsProvider.get().getUserManager().saveUser(user);
			});

			sender.sendMessage("§b" + offlinePlayer.getName() + " §awas given the prefix §7" + getPrefix(args[1].toUpperCase()).replaceAll("&", "§"));
			if(offlinePlayer.isOnline() && offlinePlayer != sender) offlinePlayer.getPlayer().sendMessage("§aYou were given the prefix §7"+ getPrefix(args[1].toUpperCase()).replaceAll("&", "§"));






		}

		
		
		return true;
	}


	 @Override
		public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
			List<String> list = new ArrayList<String>();
			if(sender.hasPermission("blockbytes.prefix.set")) {
				if(args.length == 1)
					for(Player all : Bukkit.getOnlinePlayers())
						list.add(all.getName());
				else if(args.length == 2){
					for(String key : cachedPrefixes.keySet())
						list.add(key.toLowerCase());
					list.add("clear");
				}

				
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
