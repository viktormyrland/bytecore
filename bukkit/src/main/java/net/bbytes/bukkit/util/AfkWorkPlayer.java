package net.bbytes.bukkit.util;

import java.util.UUID;

public class AfkWorkPlayer {

	public UUID player;
	public UUID projectID;
	public long fromBeforeAFK;
	
	public AfkWorkPlayer(UUID player, UUID projectID, long fromBeforeAFK) {
		this.player = player;
		this.projectID = projectID;
		this.fromBeforeAFK = fromBeforeAFK;
	}
}
