package io.dpteam.NoSmoking;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;

public class SmokeEffect {
	String world;
	int x;
	int y;
	int z;
	int data;
	int radius;
	int chance;
	int rate;

	public SmokeEffect() {
		super();
	}

	public void play() {
		World w = Bukkit.getWorld(this.world);
		Location loc = new Location(w, (double)this.x, (double)this.y, (double)this.z);
		w.playEffect(loc, Effect.SMOKE, this.data);
	}

	public Location getLocation() {
		return new Location(Bukkit.getWorld(this.world), (double)this.x, (double)this.y, (double)this.z);
	}

	public String toString() {
		return "[" + this.world + "] (" + this.x + ", " + this.y + ", " + this.z + ") data=" + this.data + ", radius=" + this.radius + ", chance=" + this.chance + ", rate=" + this.rate + "]";
	}
}
