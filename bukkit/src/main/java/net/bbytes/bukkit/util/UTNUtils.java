package net.bbytes.bukkit.util;

import net.bbytes.bukkit.Main;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class UTNUtils {
	/**
	 * UUID To Name Utils
	 * To limit the amount of requests to Mojang, store UUID and name pairs in a database.
	 */
	
	private ExecutorService executor;
	private Plugin plugin;
	Main api = Main.getInstance();

	private Map<String, String> cachedList = new HashMap<>();

	public UTNUtils(Plugin owner) {
		executor = Executors.newCachedThreadPool();
		plugin = owner;

	}
	
	/*
	 * Execute getUUIDFromName_Sync async.
	 */
	public void getUUIDFromName(String name, Consumer<UUID> consumer) {


		executor.execute(() -> {
			Bukkit.getScheduler().runTask(plugin, () -> consumer.accept(getUUIDFromName_Sync(name)));
		});
	}
	
	/*
	 * Check if the UUID exists in the database. If not, return from Mojang API and update the database
	 */
	
	public UUID getUUIDFromName_Sync(String name){
		for(String uuid1 : cachedList.keySet())
			if(cachedList.get(uuid1).equalsIgnoreCase(name)){
				return UUID.fromString(uuid1);
			}

		ResultSet rs = api.getMySQLManager().mysql.getMySQL().query("SELECT UUID FROM UUIDToName WHERE Username LIKE '" + name + "';");
		try {
			if(rs.next()) {
				return UUID.fromString(rs.getString("UUID"));
			}
		} catch (SQLException e) {e.printStackTrace();}
		
		UUID uuid = MojangAPI.getUUID(name);
		if(uuid != null)updateUUID_Sync(uuid);
		return uuid;
	}
	
	/*
	 * Execute getNameFromUUID_Sync async
	 */
	
	public void getNameFromUUID(UUID uuid, Consumer<String> consumer) {
		executor.execute(() ->{
			Bukkit.getScheduler().runTask(plugin, () -> consumer.accept(getNameFromUUID_Sync(uuid)));
		});
	}
	
	/*
	 * Check if the username exists in the database. If not, return from Mojang API and update the database
	 */
	
	public String getNameFromUUID_Sync(UUID uuid) {


		ResultSet rs = api.getMySQLManager().mysql.getMySQL().query("SELECT Username FROM UUIDToName WHERE UUID = '" + uuid.toString() + "';");
		
		try {
			if(rs.next()) {
				return rs.getString("Username");
			}
		} catch (SQLException e) {e.printStackTrace();}
		
		String mojangName = MojangAPI.getCurrentName(uuid);
		updateUUIDWithName_Sync(uuid, mojangName);
		return mojangName;
	}
	
	/*
	 * Execute getFixedName_Sync async
	 */
	
	public void getFixedName(String name, Consumer<String> consumer) {
		executor.execute(() ->{
			Bukkit.getScheduler().runTask(plugin, () -> consumer.accept(getFixedName_Sync(name)));
		});
	}
	
	/*
	 * Check if the Name exists in the database. If not, return from Mojang API and update the database
	 */
	
	public String getFixedName_Sync(String name) {
		ResultSet rs = api.getMySQLManager().mysql.getMySQL().query("SELECT Username FROM UUIDToName WHERE Username LIKE '"+name+"';");
		try {
			if(rs.next()) {
				return rs.getString("Username");
			}
		} catch (SQLException e) {e.printStackTrace();}
		
		
		String mojangName = MojangAPI.getFixedName(name);
		updateUUIDWithName_Sync(getUUIDFromName_Sync(name), mojangName);
		return mojangName;
		
	}
	
	/*
	 * Send a list of UUIDs, receive a list of names.
	 * Execute getNameFromUUID_Sync async looped
	 */
	
	public void getNamesFromUUIDs(List<String> uuidList, Consumer<List<String>> consumer) {
		executor.execute(() -> {
			List<String> returnList = new ArrayList<String>();
			for(String uuid : uuidList) {
				returnList.add(getNameFromUUID_Sync(UUID.fromString(uuid)));
			}
			Bukkit.getScheduler().runTask(plugin, () -> consumer.accept(returnList));
			
			
		});
	}
	
	public void updateUUID(UUID uuid) {
		executor.execute(() ->{
			updateUUID_Sync(uuid);
		}); 
	}
	
	public void updateUUID_Sync(UUID uuid) {
		updateUUIDWithName_Sync(uuid, MojangAPI.getCurrentName(uuid));
	}
	
	public void updateUUIDWithName(UUID uuid, String name) {
		executor.execute(() ->{
			updateUUIDWithName_Sync(uuid, name);
		});
	}
	
	public void updateUUIDWithName_Sync(UUID uuid, String name) {
		this.cachedList.put(uuid.toString(), name);
		ResultSet rs = api.getMySQLManager().mysql.getMySQL().query("SELECT * FROM UUIDToName WHERE UUID='" + uuid.toString() + "';");
		
		try {
			if(rs.next()) {
				api.getMySQLManager().mysql.getMySQL().queryUpdate("UPDATE UUIDToName SET Username = '" + name + "' WHERE UUID = '" + uuid.toString() + "';");
			}else {
				api.getMySQLManager().mysql.getMySQL().queryUpdate("INSERT INTO UUIDToName (UUID, Username) VALUES ('" + uuid.toString() + "', '" + name + "');");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getCachedUsername(UUID uuid){
		if(this.cachedList.containsKey(uuid.toString()))
			return this.cachedList.get(uuid.toString());
		return null;
	}

	public void cacheList(){
		Main.getInstance().getMySQLManager().mysql.query("SELECT * FROM UUIDToName;", (rs) -> {
			while(true){
				try {
					if (!rs.next()) break;

					this.cachedList.put(rs.getString("UUID"), rs.getString("Username"));
				} catch (SQLException throwables) {
					throwables.printStackTrace();
				}

			}

		});
	}

	
}
