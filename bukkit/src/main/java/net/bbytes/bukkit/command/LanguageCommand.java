package net.bbytes.bukkit.command;

import net.bbytes.bukkit.Main;
import net.bbytes.bukkit.user.Language;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LanguageCommand implements CommandExecutor, TabCompleter{


	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(!(sender instanceof Player)){
			sender.sendMessage("§cOnly executable as player");
			return true;
		}
//		if(!sender.hasPermission("honeyfrost.user")) {
//			sender.sendMessage(Message.NO_PERMISSION.get(sender));
//			return true;
//		}


		//prefix <user> <prefix>
		if(args.length < 1) {
			sender.sendMessage("§cUsage: §7/language <language>");

			StringBuilder langList = new StringBuilder();
			langList.append("§cAvailable Languages: §7");

			for(Language lang : Language.values()) {
				langList.append(lang.getID().toUpperCase()).append(", ");

			}
			String languageList = langList.toString().substring(0, langList.length() - 2);


			sender.sendMessage(languageList);

		}else if(Language.getLanguage(args[0]) == null) {
			sender.sendMessage("§7Invalid language: '§c" + args[0] + "§7'");
		}else {

			Main.getInstance().getUserManager().getUser((Player) sender).setLanguage(Language.getLanguage(args[0]));
			sender.sendMessage("§aYour language was set to §b" + Language.getLanguage(args[0]).name());

		}


		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> list = new ArrayList<String>();
		if(args.length == 1)
			for(Language lang : Language.values())
				list.add(lang.getID());

		List<String> returnList = new ArrayList<String>();
		for(String str : list) {
			if(str.toLowerCase().startsWith(args[args.length-1].toLowerCase()))
				returnList.add(str);
		}
		//Collections.sort(returnList, String.CASE_INSENSITIVE_ORDER);

		return returnList;
	}

}
