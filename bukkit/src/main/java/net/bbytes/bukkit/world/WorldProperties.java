package net.bbytes.bukkit.world;

import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

public class WorldProperties {

    private boolean iceSnowMelting = false; // Whether ice and snow can melt
    private boolean leafDecay = false; // Whether leaves decay
    private boolean blockGravity = false; // Whether gravel and sand falls
    private boolean liquidFlow = false; // Whether water and lava flows
    private boolean snowFall = false; // Whether snow forms
    private boolean growth = false; // Whether crops, mushrooms, vines, grass and mycelium grows
    private boolean fireSpread = false; 
    
    private boolean mobSpawn = false;
    private boolean tntExplode = false;
    private boolean mobGriefing = false;

    private boolean weatherLock = true;
    private boolean timeLock = true;

    private World.Environment environment = World.Environment.NORMAL;
    private long seed = 0;
    private ConfigurableWorldType configurableWorldType = ConfigurableWorldType.VOID;
    private ConfigurableWorld parentWorld;

    public WorldProperties(ConfigurableWorld parentWorld) {
        this.parentWorld = parentWorld;
    }
    public WorldProperties(Map<String, Object> map, ConfigurableWorld parentWorld){
        this.parentWorld = parentWorld;
        if(map.containsKey("iceSnowMelting")) this.iceSnowMelting = (boolean) map.get("iceSnowMelting");
        if(map.containsKey("leafDecay")) this.leafDecay = (boolean) map.get("leafDecay");
        if(map.containsKey("blockGravity")) this.blockGravity = (boolean) map.get("blockGravity");
        if(map.containsKey("liquidFlow")) this.liquidFlow = (boolean) map.get("liquidFlow");
        if(map.containsKey("snowFall")) this.snowFall = (boolean) map.get("snowFall");
        if(map.containsKey("growth")) this.growth = (boolean) map.get("growth");
        if(map.containsKey("fireSpread")) this.fireSpread = (boolean) map.get("fireSpread");
        if(map.containsKey("mobSpawn")) this.mobSpawn = (boolean) map.get("mobSpawn");
        if(map.containsKey("tntExplode")) this.tntExplode = (boolean) map.get("tntExplode");
        if(map.containsKey("mobGriefing")) this.mobGriefing = (boolean) map.get("mobGriefing");
        if(map.containsKey("weatherLock")) this.weatherLock = (boolean) map.get("weatherLock");
        if(map.containsKey("timeLock")) this.timeLock = (boolean) map.get("timeLock");

        if(map.containsKey("environment")) this.environment = World.Environment.getEnvironment((int) map.get("environment"));
        if(map.containsKey("configurableWorldType")) this.configurableWorldType = ConfigurableWorldType.valueOf((String) map.get("configurableWorldType"));
        if(map.containsKey("seed")) this.seed = Long.parseLong((String) map.get("seed"));
    }
    
    
    public Map<String, Object> serialize(){
        WorldProperties wo = new WorldProperties(null);
        
        Map<String, Object> map = new HashMap<>();
        if(this.iceSnowMelting != wo.iceSnowMelting) map.put("iceSnowMelting", this.iceSnowMelting);
        if(this.leafDecay != wo.leafDecay) map.put("leafDecay", this.leafDecay);
        if(this.blockGravity != wo.blockGravity) map.put("blockGravity", this.blockGravity);
        if(this.liquidFlow != wo.liquidFlow) map.put("liquidFlow", this.liquidFlow);
        if(this.snowFall != wo.snowFall) map.put("snowFall", this.snowFall);
        if(this.growth != wo.growth) map.put("growth", this.growth);
        if(this.fireSpread != wo.fireSpread) map.put("fireSpread", this.fireSpread);
        if(this.mobSpawn != wo.mobSpawn) map.put("mobSpawn", this.mobSpawn);
        if(this.tntExplode != wo.tntExplode) map.put("tntExplode", this.tntExplode);
        if(this.mobGriefing != wo.mobGriefing) map.put("mobGriefing", this.mobGriefing);
        if(this.weatherLock != wo.weatherLock) map.put("weatherLock", this.weatherLock);
        if(this.timeLock != wo.timeLock) map.put("timeLock", this.timeLock);

        if(this.environment != wo.environment) map.put("environment", this.environment.getId());
        if(!this.configurableWorldType.equals(wo.configurableWorldType)) map.put("configurableWorldType", this.configurableWorldType.name());
        if(this.seed != wo.seed) map.put("seed", this.seed + "");
        
        
        return map;
    }

    public boolean isIceSnowMelting() {
        return iceSnowMelting;
    }

    public void setIceSnowMelting(boolean iceSnowMelting) {
        this.iceSnowMelting = iceSnowMelting;
    }

    public boolean isLeafDecay() {
        return leafDecay;
    }

    public void setLeafDecay(boolean leafDecay) {
        this.leafDecay = leafDecay;
    }

    public boolean isBlockGravity() {
        return blockGravity;
    }

    public void setBlockGravity(boolean blockGravity) {
        this.blockGravity = blockGravity;
    }

    public boolean isLiquidFlow() {
        return liquidFlow;
    }

    public void setLiquidFlow(boolean liquidFlow) {
        this.liquidFlow = liquidFlow;
    }

    public boolean isSnowFall() {
        return snowFall;
    }

    public void setSnowFall(boolean snowFall) {
        this.snowFall = snowFall;
    }

    public boolean isGrowth() {
        return growth;
    }

    public void setGrowth(boolean growth) {
        this.growth = growth;
    }

    public boolean isFireSpread() {
        return fireSpread;
    }

    public void setFireSpread(boolean fireSpread) {
        this.fireSpread = fireSpread;
        if(parentWorld.isWorldLoaded()) parentWorld.getLoadedWorld().setGameRuleValue("doFireTick", Boolean.toString(this.fireSpread));
    }

    public boolean isMobSpawn() {
        return mobSpawn;
    }

    public void setMobSpawn(boolean mobSpawn) {
        this.mobSpawn = mobSpawn;
    }

    public boolean isTntExplode() {
        return tntExplode;
    }

    public void setTntExplode(boolean tntExplode) {
        this.tntExplode = tntExplode;
    }

    public boolean isMobGriefing() {
        return mobGriefing;
    }

    public void setMobGriefing(boolean mobGriefing) {
        this.mobGriefing = mobGriefing;
        if(parentWorld.isWorldLoaded()) parentWorld.getLoadedWorld().setGameRuleValue("mobGriefing", Boolean.toString(this.mobGriefing));
    }

    public boolean isWeatherLock() {
        return weatherLock;
    }

    public void setWeatherLock(boolean weatherLock) {
        this.weatherLock = weatherLock;
    }

    public boolean isTimeLock() {
        return timeLock;
    }

    public void setTimeLock(boolean timeLock) {
        this.timeLock = timeLock;
        if(parentWorld.isWorldLoaded()) parentWorld.getLoadedWorld().setGameRuleValue("doDaylightCycle", Boolean.toString(!this.timeLock));
    }

    public World.Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(World.Environment environment) {
        this.environment = environment;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public ConfigurableWorldType getConfigurableWorldType() {
        return configurableWorldType;
    }

    public void setConfigurableWorldType(ConfigurableWorldType configurableWorldType) {
        this.configurableWorldType = configurableWorldType;
    }

    public ConfigurableWorld getParentWorld() {
        return parentWorld;
    }

    public void setParentWorld(ConfigurableWorld parentWorld) {
        this.parentWorld = parentWorld;
    }
}
