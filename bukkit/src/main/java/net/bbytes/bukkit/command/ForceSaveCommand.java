package net.bbytes.bukkit.command;

import net.bbytes.bukkit.Main;
import net.bbytes.bukkit.message.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class ForceSaveCommand implements CommandExecutor, TabCompleter{

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(!sender.hasPermission("bbytes.admin")){
            sender.sendMessage(Message.NO_PERMISSION.get(sender));
            return true;
        }


        Main.getInstance().getWorldManager().saveWorlds();
        Main.getInstance().getWorldManager().getRecycleBin().saveRecycleBin();
        Main.getInstance().getWarpManager().saveWarps();
        sender.sendMessage("§aSaving worlds and warps...");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

        return new ArrayList<String>();
    }

}
