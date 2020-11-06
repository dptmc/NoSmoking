package io.dpteam.NoSmoking;

import org.bukkit.entity.Player;

public class NSUtil extends FGUtilCore {
	NoSmoking plg;

	public NSUtil(NoSmoking plugin, boolean vcheck, boolean savelng, String language, String devbukkitname, String version_name, String plgcmd, String px) {
		super(plugin, savelng, language, plgcmd, devbukkitname);
		this.plg = plugin;
		this.initUpdateChecker(version_name, "33820", "a2a7b26dd4dc9bc496c80de4b49e87cb42e34ae3", devbukkitname, vcheck);
		this.FillMSG();
		this.InitCmd();
		if (savelng) {
			this.SaveMSG();
		}

	}

	public void PrintCfg(Player p) {
	}

	public void InitCmd() {
		this.cmds.clear();
		this.cmdlist = "";
		this.addCmd("help", "config", "cmd_help", "&3/react help [command]", 'b');
	}

	public void FillMSG() {
		this.addMSG("msg_effectadded", "Effect added: %1%");
	}
}
