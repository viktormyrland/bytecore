package net.bbytes.bukkit.command;

import net.bbytes.bukkit.Main;
import net.bbytes.bukkit.inventory.GUIInventory;
import net.bbytes.bukkit.message.Message;
import net.bbytes.bukkit.project.Project;
import net.bbytes.bukkit.user.User;
import net.bbytes.bukkit.util.BookUtil;
import net.bbytes.bukkit.world.ConfigurableWorld;
import net.bbytes.bukkit.world.ImportWorldInfo;
import net.bbytes.bukkit.world.WorldUploader;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class WorldCommand implements CommandExecutor, TabCompleter{

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

        if(args.length == 0){
            sender.sendMessage("§3§lBlockbytes Worlds");
            sender.sendMessage("§8- §b/world download §8- §7" + Message.WORLD_DOWNLOAD.get(sender));
            sender.sendMessage("§8- §b/world upload §8- §7" + Message.WORLD_UPLOAD.get(sender));
        }else if(args[0].equalsIgnoreCase("download")){
            ConfigurableWorld world = ConfigurableWorld.getWorld(((Player) sender).getWorld().getName());
            if(world == null){
                sender.sendMessage(Message.ERROR_NONSUPPORT.get(sender));
                return true;
            }
            world.downloadWorld((Player) sender);
        }else if(args[0].equalsIgnoreCase("upload")){




            if(args.length > 1) if(args[1].startsWith("id:")){
                ExecutorService ex = Executors.newCachedThreadPool();
                ex.execute(() -> {


                    AtomicBoolean isLoaded = new AtomicBoolean(false);
                    ExecutorService ex2 = Executors.newCachedThreadPool();
                    ex2.execute(() -> {
                        int i = 1;
                        long start = System.currentTimeMillis();
                        while(!isLoaded.get()){
                            String msg = "§6Loading" + new String(new char[i]).replace("\0", ".");
                            ((Player) sender).sendTitle(msg, "", 0, 8, 0);
                            try {
                                Thread.sleep(180);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }

                            i++;
                            if(i > 3) i = 1;
                            if(System.currentTimeMillis() - start > 5000) sender.sendMessage("§cError: §4Couldn't fetch world. Please try again.");
                        }
                    });
                    ex2.shutdown();

                    WorldUploader uploader = new WorldUploader(args[1].replace("id:", ""));
                    WorldUploader.Response response = uploader.downloadToTemp();

                    isLoaded.set(true);

                    if(response == WorldUploader.Response.INVALID_ID){
                        sender.sendMessage(Message.ERROR_INVALID_ID.get(sender));
                        return;
                    }else if(response == WorldUploader.Response.INVALID_WORLD){
                        sender.sendMessage(Message.ERROR_INVALID_WORLD.get(sender));
                        return;
                    }else if(response == WorldUploader.Response.SUCCESS){

                        Project project = User.getUser(((Player) sender)).getProjectToUpload();
                        ImportWorldInfo info = new ImportWorldInfo();
                        info.setProject(project);
                        info.setDisplayname(Main.getInstance().getWorldManager().generateName(project, "Uploaded world"));
                        info.setWorldID(uploader.getWorld());
                        info.setUpload(true);
                        GUIInventory.IMPORT_WORLD.display(((Player) sender), info);

                    }



                });
                ex.shutdown();

                return true;
            }

            Project project = null;
            ConfigurableWorld world = ConfigurableWorld.getWorld(((Player) sender).getWorld().getName());
            if(world != null)if(world.getProject() != null) project = world.getProject();

            BookUtil.openUploadBook((Player) sender);
            //sender.sendMessage(Main.getInstance().PREFIX + "Upload a world through this link: §bhttps://upload.honeyfrost.net");
            User.getUser(((Player) sender)).setProjectToUpload(project);


        }else{
            sender.sendMessage("§3§lBlockbytes Worlds");
            sender.sendMessage("§8- §b/world download §8- §7Download the current world.");
            sender.sendMessage("§8- §b/world upload §8- §7Upload a world from your PC.");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return null;
    }
}
