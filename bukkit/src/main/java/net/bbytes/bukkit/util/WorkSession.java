package net.bbytes.bukkit.util;

import net.bbytes.bukkit.Main;
import org.bukkit.Bukkit;

import java.util.UUID;
import java.util.function.Consumer;

public class WorkSession {
	
	private UUID uuid;
	private UUID projectID;
	private long startTime;
	private long fromBeforeAFK = 0;
	
	
	public WorkSession(UUID uuid, UUID projectID) {
		this.projectID = projectID;
		this.uuid = uuid;
		this.startTime = System.currentTimeMillis();
	}
	
	public WorkSession(UUID uuid, UUID projectID, long fromBeforeAFK) {
		this.projectID = projectID;
		this.uuid = uuid;
		this.startTime = System.currentTimeMillis();
		this.fromBeforeAFK = fromBeforeAFK;
	}
	
	
	
	public void endSession(boolean verbose) {
		final long endTime = System.currentTimeMillis();
		
		Main.getInstance().getMySQLManager().mysql.insert("INSERT INTO TT_Sessions (UUID, ProjectID, StartSession, EndSession) VALUES ('"+uuid.toString()+"', '"+projectID+"', '"+startTime+"', '"+endTime+"');", new Consumer<Integer>() {

			@Override
			public void accept(Integer i) {
				if(verbose)
				if(Bukkit.getPlayer(uuid) != null) {
					
					long workTime = endTime-startTime + fromBeforeAFK;
					
					Bukkit.getPlayer(uuid).sendMessage("§c§lWork Session Ended");
					Bukkit.getPlayer(uuid).sendMessage("§8--------------------");
					Bukkit.getPlayer(uuid).sendMessage(" §a- §6Total Time: §a" + (int)(workTime/1000/60/60) + " §6hours, §a" + (	workTime/1000/60) % 60 + " §6minutes and §a" + (workTime/1000) % 60 + " §6seconds");
					Bukkit.getPlayer(uuid).sendMessage(" §a- §7Session ID: " + i);
					Bukkit.getPlayer(uuid).sendMessage("§8--------------------");
				}	
			}
		});
		
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public UUID getProjectID() {
		return projectID;
	}
	
	public long getFromBeforeAFK(){
		return fromBeforeAFK;
	}
	
	public void setFromBeforeAFK(long i) {
		this.fromBeforeAFK = i;
	}
	
	public long getStartTime() {
		return startTime;
	}
}
