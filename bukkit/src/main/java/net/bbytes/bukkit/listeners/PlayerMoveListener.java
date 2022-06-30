package net.bbytes.bukkit.listeners;

import net.bbytes.bukkit.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener{
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if(Main.getInstance().getTwoFactorUtils().notAuthenticatedUsers.contains(e.getPlayer())) {
			e.setCancelled(true);
		}
	}

}
