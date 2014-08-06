package com.cokoc.fancyroulette;

import java.io.Serializable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class SerializableLocation implements Serializable {
	private static final long serialVersionUID = 1293841631691062060L;
	private String world;
    public double x;
    public double y;
    public double z;
    public float pitch;
    public float yaw;
 
    public SerializableLocation(World world, Double x, Double y, Double z) {
        this.world = world.getName();
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = 0;
        this.yaw = 0;
    }
    
    public SerializableLocation(World world, int x, int y, int z) {
		this.world = world.getName();
		this.x = (double) x;
		this.y = (double) y;
		this.z = (double) z;
		this.pitch = 0;
		this.yaw = 0;
	}
 
    public SerializableLocation(Location location) {
        this.world = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.pitch = location.getPitch();
        this.yaw = location.getYaw();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o)
        	return true;
        if (o == null || getClass() != o.getClass()) 
        	return false;
        SerializableLocation loc = (SerializableLocation) o;
        if (x != loc.x)
        	return false;
        if (y != loc.y)
        	return false;
        if (z != loc.z)
        	return false;

        return true;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (int)x;
        hash = 23 * hash + (int)y;
        hash = 23 * hash + (int)z;
        return hash;
    }
 
    public Location toLocation() {
        Location l = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
        return l;
    }
}
