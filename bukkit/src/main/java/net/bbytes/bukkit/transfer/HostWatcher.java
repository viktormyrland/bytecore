package net.bbytes.bukkit.transfer;

import net.bbytes.bukkit.Main;

import java.io.IOException;
import java.net.Socket;

public class HostWatcher implements Runnable{

    @Override
    public void run() {

        while(Main.getInstance().isEnabled()) {
            try {

                if(Main.getInstance().getBbConnector().isDisconnected()){
                    try {
                        Socket socket = new Socket(Main.getInstance().getBbConnector().HOST, Main.getInstance().getBbConnector().PORT);
                        socket.close();
                        Main.getInstance().getBbConnector().connect();
                    } catch (IOException e) {
                    }


                }

                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


}