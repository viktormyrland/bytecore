package net.bbytes.bukkit.command;

import net.bbytes.bukkit.Main;
import net.bbytes.bukkit.inventory.GUIInventory;
import net.bbytes.bukkit.project.Project;
import net.bbytes.bukkit.world.ConfigurableWorld;
import net.bbytes.bukkit.world.ConfigurableWorldType;
import net.bbytes.bukkit.world.ImportWorldInfo;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class WorldsCommand implements CommandExecutor, TabCompleter{

    String[] forceTPAliases = {
            "w", "world", "mvtp", "goto"
    };

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

//        if(!sender.hasPermission("honeyfrost.user")){
//            sender.sendMessage(Message.NO_PERMISSION.get(sender));
//            return true;
//        }

        if(!(sender instanceof Player)){
            return true;
        }



        if(args.length > 0 && Arrays.asList(forceTPAliases).contains(label.toLowerCase())){

            StringBuilder query = new StringBuilder(args[0]);
            
            for(int i = 1; i < args.length; i++){
                query.append(" ").append(args[i]);
            }
            
            
            ConfigurableWorld world = null;

            /*
                Check if query matches file name or displayname exactly
             */
            for(ConfigurableWorld w : Main.getInstance().getWorldManager().getWorldList()){
                if(w.getFileWorldName().equalsIgnoreCase(query.toString()) || w.getDisplayname().equalsIgnoreCase(query.toString())){
                    if(w.getProject() == null || w.getProject().canAccess(((Player) sender).getUniqueId())){
                        world = w;
                        break;
                    }
                }
            }

             /*
                Check if query starts with file name or displayname
             */

            if(world == null)
                for(ConfigurableWorld w : Main.getInstance().getWorldManager().getWorldList()){
                    if(w.getFileWorldName().toLowerCase().startsWith(query.toString().toLowerCase()) || w.getDisplayname().toLowerCase().startsWith(query.toString().toLowerCase())){
                        if(w.getProject() == null || w.getProject().canAccess(((Player) sender).getUniqueId())){
                            world = w;
                            break;
                        }

                    }
            }

            /*
                Check if query matches an unimported world. If true, import the world into the current project as a void world
             */


            if(world == null)
                for(File worldFile : Objects.requireNonNull(Bukkit.getWorldContainer().listFiles())){
                    if(worldFile.getName().equalsIgnoreCase(query.toString())){
                        Project p = null;
                        ConfigurableWorld try_world = ConfigurableWorld.getWorld(((Player) sender).getWorld().getName());
                        if(try_world != null) p = try_world.getProject();

                        sender.sendMessage(Main.getInstance().PREFIX + "Importing world §b" + worldFile.getName() + " §6into §b" + (p != null ? p.getName() : "Uncategorized Worlds"));
                        ImportWorldInfo importWorldInfo = new ImportWorldInfo();
                        importWorldInfo.setProject(p);
                        importWorldInfo.setConfigurableWorldType(ConfigurableWorldType.VOID);
                        importWorldInfo.setEnvironment(World.Environment.NORMAL);
                        importWorldInfo.setWorldID(worldFile.getName());
                        importWorldInfo.setDisplayname(worldFile.getName());
                        importWorldInfo.setUpload(false);
                        importWorldInfo.createWorldTeleport((Player)sender);

                        return true;
                    }
                }


            if(world == null){
                sender.sendMessage("§cError: §4Unknown world: " + query.toString());
                return true;
            }

            sender.sendMessage(Main.getInstance().PREFIX + "Teleporting to §b" + world.getDisplayname());

            world.enterWorld((Player) sender);

            return true;
        }


        GUIInventory.PROJECT_LIST.display((Player) sender, 1);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> list = new ArrayList<String>();
        if(args.length == 1 && Arrays.asList(forceTPAliases).contains(label.toLowerCase()))
            for(ConfigurableWorld world : Main.getInstance().getWorldManager().getWorldList())
                if(world.getProject() == null || world.getProject().canAccess(((Player) sender).getUniqueId()))
                    list.add(world.getDisplayname());

        List<String> returnList = new ArrayList<String>();
        for(String str : list) {
            if(str.toLowerCase().startsWith(args[args.length-1].toLowerCase()))
                returnList.add(str);
        }
        Collections.sort(returnList, String.CASE_INSENSITIVE_ORDER);

        return returnList;
    }

}
