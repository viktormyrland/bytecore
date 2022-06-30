package net.bbytes.bukkit.redis;

public interface RedisMessageReceiver {
	
	public void onRedisMessageReceived(String channel, String message);
	
}
