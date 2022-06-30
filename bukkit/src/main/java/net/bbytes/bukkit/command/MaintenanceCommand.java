package net.bbytes.bukkit.command;

import net.bbytes.bukkit.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;


public class MaintenanceCommand implements CommandExecutor,TabCompleter{

	public static boolean maintenanceEnabled = false;


	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if(!sender.hasPermission("bbytes.admin")) {
			sender.sendMessage(Message.NO_PERMISSION.get(sender));
			return true;
		}

		maintenanceEnabled = !maintenanceEnabled;

		if(maintenanceEnabled){
			Bukkit.broadcastMessage("§cServer maintenance enabled");
		}else{
			Bukkit.broadcastMessage("§cServer maintenance enabled");
		}

		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

		return new ArrayList<>();
	}

	
}
