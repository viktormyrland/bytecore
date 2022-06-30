package net.bbytes.bukkit.world;

import com.earth2me.essentials.Essentials;
import net.bbytes.bukkit.Main;
import net.bbytes.bukkit.message.Message;
import net.bbytes.bukkit.project.Project;
import net.bbytes.bukkit.user.User;
import net.bbytes.bukkit.util.VoidGenerator;
import net.bbytes.bukkit.warp.Vector5;
import net.bbytes.bukkit.warp.Warp;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * The HoneyfrostWorld class is a class that contains the bukkit world, options for the world
 * and everything else it might need.
 */
public class ConfigurableWorld {


    private String fileWorldName;
    private WorldProperties worldProperties = new WorldProperties(this);
    private World loadedWorld = null;
    private ItemStack displayItem = new ItemStack(Material.OAK_LOG);
    private String displayname = "Unnamed World";
    private UUID projectID = null;

    public ConfigurableWorld(){
        this.fileWorldName = generateRandomName();
    }

    public ConfigurableWorld(String fileWorldName){
        this.fileWorldName = fileWorldName;
        this.displayname = fileWorldName;
    }

    /**
     * A constructor taking the String, Object map from worlds.yml
     * @param map The config map containing world
     */
    public ConfigurableWorld(Map<String, Object> map, String fileWorldName){
        //this.fileWorldName = (String) map.get("fileWorldName");
        if(fileWorldName == null) this.fileWorldName = generateRandomName();
        else this.fileWorldName = fileWorldName;
        this.displayItem = Main.getInstance().getItemStackUtils().deserializeItemStack((String) map.get("displayItem"));
        this.displayname = (String ) map.get("displayname");
        if(map.containsKey("projectID")) this.projectID = UUID.fromString((String) map.get("projectID"));
        this.worldProperties = new WorldProperties((Map<String, Object>) map.get("properties"), this);
    }

    /**
     *
     * @return This object, serialized in a String, Object map
     */
    public Map<String, Object> serialize(){
        Map<String, Object> map = new HashMap<>();

        //map.put("fileWorldName", this.fileWorldName);
        map.put("displayItem", Main.getInstance().getItemStackUtils().serializeItemStack(this.displayItem));
        map.put("displayname", this.displayname);
        if(projectID != null) map.put("projectID", this.projectID.toString());
        map.put("properties", this.worldProperties.serialize());

        return map;
    }

    public static String generateRandomName(){
        String uuid = UUID.randomUUID().toString();
        String str = uuid.substring(0, uuid.indexOf("-", uuid.indexOf("-") + 1)).substring(0,10).replace("-", "");
        String[] listFiles = Bukkit.getWorldContainer().list();
        if(listFiles != null){
            if(Arrays.asList(listFiles).contains(str))
                return generateRandomName();
        }
        return str;
    }

    /**
     *  Load the world
     *
     * @return Whether it could load the bukkit world or not.
     */
    public boolean doLoad(){
        try{
            WorldCreator creator = new WorldCreator(this.fileWorldName);
            creator.environment(
                    worldProperties.getEnvironment()).
                    seed(worldProperties.getSeed());

            if(worldProperties.getConfigurableWorldType() == ConfigurableWorldType.VOID){
                creator.generator(new VoidGenerator());
            }else if(worldProperties.getConfigurableWorldType() == ConfigurableWorldType.FLAT){
                creator.type(WorldType.FLAT);
            }

            System.out.println("[WorldLoader] Loading " + this.fileWorldName + "...");
            setLoadedWorld(creator.createWorld());

            getLoadedWorld().setGameRuleValue("doDaylightCycle", Boolean.toString(!worldProperties.isTimeLock()));
            getLoadedWorld().setGameRuleValue("doFireTick", Boolean.toString(worldProperties.isFireSpread()));
            getLoadedWorld().setGameRuleValue("mobGriefing", Boolean.toString(worldProperties.isMobGriefing()));


        }catch(Exception e){
            e.printStackTrace();
            return false;
        }


        return true;
    }

    public void doUnload(){
        Bukkit.getScheduler().callSyncMethod(Main.getInstance(), () -> {
            Main.getInstance().getServer().unloadWorld(getLoadedWorld(), true);
            return null;
        });

        this.setLoadedWorld(null);
    }

    public void changeProject(Project project){
        this.projectID = UUID.fromString(project.getUUID().toString());
        Main.getInstance().getWorldManager().getWorldList().removeIf(world -> world.getFileWorldName().equals(this.fileWorldName));
        Main.getInstance().getWorldManager().getWorldList().add(this);
    }

    public void unassignFromProject() {
        this.projectID = null;
        Main.getInstance().getWorldManager().getWorldList().removeIf(world -> world.getFileWorldName().equals(this.fileWorldName));
        Main.getInstance().getWorldManager().getWorldList().add(this);
    }

    public void enterWorld(Player player){
        enterLocation(player, null);
    }

    public void enterLocation(Player player, Vector5 location){
        if(getProject() != null) if(!getProject().canAccess(player.getUniqueId())){
            player.sendMessage(Message.NO_ACCESS_WORLD.get(player));
            player.sendTitle(Message.NO_ACCESS.get(player), "", 4, 40, 4);
            player.closeInventory();
            return;
        }



        player.closeInventory();
        if(!isWorldLoaded()){
            ExecutorService ex = Executors.newCachedThreadPool();
            ex.execute(() -> {

                AtomicBoolean isLoaded = new AtomicBoolean(false);
                AtomicBoolean failed = new AtomicBoolean(false);

                // Define LOADING or GENERATING text
                String text = Message.INFO_LOADING.get(player);
                if(!new File(Bukkit.getWorldContainer() + "/" + getFileWorldName()).isDirectory()) {
                    text = Message.INFO_GENERATING.get(player);
                }

                /*
                    Run the text as long as isLoaded is false
                 */
                ExecutorService ex2 = Executors.newCachedThreadPool();
                String finalText = text;
                ex2.execute(() -> {
                    int i = 1;

                    while(!isLoaded.get()){
                        String msg = "§6" + finalText + new String(new char[i]).replace("\0", ".");
                        player.sendTitle(msg, "", 0, 40, 0);

                        try {
                            Thread.sleep(180);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        i++;
                        if(i > 3) i = 1;
                    }
                });
                ex2.shutdown(); // Mark it as shutdown once it's done


                /*
                    Load the world synchronously. Once it is finished, set isLoaded to true
                 */
                Bukkit.getScheduler().callSyncMethod(Main.getInstance(), () -> {
                    if(!doLoad()) {
                        player.sendMessage(Message.ERROR_LOAD_FAIL.get(player));
                        failed.set(true);
                        isLoaded.set(true);
                    }
                    // In 1.12, the loading is sync. Once doLoad() is finished, the world is loaded
//                    if(Main.getInstance().CLIENTNAME.equals("BUILD12"))
//                        isLoaded.set(true);

                    return null;
                });

                long start = System.currentTimeMillis();
                ExecutorService executorService = Executors.newCachedThreadPool();
                executorService.execute(() -> {
                    /*
                     * On 1.15+, the world loading is asynchronous.
                     * This will listen for console output and mark it as loaded once it really is loaded
                     */
                    WorldLoadListener listener = null;
                    if(Main.getInstance().getSubversion() > 15 ){
                        listener = new WorldLoadListener(this.fileWorldName, isLoaded);
                        Bukkit.getPluginManager().registerEvents(listener, Main.getInstance());

                    }
                    while(true){
                        if(failed.get())break;
                        if(isLoaded.get()){
                            if(listener != null)WorldLoadEvent.getHandlerList().unregister(listener);
                            player.sendTitle("", "", 0, 1, 0);

                            if(Main.getInstance().getSubversion() > 15 ) {
                                while(listener != null && !listener.atomicBoolean.get())
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                            }

                            /*
                                Teleport the player to the destination.
                             */
                            Bukkit.getScheduler().callSyncMethod(Main.getInstance(), () -> {
                                Essentials ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
                                ess.getUser(player).setLastLocation(player.getLocation());
                                if(location == null) player.teleport(getLoadedWorld().getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                                else player.teleport(location.generateLocation(getLoadedWorld()), PlayerTeleportEvent.TeleportCause.PLUGIN);

                                if(getProject() != null) if(!getProject().getMemberList().contains(player.getUniqueId())){
                                    Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                                            player.sendMessage(Main.getInstance().PREFIX + "§7You are not a member of '§c" + getProject().getName() + "§7'. If this is an error, please contact an admin to fix this.");
                                        }, 10);
                                }


                                return null;
                            });

                            User.getUser(player).logWorld(this);
                            break;
                        }

                        if(System.currentTimeMillis() - start > 7000) {
                            isLoaded.set(true);
                            break;
                        }

                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                executorService.shutdown();



            });
        }else{
            Essentials ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
            ess.getUser(player).setLastLocation(player.getLocation());
            if(location == null) player.teleport(getLoadedWorld().getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
            else player.teleport(location.generateLocation(getLoadedWorld()), PlayerTeleportEvent.TeleportCause.PLUGIN);
        }
    }

    public void downloadWorld(Player player){
        player.sendMessage(Main.getInstance().PREFIX + "Packing world...");
        if(getLoadedWorld() != null) getLoadedWorld().save();
        ExecutorService ex = Executors.newCachedThreadPool();
        ex.execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // From start to second hyphen
//            UUID.randomUUID().toString().replaceAll("-", "").substring(0, 21);
            String id;
            id = Long.toHexString(new Random().nextLong());

            File destination = new File(Main.getInstance().getDataFolder() + "/downloads/" + id + "/download.zip");
            destination.mkdirs();
            if(destination.exists())destination.delete();

            File regionFolder = new File(Bukkit.getWorldContainer() + "/" + fileWorldName + "/region");
            File levelDatFile = new File(Bukkit.getWorldContainer() + "/" + fileWorldName + "/level.dat");
            try {
                FileOutputStream fos = new FileOutputStream(destination);
                ZipOutputStream zipOut = new ZipOutputStream(fos);
                zipFile(regionFolder, displayname + "/region", zipOut);
                zipFile(levelDatFile, displayname + "/level.dat", zipOut);
                zipOut.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            player.sendMessage(Message.WORLD_DOWNLOAD_FILE.getWithPrefix(player) + ": §a§nhttps://b" + Main.getInstance().getSubversion() + "d.bbytes.net/" + id);
        });
        ex.shutdown();

    }

    public void unloadWorld(){
        World world = getLoadedWorld();
        if(world != null){
            for(Player all : world.getPlayers())
                all.teleport(Main.getInstance().getSpawnLocation());
            this.doUnload();

        }
    }

    public void transferWorld(Player player, String server){
        if(getLoadedWorld() != null) getLoadedWorld().save();
        player.closeInventory();
//        Main.getInstance().getBbConnector().sendData(0x02, new Object[]{
//                server,
//                player.getUniqueId().toString(),
//                this.fileWorldName,
//                this.displayname,
//                Main.getInstance().getItemStackUtils().serializeItemStack(this.displayItem),
//                this.worldProperties.getConfigurableWorldType().name(),
//                this.worldProperties.getEnvironment().name(),
//                (this.getProject() != null ? this.projectID.toString() : "none"),
//                this.worldProperties.getSeed()
//
//        });

        Main.getInstance().getRedisManager().publishMessage("TRANSFER_WORLD_BUNGEE",
                Main.getInstance().CLIENTNAME + ";" +
        server + ";" +
                player.getUniqueId().toString() + ";" +
                this.fileWorldName + ";" +
                this.displayname + ";" +
                Main.getInstance().getItemStackUtils().serializeItemStack(this.displayItem) + ";" +
                this.worldProperties.getConfigurableWorldType().name() + ";" +
                this.worldProperties.getEnvironment().name() + ";" +
                (this.getProject() != null ? this.projectID.toString() : "none") + ";" +
                this.worldProperties.getSeed());
    }

    public void recycleWorld(Player sender){
        unloadWorld();
        Main.getInstance().getWorldManager().getWorldList().remove(this);
        RecycledConfigurableWorld recycledConfigurableWorld = new RecycledConfigurableWorld(serialize(), this.fileWorldName);
        recycledConfigurableWorld.setRecycled(System.currentTimeMillis());
        if(sender != null) recycledConfigurableWorld.setRecycledBy(sender.getName());
        Main.getInstance().getWorldManager().getRecycleBin().getRecycledWorldsList().add(recycledConfigurableWorld);
    }

    public List<Warp> getWarpsInWorld(){
        List<Warp> warpList = new ArrayList<>();
        for(Warp warp : Main.getInstance().getWarpManager().getWarpList())
            if(warp.getConfigurableWorld() == this){
                warpList.add(warp);
            }
        return warpList;
    }

    public ConfigurableWorld cloneWorld(){
        ConfigurableWorld configurableWorld =  Main.getInstance().getWorldManager().newWorld(this.serialize());;
        configurableWorld.setFileWorldName(generateRandomName());

        configurableWorld.setDisplayname("Copy of " + this.displayname);

        try {
            FileUtils.copyDirectory(new File(Bukkit.getWorldContainer() + "/" + this.getFileWorldName()), new File(Bukkit.getWorldContainer() + "/" + configurableWorld.getFileWorldName()));
            new File(Bukkit.getWorldContainer() + "/" + configurableWorld.getFileWorldName() + "/uid.dat").delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return configurableWorld;
    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
            }
            zipOut.closeEntry();
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }

    public boolean isRecycled(){
        for(RecycledConfigurableWorld world : Main.getInstance().getWorldManager().getRecycleBin().getRecycledWorldsList())
            if(world.getFileWorldName().equals(this.fileWorldName))
                return true;
            return false;
    }

    public Project getProject(){
        if(projectID == null)return null;
        return Project.getProject(projectID.toString());
    }


    public World getLoadedWorld() {
//        return loadedWorld;
        return Bukkit.getWorld(fileWorldName);
    }

    public boolean isWorldLoaded(){
        if(this.loadedWorld == null) return false;
//        if(Bukkit.getWorld(this.getFileWorldName()) == null) return false;
        return true;
    }

    @Deprecated
    public void setLoadedWorld(World loadedWorld) {
        this.loadedWorld = loadedWorld;
    }

    public WorldProperties getWorldProperties() {
        return worldProperties;
    }

    public String getFileWorldName() {
        return fileWorldName;
    }

    public void setFileWorldName(String fileWorldName) {
        this.fileWorldName = fileWorldName;
    }

    public String getDisplayname() {
        return displayname;
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public UUID getProjectID() {
        return projectID;
    }

    public void setProjectID(UUID projectID) {
        this.projectID = projectID;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public void setDisplayItem(ItemStack displayItem) {
        this.displayItem = displayItem;
    }

    public static ConfigurableWorld getWorld(String ID){
        return Main.getInstance().getWorldManager().getWorld(ID);
    }


    public String getProjectIDStringNotNull() {
        if(getProjectID() == null)
            return "none";
        return getProjectID().toString();
    }


    public static class WorldLoadListener implements Listener{

        private final String world;
        private final AtomicBoolean atomicBoolean;
        private final long start = System.currentTimeMillis();

        public WorldLoadListener(String world, AtomicBoolean atomicBoolean) {
            this.world = world;
            this.atomicBoolean = atomicBoolean;
        }

        @EventHandler
        public void onWorldLoad(WorldLoadEvent e){
            if(e.getWorld().getName().equals(world)) {
                atomicBoolean.set(true);
                //Bukkit.broadcastMessage("Time: " + ((System.currentTimeMillis()-start)) + "ms");
            }
        }

    }
}
