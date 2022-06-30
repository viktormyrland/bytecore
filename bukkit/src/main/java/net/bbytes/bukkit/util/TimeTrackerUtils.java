package net.bbytes.bukkit.util;

import com.earth2me.essentials.Essentials;
import net.bbytes.bukkit.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TimeTrackerUtils {
	
	public List<WorkSession> currentSessions = new ArrayList<WorkSession>();
	public List<AfkWorkPlayer> currentAFKPlayersInSession = new ArrayList<AfkWorkPlayer>();
	
	public BukkitTask runnable;
	Essentials es = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
	
	public void newWorkSession(Player p, UUID projectID) {
		currentSessions.add(new WorkSession(p.getUniqueId(), projectID));
		
	}
	
	public void endWorkSession(UUID p) {
		WorkSession ws = getPlayerWorkSession(p);
		currentSessions.remove(ws);
		ws.endSession(true);
		
	}
	
	public void sendListOfAvailableSessionsTo(Player player) {
		Main.getInstance().getMySQLManager().mysql.query("SELECT Name, ID FROM TT_Projects WHERE Active='TRUE'", rs -> {
			player.sendMessage("§6Available Projects:");
			try {
				while(rs.next()) {
					player.sendMessage(" §7- §b" + rs.getString("Name") + "§7, §aID: " + rs.getInt("ID"));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		});
	}
	
	public WorkSession getPlayerWorkSession(UUID uuid) {
		for(WorkSession ws : currentSessions) {
			if(ws.getUUID() == uuid) {
				return ws;
			}
		}
		return null;
	}
	
	public boolean isPlayerWorking(UUID uuid) {
		for(WorkSession ws : currentSessions) {
			if(ws.getUUID() == uuid) {
				return true;
			}
		}
		return false;
	}
	
	public void startRunnable() {
		
		runnable = new BukkitRunnable() {

			@Override
			public void run() {
				for(WorkSession ws : currentSessions) {
					Player p = Bukkit.getPlayer(ws.getUUID());
					
					if(es.getUser(ws.getUUID()).isAfk()) {
						ws.endSession(false);
						
						currentAFKPlayersInSession.add(new AfkWorkPlayer(p.getUniqueId(), ws.getProjectID(), ws.getFromBeforeAFK() - ws.getStartTime() + System.currentTimeMillis()));
						
						p.sendMessage("§c§lWork session ended");
						p.sendMessage("§7A new session will start once you're not AFK anymore.");
						currentSessions.remove(ws);
						break;
						
					}
				}
				
				for(AfkWorkPlayer awp : currentAFKPlayersInSession) {
					Player p = Bukkit.getPlayer(awp.player);
					if(!es.getUser(p.getUniqueId()).isAfk()){
						WorkSession ws = new WorkSession(p.getUniqueId(), awp.projectID);
						ws.setFromBeforeAFK(awp.fromBeforeAFK);
						
						currentSessions.add(ws);
						currentAFKPlayersInSession.remove(awp);
						
						p.sendMessage("§a§lWork session started");
						p.sendMessage("§7You returned from being AFK in the middle of a work session, and a new session has automatically been started.");
						break;
					}
				}
				
				
			}
			
			
		}.runTaskTimerAsynchronously(Main.getInstance(), 0, 10);
	}
	
	
	

}
