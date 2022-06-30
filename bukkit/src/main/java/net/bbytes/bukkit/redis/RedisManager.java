package net.bbytes.bukkit.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import net.bbytes.bukkit.Main;
import net.bbytes.bukkit.project.Project;
import net.bbytes.bukkit.world.ConfigurableWorld;
import net.bbytes.bukkit.world.ConfigurableWorldType;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class RedisManager implements RedisMessageReceiver{
	
	private String host;
	private String password;
	private int port;
	
	private StatefulRedisConnection<String, String> connection;
	private StatefulRedisPubSubConnection<String, String> pubSubConnection;
	private RedisPubSubListener<String, String> listener;
	private RedisCommands<String, String> redis;
	private RedisPubSubCommands<String, String> redisPubSub;
	private ArrayList<RedisMessageReceiver> redisMessageHandlers;
	
	public RedisManager() {
		
		redisMessageHandlers = new ArrayList<RedisMessageReceiver>();

		
	}
	
	public void connectToRedis() {
		
		FileConfiguration cfg = Main.getInstance().getConfig();
		boolean canConnect = true;
		
		try {

			
			host = cfg.getString("Redis.Host");
			port = cfg.getInt("Redis.Port");
			password = cfg.getString("Redis.Password");
		
		} catch(NullPointerException ex) {
			
			canConnect = false;
			ex.printStackTrace();
			
		} 
		
		if(canConnect) {
			
			RedisClient client = RedisClient.create("redis://" + password + "@" + host + ":" + port);
			connection = client.connect();
			
			if(connection.isOpen()) {
				
				redis = connection.sync();
				
				pubSubConnection = client.connectPubSub();
				redisPubSub = pubSubConnection.sync();
				
				setupPubSub();
				
			}
			
		}
		
	}
	
	public void disconnectFromRedis() {
		
		if(connection != null && connection.isOpen()) {
			
			connection.close();
			
		}
		
		if(pubSubConnection != null && pubSubConnection.isOpen()) {
			
			pubSubConnection.close();
			
		}
		
	}
	
	private void setupPubSub() {
		
		listener = new RedisPubSubAdapter<String, String>() {
			
			@Override
			public void message(String channel, String message) {
				
				Bukkit.getScheduler().runTask(Main.getInstance(), new Runnable() {
					
					@Override
					public void run() {
						for(RedisMessageReceiver receiver : redisMessageHandlers) {
							
							receiver.onRedisMessageReceived(channel, message);
							
						}
						
					}
					
				});
				
			}
			
		};
		
		pubSubConnection.addListener(listener);
		
	}
	
	public void registerMessageReceiver(RedisMessageReceiver messageReceiver) {
		
		if(!redisMessageHandlers.contains(messageReceiver)) redisMessageHandlers.add(messageReceiver);
		
	}
	
	public void unregisterMessageReceiver(RedisMessageReceiver messageReceiver) {
		
		if(redisMessageHandlers.contains(messageReceiver)) redisMessageHandlers.remove(messageReceiver);
		
	}
	
	public void subscribeToChannel(String... channels) {
		
		redisPubSub.subscribe(channels);
		
		String channel = "";
		for(String c : channels) channel += ", " + c;
		channel = channel.replaceFirst(", ", "");

		
	}
	
	public void publishMessage(String channel, String message) {
		
		redis.publish(channel, message);

		
	}
	
	public RedisCommands<String, String> getRedis() {
		return redis;
	}

	@Override
	public void onRedisMessageReceived(String channel, String message) {

		switch (channel) {
			case "PLAYERMSG":
				for (Player all : Bukkit.getOnlinePlayers()) {
					if (message.split(";")[0].equals(all.getUniqueId().toString())) {
						all.sendMessage(message.split(";")[1]);
						return;
					}
				}
				break;
			case "TRANSFER_SUCCESSFUL":
				String to = message.split(";")[0];

				if (!to.equals(Main.getInstance().CLIENTNAME)) return;

				String worldID = message.split(";")[1];
				String displayname = message.split(";")[2];
				ItemStack displayItem = Main.getInstance().getItemStackUtils().deserializeItemStack(message.split(";")[3], true);
				ConfigurableWorldType type = ConfigurableWorldType.valueOf(message.split(";")[4]);
				World.Environment environment = World.Environment.valueOf(message.split(";")[5]);
				Project project = Project.getProject(message.split(";")[6]);
				long seed = Long.parseLong(message.split(";")[7]);

				ConfigurableWorld world = Main.getInstance().getWorldManager().newWorld();
				world.setFileWorldName(worldID);
				world.setDisplayname(displayname);
				world.setDisplayItem(displayItem);
				world.getWorldProperties().setConfigurableWorldType(type);
				world.getWorldProperties().setEnvironment(environment);
				if (project != null) world.setProjectID(project.getUUID());
				world.getWorldProperties().setSeed(seed);
				break;
			case "UPDATE_PROJECTS":
				if(!message.equals(Main.getInstance().CLIENTNAME))
					Main.getInstance().getProjectManager().loadProjects();
				break;
			case "CHAT_MESSAGE":{
				if(message.split(";")[0].equals(Main.getInstance().CLIENTNAME))break;
				Bukkit.broadcastMessage(message.split(";")[1]);
				break;
			}
		}

	}
}
