package net.bbytes.bukkit.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;


public class ReloadBlockbytesCommand implements CommandExecutor,TabCompleter{


	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
//		RankCommand.updateGroups();
		PrefixCommand.updateCache();
		
		sender.sendMessage("§aThe Blockbytes plugin was reloaded");
			
		
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

		
		return new ArrayList<String>();
	}
	

	
	
	
}
