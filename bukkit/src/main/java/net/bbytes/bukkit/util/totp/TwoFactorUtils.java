package net.bbytes.bukkit.util.totp;

import net.bbytes.bukkit.Main;
import org.bukkit.entity.Player;

import java.security.GeneralSecurityException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TwoFactorUtils {
	
	public final int HOURS = 72;

	
	public List<Player> notAuthenticatedUsers = new ArrayList<Player>();
	
	public HashMap<UUID, String> cachedSecrets = new HashMap<UUID, String>();
	
	public String getSecretFromUUID(UUID uuid) {
		ResultSet rs = Main.getInstance().getMySQLManager().mysql.getMySQL().query("SELECT Secret FROM 2FA_CODES WHERE UUID='"+uuid.toString()+"';");
		
		try {
			if(rs.next()) {
				cachedSecrets.put(uuid, rs.getString("Secret"));
				return rs.getString("Secret");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getLastIPFromUUID(UUID uuid) {
		ResultSet rs = Main.getInstance().getMySQLManager().mysql.getMySQL().query("SELECT LastIP FROM 2FA_LOG WHERE UUID='"+uuid.toString()+"';");
		
		try {
			if(rs.next()) {
				return rs.getString("LastIP");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public long getLastAuthenticatedFromUUID(UUID uuid) {
		ResultSet rs = Main.getInstance().getMySQLManager().mysql.getMySQL().query("SELECT LastAuthenticated FROM 2FA_LOG WHERE UUID='"+uuid.toString()+"';");
		
		try {
			if(rs.next()) {
				return rs.getLong("LastAuthenticated");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public boolean isCodeCorrect(UUID uuid, String code) {
		String secret = "";
		if(cachedSecrets.containsKey(uuid))
			secret = cachedSecrets.get(uuid);
		else {
			secret = getSecretFromUUID(uuid);
		}
		
		try {
			if(Main.getInstance().getTfau().generateCurrentNumber(secret).equalsIgnoreCase(code))
				return true;
		} catch (GeneralSecurityException e) {e.printStackTrace();}
		return false;
	}
	
	public void setNewSecret(UUID uuid, String secret) {
		Main.getInstance().getMySQLManager().mysql.query("SELECT Secret FROM 2FA_CODES WHERE UUID='"+uuid.toString()+"';", (rs) -> {
			try {
				if(rs.next()) {
					Main.getInstance().getMySQLManager().mysql.update("UPDATE 2FA_CODES SET Secret='" + secret + "' WHERE UUID='" + uuid.toString() + "';");
				}else {
					Main.getInstance().getMySQLManager().mysql.update("INSERT INTO 2FA_CODES (UUID, Secret) VALUES ('" + uuid.toString() +"', '" + secret + "');");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
	}
	
	public void remove2fa(UUID uuid) {
		Main.getInstance().getMySQLManager().mysql.update("DELETE FROM 2FA_CODES WHERE UUID='" + uuid.toString() + "';");
	}

	
	public void authenticate(Player player) {
		for(Player p : notAuthenticatedUsers)
			if(p.getName().equalsIgnoreCase(player.getName())){
				notAuthenticatedUsers.remove(p);
				break;
			}
		
		registerActivity(player);
	}
	
	public void registerActivity(Player player) {
			
		Main.getInstance().getMySQLManager().mysql.query("SELECT LastIP FROM 2FA_LOG WHERE UUID='"+player.getUniqueId().toString()+"';", (rs) -> {
			try {
				if(rs.next()) {
					Main.getInstance().getMySQLManager().mysql.update("UPDATE 2FA_LOG SET LastAuthenticated='" + System.currentTimeMillis() + "', LastIP='" + player.getAddress().getHostString() +"' WHERE UUID='" + player.getUniqueId().toString() + "';");
				}else {
					Main.getInstance().getMySQLManager().mysql.update("INSERT INTO 2FA_LOG (UUID, LastAuthenticated, LastIP) VALUES ('" + player.getUniqueId().toString() + "', '" + System.currentTimeMillis() + "', '" + player.getAddress().getHostString() + "');");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
			


	}
	

}
