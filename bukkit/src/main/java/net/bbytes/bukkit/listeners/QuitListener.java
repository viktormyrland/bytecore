package net.bbytes.bukkit.listeners;

import com.earth2me.essentials.Essentials;
import net.bbytes.bukkit.Main;
import net.bbytes.bukkit.user.User;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuitListener implements Listener{
	
	Essentials es = (Essentials)Bukkit.getPluginManager().getPlugin("Essentials");

	@EventHandler
	public void removeUserObject(PlayerQuitEvent e){

		ExecutorService ex = Executors.newCachedThreadPool();
		ex.execute(() -> {
			try {
				User.getUser(e.getPlayer()).saveUser_Sync();
				Main.getInstance().getUserManager().removeUser(e.getPlayer());
			} catch (SQLException throwables) {
				throwables.printStackTrace();
			}
		});
		ex.shutdown();

	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		if(Main.getInstance().getTwoFactorUtils().notAuthenticatedUsers.contains(e.getPlayer())) {
			Main.getInstance().getTwoFactorUtils().notAuthenticatedUsers.remove(e.getPlayer());
			Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> Bukkit.broadcastMessage("§c§lWarning: §c" + e.getPlayer().getName() + " left the server without authenticating. "), 20);
		}
		
		if(Main.getInstance().getTtu().isPlayerWorking(e.getPlayer().getUniqueId())) {
			Main.getInstance().getTtu().endWorkSession(e.getPlayer().getUniqueId());
		}

		e.getPlayer().setOp(false);
	}

	@EventHandler
	public void announce(PlayerQuitEvent e) {
		e.setQuitMessage("§8[§c§l-§8] §c" + e.getPlayer().getName());
	}

	
	

}
