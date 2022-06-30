package net.bbytes.bukkit.command;

import net.bbytes.bukkit.inventory.GUIInventory;
import net.bbytes.bukkit.message.Message;
import net.bbytes.bukkit.warp.Warp;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;


public class WarpCommand implements CommandExecutor,TabCompleter, Listener {

	private final int PAGE_SIZE = 20;

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {


//		if(!sender.hasPermission("honeyfrost.user")){
//			sender.sendMessage(Message.NO_PERMISSION.get(sender));
//			return true;
//		}

		if(!(sender instanceof Player)){
			sender.sendMessage("§cOnly executable by player");
			return true;
		}

		Player player = (Player) sender;
		if(args.length == 0){

			GUIInventory.WARP_PROJECTS.display(player, 1);
		}else{
			StringBuilder query = new StringBuilder(args[0]);
			for(int i = 1; i < args.length; i++)
				query.append(" ").append(args[i]);

			Warp warp = Warp.getWarp(query.toString());

			if(warp == null){
				player.sendMessage(Message.ERROR_WARP_NO_EXIST.get(sender));
				return true;
			}

			warp.goTo(player);


		}

		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

		
		return new ArrayList<String>();
	}

	
}
