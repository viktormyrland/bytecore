package net.bbytes.bukkit.listeners;

import net.bbytes.bukkit.Main;
import net.bbytes.bukkit.message.Message;
import net.bbytes.bukkit.world.ConfigurableWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportListener implements Listener {

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e){
        ConfigurableWorld world = ConfigurableWorld.getWorld(e.getTo().getWorld().getName());
        if(world != null){
            if(world.getProject() != null) if(!world.getProject().canAccess(e.getPlayer().getUniqueId())){
                e.setCancelled(true);
                e.getPlayer().sendMessage(Message.NO_ACCESS_WORLD.get(e.getPlayer()));
                e.getPlayer().sendTitle(Message.NO_ACCESS.get(e.getPlayer()), "", 4, 40, 4);
                e.getPlayer().closeInventory();
            }

        }
    }

//    @EventHandler
//    public void onWorldChange(PlayerChangedWorldEvent e){
//        Main.getInstance().getF3NPerm().update(e.getPlayer());
//    }
}
