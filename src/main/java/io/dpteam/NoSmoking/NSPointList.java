package io.dpteam.NoSmoking;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

public class NSPointList {
	NoSmoking plg;
	Random rnd = new Random();
	ArrayList PList = new ArrayList();
	protected BufferedReader rdr;
	protected BufferedWriter wtr;
	Logger log = Logger.getLogger("Minecraft");

	public NSPointList(NoSmoking plg) {
		super();
		this.plg = plg;
	}

	public boolean checkLoc(Location loc) {
		if (this.PList.size() > 0) {
			for(int i = this.PList.size() - 1; i >= 0; --i) {
				if (this.CmpSpLoc(i, loc)) {
					return true;
				}
			}
		}

		return false;
	}

	public String checkLoc2Str(Location loc) {
		if (this.PList.size() > 0) {
			for(int i = this.PList.size() - 1; i >= 0; --i) {
				if (this.CmpSpLoc(i, loc)) {
					return this.SPtoString(i);
				}
			}
		}

		return "";
	}

	public void add(SmokePoint sp) {
		this.PList.add(sp);
		this.saveSmokePoints();
	}

	public void remove(int i) {
		this.PList.remove(i);
		this.saveSmokePoints();
	}

	public int remove_id(String id) {
		int count = 0;

		for(int i = this.PList.size() - 1; i >= 0; --i) {
			if (((SmokePoint)this.PList.get(i)).id.equalsIgnoreCase(id)) {
				this.PList.remove(i);
				++count;
			}
		}

		if (count > 0) {
			this.saveSmokePoints();
		}

		return count;
	}

	public boolean ToggleSmoke(String id) {
		boolean show = false;
		if (this.PList.size() > 0) {
			for(int i = this.PList.size() - 1; i >= 0; --i) {
				if (((SmokePoint)this.PList.get(i)).id.equalsIgnoreCase(id)) {
					((SmokePoint)this.PList.get(i)).showpoint = !((SmokePoint)this.PList.get(i)).showpoint;
					show = ((SmokePoint)this.PList.get(i)).showpoint;
				}
			}
		}

		return show;
	}

	public int SwitchSmoke(String id, boolean shp) {
		int count = 0;

		for(int i = this.PList.size() - 1; i >= 0; --i) {
			if (((SmokePoint)this.PList.get(i)).id.equalsIgnoreCase(id)) {
				((SmokePoint)this.PList.get(i)).showpoint = shp;
				++count;
			}
		}

		if (count > 0) {
			this.saveSmokePoints();
			this.plg.FillPlayList();
		}

		return count;
	}

	public int remove_loc(Location loc) {
		int count = 0;

		for(int i = this.PList.size() - 1; i >= 0; --i) {
			if (this.CmpSpLoc(i, loc)) {
				this.PList.remove(i);
				++count;
			}
		}

		if (count > 0) {
			this.saveSmokePoints();
		}

		return count;
	}

	public SmokePoint get(int i) {
		return (SmokePoint)this.PList.get(i);
	}

	public String GetSpStr(int i) {
		String str = "";
		if (i < this.PList.size() && i >= 0) {
			str = ((SmokePoint)this.PList.get(i)).id + "," + ((SmokePoint)this.PList.get(i)).world + "," + Integer.toString(((SmokePoint)this.PList.get(i)).x) + "," + Integer.toString(((SmokePoint)this.PList.get(i)).y) + "," + Integer.toString(((SmokePoint)this.PList.get(i)).z) + "," + Integer.toString(((SmokePoint)this.PList.get(i)).effect_type) + "," + Integer.toString(((SmokePoint)this.PList.get(i)).direction) + "," + Integer.toString(((SmokePoint)this.PList.get(i)).rate) + "," + ((SmokePoint)this.PList.get(i)).cr_name + "," + Boolean.toString(((SmokePoint)this.PList.get(i)).showpoint);
		}

		return str;
	}

	public String SPtoString(int i) {
		String str = "";
		String dirstr = " ";
		int eff = ((SmokePoint)this.PList.get(i)).effect_type;
		int dir = ((SmokePoint)this.PList.get(i)).direction;
		if (eff == 0) {
			dirstr = this.plg.Dir2Wind(dir);
		} else if (eff == 3) {
			dirstr = ":" + Integer.toString(dir) + " ";
		} else if (eff == 6) {
			dirstr = " (" + this.plg.Song2Str(dir) + ") ";
		} else if (eff == 7) {
			dirstr = " (" + this.plg.Sfx2Str(dir) + ") ";
		}

		if (i < this.PList.size() && i >= 0) {
			str = ((SmokePoint)this.PList.get(i)).id + " [" + ((SmokePoint)this.PList.get(i)).world + "] (" + Integer.toString(((SmokePoint)this.PList.get(i)).x) + "," + Integer.toString(((SmokePoint)this.PList.get(i)).y) + "," + Integer.toString(((SmokePoint)this.PList.get(i)).z) + ") " + this.plg.Eff2Str(eff) + dirstr + " r:" + Integer.toString(((SmokePoint)this.PList.get(i)).rate) + " {" + ((SmokePoint)this.PList.get(i)).cr_name + "}";
		}

		return str;
	}

	public int size() {
		return this.PList.size();
	}

	public void loadSmokePoints() {
		try {
			File f = new File(this.plg.getDataFolder() + File.separator + "smokepoints.yml");
			YamlConfiguration spl = new YamlConfiguration();
			if (f.exists()) {
				spl.load(f);
				Iterator var4 = spl.getKeys(false).iterator();

				while(var4.hasNext()) {
					String key = (String)var4.next();
					if (!key.equalsIgnoreCase("point-list-version")) {
						SmokePoint sp = new SmokePoint(spl.getString(key + ".id", this.plg.NewId()), spl.getString(key + ".world"), spl.getInt(key + ".x"), spl.getInt(key + ".y"), spl.getInt(key + ".z"), spl.getInt(key + ".effect.type"), spl.getInt(key + ".effect.param"), spl.getInt(key + ".effect.rate"), spl.getString(key + ".effect.param2"), spl.getString(key + ".creator"), spl.getBoolean(key + ".active"));
						this.PList.add(sp);
					}
				}
			}
		} catch (Exception var6) {
			var6.printStackTrace();
		}

	}

	public void saveSmokePoints() {
		try {
			File f = new File(this.plg.getDataFolder() + File.separator + "smokepoints.yml");
			if (f.exists()) {
				f.delete();
			}

			if (this.PList.size() > 0) {
				YamlConfiguration spl = new YamlConfiguration();
				spl.set("point-list-version", 3);
				spl.options().header("(c)2011-2012, NoSmoking! by fromgate");

				for(int i = 0; i < this.PList.size(); ++i) {
					SmokePoint sp = (SmokePoint)this.PList.get(i);
					String key = sp.id + "-" + sp.world + "-" + sp.x + "-" + sp.y + "-" + sp.z + ".";
					spl.set(key + "id", sp.id);
					spl.set(key + "world", sp.world);
					spl.set(key + "x", sp.x);
					spl.set(key + "y", sp.y);
					spl.set(key + "z", sp.z);
					spl.set(key + "effect.type", sp.effect_type);
					spl.set(key + "effect.param", sp.direction);
					spl.set(key + "effect.param2", sp.text_param);
					spl.set(key + "effect.rate", sp.rate);
					spl.set(key + "creator", sp.cr_name);
					spl.set(key + "active", sp.showpoint);
				}

				spl.save(f);
				f = new File(this.plg.getDataFolder() + File.separator + "smokepoints.cfg");
				if (f.exists()) {
					f.delete();
				}
			}
		} catch (Exception var6) {
			var6.printStackTrace();
		}

	}

	public void LoadPList() {
		this.PList.clear();

		try {
			File pointfile = new File(this.plg.getDataFolder() + File.separator + "smokepoints.cfg");
			if (pointfile.exists()) {
				this.rdr = new BufferedReader(new InputStreamReader(new FileInputStream(pointfile)));
				String line = "";

				while((line = this.rdr.readLine()) != null) {
					if (line != "" && line != "\n") {
						String[] items = line.split(",");
						String tid = "";
						String wname = "";
						int e = 0;
						int d = true;
						int r = 1;
						String crname = "";
						boolean shp = true;
						int x;
						int y;
						int z;
						SmokePoint sp;
						int d;
						if (items.length == 7) {
							tid = items[0];
							wname = items[1];
							x = (int)Double.parseDouble(items[2]);
							y = (int)Double.parseDouble(items[3]);
							z = (int)Double.parseDouble(items[4]);
							d = this.plg.ParseDirection(items[5]);
							crname = items[6];
							sp = new SmokePoint(tid, wname, x, y, z, e, d, r, crname, shp);
							this.PList.add(sp);
						} else if (items.length == 10) {
							tid = items[0];
							wname = items[1];
							x = Integer.parseInt(items[2]);
							y = Integer.parseInt(items[3]);
							z = Integer.parseInt(items[4]);
							int e = Integer.parseInt(items[5]);
							d = Integer.parseInt(items[6]);
							int r = Integer.parseInt(items[7]);
							crname = items[8];
							shp = Boolean.parseBoolean(items[9]);
							sp = new SmokePoint(tid, wname, x, y, z, e, d, r, crname, shp);
							this.PList.add(sp);
						}
					}
				}

				this.rdr.close();
				pointfile.delete();
			}

			this.loadSmokePoints();
			this.saveSmokePoints();
			this.log.info("[NS] Effects loaded: " + Integer.toString(this.PList.size()));
		} catch (Exception var15) {
			this.log.info("[NS] Error reading point-list file");
			var15.printStackTrace();
		}

	}

	public Location GetLoc(int i) {
		Location loc = new Location(Bukkit.getWorld(((SmokePoint)this.PList.get(i)).world), (double)((SmokePoint)this.PList.get(i)).x, (double)((SmokePoint)this.PList.get(i)).y, (double)((SmokePoint)this.PList.get(i)).z);
		return loc;
	}

	public World GetWorld(int i) {
		return Bukkit.getWorld(((SmokePoint)this.PList.get(i)).world);
	}

	public boolean CmpSpLoc(int i, Location loc) {
		return ((SmokePoint)this.PList.get(i)).world.equalsIgnoreCase(loc.getWorld().getName()) && ((SmokePoint)this.PList.get(i)).x == loc.getBlockX() && ((SmokePoint)this.PList.get(i)).y == loc.getBlockY() && ((SmokePoint)this.PList.get(i)).z == loc.getBlockZ();
	}
}
