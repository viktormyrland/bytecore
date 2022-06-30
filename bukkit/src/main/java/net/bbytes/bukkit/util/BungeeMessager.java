package net.bbytes.bukkit.util;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.bbytes.bukkit.Main;
import net.bbytes.bukkit.command.WhitelistCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BungeeMessager implements PluginMessageListener {
	
	
	Main api = Main.getInstance();
	ExecutorService executor = null;
	
	public BungeeMessager(){
		Bukkit.getMessenger().registerOutgoingPluginChannel(Main.getInstance(), "BungeeCord");
		Bukkit.getMessenger().registerIncomingPluginChannel(Main.getInstance(), "BungeeCord", this);
	}
	
	public void forwardBungeeMessage(String subchannel, List<Object> data) {
		Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
		if(player != null) forwardBungeeMessage(subchannel, data, player);
	}
	
	public void forwardBungeeMessage(String subchannel, List<Object> data, Player player) {
		if(data == null)data = new ArrayList<Object>();
		data.add(0, System.currentTimeMillis());
		sendBungeeMessage("Forward", "ALL", subchannel, data, player);		
	}
	
	
	public void sendGlobalAllChatMessage(UUID from, String chatMessage) {
		List<Object> data = new ArrayList<Object>();
		data.add(from.toString());
		data.add(chatMessage);
		forwardBungeeMessage("Chat", data);
	}
	
	
	
	public void sendPlayerChatMessage(String chatMessage, String player) {
		if(Bukkit.getPlayerExact(player) != null) { 
			Bukkit.getPlayerExact(player).sendMessage(chatMessage);
			return;
		}
		if(player.equalsIgnoreCase("ALL")) Bukkit.broadcastMessage(chatMessage);
		
		List<Object> data = new ArrayList<Object>();
		data.add(player);
		data.add(chatMessage);
		
		forwardBungeeMessage("SendChatMessage", data);
		//sendBungeeMessage("Message", player, chatMessage);
	}
	
	public void sendBungeeMessage(String arg1, String arg2, String arg3) {
		sendBungeeMessage(arg1, arg2, arg3, (List<Object>)null);
	}
	
	public void sendBungeeMessage(String arg1, String arg2, String arg3, List<Object> data) {
		Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
		if(player != null) sendBungeeMessage(arg1, arg2, arg3, data, player);
	}
	

	public void sendBungeeMessage(String arg1, String arg2, String arg3, Player player) {
		sendBungeeMessage(arg1,arg2,arg3,null,player);
	}
	
	public void sendBungeeMessage(String arg1, String arg2, String arg3, List<Object> data, Player player) {
		

		
		if(arg1.equals("Forward")) arg3 = "FW;" + arg3;
		
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF(arg1); // FORWARD / arg1
		out.writeUTF(arg2); // ALL / arg2
		out.writeUTF(arg3); // CommandTeleport / arg3
		
		ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
		DataOutputStream msgout = new DataOutputStream(msgbytes);
		
		try {
			if(data != null)
				for(Object obj : data) {
					if(obj instanceof String) msgout.writeUTF((String) obj);
					else if(obj instanceof Integer) msgout.writeInt((Integer) obj);
					else if(obj instanceof Boolean) msgout.writeBoolean((Boolean) obj);
					else if(obj instanceof Short) msgout.writeShort((Short) obj);
					else if(obj instanceof Double) msgout.writeDouble((Double) obj);
					else if(obj instanceof Float) msgout.writeFloat((Float) obj);
					else if(obj instanceof Long) msgout.writeLong((Long) obj);
					else if(obj instanceof Character) msgout.writeChar((Character)obj);
				}

			
			
		}catch(IOException ex) {
			ex.printStackTrace();
		}
		
		out.writeShort(msgbytes.toByteArray().length);
		out.write(msgbytes.toByteArray());

		
		
		
		player.sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
	}


	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if(executor == null)executor = Executors.newCachedThreadPool();
		 if (!(channel.equals("BungeeCord"))) {
		 	return;
		    }
		 
		 ByteArrayDataInput in = ByteStreams.newDataInput(message);

		 String subchannel = in.readUTF();
		 
		 /*
		  * Forward message handler
		  */
		 DataInputStream msgin = null;
		 
		 long forwardSendTime = 0;
		 
		 if(subchannel.split(";")[0].equals("FW")) {
			 subchannel = subchannel.split(";")[1];
			 short len = in.readShort();
			 byte[] msgbytes = new byte[len];
			 in.readFully(msgbytes);
			 msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
			 
			 try {
				forwardSendTime = msgin.readLong();
				if(System.currentTimeMillis() - forwardSendTime > 5 * 1000) {
					//Bukkit.getLogger().info("Discarding a FORWARD which was sent " + (System.currentTimeMillis() - forwardSendTime) + " ms ago.");
					return;
				}
			} catch (IOException e) {e.printStackTrace();}
			
		 }
		 
			
		 
		 /*
		  * Global messaging receive.
		  * Add more subchannel listeners under here
		  */
		 try {
			 if(subchannel.equals("SendChatMessage")) {
				 String playerReceived = msgin.readUTF();
				 String messageIn = msgin.readUTF();
				
				 if(playerReceived.equalsIgnoreCase("OTHERS")) {
					 for(Player all : Bukkit.getOnlinePlayers())
						 all.sendMessage(messageIn.replaceAll("&", "§"));
				 }
			 }else if(subchannel.equals("UpdateWhitelist")) {
				 WhitelistCommand.loadMemoryWhitelist();
			 }
		 }catch(IOException ex) {ex.printStackTrace();}
		 
		 
	}

}
