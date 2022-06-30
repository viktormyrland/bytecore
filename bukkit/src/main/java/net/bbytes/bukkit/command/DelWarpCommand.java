package net.bbytes.bukkit.command;

import net.bbytes.bukkit.Main;
import net.bbytes.bukkit.message.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;


public class DelWarpCommand implements CommandExecutor,TabCompleter, Listener {


	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {


		if(!sender.hasPermission("blockbytes.warp.manage")){
			sender.sendMessage(Message.NO_PERMISSION.get(sender));
			return true;
		}

		if(!(sender instanceof Player)){
			sender.sendMessage("§cOnly executable by player");
			return true;
		}

		Player player = (Player) sender;

		if(args.length == 0){
			sender.sendMessage("§" + Message.USAGE.get(sender) + ": §7/delwarp <name>");
			return true;
		}

		StringBuilder query = new StringBuilder(args[0]);
		for(int i = 1; i < args.length; i++)
			query.append(" ").append(args[i]);

		Main.getInstance().getWarpManager().deleteWarp(player, query.toString());

		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

		
		return new ArrayList<String>();
	}

	
}
