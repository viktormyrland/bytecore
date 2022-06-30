package net.bbytes.bukkit;

import com.earth2me.essentials.spawn.EssentialsSpawn;
import de.tr7zw.changeme.nbtapi.NBTItem;
import net.bbytes.bukkit.command.*;
import net.bbytes.bukkit.inventory.HiddenStringUtils;
import net.bbytes.bukkit.inventory.InventoryChangedRunnable;
import net.bbytes.bukkit.inventory.InventoryManager;
import net.bbytes.bukkit.inventory.ItemStackUtils;
import net.bbytes.bukkit.listeners.*;
import net.bbytes.bukkit.message.Message;
import net.bbytes.bukkit.project.ProjectManager;
import net.bbytes.bukkit.redis.RedisManager;
import net.bbytes.bukkit.transfer.HFConnector;
import net.bbytes.bukkit.user.User;
import net.bbytes.bukkit.user.UserManager;
import net.bbytes.bukkit.util.*;
import net.bbytes.bukkit.util.mysql.MySQLManager;
import net.bbytes.bukkit.util.totp.TwoFactorAuthUtil;
import net.bbytes.bukkit.util.totp.TwoFactorUtils;
import net.bbytes.bukkit.version.VersionMatcher;
import net.bbytes.bukkit.warp.WarpManager;
import net.bbytes.bukkit.world.WorldManager;
import net.bbytes.bukkit.world.WorldUnloaderRunnable;
import net.bbytes.core.VersionWrapper;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class Main extends JavaPlugin {

    private static Main instance;

    private HiddenStringUtils hiddenStringUtils;
    private InventoryManager inventoryManager;
    private ItemStackUtils itemStackUtils;
    private ProjectManager projectManager;

    private BungeeMessager bungeeMessager;
    private MySQLManager mySQLManager;
    private TwoFactorUtils twoFactorUtils;
    private TwoFactorAuthUtil tfau;
    private TimeTrackerUtils ttu;
    private UTNUtils UTNUtils;
    private AssetsWorldManager assetsWorldManager;
//    private F3NFixManager f3NPerm;
    private UserManager userManager;
    private WorldManager worldManager;
    private WarpManager warpManager;
    private BackupManager backupManager;
    private RedisManager redisManager;

    private HFConnector bbConnector;

    private LuckPerms luckPerms;

    private VersionWrapper wrapper;

    public String DIR;

    public final String PREFIX = "§e[§6§lBB§e] §8§l» §6";


    public String CLIENTNAME = "";
    private int subversion = 12;

    @Override
    public void onEnable() {

        String jarPath = getClass().getProtectionDomain().getCodeSource() .getLocation().getPath();
        DIR = new File(jarPath).getParentFile().getParentFile().getAbsolutePath();

        File file = new File(getDataFolder(), "config.yml");

        if(!file.exists()) {
            try {file.createNewFile();} catch (IOException e) {e.printStackTrace();}}
        saveConfig();

        getConfig().addDefault("MySQL.Address", "localhost");
        getConfig().addDefault("MySQL.Port", 3306);
        getConfig().addDefault("MySQL.Username", "root");
        getConfig().addDefault("MySQL.Password", "pass");
        getConfig().addDefault("MySQL.Database", "Blockbytes");
        getConfig().addDefault("Redis.Host", "localhost");
        getConfig().addDefault("Redis.Port", 6379);
        getConfig().addDefault("Redis.Password", "password");
        getConfig().options().copyDefaults(true);
        saveConfig();


        instance = this;

        final String serverVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].substring(1);

        wrapper = new VersionMatcher().match(serverVersion);

        String version = serverVersion.split("_")[1];
        setSubversion(Integer.parseInt(version));
        CLIENTNAME = "BUILD"+version;

        luckPerms = LuckPermsProvider.get();

        warpManager = new WarpManager();
        itemStackUtils = new ItemStackUtils();
        inventoryManager = new InventoryManager();
        hiddenStringUtils = new HiddenStringUtils();
        projectManager = new ProjectManager();
        bungeeMessager = new BungeeMessager();
        twoFactorUtils = new TwoFactorUtils();
        tfau = new TwoFactorAuthUtil();
        ttu = new TimeTrackerUtils();
        UTNUtils = new UTNUtils(this);
        assetsWorldManager = new AssetsWorldManager();
//        f3NPerm = new F3NFixManager();
        userManager = new UserManager();
        worldManager = new WorldManager();
        bbConnector = new HFConnector();
        backupManager = new BackupManager();
        redisManager = new RedisManager();

        redisManager.connectToRedis();
        redisManager.registerMessageReceiver(redisManager);
        redisManager.registerMessageReceiver(new WhitelistCommand());
        redisManager.subscribeToChannel("TRANSFER_SUCCESSFUL", "PLAYERMSG", "UPDATE_PROJECTS", "WHITELIST");

        registerCommand("worlds", WorldsCommand.class);
        registerCommand("reloadbb", ReloadBlockbytesCommand.class);
        registerCommand("prefix", PrefixCommand.class);
        registerCommand("/upload", UploadCommand.class);
        registerCommand("whitelist", WhitelistCommand.class);
//        registerCommand("rank", RankCommand.class);
//        registerCommand("language", LanguageCommand.class);
        registerCommand("2fa", TwoFactorCommand.class);
        registerCommand("settings", SettingsCommand.class);
        registerCommand("project", ProjectCommand.class);
//        registerCommand("exportyml", ExportYml.class);
        registerCommand("warp", WarpCommand.class);
        registerCommand("warps", WarpsCommand.class);
        registerCommand("setwarp", SetWarpCommand.class);
        registerCommand("delwarp", DelWarpCommand.class);
        registerCommand("member", MemberCommand.class);
        registerCommand("world", WorldCommand.class);
        registerCommand("info", InfoCommand.class);
//        registerCommand("reconnect", ReconnectCommand.class);
//        registerCommand("gamerule", GameruleCommand.class);
        registerCommand("maintenance", MaintenanceCommand.class);
//        registerCommand("work", WorkCommand.class);
        registerCommand("forcesave", ForceSaveCommand.class);

        registerEvents(new Class<?>[]{
                InventoryListener.class,
                CommandBlockOpListener.class,
                CommandListener.class,
                JoinListener.class,
                QuitListener.class,
                PlayerChatListener.class,
                PlayerMoveListener.class,
                WorldPropertyListener.class,
                TeleportListener.class
        });



        mySQLManager = new MySQLManager();
        mySQLManager.connectToMySQL();
        mySQLManager.setupDefaultTables();

        PrefixCommand.updateCache();




        projectManager.loadProjects();
        worldManager.loadWorlds();
        warpManager.loadWarps();

        WhitelistCommand.loadMemoryWhitelist();

        for(Player all : Bukkit.getOnlinePlayers())
            userManager.addUser(all);



        getUTNUtils().cacheList();
        try {
            Message.loadLanguages();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new InventoryChangedRunnable(), 10, 5);
        int WORLD_UNLOAD_INTERVAL_MINUTES = 20;
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new WorldUnloaderRunnable(), WORLD_UNLOAD_INTERVAL_MINUTES *60*20, WORLD_UNLOAD_INTERVAL_MINUTES *60*20);

        Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), () -> {
            Main.getInstance().getWorldManager().saveWorlds();
            Main.getInstance().getWorldManager().getRecycleBin().saveRecycleBin();
            Main.getInstance().getWarpManager().saveWarps();
        }, 3600, 3600);
//
//        ExecutorService ex = Executors.newCachedThreadPool();
//        ex.execute(() -> {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            bbConnector.connect();
//
//
//        });
//        ex.shutdown();

        queueAutoRestart();

        (new BukkitRunnable(){

            @Override
            public void run() {
                backupManager.backup();
            }
        }).runTaskLaterAsynchronously(Main.getInstance(), 10 * 20);

    }

    @Override
    public void onDisable() {
//        bbConnector.disconnect();
        worldManager.saveWorlds();
        worldManager.getRecycleBin().saveRecycleBin();
        warpManager.saveWarps();
        redisManager.disconnectFromRedis();

        for(Player all : Bukkit.getOnlinePlayers()){
            User u = User.getUser(all);
            if(u != null) {
                try {
                    u.saveUser_Sync();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

    }

    public void registerCommand(String name, Class<?> cmd_class) {
        Object cmd;
        try {
            cmd = cmd_class.newInstance();

            this.getCommand(name).setExecutor((CommandExecutor) cmd);
            if(cmd instanceof TabCompleter)
                this.getCommand(name).setTabCompleter((TabCompleter) cmd);

        } catch (InstantiationException | IllegalAccessException e) {
            System.out.println("Could not register command '" + name + "'");}
    }

    public void registerEvents(Class<?>[] classes){
        for(Class<?> c : classes){
            try {
                Bukkit.getPluginManager().registerEvents((Listener) c.newInstance(), this);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }

    public void queueAutoRestart(){

        (new BukkitRunnable(){

            @Override
            public void run() {

                try {
                    sendRestartTime(TimeUnit.HOURS.toMillis(1));

                    Thread.sleep(TimeUnit.MINUTES.toMillis(30));
                    sendRestartTime(TimeUnit.MINUTES.toMillis(30));

                    Thread.sleep(TimeUnit.MINUTES.toMillis(20));
                    sendRestartTime(TimeUnit.MINUTES.toMillis(10));

                    Thread.sleep(TimeUnit.MINUTES.toMillis(5));
                    sendRestartTime(TimeUnit.MINUTES.toMillis(5));

                    Thread.sleep(TimeUnit.MINUTES.toMillis(4));
                    sendRestartTime(TimeUnit.MINUTES.toMillis(1));

                    Thread.sleep(TimeUnit.SECONDS.toMillis(30));
                    sendRestartTime(TimeUnit.SECONDS.toMillis(30));

                    Thread.sleep(TimeUnit.SECONDS.toMillis(20));
                    sendRestartTime(TimeUnit.SECONDS.toMillis(10));

                    Thread.sleep(TimeUnit.SECONDS.toMillis(10));

                    Bukkit.getScheduler().callSyncMethod(Main.getInstance(), () -> {
                        Bukkit.getServer().shutdown();
                        return null;
                    });



                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).runTaskLaterAsynchronously(Main.getInstance(), TimeUnit.HOURS.toSeconds(23) * 20);
    }

    private void sendRestartTime(long millis){
        if(millis >= 3600000){
            for(Player all : Bukkit.getOnlinePlayers())
                all.sendMessage(Message.RESTART_TIME_HOUR.getWithPrefix(all).replace("{time}", ""+TimeUnit.HOURS.convert(millis, TimeUnit.MILLISECONDS)));
            Bukkit.getLogger().info(Message.RESTART_TIME_HOUR.getRaw().replace("{time}", ""+TimeUnit.HOURS.convert(millis, TimeUnit.MILLISECONDS)));
        }else if(millis >= 60000){
            for(Player all : Bukkit.getOnlinePlayers())
                all.sendMessage(Message.RESTART_TIME_MINUTE.getWithPrefix(all).replace("{time}", ""+TimeUnit.MINUTES.convert(millis, TimeUnit.MILLISECONDS)));
            Bukkit.getLogger().info(Message.RESTART_TIME_MINUTE.getRaw().replace("{time}", ""+TimeUnit.MINUTES.convert(millis, TimeUnit.MILLISECONDS)));
        }else{
            for(Player all : Bukkit.getOnlinePlayers())
                all.sendMessage(Message.RESTART_TIME_SECOND.getWithPrefix(all).replace("{time}", ""+TimeUnit.SECONDS.convert(millis, TimeUnit.MILLISECONDS)));
            Bukkit.getLogger().info(Message.RESTART_TIME_SECOND.getRaw().replace("{time}", ""+TimeUnit.SECONDS.convert(millis, TimeUnit.MILLISECONDS)));

        }
    }

    public Location getSpawnLocation(){
        if(Bukkit.getPluginManager().getPlugin("EssentialsSpawn") != null)
            return ((EssentialsSpawn)Bukkit.getPluginManager().getPlugin("EssentialsSpawn")).getSpawn("default");
        return Bukkit.getWorlds().get(0).getSpawnLocation();
    }

    public static Main getInstance() {
        return instance;
    }

    public HiddenStringUtils getHiddenStringUtils() {
        return hiddenStringUtils;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public ItemStackUtils getItemStackUtils() {
        return itemStackUtils;
    }

    public ProjectManager getProjectManager() {
        return projectManager;
    }

    public LuckPerms getLuckPerms() {
        return luckPerms;
    }

    public BungeeMessager getBungeeMessager() {
        return bungeeMessager;
    }

    public MySQLManager getMySQLManager() {
        return mySQLManager;
    }

    public TwoFactorUtils getTwoFactorUtils() {
        return twoFactorUtils;
    }

    public TwoFactorAuthUtil getTfau() {
        return tfau;
    }

    public TimeTrackerUtils getTtu() {
        return ttu;
    }

    public net.bbytes.bukkit.util.UTNUtils getUTNUtils() {
        return UTNUtils;
    }


    public AssetsWorldManager getAssetsWorldManager() {
        return assetsWorldManager;
    }

//    public F3NFixManager getF3NPerm() {
//        return f3NPerm;
//    }

    public UserManager getUserManager() {
        return userManager;
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }

    public WarpManager getWarpManager() {
        return warpManager;
    }

    public HFConnector getBbConnector() {
        return bbConnector;
    }

    public VersionWrapper getWrapper() {
        return wrapper;
    }

    public int getSubversion() {
        return subversion;
    }

    public void setSubversion(int subversion) {
        this.subversion = subversion;
    }

    public BackupManager getBackupManager() {
        return backupManager;
    }

    public RedisManager getRedisManager() {
        return redisManager;
    }
}
