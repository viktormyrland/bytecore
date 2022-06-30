package net.bbytes.bukkit.message;

import net.bbytes.bukkit.Main;
import net.bbytes.bukkit.user.Language;
import net.bbytes.bukkit.user.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public enum Message {

    PROJECTS_TITLE("Projects"),
    PROJECTS_PLUGIN_INFO("Navigate and manage worlds on the Blockbytes server."),
    PROJECTS_RECYCLE_BIN("Recycle Bin"),
    PROJECTS_RECYCLE_BIN_INFO("Recover recently deleted worlds and projects"),
    PROJECTS_PLOT_WORLD("Plot World"),
    PROJECTS_PLOT_WORLD_INFO("Travel to the plot world, where you can build whatever you want."),
    PROJECTS_PLOT_WORLD_GO("Go to the plot world"),
    PROJECTS_SPAWN("Travel to the spawn."),
    PROJECTS_SPAWN_GO("Go to the spawn world"),
    PROJECTS_NEW("New Project"),
    PROJECTS_NEW_INFO("Create a fresh new project to start working on."),
    PROJECTS_NEW_CREATE("Create a new project"),
    PROJECTS_UNCATEGORIZED_WORLDS("Uncategorized Worlds"),
    PROJECTS_UNCATEGORIZED_WORLDS_INFO("Browse worlds without any project assigned to it"),

    PROJECT_OVERVIEW("Project Overview"),

    EDIT_PROJECT("Edit Project"),
    EDIT_PROJECT_INFO("Edit project information and who should be allowed to access it"),
    EDIT_PROJECT_DISPLAYNAME_INFO("Edit the display name of this project"),
    EDIT_PROJECT_DISPLAYNAME("Edit project name"),
    EDIT_PROJECT_LEAD("Edit Project Leader"),
    EDIT_PROJECT_LEAD_INFO("Edit the project leader of this project."),
    EDIT_PROJECT_ACCESS("Edit Membership"),
    EDIT_PROJECT_ACCESS_INFO("Edit who has access to this project and the worlds within."),
    EDIT_PROJECT_DELETE("Delete project"),
    EDIT_PROJECT_DELETE_CONFIRM("Confirm Deletion"),
    EDIT_PROJECT_DELETE_CONFIRM_INFO("Click again to confirm deletion"),
    EDIT_PROJECT_DELETE_INFO("Delete this project. All worlds inside will be moved to the recycle bin."),

    CHANGE_PROJECT("Change Project"),
    CHANGE_PROJECT_INFO("Change the project this world is assigned to. Click on a project to move the world."),
    CHANGE_PROJECT_UNASSIGN("Unassign this world from projects, making it public to anyone. This world will be moved to the uncategorized worlds-section."),

    ACCESS_NEW("New player"),
    ACCESS_NEW_INFO("Specify a new player who should get access"),

    NEW_WORLD("New World"),
    NEW_WORLD_INFO("Create a new world. You can choose to generate a new world or to upload an existing world"),
    NEW_WORLD_VOID("Void World"),
    NEW_WORLD_VOID_INFO("Create a new, completely void world"),
    NEW_WORLD_FLAT("Flat World"),
    NEW_WORLD_FLAT_INFO("Create a new, completely flat world"),
    NEW_WORLD_NORMAL("Normal World"),
    NEW_WORLD_NORMAL_INFO("Create a new vanilla world"),
    NEW_WORLD_UPLOAD("Upload World"),
    NEW_WORLD_UPLOAD_INFO("Upload a world from your computer"),
    NEW_WORLD_IMPORT("Import World"),
    NEW_WORLD_IMPORT_INFO("Import a world manually from the root server."),
    NEW_WORLD_CREATE("Create new world"),
    NEW_WORLD_UPLOAD_CREATE("Upload a world"),
    NEW_WORLD_IMPORT_CREATE("Import a world"),

    EDIT_NAME("Edit Name"),
    WORLD_NAME("World Name"),
    CURRENT_NAME("Current Name"),
    CURRENT_LEADER("Current Leader"),

    EMPTY("Empty"),
    EMPTY_NO_RECENT_WORLDS("There are no recent worlds available."),
    EMPTY_WORLDS("There are no worlds available."),
    EMPTY_NO_RECENT_WARPS("There are no recent warps available."),
    EMPTY_PROJECTS("There are no projects available"),
    EMPTY_ACCESS("No access is specified for this project"),
    EMPTY_WARPS("There are no warps available."),

    IMPORT_ITEM_TITLE("World Settings"),
    IMPORT_INFO("Tweak the settings of the world before you load it in. It's important that these settings are correct."),
    IMPORT_CREATE("Create World"),
    IMPORT_CREATE_INFO("Apply the settings and finish importing the world. Make sure that all settings are correct before you import."),
    IMPORT_TYPE_INFO("Set the world type"),
    IMPORT_ENVIROMENT_INFO("Set the world environment"),
    IMPORT_SELECT_PROJECT("Select Project"),
    IMPORT_PROJECT_INFO("Select which project to import this world to."),

    WORLD_EDIT("Edit World"),
    WORLD_EDIT_NAME("Edit World Name"),
    WORLD_EDIT_NAME_INFO("Edit the display name of this world"),
    WORLD_EDIT_DOWNLOAD("Download World"),
    WORLD_EDIT_DOWNLOAD_INFO("Download this world as a ZIP file."),
    WORLD_EDIT_TRANSFER("Transfer World"),
    WORLD_EDIT_TRANSFER_INFO("Copy this world to the 1.18 server."),
    WORLD_EDIT_CLONE("Clone World"),
    WORLD_EDIT_CLONE_INFO("Clone this world and create an exact copy"),
    WORLD_EDIT_CHANGE_PROJECT_INFO("Move this world to another project or unassign it from all projects."),
    WORLD_EDIT_DELETE("Delete World"),
    WORLD_EDIT_DELETE_INFO("Delete this world and move it to the recycle bin."),
    WORLD_EDIT_CONFIRM_DELETE("Confirm Delete World"),
    WORLD_EDIT_STATUS("Status"),
    WORLD_EDIT_ENABLED("enabled"),
    WORLD_EDIT_DISABLED("disabled"),

    PROPERTY_TIMELOCK("Time Lock"),
    PROPERTY_WEATHERLOCK("Weather Lock"),
    PROPERTY_MOBSPAWN("Mob Spawn"),
    PROPERTY_BLOCK_GRAVITY("Block Gravity"),
    PROPERTY_FIRE_SPREAD("Fire Spread"),
    PROPERTY_TNT_EXPLODE("TNT Explode"),
    PROPERTY_PLANT_GROWTH("Plant Growth"),
    PROPERTY_LIQUID_FLOW("Water & Lava Flow"),
    PROPERTY_ICE_SNOW_MELT("Ice & Snow Melting"),
    PROPERTY_MOB_GRIEFING("Mob Griefing"),
    PROPERTY_SNOW_FORMING("Snow Falling"),
    PROPERTY_LEAF_DECAY("Leaf Decay"),

    PROPERTY_TIMELOCK_INFO("If §aenabled§7, time will freeze"),
    PROPERTY_WEATHERLOCK_INFO("If §aenabled§7, weather won't change"),
    PROPERTY_MOBSPAWN_INFO("If §aenabled§7, mobs can spawn"),
    PROPERTY_BLOCK_GRAVITY_INFO("If §aenabled§7, gravel and sand will fall"),
    PROPERTY_FIRE_SPREAD_INFO("If §aenabled§7, fire will spread and destroy blocks"),
    PROPERTY_TNT_EXPLODE_INFO("If §aenabled§7, TNT will explode and destroy blocks"),
    PROPERTY_PLANT_GROWTH_INFO("If §aenabled§7, plants, grass, trees and other greenery can spread and grow"),
    PROPERTY_LIQUID_FLOW_INFO("If §aenabled§7, water and lava will flow"),
    PROPERTY_ICE_SNOW_MELT_INFO("If §aenabled§7, ice and snow can melt"),
    PROPERTY_MOB_GRIEFING_INFO("If §aenabled§7, mobs can destroy blocks"),
    PROPERTY_SNOW_FORMING_INFO("If §aenabled§7, snow will form on the ground in snow biomes"),
    PROPERTY_LEAF_DECAY_INFO("If §aenabled§7, leaves will decay"),

    WORLD_RECYCLED("World §b{world} §6moved to recycle bin."),

    TRANSFER_INFO("Transfer this world to another server. Choose what server you want to transfer it to below. The transferred world will keep its project."),
    TRANSFER_15("Transfer to 1.15"),
    TRANSFER_15_INFO("Transfer this world to our 1.15.2 server"),
    TRANSFER_16("Transfer to 1.16"),
    TRANSFER_16_INFO("Transfer this world to our 1.16.1 server."),
    TRANSFER_18("Transfer to 1.18"),
    TRANSFER_18_INFO("Transfer this world to our 1.18.2 server."),

    WARP_MENU_TITLE("Warp Menu"),
    WARP_MENU_INFO("Browse all warps through this menu. Click on a project to browse!"),
    WARP_UNCATEGORIZED("Uncategorized Warps"),
    WARP_UNCATEGORIZED_INFO("Browse warps without any project assigned to it"),
    WARP_WORLD_INFO("Browse all warps in this world. Click on a warp to warp!"),
    WARP_PROJECT_INFO("Browse all worlds in this project. Click on a world to browse!"),


    DELETED("Deleted"),
    DELETED_BY("Deleted by"),


    PAGES("§ePage: §6{page}/{pages}"),
    PAGE_NEXT("Next Page"),
    PAGE_PREVIOUS("Previous Page"),

    WORD_PROJECT("Project"),
    WORD_NONE("§8none"),
    WORD_PROJECT_LEADER("Project Leader"),
    WORD_WORLDS("Worlds"),
    WORD_WORLD("World"),
    WORD_ENABLED("§aEnabled"),
    WORD_DISABLED("§cDisabled"),
    WORD_WARPS("Warps"),
    WORD_NAME("Name"),
    WORD_LOCATION("Location"),
    WORD_INFORMATION("Information"),

    INFO_LOADING("Loading"),
    INFO_GENERATING("Generating"),
    INFO_ADMIN_ONLY("Admin Only"),

    GO_BACK("§aGo Back"),
    CANCEL("§cCancel"),

    NO_ACCESS("No access"),
    NO_ACCESS_PROJECT("§cError: §4You do not have access to this project"),
    NO_ACCESS_WORLD("§cError: §4You do not have access to this world"),

    CLICK_TO_OPEN("Click to open"),
    CLICK_TO_BROWSE("Click to browse"),
    CLICK_TRAVEL("Click to travel to this world"),
    CLICK_EXPLORE_PROJECT("Click to explore project"),
    CLICK_SHIFT_EDIT("Shift + Click to edit world"),
    CLICK_NEW_WORLD("Click to create a new world"),
    CLICK_EDIT_PROJECT("Click to edit project"),
    CLICK_EDIT_ICON("Click with an item to change icon"),
    CLICK_ADD("Click to add"),
    CLICK_REMOVE("Click to remove"),
    CLICK_UNASSIGN("Click to unassign"),
    CLICK_CHANGE_PROJECT("Click to change project"),
    CLICK_IMPORT("Click to import"),
    CLICK_CREATE_WORLD("Click to create world"),
    CLICK_SELECT("Click to select"),
    CLICK_RESTORE("Click to restore world"),
    CLICK_SHIFT_DELETE("Shift + Click to force delete"),
    CLICK_TOGGLE("Click to toggle"),
    CLICK_WARP("Click to warp"),
    CLICK_BROWSE_WARPS("Click to browse warps"),

    NO_PERMISSION("§cNo Permission."),

    ACCESS_MANAGEMENT("Member Management"),
    ACCESS_GIVE("Add a player as a member to your current project"),
    ACCESS_GIVEN("Player {p} is now a member of {project}."),
    ACCESS_REVOKE("Remove a member from your current project"),
    ACCESS_REVOKED("Player {p} is no longer a member of {project}"),

    WARPS_CHAT("There are {amount} warps. Showing page {page} of {pages}."),
    WARP_SET("Warp {warp} set"),
    WARP_REMOVED("Warp {warp} has been removed"),
    WARP_WARPING("Warping to {warp}"),

    GAMERULE_INFO("§cUse /settings to change world options."),

    WORLD_DOWNLOAD("Download the current world."),
    WORLD_DOWNLOAD_FILE("Download your file"),
    WORLD_UPLOAD(""),

    NEW_WORLD_CREATED("New {type} world created!"),

    USAGE("Usage"),

    ERROR_NOT_IN_PROJECT("§cError: §4You are not currently in a project."),
    ERROR_PLAYER_NOT_FOUND("§cError: §4Player not found"),
    ERROR_NOT_IN_PROJECT_WORLD("§cError: §4You are not in a world assigned to a project."),
    ERROR_NO_SETTINGS("§cError: §4Your current world does not provide settings."),
    ERROR_WARP_NO_EXIST("§cError: §4That warp does not exist"),
    ERROR_NO_WARPS("§cError: §6No warps found"),
    ERROR_NONSUPPORT("§cError: §4Your world does not provide download."),
    ERROR_INVALID_WORLD("§cError: §4Invalid world"),
    ERROR_INVALID_ID("§cError: §4ID is invalid"),
    ERROR_LOAD_FAIL("§cError: §4World failed to load"),
    ERROR_WARP_NAME_EXISTS("§cError: §4A warp with that name already exists"),
    ERROR_NO_ACCESS_DELETE_WARP("§cError: §4You do not have access to delete this warp"),
    ERROR_NO_ACCESS_GOTO_WORLD("§cError: §4You do not have access to go to that world"),

    RESTART_TIME_HOUR("§6Auto-Restarting server in §c{time} hour§6."),
    RESTART_TIME_MINUTE("§6Auto-Restarting server in §c{time} minute(s)§6."),
    RESTART_TIME_SECOND("§6Auto-Restarting server in §c{time} second(s)§6."),

    WORLD_DELETE_FORCE("Successfully permanently deleted the world §b{world}"),



    FORMAT_DIVIDER("");


    private final String message;

    private static Map<Message, String> frenchMap = new HashMap<>();
    private static Map<Message, String> russianMap = new HashMap<>();
    private static Map<Message, String> spanishMap = new HashMap<>();

    Message(String message) {
        this.message = message;
    }

    public String get(CommandSender sender){
        if(sender instanceof Player)
            return get((Player) sender);
        return getRaw();
    }
    public String get(Player player){

        return get(User.getUser(player).getLanguage());
    }

    public String get(Language language){
        if(language != null)
            switch(language){
                case FRENCH:{
                    if(frenchMap.containsKey(this))
                        return frenchMap.get(this);
                    break;
                }
                case SPANISH:{
                    if(spanishMap.containsKey(this))
                        return frenchMap.get(this);
                    break;
                }
                case RUSSIAN:{
                    if(russianMap.containsKey(this))
                        return frenchMap.get(this);
                    break;
                }
                default:{
                    return this.message;
                }
            }

        return this.message;
    }

    public String getWithPrefix(CommandSender sender){
        if(sender instanceof Player)
            return getWithPrefix((Player) sender);
        return Main.getInstance().PREFIX + getRaw();
    }

    public String getWithPrefix(Player player){
        return Main.getInstance().PREFIX + get(player);
    }

    public String getRaw(){
        if(this == Message.FORMAT_DIVIDER){
            if(Main.getInstance().CLIENTNAME.equals("BUILD12"))
                return "§8§m--";
            else return "§8§m⎯⎯";
        }
        return this.message;
    }

    public static String enumToYml(String str){
        return str.toLowerCase().replaceAll("_", "-");
    }

    public static String YmlToEnum(String str){
        return str.toUpperCase().replaceAll("-", "_");
    }

    public static void loadLanguages() throws IOException {
        Yaml yaml = new Yaml();
        File file = new File(Main.getInstance().getDataFolder() + "/fr.yml");
        if(file.exists()){
            InputStreamReader inputStream = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            Map<Message, String> frenchMap = new HashMap<>();

            Map<String, String> map = yaml.load(inputStream);
            for(String key : map.keySet()){
                try{
                    Message msg = Message.valueOf(YmlToEnum(key));
                    frenchMap.put(msg, map.get(key).replaceAll("&", "§"));
                }catch(IllegalArgumentException e){
                }
            }
            Message.frenchMap = new HashMap<>(frenchMap);
            inputStream.close();
        }

        file = new File(Main.getInstance().getDataFolder() + "/es.yml");
        if(file.exists()){
            InputStreamReader inputStream = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            Map<Message, String> spanishMap = new HashMap<>();

            Map<String, String> map = yaml.load(inputStream);
            for(String key : map.keySet()){
                try{
                    Message msg = Message.valueOf(YmlToEnum(key));
                    spanishMap.put(msg, map.get(key).replaceAll("&", "§"));
                }catch(IllegalArgumentException e){
                }
            }
            Message.spanishMap = new HashMap<>(spanishMap);
            inputStream.close();
        }

        file = new File(Main.getInstance().getDataFolder() + "/ru.yml");
        if(file.exists()){
            InputStreamReader inputStream = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);;
            Map<Message, String> russianMap = new HashMap<>();

            Map<String, String> map = yaml.load(inputStream);
            for(String key : map.keySet()){
                try{
                    Message msg = Message.valueOf(YmlToEnum(key));
                    russianMap.put(msg, map.get(key).replaceAll("&", "§"));
                }catch(IllegalArgumentException e){
                }
            }
            Message.russianMap = new HashMap<>(russianMap);
            inputStream.close();
        }

    }


    @Override
    public String toString() {
        return this.message;
    }
}
