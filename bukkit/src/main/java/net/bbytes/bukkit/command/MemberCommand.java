package net.bbytes.bukkit.command;

import net.bbytes.bukkit.Main;
import net.bbytes.bukkit.message.Message;
import net.bbytes.bukkit.project.Project;
import net.bbytes.bukkit.user.User;
import net.bbytes.bukkit.world.ConfigurableWorld;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;


public class MemberCommand implements CommandExecutor,TabCompleter, Listener {


	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if(!(sender instanceof Player)){
			return true;
		}
		if(!sender.hasPermission("bbytes.admin")) {
			sender.sendMessage(Message.NO_PERMISSION.get(sender));
			return true;
		}

		User user = User.getUser((Player) sender);

		if(args.length == 0){
			sender.sendMessage("§3§l" + Message.ACCESS_MANAGEMENT.get(sender));
			sender.sendMessage("§8- §b/member add <player> §7- §f" + Message.ACCESS_GIVE.get(sender));
			sender.sendMessage("§8- §b/member remove <player> §7- §f"+ Message.ACCESS_REVOKE.get(sender));
		}else if(args[0].equalsIgnoreCase("add")){
			if(args.length < 2){
				sender.sendMessage("§c" + Message.USAGE.get(sender) + ": §7/member add <player>");
				sender.sendMessage("§c" + Message.ACCESS_GIVE.get(sender));
				return true;
			}
			ConfigurableWorld world = ConfigurableWorld.getWorld(((Player) sender).getWorld().getName());

			if(world == null){
				sender.sendMessage(Message.ERROR_NOT_IN_PROJECT.get(sender));
				return true;
			}

			Project project = world.getProject();
			if(project == null){
				sender.sendMessage(Message.ERROR_NOT_IN_PROJECT.get(sender));
				return true;
			}

			Main.getInstance().getUTNUtils().getUUIDFromName(args[1], (uuid) -> {
				if(uuid == null){
					sender.sendMessage(Message.ERROR_PLAYER_NOT_FOUND.get(sender));
				}else{
					project.addMember(uuid);
					sender.sendMessage(Message.ACCESS_GIVEN.getWithPrefix(sender).replace("{p}", "§b" +Main.getInstance().getUTNUtils().getNameFromUUID_Sync(uuid) + "§6").replace("{project}", "§b"+project.getName()+"§6"));
				}
			});
		}else if(args[0].equalsIgnoreCase("remove")){
			if(args.length < 2){
				sender.sendMessage("§c" + Message.USAGE.get(sender) + ": §7/member remove <player>");
				sender.sendMessage("§c" + Message.ACCESS_REVOKE.get(sender));
				return true;
			}
			ConfigurableWorld world = ConfigurableWorld.getWorld(((Player) sender).getWorld().getName());

			if(world == null){
				sender.sendMessage(Message.ERROR_NOT_IN_PROJECT.get(sender));
				return true;
			}

			Project project = world.getProject();
			if(project == null){
				sender.sendMessage(Message.ERROR_NOT_IN_PROJECT.get(sender));
				return true;
			}

			Main.getInstance().getUTNUtils().getUUIDFromName(args[1], (uuid) -> {
				if(uuid == null){
					sender.sendMessage(Message.ERROR_PLAYER_NOT_FOUND.get(sender));
				}else{
					if(project.canAccess(uuid)){
						project.removeMember(uuid);
					}
					sender.sendMessage(Message.ACCESS_REVOKED.getWithPrefix(sender).replace("{p}", "§b" +Main.getInstance().getUTNUtils().getNameFromUUID_Sync(uuid) + "§6").replace("{project}", "§b"+project.getName()+"§6"));
				}
			});


		}else{
			sender.sendMessage("§3§l" + Message.ACCESS_MANAGEMENT.get(sender));
			sender.sendMessage("§8- §b/member add <player> §7- §f" + Message.ACCESS_GIVE.get(sender));
			sender.sendMessage("§8- §b/member remove <player> §7- §f"+ Message.ACCESS_REVOKE.get(sender));
		}







		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

		List<String> list = new ArrayList<String>();
		if(sender.hasPermission("bbytes.admin"))
			if(args.length == 1) {
				list.add("give");
				list.add("revoke");
			}

		List<String> returnList = new ArrayList<String>();
		for(String str : list) {
			if(str.toLowerCase().startsWith(args[args.length-1].toLowerCase()))
				returnList.add(str);
		}
		//Collections.sort(returnList, String.CASE_INSENSITIVE_ORDER);

		return returnList;
	}

	
}
