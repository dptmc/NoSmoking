package io.dpteam.NoSmoking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import net.minecraft.server.v1_16_R3.ChunkPosition;
import net.minecraft.server.v1_16_R3.PacketPlayOutExplosion;
import net.minecraft.server.v1_16_R3.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEnderSignal;
import org.bukkit.entity.EnderSignal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	boolean version_check = true;
	boolean language_save = false;
	String language = "english";
	boolean soundsmoke = true;
	boolean allsmoke = false;
	Long ticktime = 5L;
	Long windtick = 2500L;
	Long plfltick = 60L;
	int wand = 263;
	boolean usepermissions = true;
	int dist_eff = 32;
	int dist_ent = 32;
	int dist_sfx = 32;
	int dist_sng = 100;
	int dist_lht = 100;
	int timecount = 0;
	Long timer = 0L;
	Long timern = 0L;
	NSPointList smog;
	HashMap pcfg = new HashMap();
	int idcount = 1;
	String spx;
	int wind;
	int[] arrcount;
	String[] lightmode;
	Boolean[] playsong;
	Long[] musstart;
	Long[] muslen;
	List temp;
	String[] effects;
	String[] songs;
	String[] sfxs;
	int[] potions;
	ArrayList playlist;
	List expl_blocks;
	float expl_f;
	Random random;
	int fid;
	int tid;
	int wid;
	Logger log;
	NSCommander Commander;
	NSListener l;
	NSUtil u;

	public NoSmoking() {
		super();
		this.spx = ChatColor.AQUA + "[NS]: " + ChatColor.WHITE;
		this.wind = 4;
		this.arrcount = new int[12];
		this.lightmode = new String[]{"anytime", "day", "night", "day-storm", "night-storm", "storm"};
		this.playsong = new Boolean[]{false, false, false, false, false, false, false, false, false, false, false, false};
		this.musstart = new Long[]{0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};
		this.muslen = new Long[]{177000L, 192000L, 352000L, 186000L, 169000L, 203000L, 101000L, 152000L, 189000L, 249000L, 72000L, 240000L};
		this.temp = new ArrayList();
		this.effects = new String[]{"smoke", "flame", "signal", "potion", "pearl", "eye", "song", "sfx", "light", "explosion"};
		this.songs = new String[]{"13", "cat", "blocks", "chirp", "far", "mall", "mellohi", "stal", "strad", "ward", "11disc", "wait"};
		this.sfxs = new String[]{"blaze", "bow", "click1", "click2", "door", "extinguish", "ghast shoot", "ghast shriek", "zmb iron door", "zmb wood door", "zmb destroy"};
		this.potions = new int[]{0, 1, 2, 3, 4, 5, 8, 9, 10, 12};
		this.playlist = new ArrayList();
		this.expl_blocks = new ArrayList();
		this.expl_f = 0.1F;
		this.random = new Random();
		this.fid = 0;
		this.tid = 0;
		this.wid = 0;
		this.log = Logger.getLogger("Minecraft");
	}

	@Override
	public void onDisable() {
		this.log.info("[NS]: Ooofff... you can smoke now...");
		System.out.println("NoSmoking Disabled");
	}

	public void onEnable() {
		this.u = new NSUtil(this, this.version_check, this.language_save, this.language, "no-smoking", "No Smoking!", "smoke", "&3[NS]&f ");
		this.smog = new NSPointList(this);
		this.smog.LoadPList();
		this.LoadCfg();
		this.SaveCfg();
		this.ArrCountInit();
		this.Commander = new NSCommander(this);
		this.getCommand("smoke").setExecutor(this.Commander);
		PluginManager pm = this.getServer().getPluginManager();
		this.l = new NSListener(this);
		pm.registerEvents(this.l, this);
		this.Rst();
		System.out.println("NoSmoking Enabled");
	}

	public int ParseDirection(String dir_str) {
		int d = 10;
		if (dir_str.equalsIgnoreCase("n")) {
			d = 7;
		}

		if (dir_str.equalsIgnoreCase("nw")) {
			d = 8;
		}

		if (dir_str.equalsIgnoreCase("ne")) {
			d = 6;
		}

		if (dir_str.equalsIgnoreCase("s")) {
			d = 1;
		}

		if (dir_str.equalsIgnoreCase("sw")) {
			d = 2;
		}

		if (dir_str.equalsIgnoreCase("se")) {
			d = 0;
		}

		if (dir_str.equalsIgnoreCase("w")) {
			d = 5;
		}

		if (dir_str.equalsIgnoreCase("e")) {
			d = 3;
		}

		if (dir_str.equalsIgnoreCase("calm")) {
			d = 4;
		}

		if (dir_str.equalsIgnoreCase("up")) {
			d = 4;
		}

		if (dir_str.equalsIgnoreCase("wind")) {
			d = 9;
		}

		if (dir_str.equalsIgnoreCase("all")) {
			d = 10;
		}

		if (dir_str.equalsIgnoreCase("rnd")) {
			d = 11;
		}

		if (dir_str.equalsIgnoreCase("random")) {
			d = 11;
		}

		return d;
	}

	public String Dir2Wind(int dir) {
		String wd = "unknown";
		if (dir == 7) {
			wd = "N";
		}

		if (dir == 8) {
			wd = "NW";
		}

		if (dir == 6) {
			wd = "NE";
		}

		if (dir == 1) {
			wd = "S";
		}

		if (dir == 2) {
			wd = "SW";
		}

		if (dir == 0) {
			wd = "SE";
		}

		if (dir == 5) {
			wd = "W";
		}

		if (dir == 3) {
			wd = "E";
		}

		if (dir == 4) {
			wd = "calm";
		}

		if (dir == 9) {
			wd = "wind";
		}

		if (dir == 10) {
			wd = "all";
		}

		if (dir == 11) {
			wd = "random";
		}

		return wd;
	}

	public String NewId() {
		String id = "id";
		boolean newfound = false;

		do {
			newfound = false;

			for(int i = 0; i < this.smog.size(); ++i) {
				newfound = false;
				if (this.smog.get(i).id.equalsIgnoreCase("id" + Integer.toString(this.idcount))) {
					newfound = true;
					++this.idcount;
				}
			}
		} while(newfound);

		id = "id" + Integer.toString(this.idcount);
		++this.idcount;
		return id;
	}

	public void AddNewSmoke(Location loc, Player p) {
		String pname = p.getName();
		int curdir = 0;
		int cureff = ((PlayerSettings)this.pcfg.get(pname)).effect;
		int currate = ((PlayerSettings)this.pcfg.get(pname)).rate;
		if (currate > 10 && cureff != 8 && cureff != 9) {
			currate = 10;
		}

		if (cureff == 0) {
			curdir = ((PlayerSettings)this.pcfg.get(pname)).wd;
		} else if (cureff == 3) {
			curdir = ((PlayerSettings)this.pcfg.get(pname)).pot;
		} else if (cureff == 6) {
			curdir = ((PlayerSettings)this.pcfg.get(pname)).song;
			currate = 10;
		} else if (cureff == 7) {
			curdir = ((PlayerSettings)this.pcfg.get(pname)).sfx;
		} else if (cureff == 8) {
			curdir = ((PlayerSettings)this.pcfg.get(pname)).chance + 1000 * ((PlayerSettings)this.pcfg.get(pname)).lmode;
			if (currate > 25 && currate < 75) {
				currate = 50;
			} else if (currate >= 75) {
				currate = 100;
			} else {
				currate = 10;
			}
		} else if (cureff == 9) {
			if (currate > 25 && currate < 75) {
				currate = 50;
			} else if (currate >= 75) {
				currate = 100;
			} else if (currate >= 11 && currate <= 24) {
				currate = 10;
			}
		}

		String id = "";
		if (!((PlayerSettings)this.pcfg.get(pname)).id.isEmpty()) {
			id = ((PlayerSettings)this.pcfg.get(pname)).id;
		} else {
			id = this.NewId();
		}

		((PlayerSettings)this.pcfg.get(pname)).last_id = id;
		SmokePoint tempsmoke = new SmokePoint(id, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), cureff, curdir, currate, pname, true);
		this.smog.add(tempsmoke);
		this.u.printMSG(p, new Object[]{"msg_effectadded", this.smog.SPtoString(this.smog.size() - 1)});
	}

	public void PlaySmoke(int i) {
		Location loc = this.Smoke2Loc((SmokePoint)this.playlist.get(i));
		World w = loc.getWorld();
		SmokePoint cursmoke = (SmokePoint)this.playlist.get(i);
		switch(cursmoke.effect_type) {
		case 0:
			if (cursmoke.direction == 10) {
				for(int j = 0; j < 9; ++j) {
					w.playEffect(loc, Effect.SMOKE, j);
				}

				return;
			} else {
				if (cursmoke.direction == 9) {
					w.playEffect(loc, Effect.SMOKE, this.wind);
				} else if (cursmoke.direction == 11) {
					w.playEffect(loc, Effect.SMOKE, this.random.nextInt(9));
				} else {
					w.playEffect(loc, Effect.SMOKE, cursmoke.direction);
				}
				break;
			}
		case 1:
			w.playEffect(loc, Effect.MOBSPAWNER_FLAMES, 0);
			break;
		case 2:
			w.playEffect(loc, Effect.ENDER_SIGNAL, 0);
			break;
		case 3:
			if (cursmoke.direction > 0 && cursmoke.direction <= 10) {
				w.playEffect(loc.add(0.5D, 0.0D, 0.5D), Effect.POTION_BREAK, this.potions[cursmoke.direction - 1]);
			} else if (cursmoke.direction == 11) {
				w.playEffect(loc.add(0.5D, 0.0D, 0.5D), Effect.POTION_BREAK, this.potions[this.random.nextInt(10)]);
			} else if (cursmoke.direction == 12) {
				w.playEffect(loc.add(0.5D, 0.0D, 0.5D), Effect.POTION_BREAK, this.potions[this.random.nextInt(10)]);
				w.playEffect(loc.add(0.5D, 0.0D, 0.5D), Effect.POTION_BREAK, this.potions[this.random.nextInt(10)]);
				w.playEffect(loc.add(0.5D, 0.0D, 0.5D), Effect.POTION_BREAK, this.potions[this.random.nextInt(10)]);
			}
			break;
		case 4:
			w.playEffect(loc, Effect.ENDER_SIGNAL, 0);
			w.playEffect(loc.add(0.0D, 1.0D, 0.0D), Effect.ENDER_SIGNAL, 1);
			w.playEffect(loc.add(0.0D, 1.0D, 0.0D), Effect.ENDER_SIGNAL, 2);
			break;
		case 5:
			EnderSignal e = (EnderSignal)w.spawnEntity(loc.add(0.5D, 0.5D, 0.5D), EntityType.ENDER_SIGNAL);
			this.temp.add(e);
			((CraftEnderSignal)e).getHandle().die();
			break;
		case 6:
			if (this.soundsmoke) {
				w.playEffect(loc, Effect.SMOKE, 4);
			}

			if (!this.playsong[cursmoke.direction]) {
				w.playEffect(loc, Effect.RECORD_PLAY, 2256 + cursmoke.direction, 100);
			}
			break;
		case 7:
			if (this.soundsmoke) {
				w.playEffect(loc, Effect.SMOKE, 4);
			}

			w.playEffect(loc, this.SfxEffect(cursmoke.direction), 0);
			break;
		case 8:
			if (this.soundsmoke) {
				w.playEffect(loc, Effect.SMOKE, 4);
			}

			if (this.StrikeBolt(cursmoke.direction, w)) {
				w.strikeLightningEffect(loc);
			}
			break;
		case 9:
			this.FillExplosionBlocks(loc, 5);
			Vec3D v = Vec3D.a(0.0D, 0.0D, 0.0D);
			((CraftServer)Bukkit.getServer()).getHandle().sendPacketNearby(loc.getX(), loc.getY(), loc.getZ(), 64.0D, ((CraftWorld)loc.getWorld()).getHandle().dimension, new PacketPlayOutExplosion(loc.getX(), loc.getY(), loc.getZ(), this.expl_f, this.expl_blocks, v));
		}

	}

	protected void FillExplosionBlocks(Location loc, int r) {
		this.expl_blocks.clear();

		for(int i = -r; i <= r; ++i) {
			for(int j = -r; j <= r; ++j) {
				for(int k = -r; k <= r; ++k) {
					Location la = loc.add((double)i, (double)j, (double)k);
					if (loc.distance(la) <= (double)r) {
						this.expl_blocks.add(new ChunkPosition(la.getBlockX(), la.getBlockY(), la.getBlockZ()));
					}
				}
			}
		}

	}

	public void Tick() {
		if (this.playlist.size() > 0) {
			for(int i = 0; i < this.playlist.size(); ++i) {
				if (!this.allsmoke) {
					if (this.CheckRate(((SmokePoint)this.playlist.get(i)).rate)) {
						this.PlaySmoke(i);
					}
				} else {
					World w = Bukkit.getWorld(((SmokePoint)this.playlist.get(i)).world);
					w.playEffect(this.Smoke2Loc((SmokePoint)this.playlist.get(i)), Effect.SMOKE, 4);
				}
			}
		}

		if (this.CheckRate(10)) {
			this.MusTimeTick();
		}

		this.ArrCountTick();
		this.DespawnEntities();
	}

	public void DespawnEntities() {
		if (this.temp.size() > 0) {
			for(int i = this.temp.size() - 1; i >= 0; --i) {
				if (((Entity)this.temp.get(i)).getTicksLived() > 5 || ((Entity)this.temp.get(i)).isDead()) {
					((Entity)this.temp.get(i)).remove();
					this.temp.remove(i);
				}
			}
		}

	}

	public void ClearEntity() {
		if (this.temp.size() > 0) {
			for(int i = 0; i < this.temp.size(); ++i) {
				((Entity)this.temp.get(i)).remove();
			}
		}

		this.temp.clear();
	}

	public void MusTimeTick() {
		Long curtime = System.currentTimeMillis();

		for(int i = 0; i <= 11; ++i) {
			this.playsong[i] = true;
			if (curtime - this.musstart[i] > this.muslen[i]) {
				this.musstart[i] = curtime;
				this.playsong[i] = false;
			}
		}

	}

	public void Wind() {
		this.wind = this.random.nextInt(9);
	}

	public String Eff2Str(int eff) {
		String str = "unknown";
		if (eff >= 0 && eff <= 9) {
			str = this.effects[eff];
		}

		return str;
	}

	public String Song2Str(int song) {
		String str = "unknown";
		if (song >= 0 && song <= 11) {
			str = this.songs[song];
		}

		return str;
	}

	public String Sfx2Str(int sfx) {
		String str = "unknown";
		if (sfx >= 0 && sfx <= 10) {
			str = this.sfxs[sfx];
		}

		return str;
	}

	public void ArrCountInit() {
		for(int i = 1; i <= 10; this.arrcount[i - 1] = i++) {
		}

		this.arrcount[10] = 50;
		this.arrcount[10] = 100;
	}

	public void ArrCountTick() {
		int var10002;
		for(int i = 1; i <= 10; ++i) {
			var10002 = this.arrcount[i - 1]++;
			if (this.arrcount[i - 1] > i) {
				this.arrcount[i - 1] = 1;
			}
		}

		var10002 = this.arrcount[10]++;
		if (this.arrcount[10] > 50) {
			this.arrcount[10] = 1;
		}

		var10002 = this.arrcount[11]++;
		if (this.arrcount[11] > 100) {
			this.arrcount[11] = 1;
		}

	}

	public boolean CheckRate(int rate) {
		if (rate == 50) {
			return rate == this.arrcount[10];
		} else if (rate == 100) {
			return rate == this.arrcount[11];
		} else {
			return rate == this.arrcount[rate - 1];
		}
	}

	public Effect SfxEffect(int i) {
		Effect eff = Effect.SMOKE;
		switch(i) {
		case 0:
			eff = Effect.BLAZE_SHOOT;
			break;
		case 1:
			eff = Effect.BOW_FIRE;
			break;
		case 2:
			eff = Effect.CLICK1;
			break;
		case 3:
			eff = Effect.CLICK2;
			break;
		case 4:
			eff = Effect.DOOR_TOGGLE;
			break;
		case 5:
			eff = Effect.EXTINGUISH;
			break;
		case 6:
			eff = Effect.GHAST_SHOOT;
			break;
		case 7:
			eff = Effect.GHAST_SHRIEK;
			break;
		case 8:
			eff = Effect.ZOMBIE_CHEW_IRON_DOOR;
			break;
		case 9:
			eff = Effect.ZOMBIE_CHEW_WOODEN_DOOR;
			break;
		case 10:
			eff = Effect.ZOMBIE_DESTROY_DOOR;
		}

		return eff;
	}

	public void LoadCfg() {
		this.soundsmoke = this.getConfig().getBoolean("no-smoking.play-smoke-on-sfx", true);
		this.allsmoke = this.getConfig().getBoolean("no-smoking.replace-effects-with-smoke", false);
		this.wand = this.getConfig().getInt("no-smoking.wand-item", 263);
		this.usepermissions = this.getConfig().getBoolean("no-smoking.use-permissions", true);
		this.ticktime = this.getConfig().getLong("no-smoking.tick.time", 10L);
		this.windtick = this.getConfig().getLong("no-smoking.tick.wind", 2500L);
		this.plfltick = this.getConfig().getLong("no-smoking.tick.play", 60L);
		this.dist_eff = this.getConfig().getInt("no-smoking.distance.effect", 32);
		this.dist_ent = this.getConfig().getInt("no-smoking.distance.entity", 32);
		this.dist_sfx = this.getConfig().getInt("no-smoking.distance.sound", 48);
		this.dist_sng = this.getConfig().getInt("no-smoking.distance.song", 100);
		this.dist_lht = this.getConfig().getInt("no-smoking.distance.lightning", 100);
		this.version_check = this.getConfig().getBoolean("general.check-updates", true);
	}

	public void SaveCfg() {
		this.getConfig().set("general.check-updates", this.version_check);
		this.getConfig().set("no-smoking.play-smoke-on-sfx", this.soundsmoke);
		this.getConfig().set("no-smoking.replace-effects-with-smoke", this.allsmoke);
		this.getConfig().set("no-smoking.wand-item", this.wand);
		this.getConfig().set("no-smoking.use-permissions", this.usepermissions);
		this.getConfig().set("no-smoking.tick.time", this.ticktime);
		this.getConfig().set("no-smoking.tick.play", this.plfltick);
		this.getConfig().set("no-smoking.tick.wind", this.windtick);
		this.getConfig().set("no-smoking.distance.effect", this.dist_eff);
		this.getConfig().set("no-smoking.distance.entity", this.dist_ent);
		this.getConfig().set("no-smoking.distance.song", this.dist_sng);
		this.getConfig().set("no-smoking.distance.sound", this.dist_sfx);
		this.getConfig().set("no-smoking.distance.lightning", this.dist_lht);
		this.saveConfig();
	}

	public boolean CheckPerm(Player player, String node) {
		return this.usepermissions ? player.hasPermission(node) : player.isOp();
	}

	public void FillPlayList() {
		boolean incl = false;
		this.playlist.clear();
		Player[] players = Bukkit.getServer().getOnlinePlayers();
		if (this.smog.PList.size() > 0 && players.length > 0) {
			for(int i = 0; i < this.smog.PList.size(); ++i) {
				incl = false;
				Player[] var7 = players;
				int var6 = players.length;

				for(int var5 = 0; var5 < var6; ++var5) {
					Player p = var7[var5];
					if (this.smog.get(i).showpoint) {
						int t = this.smog.get(i).effect_type;
						if (this.smog.get(i).world.equalsIgnoreCase(p.getWorld().getName())) {
							double dist = this.smog.GetLoc(i).distance(p.getLocation());
							if ((t < 0 || t > 3) && t != 9) {
								if (t >= 4 && t <= 5) {
									if (dist <= (double)this.dist_ent) {
										incl = true;
										break;
									}
								} else if (t == 6) {
									if (dist <= (double)this.dist_sng) {
										incl = true;
										break;
									}
								} else if (t == 7) {
									if (dist <= (double)this.dist_sfx) {
										incl = true;
										break;
									}
								} else if (t == 8 && dist <= (double)this.dist_lht) {
									incl = true;
									break;
								}
							} else if (dist <= (double)this.dist_eff) {
								incl = true;
								break;
							}
						}
					}
				}

				if (incl) {
					this.playlist.add(this.smog.get(i));
				}
			}
		}

	}

	public Location Smoke2Loc(SmokePoint sp) {
		Location loc = new Location(Bukkit.getWorld(sp.world), (double)sp.x, (double)sp.y, (double)sp.z);
		return loc;
	}

	public boolean StrikeBolt(int param, World w) {
		int lightmode = param / 1000;
		int chance = param % 1000;
		boolean strike = false;
		boolean day = this.day(w);
		boolean storm = w.hasStorm();
		if (this.random.nextInt(100) <= chance) {
			switch(lightmode) {
			case 0:
				strike = true;
				break;
			case 1:
				strike = day;
				break;
			case 2:
				strike = !day;
				break;
			case 3:
				strike = day && storm;
				break;
			case 4:
				strike = !day && storm;
				break;
			case 5:
				strike = storm;
			}
		}

		return strike;
	}

	public boolean day(World w) {
		long time = w.getTime();
		return time < 12300L || time > 23850L;
	}

	public void Rst() {
		Bukkit.getScheduler().cancelTask(this.fid);
		Bukkit.getScheduler().cancelTask(this.tid);
		Bukkit.getScheduler().cancelTask(this.wid);
		Long curtime = System.currentTimeMillis();
		this.ClearEntity();
		this.FillPlayList();

		for(int i = 0; i <= 10; ++i) {
			this.playsong[i] = false;
			this.musstart[i] = curtime;
		}

		this.fid = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				NoSmoking.this.FillPlayList();
			}
		}, 30L, this.plfltick);
		this.tid = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				NoSmoking.this.Tick();
			}
		}, 30L, this.ticktime);
		this.wid = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				NoSmoking.this.Wind();
			}
		}, 60L, this.windtick);
	}
}
