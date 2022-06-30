package net.bbytes.bukkit.warp;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.LinkedHashMap;
import java.util.Map;

public class Vector5 {

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public Vector5(Location loc){
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();
    }

    public Vector5(Map<String, Object> map){
        this.x = (double) map.get("x");
        this.y = (double) map.get("y");
        this.z = (double) map.get("z");
        this.yaw = ((Double)map.get("yaw")).floatValue();
        this.pitch =  ((Double)map.get("pitch")).floatValue();
    }

    public Map<String, Object> serialize(){
        Map<String, Object> map = new LinkedHashMap<>();

        map.put("x", this.x);
        map.put("y", this.y);
        map.put("z", this.z);
        map.put("yaw", this.yaw);
        map.put("pitch", this.pitch);

        return map;
    }

    public Location generateLocation(World w){
        return new Location(w, x, y, z, yaw, pitch);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
}
