package net.bbytes.bukkit.world;

import net.bbytes.bukkit.Main;

public class WorldUnloaderRunnable implements Runnable{
    @Override
    public void run() {
        for(ConfigurableWorld world : Main.getInstance().getWorldManager().getWorldList()){
            if(world.getLoadedWorld() != null){
                if(world.getLoadedWorld().getPlayers().size() == 0){
                    world.unloadWorld();
                }
            }
        }
    }
}
