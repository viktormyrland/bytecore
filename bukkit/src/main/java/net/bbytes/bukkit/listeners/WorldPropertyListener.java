package net.bbytes.bukkit.listeners;

import net.bbytes.bukkit.Main;
import net.bbytes.bukkit.world.ConfigurableWorld;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.StructureGrowEvent;

public class WorldPropertyListener implements Listener {

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e){
         ConfigurableWorld configurableWorld = Main.getInstance().getWorldManager().getWorld(e.getWorld());
         if(configurableWorld != null) if(configurableWorld.getWorldProperties().isWeatherLock())
             e.setCancelled(true);
    }

//    @EventHandler
//    public void onBlockGravity(BlockPhysicsEvent e){
//        if(!e.getBlock().getType().hasGravity())return;
//
//        ConfigurableWorld configurableWorld = Main.getInstance().getWorldManager().getWorld(e.getBlock().getWorld());
//        if(configurableWorld != null) if(!configurableWorld.getWorldProperties().isBlockGravity()){
//                    e.setCancelled(true);
//            }
//
//    }

    @EventHandler
    public void onGravityBlockPlace(EntityChangeBlockEvent e){
        if(!(e.getEntityType()==EntityType.FALLING_BLOCK && e.getTo()==Material.AIR))return;

        ConfigurableWorld configurableWorld = Main.getInstance().getWorldManager().getWorld(e.getBlock().getWorld());
        if(configurableWorld != null) if(!configurableWorld.getWorldProperties().isBlockGravity()) {
            e.setCancelled(true);
            e.getBlock().getState().update(false, false);
        }
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent e){
        ConfigurableWorld configurableWorld = Main.getInstance().getWorldManager().getWorld(e.getBlock().getWorld());
        if(configurableWorld != null) if(!configurableWorld.getWorldProperties().isLiquidFlow()){
            if(e.getBlock().isLiquid()) e.setCancelled(true);
        }
    }

    @EventHandler
    public void onTNTExplode(EntityExplodeEvent e){
        if(e.getEntityType().equals(EntityType.PRIMED_TNT)){
            ConfigurableWorld configurableWorld = Main.getInstance().getWorldManager().getWorld(e.getLocation().getWorld());
            if(configurableWorld != null) if(!configurableWorld.getWorldProperties().isTntExplode()){
                e.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e){
        ConfigurableWorld configurableWorld = Main.getInstance().getWorldManager().getWorld(e.getLocation().getWorld());
        if(configurableWorld != null) if(!configurableWorld.getWorldProperties().isMobSpawn()){
            if(e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL)
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void onGrassSpread(BlockSpreadEvent e){
        ConfigurableWorld configurableWorld = Main.getInstance().getWorldManager().getWorld(e.getBlock().getWorld());
        if(configurableWorld != null) if(!configurableWorld.getWorldProperties().isGrowth()){
            if(e.getSource().getType() == Material.GRASS_BLOCK || e.getSource().getType() == Material.MYCELIUM)
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void onGrassSpread(BlockFormEvent e) {

        ConfigurableWorld configurableWorld = Main.getInstance().getWorldManager().getWorld(e.getBlock().getWorld());
        if (configurableWorld != null) {
            if (!configurableWorld.getWorldProperties().isSnowFall()) {
                if(e.getNewState().getType() == Material.SNOW
                || e.getNewState().getType() == Material.ICE)
                    e.setCancelled(true);
            }
            if (!configurableWorld.getWorldProperties().isLiquidFlow()) {
                if(e.getNewState().getType() == Material.OBSIDIAN
                        || e.getNewState().getType() == Material.COBBLESTONE
                        || e.getNewState().getType() == Material.STONE)
                    e.setCancelled(true);
            }

        }
    }

    @EventHandler
    public void onTreeGrow(StructureGrowEvent e){
        ConfigurableWorld configurableWorld = Main.getInstance().getWorldManager().getWorld(e.getWorld());
        if(configurableWorld != null) if(!configurableWorld.getWorldProperties().isGrowth()){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlantGrow(BlockGrowEvent e){
        ConfigurableWorld configurableWorld = Main.getInstance().getWorldManager().getWorld(e.getBlock().getWorld());
        if(configurableWorld != null) if(!configurableWorld.getWorldProperties().isGrowth()){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent e){
        ConfigurableWorld configurableWorld = Main.getInstance().getWorldManager().getWorld(e.getBlock().getWorld());
        if(configurableWorld != null) if(!configurableWorld.getWorldProperties().isLeafDecay()){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent e){
        if(e.getBlock().getType().equals(Material.ICE)
        || e.getBlock().getType().equals(Material.PACKED_ICE)
        || e.getBlock().getType().equals(Material.FROSTED_ICE)
        || e.getBlock().getType().equals(Material.SNOW)
        || e.getBlock().getType().equals(Material.SNOW_BLOCK)){
            ConfigurableWorld configurableWorld = Main.getInstance().getWorldManager().getWorld(e.getBlock().getWorld());
            if(configurableWorld != null) if(!configurableWorld.getWorldProperties().isIceSnowMelting()){
                e.setCancelled(true);
            }
        }

    }

}
