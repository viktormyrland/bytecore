package net.bbytes.bukkit.util.mysql;

import net.bbytes.bukkit.Main;
import org.bukkit.Bukkit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class MySQLManager {
	
	public Executor executor;
	
	public AsyncMySQL mysql;
	public boolean canConnect = false;
	public boolean isConnected = false;
	
	String address = "";
	int port = 0;
	String database = "";
	String username = "";
	String password = "";
	
	
	public void connectToMySQL() {	
		
		this.executor = Executors.newCachedThreadPool();
		
		
		try {
			address = Main.getInstance().getConfig().getString("MySQL.Address");
			port = Main.getInstance().getConfig().getInt("MySQL.Port");
			database = Main.getInstance().getConfig().getString("MySQL.Database");
			username = Main.getInstance().getConfig().getString("MySQL.Username");
			password = Main.getInstance().getConfig().getString("MySQL.Password");
			
			canConnect = true;
		}catch(Exception ex) {}
		
		if(canConnect) {
			mysql = new AsyncMySQL(Main.getInstance(), address, port, username, password, database);
			
			if(mysql.getMySQL().isConnected()) {
				isConnected = true;
			}
		}

		keepAlive();
	}
	
	public void setupDefaultTables() {
		
	
		
		mysql.update("CREATE TABLE IF NOT EXISTS 2FA_CODES(UUID VARCHAR(36) NOT NULL);");
		
		List<String> columns = new ArrayList<String>();
		columns.add("Secret VARCHAR(16)");

		
		for(String column : columns) {
			
			mysql.query("SELECT * FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = '" + database + "' AND TABLE_NAME = '2FA_CODES' AND COLUMN_NAME = '" + column.split(" ")[0] + "';", new Consumer<ResultSet>() {

				@Override
				public void accept(ResultSet rs) {
					try {
						if(!rs.next())
							mysql.update("ALTER TABLE 2FA_CODES ADD " + column + ";");
					} catch (SQLException e) {e.printStackTrace();}
					
				}
				
			});
			
			
		}
		
		
		mysql.update("CREATE TABLE IF NOT EXISTS 2FA_LOG(UUID VARCHAR(36) NOT NULL);");
		
		columns = new ArrayList<String>();
		columns.add("LastAuthenticated BIGINT");
		columns.add("LastIP VARCHAR(100)");

		
		for(String column : columns) {
			
			mysql.query("SELECT * FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = '" + database + "' AND TABLE_NAME = '2FA_LOG' AND COLUMN_NAME = '" + column.split(" ")[0] + "';", new Consumer<ResultSet>() {

				@Override
				public void accept(ResultSet rs) {
					try {
						if(!rs.next())
							mysql.update("ALTER TABLE 2FA_LOG ADD " + column + ";");
					} catch (SQLException e) {e.printStackTrace();}
					
				}
				
			});
			
			
		}
		
		mysql.update("CREATE TABLE IF NOT EXISTS TT_Projects(ID INT AUTO_INCREMENT, PRIMARY KEY(ID));");
		
		columns = new ArrayList<String>();
		columns.add("Name VARCHAR(32)");
		columns.add("Active TINYINT(1)");
		columns.add("TimeCreated BIGINT");

		
		for(String column : columns) {
			
			mysql.query("SELECT * FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = '" + database + "' AND TABLE_NAME = 'TT_Projects' AND COLUMN_NAME = '" + column.split(" ")[0] + "';", new Consumer<ResultSet>() {

				@Override
				public void accept(ResultSet rs) {
					try {
						if(!rs.next())
							mysql.update("ALTER TABLE TT_Projects ADD " + column + ";");
					} catch (SQLException e) {e.printStackTrace();}
					
				}
				
			});
			
			
		}
		
		mysql.update("CREATE TABLE IF NOT EXISTS TT_Sessions(ID INT AUTO_INCREMENT, PRIMARY KEY(ID));");
		
		columns = new ArrayList<String>();
		columns.add("UUID VARCHAR(36)");
		columns.add("ProjectID INT");
		columns.add("StartSession BIGINT");
		columns.add("EndSession BIGINT");

		
		for(String column : columns) {
			
			mysql.query("SELECT * FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = '" + database + "' AND TABLE_NAME = 'TT_Sessions' AND COLUMN_NAME = '" + column.split(" ")[0] + "';", new Consumer<ResultSet>() {

				@Override
				public void accept(ResultSet rs) {
					try {
						if(!rs.next())
							mysql.update("ALTER TABLE TT_Sessions ADD " + column + ";");
					} catch (SQLException e) {e.printStackTrace();}
					
				}
				
			});
			
			
		}
		
		mysql.update("CREATE TABLE IF NOT EXISTS Whitelist(UUID VARCHAR(36), PRIMARY KEY(UUID));");
		
		mysql.update("CREATE TABLE IF NOT EXISTS UUIDToName(UUID VARCHAR(36) NOT NULL UNIQUE, Username VARCHAR(16));");

		mysql.update("CREATE TABLE IF NOT EXISTS Users(UUID VARCHAR(36) NOT NULL, Language VARCHAR(16) DEFAULT 'EN', RecentWorlds12 TEXT, RecentWarps12 TEXT, RecentWorlds15 TEXT, RecentWarps15 TEXT, RecentWorlds16 TEXT, RecentWarps16 TEXT, UNIQUE (UUID));");

		mysql.update("CREATE TABLE IF NOT EXISTS Projects(UUID VARCHAR(36) NOT NULL, Name VARCHAR(64) NOT NULL, ProjectLead VARCHAR(64) NOT NULL, " +
				"DisplayItem VARCHAR(64) NOT NULL, TimeCreated BIGINT, UNIQUE (UUID));");

		mysql.update("CREATE TABLE IF NOT EXISTS AccessList(UUID VARCHAR(36) NOT NULL, Project VARCHAR(36) NOT NULL, UNIQUE (UUID, Project));");

		mysql.update("CREATE TABLE IF NOT EXISTS Prefixes(ID VARCHAR(36) NOT NULL, Prefix VARCHAR(64) NOT NULL);");

	}

	public void keepAlive() {

		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {@Override public void run() {
			mysql.query("SELECT UUID FROM Users LIMIT 1;", new Consumer<ResultSet>() {@Override public void accept(ResultSet rs){}});}}, 20*60*60, 20*60*60);

	}

}
