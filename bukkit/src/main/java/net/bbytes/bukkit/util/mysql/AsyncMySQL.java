package net.bbytes.bukkit.util.mysql;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;


/**
 * 
 * @author Sandro Hahn (xShadoow)
 * @version 0.2.1
 * @category MySQL Connection Utils
 *
 */
public class AsyncMySQL {

	private ExecutorService executor;
	private Plugin plugin;
	private MySQL sql;

	/**
	 * Called when a new AsyncMySQL object is created.
	 * Tries to establish connection immediately.
	 * @param owner plugin instance
	 * @param host ip address of mysql server
	 * @param port port of mysql server (usually 3306)
	 * @param user username (usually "root")
	 * @param password password of username
	 * @param database database to connect to
	 */
	public AsyncMySQL(Plugin owner, String host, int port, String user, String password, String database) {
		
		sql = new MySQL(host, port, user, password, database);
		executor = Executors.newCachedThreadPool();
		plugin = owner;
		
	}
	
	/**
	 * Executes a mysql update asynchronous using a PreparedStatement object.
	 * @param statement sql update as PreparedStatement object
	 */
	public void update(PreparedStatement statement) {
		executor.execute(() -> sql.queryUpdate(statement));
	}
	
	/**
	 * Executes a mysql update asynchronous using a String object.
	 * String argument gets converted to a PreparedStatement object automatically.
	 * @param statement sql update as a String object
	 */
	public void update(String statement) {
		executor.execute(() -> sql.queryUpdate(statement));
	}
	
	/**
	 * Can be used to insert a new record into the database asynchronous if "auto increment"
	 * is turned on for the "ID" column.
	 * The returned integer in the consumer is the id that got generated.
	 * @param insert sql insert query as a String
	 * @param consumer consumer object that returns the generated id
	 */
	public void insert(String insert, Consumer<Integer> consumer) {
		
		executor.execute(() -> {
			
			try {
			
				String[] returnId = {"ID"};
				PreparedStatement statement = sql.conn.prepareStatement(insert, returnId);
				statement.executeUpdate(); 
				ResultSet rs = statement.getGeneratedKeys();
			        
		    	if (rs.next()) {
		        	
		    		Integer integer = new Integer(rs.getInt(1));
		    		Bukkit.getScheduler().runTask(plugin, () -> consumer.accept(integer));
		        	
		        }

		        rs.close();
			    
			} catch(SQLException ex) {
				
				ex.printStackTrace();
				
			}
		    
		});
		
	}
	
	/**
	 * Executes a sql query asynchronous using a PreparedStatement object.
	 * The consumer object returns the ResultSet of the query.
	 * @param statement sql query as PreparedStatement object
	 * @param consumer consumer object that returns the ResultSet of the query
	 */
	public void query(PreparedStatement statement, Consumer<ResultSet> consumer) {
		executor.execute(() -> {
			ResultSet result = sql.query(statement);
			Bukkit.getScheduler().runTask(plugin, () -> consumer.accept(result));
		});
	}
	
	/**
	 * Executes a sql query asynchronous using a String object.
	 * String object gets converted to a PreparedStatement automatically.
	 * The consumer object returns the ResultSet of the query.
	 * @param statement sql query as a String object
	 * @param consumer consumer object that returns the ResultSet of the query
	 */
	public void query(String statement, Consumer<ResultSet> consumer) {
		executor.execute(() ->  {
			ResultSet result = sql.query(statement);
			Bukkit.getScheduler().runTask(plugin, () -> consumer.accept(result));
		});
	}
	
	/**
	 * Used to convert a String object query/update to a PreparedStatement object
	 * @param query sql query/update as a String object
	 * @return the query as a PreparedStatement object
	 */
	public PreparedStatement prepare(String query) {
		
		try {
			
			return sql.getConnection().prepareStatement(query);
			
		} catch(SQLException ex) {
			
			
		}
		
		return null;
	}
	
	/**
	 * Getter for inner mysql class
	 * @return the inner mysql class
	 */
	public MySQL getMySQL() {
		return sql;
	}
	
	public static class MySQL {
		
		private String host;
		private String user;
		private String password;
		private String database;
		private int port;
		
		private Connection conn;
		
		public MySQL(String host, int port, String user, String password, String database) {
			
			this.host = host;
			this.port = port;
			this.user = user;
			this.password = password;
			this.database = database;
			
			this.openConnection();
			
		}
		
		/**
		 * Called when AsyncMySQL object is created. Establishes mysql connection
		 * using the given login credentials.
		 * @return connection object
		 * @return null if no connection was made
		 */
		public Connection openConnection() {
			
			try {
				
				Class.forName("com.mysql.jdbc.Driver");
				return this.conn = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?autoReconnect=true&useSSL=false", this.user, this.password);
			
			} catch (SQLException ex) {
				
				ex.printStackTrace();
				
			} catch (ClassNotFoundException ex) {

				ex.printStackTrace();
				
			}
			
			return null;
		}
		
		/**
		 * Call this function when the plugin gets disabled.
		 */
		public void closeConnection() {
			
			try {
				
				this.conn.close();
				
			} catch (SQLException e) {
				
				e.printStackTrace();
				
			} finally {
				
				this.conn = null;
				
			}
			
		}
		
		/**
		 * Checks if connection is established.
		 * @return true if connected to mysql server
		 * @return false if not connected to mysql server
		 */
		public boolean isConnected() {
			
			try {
				
				if((this.conn == null) || (!this.conn.isValid(10)) || (this.conn.isClosed())) {
					
					return false;
				} else {
					
					return true;
				}
			
			} catch (SQLException e) {
				
				e.printStackTrace();
			
			}
			
			return false;
		}
		
		/**
		 * Checks if connection is established. If reconnect is true it will try
		 * to reconnect if connection isnt established.
		 * @param reconnect whether it should try to reconnect
		 * @return true if connected to mysql server
		 * @return false if not connected to mysql server
		 */
		public boolean isConnected(boolean reconnect) {
			
			try {
				
				if((this.conn == null) || (!this.conn.isValid(10)) || (this.conn.isClosed())) {
					
					reconnect();
					
					return false;
				} else {
					
					return true;
				}
			
			} catch (SQLException e) {
				
				e.printStackTrace();
			
			}
			
			return false;
		}
		
		/**
		 * Call this to reconnect to the mysql server.
		 * @return true if successfully reconnected or already connected
		 * @return false if failed to reconnect
		 */
		public boolean reconnect() {
			
			if(!isConnected()) {
				
				openConnection();
				
				if(isConnected()) {
					return true;
				} else {
					return false;
				}
				
			} else {
			
				return true;
			}
		
		}
		
		/**
		 * Getter for Connection object
		 * @return connection object
		 */
		public Connection getConnection() {
			return this.conn;
		}
		
		/**
		 * Executes a mysql update SYNCHRONOUS (Use with caution!) using a String object.
		 * String argument gets converted to a PreparedStatement object automatically.
		 * @param query sql update as a String object
		 */
		public void queryUpdate(String query) {
			
			try(PreparedStatement statement = conn.prepareStatement(query)) {
				
				queryUpdate(statement);
				
			} catch(SQLException ex) {
				
				ex.printStackTrace();
				
			}
			
		}
		
		/**
		 * Executes a mysql update SYNCHRONOUS (Use with caution!) using a PreparedStatement object.
		 * @param query sql update as a PreparedStatement object
		 */
		public void queryUpdate(PreparedStatement statement) {
			
			if(!isConnected()) {
				return;
			}
			
			try {
				
				statement.executeUpdate();
				
			} catch (SQLException ex) {
				ex.printStackTrace();
				
			}
			
		}
		
		/**
		 * Executes a sql query SYNCHRONOUS (Use with caution!) using a String object.
		 * String object gets converted to a PreparedStatement automatically.
		 * @param query sql query as a String object
		 * @return the ResultSet of the query
		 */
		public ResultSet query(String query) {
			
			try {
				
				return query(conn.prepareStatement(query));
				
			} catch(SQLException ex) {
				
			}
			
			return null;
		}
		
		/**
		 * Executes a sql query SYNCHRONOUS (Use with caution!) using a PreparedStatement object.
		 * @param statement sql query as a PreparedStatement object
		 * @return the ResultSet of the query
		 */
		public ResultSet query(PreparedStatement statement) {
			
			if(!isConnected()) {
				return null;
			}
			
			try {
				
				return statement.executeQuery();
				
			} catch(SQLException ex) {
				ex.printStackTrace();
				
			}
			
			return null;
		}
		
	}
	
}
