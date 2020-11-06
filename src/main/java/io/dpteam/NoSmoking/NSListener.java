package io.dpteam.NoSmoking;

import java.util.HashSet;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class NSListener implements Listener {
	NoSmoking plg;

	public NSListener(NoSmoking plg) {
		super();
		this.plg = plg;
	}

	@EventHandler(
	priority = EventPriority.NORMAL
	)
	public void onChunkUnload(ChunkUnloadEvent event) {
		Entity[] entities = event.getChunk().getEntities();
		if (entities.length > 0) {
			Entity[] var6 = entities;
			int var5 = entities.length;

			for(int var4 = 0; var4 < var5; ++var4) {
				Entity e = var6[var4];
				if (this.plg.temp.contains(e)) {
					this.plg.temp.remove(e);
					e.remove();
				}
			}
		}

	}

	@EventHandler(
	priority = EventPriority.NORMAL
	)
	public void onPlayerJoin(PlayerJoinEvent event) {
		this.plg.u.updateMsg(event.getPlayer());
	}

	@EventHandler(
	priority = EventPriority.NORMAL
	)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && this.plg.CheckPerm(p, "no-smoking.smoke") && this.plg.pcfg.containsKey(p.getName()) && ((PlayerSettings)this.plg.pcfg.get(p.getName())).mode && p.getItemInHand().getTypeId() == this.plg.wand) {
			Location loc = p.getTargetBlock((HashSet)null, 150).getLocation();
			if (loc.getBlockY() < loc.getWorld().getMaxHeight() - 2) {
				loc.setY(loc.getY() + 1.0D);
				if (this.plg.smog.checkLoc(loc)) {
					p.sendMessage(this.plg.spx + Integer.toString(this.plg.smog.remove_loc(loc)) + " effect(s) deleted");
				} else {
					this.plg.AddNewSmoke(loc, p);
				}

				this.plg.FillPlayList();
			} else {
				p.sendMessage(this.plg.spx + "Target block is too high.");
			}
		}

	}

	@EventHandler(
	priority = EventPriority.NORMAL
	)
	public void onSignChange(SignChangeEvent event) {
		String str = event.getLine(1);
		Player player = event.getPlayer();
		if (str.equalsIgnoreCase("[ns-switch]")) {
			if (!this.plg.CheckPerm(player, "no-smoking.switch.sign")) {
				event.setLine(1, "{ns-switch}");
			} else {
				event.setLine(1, ChatColor.RED + "[ns-switch]");
				event.setLine(2, ChatColor.GOLD + event.getLine(2));
			}
		}

		if (str.equalsIgnoreCase("[ns-toggle]")) {
			if (!this.plg.CheckPerm(player, "no-smoking.switch.sign")) {
				event.setLine(1, "{ns-toggle}");
			} else {
				event.setLine(1, ChatColor.RED + "[ns-toggle]");
				event.setLine(2, ChatColor.GOLD + event.getLine(2));
			}
		}

	}

	@EventHandler(
	priority = EventPriority.NORMAL
	)
	public void onBlockRedstoneChange(BlockRedstoneEvent event) {
		Block b = event.getBlock();
		if (b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN) {
			BlockState state = b.getState();
			if (state instanceof Sign) {
				Sign sign = (Sign)state;
				String str;
				if (ChatColor.stripColor(sign.getLine(1)).equalsIgnoreCase("[ns-switch]") && !sign.getLine(2).isEmpty()) {
					this.plg.smog.SwitchSmoke(ChatColor.stripColor(sign.getLine(2)), b.isBlockPowered() || b.isBlockIndirectlyPowered());
					str = ChatColor.stripColor(sign.getLine(2));
					if (!b.isBlockPowered() && !b.isBlockIndirectlyPowered()) {
						sign.setLine(2, ChatColor.GOLD + str);
					} else {
						sign.setLine(2, ChatColor.GREEN + str);
					}

					sign.update(true);
				}

				if (ChatColor.stripColor(sign.getLine(1)).equalsIgnoreCase("[ns-toggle]") && !sign.getLine(2).isEmpty()) {
					str = ChatColor.stripColor(sign.getLine(2));
					if (this.plg.smog.ToggleSmoke(ChatColor.stripColor(sign.getLine(2)))) {
						sign.setLine(2, ChatColor.GREEN + str);
					} else {
						sign.setLine(2, ChatColor.GOLD + str);
					}

					sign.update(true);
				}
			}
		}

	}
}
