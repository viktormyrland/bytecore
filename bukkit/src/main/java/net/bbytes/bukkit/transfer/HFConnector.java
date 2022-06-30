package net.bbytes.bukkit.transfer;

import net.bbytes.bukkit.Main;
import org.bukkit.Bukkit;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HFConnector {


    private boolean disconnected = true;

    private Socket socket = null;

    public Thread hostWatcherThread;
    private Thread connectionListenerThread;

    public String HOST = "144.91.95.22";
    public int PORT = 6009;
    public void connect(){
        if(!Main.getInstance().isEnabled())return;


        try {
            Socket socket = new Socket(HOST, PORT);
            setSocket(socket);

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            out.write(0x01);
            out.writeShort(9);
            out.writeUTF(Main.getInstance().CLIENTNAME);



            Bukkit.getConsoleSender().sendMessage("§a[HFTransfer] Authenticating...");

            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                this.connectionListenerThread = new Thread(new ConnectionListener(), "Thread-ConnectionListener");
                this.connectionListenerThread.start();
            }, 5);

            if(hostWatcherThread != null) if(hostWatcherThread.isAlive())hostWatcherThread.stop();


        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage("§c[HFTransfer] Host is not running.");
        }
    }

    public void startThread(){
        hostWatcherThread = new Thread(new HostWatcher(), "Thread-HostWatcher");
        hostWatcherThread.start();
    }

    public void disconnect(){
        try {
            this.socket.close();
        } catch (IOException | NullPointerException ignored) {
        }
    }

    public void sendData(int b, Object[] data){
        sendData(b, Arrays.asList(data));

    }
    public void sendData(int b, List<Object> dataInput){
        if(true){
            System.out.println("Attempted to send data through deprecated HFConnector.");
            System.out.println("Data:");
            for(Object o : dataInput){
                System.out.println(o.toString());
            }
            return;
        }
        final List<Object> finalDataInput = dataInput;
        ExecutorService ex = Executors.newSingleThreadExecutor();
        ex.execute(() -> {
            List<Object> data = finalDataInput;
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(baos);
                if (data == null) data = new ArrayList<>();

                out.writeByte(b);

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                DataOutputStream infoOut = new DataOutputStream(bos);

                infoOut.writeUTF(Main.getInstance().CLIENTNAME);

                for (Object obj : data) {
                    if (obj instanceof String) infoOut.writeUTF((String) obj);
                    else if (obj instanceof Byte) infoOut.writeByte((Byte) obj);
                    else if (obj instanceof Short) infoOut.writeShort((Short) obj);
                    else if (obj instanceof Integer) infoOut.writeInt((Integer) obj);
                    else if (obj instanceof Long) infoOut.writeLong((Long) obj);
                    else if (obj instanceof Float) infoOut.writeFloat((Float) obj);
                    else if (obj instanceof Double) infoOut.writeDouble((Double) obj);
                    else if (obj instanceof Character) infoOut.writeChar((Character) obj);
                    else if (obj instanceof Boolean) infoOut.writeBoolean((Boolean) obj);
                }

                infoOut.flush();

                byte[] information = bos.toByteArray();
                out.writeShort(information.length);
                out.write(information);

                out.flush();

                Socket socket = new Socket(HOST, PORT);
                DataOutputStream socketOut = new DataOutputStream(socket.getOutputStream());

                socketOut.write(baos.toByteArray());

                socketOut.flush();
                out.close();
                socket.close();
                infoOut.close();



            } catch (IOException e) {
                if (e.getMessage().equals("Connection refused: connect")) {
                    setSocket(null);
                    setDisconnected(true);
                }
            }
        });
        ex.shutdown();
    }



    public boolean isDisconnected() {
        return disconnected;
    }

    public void setDisconnected(boolean disconnected) {
        this.disconnected = disconnected;
        if(disconnected && Main.getInstance().isEnabled()){
            Bukkit.getConsoleSender().sendMessage("§c[HFTransfer] Lost connection to host. Trying to reconnect..");
            startThread();
            this.connectionListenerThread.stop();
        }
    }


    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }


}
