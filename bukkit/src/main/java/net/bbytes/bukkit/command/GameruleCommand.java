package net.bbytes.bukkit.command;

import net.bbytes.bukkit.message.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;


public class GameruleCommand implements CommandExecutor,TabCompleter, Listener {


	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if(!(sender instanceof Player)){
			return true;
		}
		sender.sendMessage(Message.GAMERULE_INFO.get(sender));
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

		
		return new ArrayList<String>();
	}

	
}
