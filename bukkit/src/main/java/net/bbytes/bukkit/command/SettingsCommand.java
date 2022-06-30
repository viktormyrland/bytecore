package net.bbytes.bukkit.command;

import net.bbytes.bukkit.Main;
import net.bbytes.bukkit.inventory.GUIInventory;
import net.bbytes.bukkit.message.Message;
import net.bbytes.bukkit.world.ConfigurableWorld;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class SettingsCommand implements CommandExecutor, TabCompleter{

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

//        if(!sender.hasPermission("honeyfrost.user")){
//            sender.sendMessage(Message.NO_PERMISSION.get(sender));
//            return true;
//        }

        if(!(sender instanceof Player)){
            sender.sendMessage("§cOnly executable by player");
            return true;
        }

        ConfigurableWorld configurableWorld = Main.getInstance().getWorldManager().getWorld(((Player) sender).getWorld());

        if(configurableWorld == null){
            sender.sendMessage(Message.ERROR_NO_SETTINGS.get(sender));
            return true;
        }

        GUIInventory.WORLD_EDIT.display(((Player) sender), configurableWorld.getFileWorldName());

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return null;
    }
}
