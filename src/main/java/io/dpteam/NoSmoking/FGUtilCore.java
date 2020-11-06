package io.dpteam.NoSmoking;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.bukkit.material.*;
import org.bukkit.inventory.*;

public abstract class FGUtilCore {
	JavaPlugin plg;
	private String px = "";
	private String permprefix = "fgutilcore.";
	private String language = "english";
	private String plgcmd = "<command>";
	YamlConfiguration lng;
	private boolean savelng = false;
	protected HashMap msg = new HashMap();
	private char c1 = 'a';
	private char c2 = '2';
	protected String msglist = "";
	private boolean colorconsole = false;
	private Set log_once = new HashSet();
	protected HashMap cmds = new HashMap();
	protected String cmdlist = "";
	PluginDescriptionFile des;
	private Logger log = Logger.getLogger("Minecraft");
	Random random = new Random();
	BukkitTask chId;
	private boolean project_check_version = true;
	private String project_id = "";
	private String project_apikey = "";
	private String project_name = "";
	private String project_current_version = "";
	private String project_last_version = "";
	private String project_curse_url = "";
	private String version_info_perm;
	private String project_bukkitdev;

	public FGUtilCore(JavaPlugin plg, boolean savelng, String lng, String plgcmd, String permissionPrefix) {
		super();
		this.version_info_perm = this.permprefix + "config";
		this.project_bukkitdev = "";
		this.permprefix = permissionPrefix.endsWith(".") ? permissionPrefix : permissionPrefix + ".";
		this.plg = plg;
		this.des = plg.getDescription();
		this.language = lng;
		this.InitMsgFile();
		this.initStdMsg();
		this.fillLoadedMessages();
		this.savelng = savelng;
		this.plgcmd = plgcmd;
		this.px = ChatColor.translateAlternateColorCodes('&', "&3[" + this.des.getName() + "]&f ");
	}

	public void initUpdateChecker(String plugin_name, String project_id, String apikey, String bukkit_dev_name, boolean enable) {
		this.project_id = project_id;
		this.project_apikey = apikey;
		this.project_curse_url = "https://api.curseforge.com/servermods/files?projectIds=" + this.project_id;
		this.project_name = plugin_name;
		this.project_current_version = this.des.getVersion();
		this.project_check_version = enable && !this.project_id.isEmpty() && !this.project_apikey.isEmpty();
		this.project_bukkitdev = "http://dev.bukkit.org/bukkit-plugins/" + bukkit_dev_name + "/";
		if (this.project_check_version) {
			this.updateMsg();
			Bukkit.getScheduler().runTaskTimerAsynchronously(this.plg, new Runnable() {
				public void run() {
					FGUtilCore.this.updateLastVersion();
					if (FGUtilCore.this.isUpdateRequired()) {
						FGUtilCore.this.logOnce(FGUtilCore.this.project_last_version, "Found new version of " + FGUtilCore.this.project_name + ". You can download version " + FGUtilCore.this.project_last_version + " from " + FGUtilCore.this.project_bukkitdev);
					}

				}
			}, (long)((40 + this.getRandomInt(20)) * 1200), 72000L);
		}

	}

	public void updateMsg(Player p) {
		if (this.isUpdateRequired() && p.hasPermission(this.version_info_perm)) {
			this.printMSG(p, "msg_outdated", 'e', '6', "&6" + this.des.getName() + " v" + this.des.getVersion());
			this.printMSG(p, "msg_pleasedownload", 'e', '6', this.project_current_version);
			this.printMsg(p, "&3" + this.project_bukkitdev);
		}

	}

	public void updateMsg() {
		this.plg.getServer().getScheduler().runTaskAsynchronously(this.plg, new Runnable() {
			public void run() {
				FGUtilCore.this.updateLastVersion();
				if (FGUtilCore.this.isUpdateRequired()) {
					FGUtilCore.this.log.info("[" + FGUtilCore.this.des.getName() + "] " + FGUtilCore.this.des.getName() + " v" + FGUtilCore.this.des.getVersion() + " is outdated! Recommended version is v" + FGUtilCore.this.project_last_version);
					FGUtilCore.this.log.info("[" + FGUtilCore.this.des.getName() + "] " + FGUtilCore.this.project_bukkitdev);
				}

			}
		});
	}

	private void updateLastVersion() {
		if (this.project_check_version) {
			URL url = null;

			try {
				url = new URL(this.project_curse_url);
			} catch (Exception var9) {
				this.log("Failed to create URL: " + this.project_curse_url);
				return;
			}

			try {
				URLConnection conn = url.openConnection();
				conn.addRequestProperty("X-API-Key", this.project_apikey);
				conn.addRequestProperty("User-Agent", this.project_name + " using FGUtilCore (by fromgate)");
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String response = reader.readLine();
				JSONArray array = (JSONArray)JSONValue.parse(response);
				if (array.size() > 0) {
					JSONObject latest = (JSONObject)array.get(array.size() - 1);
					String plugin_name = (String)latest.get("name");
					this.project_last_version = plugin_name.replace(this.project_name + " v", "").trim();
				}
			} catch (Exception var8) {
				this.log("Failed to check last version");
			}

		}
	}

	private boolean isUpdateRequired() {
		if (!this.project_check_version) {
			return false;
		} else if (this.project_id.isEmpty()) {
			return false;
		} else if (this.project_apikey.isEmpty()) {
			return false;
		} else if (this.project_last_version.isEmpty()) {
			return false;
		} else if (this.project_current_version.isEmpty()) {
			return false;
		} else if (this.project_current_version.equalsIgnoreCase(this.project_last_version)) {
			return false;
		} else {
			double current_version = Double.parseDouble(this.project_current_version.replaceFirst("\\.", "").replace("/", ""));
			double last_version = Double.parseDouble(this.project_last_version.replaceFirst("\\.", "").replace("/", ""));
			return last_version > current_version;
		}
	}

	private void initStdMsg() {
		this.addMSG("msg_outdated", "%1% is outdated!");
		this.addMSG("msg_pleasedownload", "Please download new version (%1%) from ");
		this.addMSG("hlp_help", "Help");
		this.addMSG("hlp_thishelp", "%1% - this help");
		this.addMSG("hlp_execcmd", "%1% - execute command");
		this.addMSG("hlp_typecmd", "Type %1% - to get additional help");
		this.addMSG("hlp_typecmdpage", "Type %1% - to see another page of this help");
		this.addMSG("hlp_commands", "Command list:");
		this.addMSG("hlp_cmdparam_command", "command");
		this.addMSG("hlp_cmdparam_page", "page");
		this.addMSG("hlp_cmdparam_parameter", "parameter");
		this.addMSG("cmd_unknown", "Unknown command: %1%");
		this.addMSG("cmd_cmdpermerr", "Something wrong (check command, permissions)");
		this.addMSG("enabled", "enabled");
		this.msg.put("enabled", ChatColor.DARK_GREEN + (String)this.msg.get("enabled"));
		this.addMSG("disabled", "disabled");
		this.msg.put("disabled", ChatColor.RED + (String)this.msg.get("disabled"));
		this.addMSG("lst_title", "String list:");
		this.addMSG("lst_footer", "Page: [%1% / %2%]");
		this.addMSG("lst_listisempty", "List is empty");
		this.addMSG("msg_config", "Configuration");
		this.addMSG("cfgmsg_general_check-updates", "Check updates: %1%");
		this.addMSG("cfgmsg_general_language", "Language: %1%");
		this.addMSG("cfgmsg_general_language-save", "Save translation file: %1%");
	}

	public void setConsoleColored(boolean colorconsole) {
		this.colorconsole = colorconsole;
	}

	public boolean isConsoleColored() {
		return this.colorconsole;
	}

	public void addCmd(String cmd, String perm, String desc_id, String desc_key) {
		this.addCmd(cmd, perm, desc_id, desc_key, this.c1, this.c2, false);
	}

	public void addCmd(String cmd, String perm, String desc_id, String desc_key, char color) {
		this.addCmd(cmd, perm, desc_id, desc_key, this.c1, color, false);
	}

	public void addCmd(String cmd, String perm, String desc_id, String desc_key, boolean console) {
		this.addCmd(cmd, perm, desc_id, desc_key, this.c1, this.c2, console);
	}

	public void addCmd(String cmd, String perm, String desc_id, String desc_key, char color, boolean console) {
		this.addCmd(cmd, perm, desc_id, desc_key, this.c1, color, console);
	}

	public void addCmd(String cmd, String perm, String desc_id, String desc_key, char color1, char color2) {
		this.addCmd(cmd, perm, desc_id, desc_key, color1, color2, false);
	}

	public void addCmd(String cmd, String perm, String desc_id, String desc_key, char color1, char color2, boolean console) {
		String desc = this.getMSG(desc_id, desc_key, color1, color2);
		this.cmds.put(cmd, new FGUtilCore.Cmd(this.permprefix + perm, desc, console));
		if (this.cmdlist.isEmpty()) {
			this.cmdlist = cmd;
		} else {
			this.cmdlist = this.cmdlist + ", " + cmd;
		}

	}

	public boolean checkCmdPerm(CommandSender sender, String cmd) {
		if (!this.cmds.containsKey(cmd.toLowerCase())) {
			return false;
		} else {
			FGUtilCore.Cmd cm = (FGUtilCore.Cmd)this.cmds.get(cmd.toLowerCase());
			if (sender instanceof Player) {
				return cm.perm.isEmpty() || sender.hasPermission(cm.perm);
			} else {
				return cm.console;
			}
		}
	}

	public void printPage(CommandSender p, List ln, int pnum, String title, String footer, boolean shownum) {
		FGUtilCore.PageList pl = new FGUtilCore.PageList(ln, title, footer, shownum);
		pl.printPage(p, pnum);
	}

	public void printPage(CommandSender p, List ln, int pnum, String title, String footer, boolean shownum, int lineperpage) {
		FGUtilCore.PageList pl = new FGUtilCore.PageList(ln, title, footer, shownum);
		pl.printPage(p, pnum, lineperpage);
	}

	public boolean isIdInList(int id, String str) {
		if (!str.isEmpty()) {
			String[] ln = str.split(",");
			if (ln.length > 0) {
				for(int i = 0; i < ln.length; ++i) {
					if (!ln[i].isEmpty() && ln[i].matches("[0-9]*") && Integer.parseInt(ln[i]) == id) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public boolean isAllIdInList(int[] ids, String str) {
		int[] var6 = ids;
		int var5 = ids.length;

		for(int var4 = 0; var4 < var5; ++var4) {
			int id = var6[var4];
			if (!this.isIdInList(id, str)) {
				return false;
			}
		}

		return true;
	}

	public boolean isWordInList(String word, String str) {
		String[] ln = str.split(",");
		if (ln.length > 0) {
			for(int i = 0; i < ln.length; ++i) {
				if (ln[i].equalsIgnoreCase(word)) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean isItemInList(int id, int data, String str) {
		String[] ln = str.split(",");
		if (ln.length > 0) {
			for(int i = 0; i < ln.length; ++i) {
				if (this.compareItemStr(id, data, ln[i])) {
					return true;
				}
			}
		}

		return false;
	}

	/** @deprecated */
	@Deprecated
	public boolean compareItemStr(int item_id, int item_data, String itemstr) {
		return this.compareItemStrIgnoreName(item_id, item_data, 1, itemstr);
	}

	public boolean compareItemStr(ItemStack item, String str) {
		String itemstr = str;
		String name = "";
		if (str.contains("$")) {
			name = str.substring(0, str.indexOf("$"));
			name = ChatColor.translateAlternateColorCodes('&', name.replace("_", " "));
			itemstr = str.substring(name.length() + 1);
		}

		if (itemstr.isEmpty()) {
			return false;
		} else {
			if (!name.isEmpty()) {
				String iname = item.hasItemMeta() ? item.getItemMeta().getDisplayName() : "";
				if (!name.equals(iname)) {
					return false;
				}
			}

			return this.compareItemStrIgnoreName(item.getType().getId(), item.getDurability(), item.getAmount(), itemstr);
		}
	}

	public boolean compareItemStrIgnoreName(int item_id, int item_data, int item_amount, String itemstr) {
		if (!itemstr.isEmpty()) {
			int id = 1;
			int amount = 1;
			int data = -1;
			String[] si = itemstr.split("\\*");
			if (si.length > 0) {
				if (si.length == 2 && si[1].matches("[1-9]+[0-9]*")) {
					amount = Integer.parseInt(si[1]);
				}

				String[] ti = si[0].split(":");
				if (ti.length > 0) {
					if (ti[0].matches("[0-9]*")) {
						id = Integer.parseInt(ti[0]);
					} else {
						id = Material.getMaterial(ti[0]).getId();
					}

					if (ti.length == 2 && ti[1].matches("[0-9]*")) {
						data = Integer.parseInt(ti[1]);
					}

					if (item_id == id && (data < 0 || item_data == data) && item_amount >= amount) {
						return true;
					}

					return false;
				}
			}
		}

		return false;
	}

	public boolean hasItemInInventory(Player p, String itemstr) {
		ItemStack item = this.parseItemStack(itemstr);
		if (item == null) {
			return false;
		} else if (item.getType() == Material.AIR) {
			return false;
		} else {
			return this.countItemInInventory(p, itemstr) >= item.getAmount();
		}
	}

	public int countItemInInventory(Player p, String itemstr) {
		return this.countItemInInventory((Inventory)p.getInventory(), itemstr);
	}

	public void removeItemInInventory(final Player p, final String itemstr) {
		Bukkit.getScheduler().runTaskLater(this.plg, new Runnable() {
			public void run() {
				FGUtilCore.this.removeItemInInventory((Inventory)p.getInventory(), itemstr);
			}
		}, 1L);
	}

	private boolean itemHasName(ItemStack item) {
		return !item.hasItemMeta() ? false : item.getItemMeta().hasDisplayName();
	}

	private boolean compareItemName(ItemStack item, String istrname) {
		if (istrname.isEmpty() && !this.itemHasName(item)) {
			return true;
		} else if (!istrname.isEmpty() && this.itemHasName(item)) {
			String name = ChatColor.translateAlternateColorCodes('&', istrname.replace("_", " "));
			return item.getItemMeta().getDisplayName().equals(name);
		} else {
			return false;
		}
	}

	public int removeItemInInventory(Inventory inv, String istr) {
		String itemstr = istr;
		int left = 1;
		if (left <= 0) {
			return -1;
		} else {
			int id = -1;
			int data = -1;
			String name = "";
			if (istr.contains("$")) {
				name = istr.substring(0, istr.indexOf("$"));
				itemstr = istr.substring(name.length() + 1);
			}

			String[] si = itemstr.split("\\*");
			if (si.length == 0) {
				return left;
			} else {
				if (si.length == 2 && si[1].matches("[1-9]+[0-9]*")) {
					left = Integer.parseInt(si[1]);
				}

				String[] ti = si[0].split(":");
				if (ti.length > 0) {
					if (ti[0].matches("[0-9]*")) {
						id = Integer.parseInt(ti[0]);
					} else {
						id = Material.getMaterial(ti[0]).getId();
					}

					if (ti.length == 2 && ti[1].matches("[0-9]*")) {
						data = Integer.parseInt(ti[1]);
					}
				}

				if (id <= 0) {
					return left;
				} else {
					for(int i = 0; i < inv.getContents().length; ++i) {
						ItemStack slot = inv.getItem(i);
						if (slot != null && this.compareItemName(slot, name) && id == slot.getType().getId() && (data <= 0 || data == slot.getDurability())) {
							int slotamount = slot.getAmount();
							if (slotamount != 0) {
								if (slotamount <= left) {
									left -= slotamount;
									inv.setItem(i, (ItemStack)null);
								} else {
									slot.setAmount(slotamount - left);
									left = 0;
								}

								if (left == 0) {
									return 0;
								}
							}
						}
					}

					return left;
				}
			}
		}
	}

	public int countItemInInventory(Inventory inv, String istr) {
		String itemstr = istr;
		int count = 0;
		int id = -1;
		int data = -1;
		String name = "";
		if (istr.contains("$")) {
			name = istr.substring(0, istr.indexOf("$"));
			itemstr = istr.substring(name.length() + 1);
		}

		String[] si = itemstr.split("\\*");
		if (si.length == 0) {
			return 0;
		} else {
			String[] ti = si[0].split(":");
			if (ti.length > 0) {
				try {
					if (ti[0].matches("[0-9]*")) {
						id = Integer.parseInt(ti[0]);
					} else {
						id = Material.getMaterial(ti[0].toUpperCase()).getId();
					}
				} catch (Exception var14) {
					this.logOnce(istr, "Wrong material type/id " + ti[0] + " at line " + istr);
					return 0;
				}

				if (ti.length == 2 && ti[1].matches("[0-9]*")) {
					data = Integer.parseInt(ti[1]);
				}
			}

			if (id <= 0) {
				return 0;
			} else {
				ItemStack[] var13;
				int var12 = (var13 = inv.getContents()).length;

				for(int var11 = 0; var11 < var12; ++var11) {
					ItemStack slot = var13[var11];
					if (slot != null && this.compareItemName(slot, name) && id == slot.getType().getId() && (data < 0 || data == slot.getDurability())) {
						count += slot.getAmount();
					}
				}

				return count;
			}
		}
	}

	public boolean removeItemInHand(Player p, String itemstr) {
		if (!itemstr.isEmpty()) {
			int id;
			int amount = 1;
			int data = -1;
			String[] si = itemstr.split("\\*");
			if (si.length > 0) {
				if (si.length == 2 && si[1].matches("[1-9]+[0-9]*")) {
					amount = Integer.parseInt(si[1]);
				}

				String[] ti = si[0].split(":");
				if (ti.length > 0) {
					if (ti[0].matches("[0-9]*")) {
						id = Integer.parseInt(ti[0]);
					} else {
						id = Material.getMaterial(ti[0]).getId();
					}

					if (ti.length == 2 && ti[1].matches("[0-9]*")) {
						data = Integer.parseInt(ti[1]);
					}

					return this.removeItemInHand(p, id, data, amount);
				}
			}
		}

		return false;
	}

	public boolean removeItemInHand(Player p, int item_id, int item_data, int item_amount) {
		if (p.getItemInHand() != null && p.getItemInHand().getType().getId() == item_id && p.getItemInHand().getAmount() >= item_amount && (item_data < 0 || item_data == p.getItemInHand().getDurability())) {
			if (p.getItemInHand().getAmount() > item_amount) {
				p.getItemInHand().setAmount(p.getItemInHand().getAmount() - item_amount);
			} else {
				p.setItemInHand(new ItemStack(Material.AIR));
			}

			return true;
		} else {
			return false;
		}
	}

	public void giveItemOrDrop(Player p, ItemStack item) {
		Iterator var4 = p.getInventory().addItem(new ItemStack[]{item}).values().iterator();

		while(var4.hasNext()) {
			ItemStack i = (ItemStack)var4.next();
			p.getWorld().dropItemNaturally(p.getLocation(), i);
		}

	}

	public void printMsg(CommandSender p, String msg) {
		String message = ChatColor.translateAlternateColorCodes('&', msg);
		if (!(p instanceof Player) && !this.colorconsole) {
			message = ChatColor.stripColor(message);
		}

		p.sendMessage(message);
	}

	public void printPxMsg(CommandSender p, String msg) {
		this.printMsg(p, this.px + msg);
	}

	public void BC(String msg) {
		this.plg.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', this.px + msg));
	}

	public void broadcastMSG(String perm, Object... s) {
		Player[] var6;
		int var5 = (var6 = Bukkit.getOnlinePlayers().toArray(new Player[0])).length;

		for(int var4 = 0; var4 < var5; ++var4) {
			Player p = var6[var4];
			if (p.hasPermission(this.permprefix + perm)) {
				this.printMSG(p, s);
			}
		}

	}

	public void broadcastMsg(String perm, String msg) {
		Player[] var6;
		int var5 = (var6 = Bukkit.getOnlinePlayers().toArray(new Player[0])).length;

		for(int var4 = 0; var4 < var5; ++var4) {
			Player p = var6[var4];
			if (p.hasPermission(this.permprefix + perm)) {
				this.printMsg(p, msg);
			}
		}

	}

	public void log(String msg) {
		this.log.info(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', this.px + msg)));
	}

	public void logOnce(String error_id, String msg) {
		if (!this.log_once.contains(error_id)) {
			this.log_once.add(error_id);
			this.log.info(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', this.px + msg)));
		}
	}

	public void SC(String msg) {
		this.plg.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', this.px + msg));
	}

	public void InitMsgFile() {
		try {
			this.lng = new YamlConfiguration();
			File f = new File(this.plg.getDataFolder() + File.separator + this.language + ".lng");
			if (f.exists()) {
				this.lng.load(f);
			} else {
				InputStream is = this.plg.getClass().getResourceAsStream("/language/" + this.language + ".lng");
				if (is != null) {
					this.lng.load(String.valueOf(is));
				}
			}
		} catch (Exception var3) {
			var3.printStackTrace();
		}

	}

	public void fillLoadedMessages() {
		if (this.lng != null) {
			Iterator var2 = this.lng.getKeys(true).iterator();

			while(var2.hasNext()) {
				String key = (String)var2.next();
				this.addMSG(key, this.lng.getString(key));
			}

		}
	}

	public void addMSG(String key, String txt) {
		this.msg.put(key, ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', this.lng.getString(key, txt))));
		if (this.msglist.isEmpty()) {
			this.msglist = key;
		} else {
			this.msglist = this.msglist + "," + key;
		}

	}

	public void SaveMSG() {
		String[] keys = this.msglist.split(",");

		try {
			File f = new File(this.plg.getDataFolder() + File.separator + this.language + ".lng");
			if (!f.exists()) {
				f.createNewFile();
			}

			YamlConfiguration cfg = new YamlConfiguration();

			for(int i = 0; i < keys.length; ++i) {
				cfg.set(keys[i], this.msg.get(keys[i]));
			}

			cfg.save(f);
		} catch (Exception var5) {
			var5.printStackTrace();
		}

	}

	public String getMSG(Object... s) {
		String str = "&4Unknown message";
		String color1 = "&" + this.c1;
		String color2 = "&" + this.c2;
		if (s.length > 0) {
			String id = s[0].toString();
			str = "&4Unknown message (" + id + ")";
			if (this.msg.containsKey(id)) {
				int px = 1;
				if (s.length > 1 && s[1] instanceof Character) {
					px = 2;
					color1 = "&" + (Character)s[1];
					if (s.length > 2 && s[2] instanceof Character) {
						px = 3;
						color2 = "&" + (Character)s[2];
					}
				}

				str = color1 + (String)this.msg.get(id);
				if (px < s.length) {
					for(int i = px; i < s.length; ++i) {
						String f = s[i].toString();
						if (s[i] instanceof Location) {
							Location loc = (Location)s[i];
							f = loc.getWorld().getName() + "[" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + "]";
						}

						str = str.replace("%" + Integer.toString(i - px + 1) + "%", color2 + f + color1);
					}
				}
			} else if (this.savelng) {
				this.addMSG(id, str);
				this.SaveMSG();
			}
		}

		return ChatColor.translateAlternateColorCodes('&', str);
	}

	public void printMSG(CommandSender p, Object... s) {
		String message = this.getMSG(s);
		if (!(p instanceof Player) && !this.colorconsole) {
			message = ChatColor.stripColor(message);
		}

		p.sendMessage(message);
	}

	public void PrintHLP(Player p) {
		this.printMsg(p, "&6&l" + this.project_name + " v" + this.des.getVersion() + " &r&6| " + this.getMSG("hlp_help", '6'));
		this.printMSG(p, "hlp_thishelp", "/" + this.plgcmd + " help");
		this.printMSG(p, "hlp_execcmd", "/" + this.plgcmd + " <" + this.getMSG("hlp_cmdparam_command", '2') + "> [" + this.getMSG("hlp_cmdparam_parameter", '2') + "]");
		this.printMSG(p, "hlp_typecmd", "/" + this.plgcmd + " help <" + this.getMSG("hlp_cmdparam_command", '2') + ">");
		this.printMsg(p, this.getMSG("hlp_commands") + " &2" + this.cmdlist);
	}

	public void printHLP(Player p, String cmd) {
		if (this.cmds.containsKey(cmd)) {
			this.printMsg(p, "&6&l" + this.project_name + " v" + this.des.getVersion() + " &r&6| " + this.getMSG("hlp_help", '6'));
			this.printMsg(p, ((FGUtilCore.Cmd)this.cmds.get(cmd)).desc);
		} else {
			this.printMSG(p, "cmd_unknown", 'c', 'e', cmd);
		}

	}

	public void PrintHlpList(CommandSender p, int page, int lpp) {
		String title = "&6&l" + this.project_name + " v" + this.des.getVersion() + " &r&6| " + this.getMSG("hlp_help", '6');
		List hlp = new ArrayList();
		hlp.add(this.getMSG("hlp_thishelp", "/" + this.plgcmd + " help"));
		hlp.add(this.getMSG("hlp_execcmd", "/" + this.plgcmd + " <" + this.getMSG("hlp_cmdparam_command", '2') + "> [" + this.getMSG("hlp_cmdparam_parameter", '2') + "]"));
		if (p instanceof Player) {
			hlp.add(this.getMSG("hlp_typecmdpage", "/" + this.plgcmd + " help <" + this.getMSG("hlp_cmdparam_page", '2') + ">"));
		}

		String[] ks = this.cmdlist.replace(" ", "").split(",");
		if (ks.length > 0) {
			String[] var10 = ks;
			int var9 = ks.length;

			for(int var8 = 0; var8 < var9; ++var8) {
				String cmd = var10[var8];
				hlp.add(((FGUtilCore.Cmd)this.cmds.get(cmd)).desc);
			}
		}

		this.printPage(p, hlp, page, title, "", false, lpp);
	}

	public String EnDis(boolean b) {
		return b ? this.getMSG("enabled", '2') : this.getMSG("disabled", 'c');
	}

	public String EnDis(String str, boolean b) {
		String str2 = ChatColor.stripColor(str);
		return b ? ChatColor.DARK_GREEN + str2 : ChatColor.RED + str2;
	}

	public void printEnDis(CommandSender p, String msg_id, boolean b) {
		p.sendMessage(this.getMSG(msg_id) + ": " + this.EnDis(b));
	}

	public void setPermPrefix(String ppfx) {
		this.permprefix = ppfx + ".";
		this.version_info_perm = this.permprefix + "config";
	}

	public boolean equalCmdPerm(String cmd, String perm) {
		return this.cmds.containsKey(cmd.toLowerCase()) && ((FGUtilCore.Cmd)this.cmds.get(cmd.toLowerCase())).perm.equalsIgnoreCase(this.permprefix + perm);
	}

	public ItemStack parseItemStack(String itemstr) {
		if (itemstr.isEmpty()) {
			return null;
		} else {
			String istr = itemstr;
			String enchant = "";
			String name = "";
			if (itemstr.contains("$")) {
				name = itemstr.substring(0, itemstr.indexOf("$"));
				istr = itemstr.substring(name.length() + 1);
			}

			if (istr.contains("@")) {
				enchant = istr.substring(istr.indexOf("@") + 1);
				istr = istr.substring(0, istr.indexOf("@"));
			}

			int amount = 1;
			short data = 0;
			String[] si = istr.split("\\*");
			if (si.length > 0) {
				if (si.length == 2) {
					amount = Math.max(this.getMinMaxRandom(si[1]), 1);
				}

				String[] ti = si[0].split(":");
				if (ti.length > 0) {
					int id;
					if (ti[0].matches("[0-9]*")) {
						id = Integer.parseInt(ti[0]);
					} else {
						Material m = Material.getMaterial(ti[0].toUpperCase());
						if (m == null) {
							this.logOnce("wrongitem" + ti[0], "Could not parse item material name (id) " + ti[0]);
							return null;
						}

						id = m.getId();
					}

					if (ti.length == 2 && ti[1].matches("[0-9]*")) {
						data = Short.parseShort(ti[1]);
					}

					ItemStack item = new ItemStack(null, amount, data);
					if (!enchant.isEmpty()) {
						item = this.setEnchantments(item, enchant);
					}

					if (!name.isEmpty()) {
						ItemMeta im = item.getItemMeta();
						im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name.replace("_", " ")));
						item.setItemMeta(im);
					}

					return item;
				}
			}

			return null;
		}
	}

	public ItemStack setEnchantments(ItemStack item, String enchants) {
		ItemStack i = item.clone();
		if (enchants.isEmpty()) {
			return i;
		} else {
			String[] ln = enchants.split(",");
			String[] var8 = ln;
			int var7 = ln.length;

			for(int var6 = 0; var6 < var7; ++var6) {
				String ec = var8[var6];
				if (!ec.isEmpty()) {
					Color clr = this.colorByName(ec);
					if (clr != null) {
						if (this.isIdInList(item.getType().getId(), "298,299,300,301")) {
							LeatherArmorMeta meta = (LeatherArmorMeta)i.getItemMeta();
							meta.setColor(clr);
							i.setItemMeta(meta);
						}
					} else {
						String ench = ec;
						int level = 1;
						if (ec.contains(":")) {
							ench = ec.substring(0, ec.indexOf(":"));
							level = Math.max(1, this.getMinMaxRandom(ec.substring(ench.length() + 1)));
						}

						Enchantment e = Enchantment.getByName(ench.toUpperCase());
						if (e != null) {
							i.addUnsafeEnchantment(e, level);
						}
					}
				}
			}

			return i;
		}
	}

	public Color colorByName(String colorname) {
		Color[] clr = new Color[]{Color.WHITE, Color.SILVER, Color.GRAY, Color.BLACK, Color.RED, Color.MAROON, Color.YELLOW, Color.OLIVE, Color.LIME, Color.GREEN, Color.AQUA, Color.TEAL, Color.BLUE, Color.NAVY, Color.FUCHSIA, Color.PURPLE};
		String[] clrs = new String[]{"WHITE", "SILVER", "GRAY", "BLACK", "RED", "MAROON", "YELLOW", "OLIVE", "LIME", "GREEN", "AQUA", "TEAL", "BLUE", "NAVY", "FUCHSIA", "PURPLE"};

		for(int i = 0; i < clrs.length; ++i) {
			if (colorname.equalsIgnoreCase(clrs[i])) {
				return clr[i];
			}
		}

		return null;
	}

	public boolean isPlayerAround(Location loc, int radius) {
		Iterator var4 = loc.getWorld().getPlayers().iterator();

		while(var4.hasNext()) {
			Player p = (Player)var4.next();
			if (p.getLocation().distance(loc) <= (double)radius) {
				return true;
			}
		}

		return false;
	}

	public String getMSGnc(Object... s) {
		return ChatColor.stripColor(this.getMSG(s));
	}

	public boolean placeBlock(Location loc, Player p, Material newType, byte newData, boolean phys) {
		return this.placeBlock(loc.getBlock(), p, newType, newData, phys);
	}

	public boolean placeBlock(Block block, Player p, Material newType, byte newData, boolean phys) {
		BlockState state = block.getState();

		BlockPlaceEvent event = new BlockPlaceEvent(state.getBlock(), state, block, p.getItemInHand(), p, true);
		this.plg.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			state.update(true);
		}

		return event.isCancelled();
	}

	public boolean rollDiceChance(int chance) {
		return this.random.nextInt(100) < chance;
	}

	public int tryChance(int chance) {
		return this.random.nextInt(chance);
	}

	public int getRandomInt(int maxvalue) {
		return this.random.nextInt(maxvalue);
	}

	public boolean isIntegerSigned(String str) {
		return str.matches("-?[0-9]+[0-9]*");
	}

	public boolean isIntegerSigned(String... str) {
		if (str.length == 0) {
			return false;
		} else {
			String[] var5 = str;
			int var4 = str.length;

			for(int var3 = 0; var3 < var4; ++var3) {
				String s = var5[var3];
				if (!s.matches("-?[0-9]+[0-9]*")) {
					return false;
				}
			}

			return true;
		}
	}

	public boolean isInteger(String str) {
		return str.matches("[0-9]+[0-9]*");
	}

	public boolean isInteger(String... str) {
		if (str.length == 0) {
			return false;
		} else {
			String[] var5 = str;
			int var4 = str.length;

			for(int var3 = 0; var3 < var4; ++var3) {
				String s = var5[var3];
				if (!s.matches("[0-9]+[0-9]*")) {
					return false;
				}
			}

			return true;
		}
	}

	public boolean isIntegerGZ(String str) {
		return str.matches("[1-9]+[0-9]*");
	}

	public boolean isIntegerGZ(String... str) {
		if (str.length == 0) {
			return false;
		} else {
			String[] var5 = str;
			int var4 = str.length;

			for(int var3 = 0; var3 < var4; ++var3) {
				String s = var5[var3];
				if (!s.matches("[1-9]+[0-9]*")) {
					return false;
				}
			}

			return true;
		}
	}

	public void printConfig(CommandSender p, int page, int lpp, boolean section, boolean usetranslation) {
		List cfgprn = new ArrayList();
		String k;
		if (!this.plg.getConfig().getKeys(true).isEmpty()) {
			Iterator var8 = this.plg.getConfig().getKeys(true).iterator();

label32:
			while(true) {
				String value;
				String str;
				while(true) {
					if (!var8.hasNext()) {
						break label32;
					}

					k = (String)var8.next();
					Object objvalue = this.plg.getConfig().get(k);
					value = objvalue.toString();
					str = k;
					if (objvalue instanceof Boolean && usetranslation) {
						value = this.EnDis((Boolean)objvalue);
					}

					if (objvalue instanceof MemorySection) {
						if (!section) {
							continue;
						}
						break;
					}

					str = k + " : " + value;
					break;
				}

				if (usetranslation) {
					str = this.getMSG("cfgmsg_" + k.replaceAll("\\.", "_"), value);
				}

				cfgprn.add(str);
			}
		}

		k = "&6&l" + this.project_current_version + " v" + this.des.getVersion() + " &r&6| " + this.getMSG("msg_config", '6');
		this.printPage(p, cfgprn, page, k, "", false);
	}

	public int getMinMaxRandom(String minmaxstr) {
		int min = 0;
		int max = 0;
		String strmin = minmaxstr;
		String strmax = minmaxstr;
		if (minmaxstr.contains("-")) {
			strmin = minmaxstr.substring(0, minmaxstr.indexOf("-"));
			strmax = minmaxstr.substring(minmaxstr.indexOf("-") + 1);
		}

		if (strmin.matches("[1-9]+[0-9]*")) {
			min = Integer.parseInt(strmin);
		}

		int max2 = min;
		if (strmax.matches("[1-9]+[0-9]*")) {
			max2 = Integer.parseInt(strmax);
		}

		return max > min ? min + this.tryChance(1 + max - min) : min;
	}

	public Long timeToTicks(Long time) {
		return Math.max(1L, time / 50L);
	}

	public Long parseTime(String time) {
		int hh = 0;
		int mm = 0;
		int ss = 0;
		int tt = 0;
		int ms = 0;
		if (this.isInteger(time)) {
			ss = Integer.parseInt(time);
		} else {
			String[] ln;
			if (time.matches("^[0-5][0-9]:[0-5][0-9]$")) {
				ln = time.split(":");
				if (this.isInteger(ln[0])) {
					mm = Integer.parseInt(ln[0]);
				}

				if (this.isInteger(ln[1])) {
					ss = Integer.parseInt(ln[1]);
				}
			} else if (time.matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$")) {
				ln = time.split(":");
				if (this.isInteger(ln[0])) {
					hh = Integer.parseInt(ln[0]);
				}

				if (this.isInteger(ln[1])) {
					mm = Integer.parseInt(ln[1]);
				}

				if (this.isInteger(ln[2])) {
					ss = Integer.parseInt(ln[2]);
				}
			} else {
				String s;
				if (time.endsWith("ms")) {
					s = time.replace("ms", "");
					if (this.isInteger(s)) {
						ms = Integer.parseInt(s);
					}
				} else if (time.endsWith("h")) {
					s = time.replace("h", "");
					if (this.isInteger(s)) {
						hh = Integer.parseInt(s);
					}
				} else if (time.endsWith("m")) {
					s = time.replace("m", "");
					if (this.isInteger(s)) {
						mm = Integer.parseInt(s);
					}
				} else if (time.endsWith("s")) {
					s = time.replace("s", "");
					if (this.isInteger(s)) {
						ss = Integer.parseInt(s);
					}
				} else if (time.endsWith("t")) {
					s = time.replace("t", "");
					if (this.isInteger(s)) {
						tt = Integer.parseInt(s);
					}
				}
			}
		}

		return (long)(hh * 3600000 + mm * '\uea60' + ss * 1000 + tt * 50 + ms);
	}

	public String itemToString(ItemStack item) {
		String str = "";
		String n = item.getItemMeta().hasDisplayName() ? ChatColor.stripColor(item.getItemMeta().getDisplayName()) : item.getType().name();
		String a = item.getAmount() > 1 ? "*" + item.getAmount() : "";
		str = n + a;
		return str;
	}

	public int safeLongToInt(long l) {
		if (l < -2147483648L) {
			return Integer.MIN_VALUE;
		} else {
			return l > 2147483647L ? Integer.MAX_VALUE : (int)l;
		}
	}

	public class Cmd {
		String perm;
		String desc;
		boolean console;

		public Cmd(String perm, String desc) {
			super();
			this.perm = perm;
			this.desc = desc;
			this.console = false;
		}

		public Cmd(String perm, String desc, boolean console) {
			super();
			this.perm = perm;
			this.desc = desc;
			this.console = console;
		}
	}

	public class PageList {
		private List ln;
		private int lpp = 15;
		private String title_msgid = "lst_title";
		private String footer_msgid = "lst_footer";
		private boolean shownum = false;

		public void setLinePerPage(int lpp) {
			this.lpp = lpp;
		}

		public PageList(List ln, String title_msgid, String footer_msgid, boolean shownum) {
			super();
			this.ln = ln;
			if (!title_msgid.isEmpty()) {
				this.title_msgid = title_msgid;
			}

			if (!footer_msgid.isEmpty()) {
				this.footer_msgid = footer_msgid;
			}

			this.shownum = shownum;
		}

		public void addLine(String str) {
			this.ln.add(str);
		}

		public boolean isEmpty() {
			return this.ln.isEmpty();
		}

		public void setTitle(String title_msgid) {
			this.title_msgid = title_msgid;
		}

		public void setShowNum(boolean shownum) {
		}

		public void setFooter(String footer_msgid) {
			this.footer_msgid = footer_msgid;
		}

		public void printPage(CommandSender p, int pnum) {
			this.printPage(p, pnum, this.lpp);
		}

		public void printPage(CommandSender p, int pnum, int linesperpage) {
			if (this.ln.size() > 0) {
				int maxp = this.ln.size() / linesperpage;
				if (this.ln.size() % linesperpage > 0) {
					++maxp;
				}

				if (pnum > maxp) {
					pnum = maxp;
				}

				int maxl = linesperpage;
				if (pnum == maxp) {
					maxl = this.ln.size() % linesperpage;
					if (maxp == 1) {
						maxl = this.ln.size();
					}
				}

				if (maxl == 0) {
					maxl = linesperpage;
				}

				if (FGUtilCore.this.msg.containsKey(this.title_msgid)) {
					FGUtilCore.this.printMsg(p, "&6&l" + FGUtilCore.this.getMSGnc(this.title_msgid));
				} else {
					FGUtilCore.this.printMsg(p, this.title_msgid);
				}

				String numpx = "";

				for(int i = 0; i < maxl; ++i) {
					if (this.shownum) {
						numpx = "&3" + Integer.toString(1 + i + (pnum - 1) * linesperpage) + ". ";
					}

					FGUtilCore.this.printMsg(p, numpx + "&a" + (String)this.ln.get(i + (pnum - 1) * linesperpage));
				}

				if (maxp > 1) {
					FGUtilCore.this.printMSG(p, this.footer_msgid, 'e', '6', pnum, maxp);
				}
			} else {
				FGUtilCore.this.printMSG(p, "lst_listisempty", 'c');
			}

		}
	}
}
