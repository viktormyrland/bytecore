package net.bbytes.bukkit.world;

import net.bbytes.bukkit.Main;

import java.util.Map;

public class RecycledConfigurableWorld extends ConfigurableWorld {

    private long recycled = 0;
    private String recycledBy = "Unknown";

    public long getRecycled() {
        return recycled;
    }

    public void setRecycled(long recycled) {
        this.recycled = recycled;
    }

    public RecycledConfigurableWorld(Map<String, Object> map, String name) {
        super(map, name);
        if(map.containsKey("recycled"))  this.recycled = ((Number) map.get("recycled")).longValue();
        if(map.containsKey("recycledBy"))  this.recycledBy = (String) map.get("recycledBy");
    }

    public String getRecycledBy() {
        return recycledBy;
    }

    public void setRecycledBy(String recycledBy) {
        this.recycledBy = recycledBy;
    }

    public void restore(){
        Main.getInstance().getWorldManager().getWorldList().add(this);
        Main.getInstance().getWorldManager().getRecycleBin().getRecycledWorldsList().remove(this);
    }
}
