package net.bbytes.bukkit.command;

import net.bbytes.bukkit.Main;
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


public class WarpsCommand implements CommandExecutor,TabCompleter, Listener {

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

		int page = 1;
		if(args.length > 0){
			try{
				page = Integer.parseInt(args[0]);
			}catch(NumberFormatException e){
				page = 1;
			}
		}

		String prefix = "§6" + Message.WARPS_CHAT.get(sender)
				.replace("{amount}", "§c{amount}§6")
				.replace("{page}", "§c{page}§6")
				.replace("{pages}", "§c{pages}§6");

		StringBuilder query = new StringBuilder();

		List<Warp> warpList = new ArrayList<>();
		for(Warp warp : Main.getInstance().getWarpManager().getWarpList()){
			if(warp.canAccess(player))warpList.add(warp);
		}

		int pages = ((warpList.size()-1)/PAGE_SIZE)+1;

		if(page > pages) page = pages;
		else if(page < 1) page = 1;


		for(int i = PAGE_SIZE * (page-1); i < (PAGE_SIZE*(page-1))+PAGE_SIZE; i++) {
			if(i >= warpList.size())break;
			query.append(warpList.get(i).getName()).append(", ");
		}



		if(query.length() == 0){
			player.sendMessage(Message.ERROR_NO_WARPS.get(sender));
		}else
			player.sendMessage(prefix.replace("{amount}", warpList.size() + "")
					.replace("{page}", page+"")
					.replace("{pages}", pages+ ""));
			player.sendMessage(query.substring(0, query.length()-2));

		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

		
		return new ArrayList<String>();
	}

	
}
