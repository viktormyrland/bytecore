package net.bbytes.bukkit.util;

import net.bbytes.bukkit.Main;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;


@Deprecated
public class F3NFixManager {

	
	private Reflector reflector;
	
	public F3NFixManager() {
		try {
			this.reflector = new Reflector();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void update(final Player player) {
//      (new BukkitRunnable()
//        {
//          public void run() {
//            Main.getInstance().getF3NPerm().reflector.sendEntityStatus(player);
//          }
//        }).runTaskLater((Plugin)Main.getInstance(), 10L);
    }
	  
	
	private class Reflector{
	    private  Class<?> entityStatusPacketClass;
	    private  Class<?> playerConnectionClass;
	    private  Class<?> entityClass;
	    private  Class<?> packetClass;
	    
	    private Reflector() throws ClassNotFoundException {
	      String namespace = Main.getInstance().getServer().getClass().getPackage().getName().split("\\.")[3];

			if(Main.getInstance().getSubversion() == 17)return;
	      this.entityStatusPacketClass = Class.forName("net.minecraft.server." + namespace + ".PacketPlayOutEntityStatus");
	      this.playerConnectionClass = Class.forName("net.minecraft.server." + namespace + ".PlayerConnection");
	      this.entityClass = Class.forName("net.minecraft.server." + namespace + ".Entity");
	      this.packetClass = Class.forName("net.minecraft.server." + namespace + ".Packet");
	    }
	    
	    private void sendEntityStatus(Player p) {
			if(Main.getInstance().getSubversion() == 17)return;
	      try {
	        Object entityPlayer = p.getClass().getDeclaredMethod("getHandle", new Class[0]).invoke(p, new Object[0]);
	        Object playerConnection = entityPlayer.getClass().getDeclaredField("playerConnection").get(entityPlayer);
	        Object packet = this.entityStatusPacketClass.getConstructor(new Class[] { this.entityClass, byte.class }).newInstance(new Object[] { entityPlayer, Byte.valueOf((byte)28) });
	        this.playerConnectionClass.getDeclaredMethod("sendPacket", new Class[] { this.packetClass }).invoke(playerConnection, new Object[] { packet });
	      } catch (Throwable e) {
	        throw new RuntimeException("Error while sending entity status 28", e);
	      } 
	    }
	  }

}
