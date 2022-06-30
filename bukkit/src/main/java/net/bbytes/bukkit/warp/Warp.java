package net.bbytes.bukkit.warp;

import com.earth2me.essentials.Essentials;
import net.bbytes.bukkit.Main;
import net.bbytes.bukkit.message.Message;
import net.bbytes.bukkit.project.Project;
import net.bbytes.bukkit.user.User;
import net.bbytes.bukkit.world.ConfigurableWorld;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;

public class Warp {

    private final String world;
    private final Vector5 location;
    private String name;
    private ItemStack displayItem = new ItemStack(Material.ENDER_PEARL);

    public Warp(Location loc, String name){
        this.world = loc.getWorld().getName();
        this.location = new Vector5(loc);
        this.name = name;
    }

    public Warp(Map<String, Object> map, String name){
        this.world = (String) map.get("world");
        this.location = new Vector5((Map<String, Object>) map.get("location"));
        if(map.containsKey("displayItem")) this.displayItem = Main.getInstance().getItemStackUtils().deserializeItemStack((String) map.get("displayItem"));
        this.name = name;
    }

    public Map<String, Object> serialize(){
        Map<String, Object> map = new LinkedHashMap<>();

        map.put("world", this.world);
        map.put("location", this.location.serialize());
        if(this.displayItem.getType() != Material.ENDER_PEARL) map.put("displayItem", Main.getInstance().getItemStackUtils().serializeItemStack(this.displayItem));

        return map;
    }

    public void goTo(Player player){

        if(getConfigurableWorld() == null){
            Essentials ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
            ess.getUser(player).setLastLocation(player.getLocation());

            player.teleport(this.location.generateLocation(Bukkit.getWorld(world)));
            player.sendMessage("§6" + Message.WARP_WARPING.get(player).replace("{warp}", "§c" + this.name + "§6"));
            User.getUser(player).logWarp(this);
            return;
        }
        Project project = getConfigurableWorld().getProject();
        if(project != null)
        if(!project.canAccess(player.getUniqueId())){
            player.sendMessage(Message.ERROR_NO_ACCESS_GOTO_WORLD.get(player));
            return;
        }

        getConfigurableWorld().enterLocation(player, this.location);
        player.sendMessage("§6" + Message.WARP_WARPING.get(player).replace("{warp}", "§c" + this.name + "§6"));
        User.getUser(player).logWarp(this);

    }

    public boolean canAccess(Player player){
        if(getConfigurableWorld() == null){
            return true;
        }
        Project project = getConfigurableWorld().getProject();
        if(project != null)
            if(!project.canAccess(player.getUniqueId())){
                return false;
            }

        return true;
    }

    public ConfigurableWorld getConfigurableWorld() {
        return ConfigurableWorld.getWorld(this.world);
    }

    public Location createLocation(){
        return location.generateLocation(Bukkit.getWorld(world));
    }

    public String getName() {
        return name;
    }

    public static Warp getWarp(String query){
        return Main.getInstance().getWarpManager().getWarp(query);
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public void setDisplayItem(ItemStack displayItem) {
        this.displayItem = displayItem;
    }

    public Vector5 getLocation() {
        return location;
    }

    public String getWorld() {
        return world;
    }
}
