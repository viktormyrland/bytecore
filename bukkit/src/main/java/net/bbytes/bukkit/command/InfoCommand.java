package net.bbytes.bukkit.command;

import net.bbytes.bukkit.message.Message;
import net.bbytes.bukkit.project.Project;
import net.bbytes.bukkit.user.User;
import net.bbytes.bukkit.world.ConfigurableWorld;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;


public class InfoCommand implements CommandExecutor,TabCompleter, Listener {


	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if(!(sender instanceof Player)){
			return true;
		}
//		if(!sender.hasPermission("honeyfrost.user")) {
//			sender.sendMessage(Message.NO_PERMISSION.get(sender));
//			return true;
//		}

		Player player = ((Player) sender);
		User user = User.getUser(player);
		World world = player.getWorld();
		ConfigurableWorld configurableWorld = ConfigurableWorld.getWorld(world.getName());
		Project project = (configurableWorld != null ? configurableWorld.getProject() : null);

		sender.sendMessage("§8§m-----§r §3§l" + Message.WORD_INFORMATION.get(sender) + " §8§m-----");
		sender.sendMessage("§8§l» §7" + Message.WORD_WORLD.get(sender) + ": §b" + (configurableWorld != null ? configurableWorld.getDisplayname() + " §8" + configurableWorld.getFileWorldName(): world.getName()));
		sender.sendMessage("§8§l» §7" + Message.WORD_PROJECT.get(sender) + ": §b" + (project != null ? project.getName() : Message.WORD_NONE.get(sender)));
		sender.sendMessage(Message.FORMAT_DIVIDER.getRaw());







		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

		
		return new ArrayList<String>();
	}

	
}
