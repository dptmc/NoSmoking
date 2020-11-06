package io.dpteam.NoSmoking;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NSCommander implements CommandExecutor {
	NoSmoking plg;
	String spx;

	NSCommander(NoSmoking plg) {
		super();
		this.plg = plg;
		this.spx = plg.spx;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player)sender;
			String pname = player.getName();
			if (this.plg.CheckPerm(player, "no-smoking.smoke")) {
				if (!this.plg.pcfg.containsKey(pname)) {
					this.plg.pcfg.put(pname, new PlayerSettings());
				}

				if (cmd.getName().equalsIgnoreCase("smoke")) {
					if (args.length <= 0) {
						((PlayerSettings)this.plg.pcfg.get(pname)).mode = !((PlayerSettings)this.plg.pcfg.get(pname)).mode;
						if (((PlayerSettings)this.plg.pcfg.get(pname)).mode) {
							sender.sendMessage(this.spx + ChatColor.GREEN + "Smoke mode enabled.");
							this.PrintCurVar(player);
							sender.sendMessage(ChatColor.DARK_GRAY + "Use coal (right-click) to place and remove smokes");
						} else {
							sender.sendMessage(this.spx + ChatColor.DARK_GREEN + "Smoke mode disabled.");
						}

						return true;
					}

					boolean prm;
					int r;
					int dy;
					if (args[0].equalsIgnoreCase("cfg")) {
						if (args.length > 1) {
							if (this.plg.CheckPerm(player, "no-smoking.config")) {
								prm = true;
								Long lprm = 0L;
								boolean rst = false;

								for(dy = 1; dy < args.length; ++dy) {
									r = -1;
									String[] ln = args[1].split("=");
									if (ln.length == 2 && ln[1].matches("[1-9]+[0-9]*")) {
										r = Integer.parseInt(ln[1]);
										lprm = Long.parseLong(ln[1]);
									}

									if (ln[0].equalsIgnoreCase("disteff")) {
										if (r < 16 || r > 128) {
											r = 32;
										}

										this.plg.dist_eff = r;
										player.sendMessage("Effects distance: " + ChatColor.GREEN + Integer.toString(this.plg.dist_eff));
									} else if (ln[0].equalsIgnoreCase("distent")) {
										if (r < 16 || r > 96) {
											r = 32;
										}

										this.plg.dist_ent = r;
										player.sendMessage("Entity-effects distance: " + ChatColor.GREEN + Integer.toString(this.plg.dist_ent));
									} else if (ln[0].equalsIgnoreCase("distsfx")) {
										if (r < 16 || r > 128) {
											r = 32;
										}

										this.plg.dist_sfx = r;
										player.sendMessage("Sound effects distance: " + ChatColor.GREEN + Integer.toString(this.plg.dist_sfx));
									} else if (ln[0].equalsIgnoreCase("distsng")) {
										if (r < 16 || r > 256) {
											r = 100;
										}

										this.plg.dist_sfx = r;
										player.sendMessage("Musics distance: " + ChatColor.GREEN + Integer.toString(this.plg.dist_sng));
									} else if (!ln[0].equalsIgnoreCase("distlht")) {
										if (ln[0].equalsIgnoreCase("tickeff")) {
											if (lprm < 5L) {
												lprm = 5L;
											}

											this.plg.ticktime = lprm;
											rst = true;
											player.sendMessage(this.spx + "Main tick time is set to " + ChatColor.GREEN + Long.toString(this.plg.plfltick));
										} else if (ln[0].equalsIgnoreCase("tickplay")) {
											if (lprm < 15L) {
												lprm = 15L;
											}

											this.plg.plfltick = lprm;
											rst = true;
											player.sendMessage(this.spx + "Effect visibility check tick time is set to " + ChatColor.GREEN + Long.toString(this.plg.ticktime));
										} else if (ln[0].equalsIgnoreCase("tickwind")) {
											if (lprm < 100L) {
												lprm = 100L;
											}

											this.plg.windtick = lprm;
											rst = true;
											player.sendMessage(this.spx + "Wind changing interval is set to " + ChatColor.GREEN + Long.toString(this.plg.plfltick));
										} else {
											player.sendMessage(this.spx + "Could not parse parameter: " + ChatColor.RED + args[dy]);
										}
									} else {
										if (r < 16 || r > 256) {
											r = 100;
										}

										this.plg.dist_sfx = r;
										player.sendMessage("Lightning distance: " + ChatColor.GREEN + Integer.toString(this.plg.dist_sng));
									}
								}

								this.plg.SaveCfg();
								if (rst) {
									this.plg.Rst();
									player.sendMessage(this.spx + ChatColor.DARK_GREEN + "Effect restarted");
								}
							}
						} else {
							String id = ((PlayerSettings)this.plg.pcfg.get(pname)).id;
							if (id.isEmpty()) {
								id = "<empty>";
							}

							player.sendMessage(ChatColor.RED + "No Smoking!" + ChatColor.GREEN + "  Current configuration");
							player.sendMessage(ChatColor.GOLD + "Thread frequency: " + ChatColor.WHITE + "Effects: " + ChatColor.GREEN + Long.toString(this.plg.ticktime) + ChatColor.WHITE + "  Visibility: " + ChatColor.GREEN + Long.toString(this.plg.plfltick) + ChatColor.WHITE + "  Wind: " + ChatColor.GREEN + Long.toString(this.plg.windtick));
							player.sendMessage(ChatColor.GOLD + "Effect play distance:");
							player.sendMessage("Effects: " + ChatColor.GREEN + Integer.toString(this.plg.dist_eff) + ChatColor.WHITE + " Entities: " + ChatColor.GREEN + Integer.toString(this.plg.dist_ent) + ChatColor.WHITE + " Songs: " + ChatColor.GREEN + Integer.toString(this.plg.dist_sng) + ChatColor.WHITE + " Sounds: " + ChatColor.GREEN + Integer.toString(this.plg.dist_sfx) + ChatColor.WHITE + " Lightning: " + ChatColor.GREEN + Integer.toString(this.plg.dist_lht));
							player.sendMessage("Show smoke in addition to song/sound effects: " + ChatColor.GREEN + this.EnDis(this.plg.soundsmoke));
							player.sendMessage("Switch all effect to smoke: " + ChatColor.GREEN + this.EnDis(this.plg.allsmoke));
							player.sendMessage(ChatColor.GOLD + "Personal variables:");
							player.sendMessage("Edit (smoke) mode: " + ChatColor.GREEN + this.EnDis(((PlayerSettings)this.plg.pcfg.get(pname)).mode));
							player.sendMessage("current effect " + ChatColor.GREEN + this.plg.Eff2Str(((PlayerSettings)this.plg.pcfg.get(pname)).effect) + ChatColor.WHITE + "  id: " + ChatColor.GREEN + id + ChatColor.WHITE + "  rate: " + ChatColor.GREEN + Integer.toString(((PlayerSettings)this.plg.pcfg.get(pname)).rate));
							player.sendMessage("wind direction: " + ChatColor.GREEN + this.plg.Dir2Wind(((PlayerSettings)this.plg.pcfg.get(pname)).wd) + ChatColor.WHITE + "  potion: " + ChatColor.GREEN + Integer.toString(((PlayerSettings)this.plg.pcfg.get(pname)).pot) + ChatColor.WHITE + "  song (disc): " + ChatColor.GREEN + this.plg.Song2Str(((PlayerSettings)this.plg.pcfg.get(pname)).song) + ChatColor.WHITE + "  sfx: " + ChatColor.GREEN + this.plg.Sfx2Str(((PlayerSettings)this.plg.pcfg.get(pname)).sfx));
							player.sendMessage("chance " + ChatColor.GREEN + Integer.toString(((PlayerSettings)this.plg.pcfg.get(pname)).chance) + "%" + ChatColor.WHITE + "  lightmode: " + ChatColor.GREEN + ((PlayerSettings)this.plg.pcfg.get(pname)).lmode);
							player.sendMessage(ChatColor.GOLD + "Total effects: " + ChatColor.DARK_RED + Integer.toString(this.plg.smog.size()) + ChatColor.GOLD + "  Active effects: " + ChatColor.DARK_RED + Integer.toString(this.plg.playlist.size()));
							player.sendMessage(ChatColor.GOLD + "Entity effects in memory: " + ChatColor.DARK_RED + Integer.toString(this.plg.temp.size()));
						}

						return true;
					}

					if (args[0].equalsIgnoreCase("help")) {
						if (args.length >= 2) {
							this.ShowHelp(player, args[1]);
						} else {
							this.ShowHelp(player, "");
						}

						return true;
					}

					if (args[0].equalsIgnoreCase("useperm") && this.plg.CheckPerm(player, "no-smoking.config")) {
						this.plg.usepermissions = !this.plg.usepermissions;
						player.sendMessage(this.spx + ChatColor.GOLD + "Permissions support is " + this.EnDis(this.plg.usepermissions));
						return true;
					}

					if (args[0].equalsIgnoreCase("hide") && this.plg.CheckPerm(player, "no-smoking.config")) {
						if (args.length == 2) {
							player.sendMessage(this.spx + ChatColor.RED + Integer.toString(this.plg.smog.SwitchSmoke(args[1], false)) + ChatColor.WHITE + " effect was hidden");
						} else {
							this.ShowHelp(player, "hide");
						}

						return true;
					}

					if (args[0].equalsIgnoreCase("show") && this.plg.CheckPerm(player, "no-smoking.config")) {
						if (args.length == 2) {
							player.sendMessage(this.spx + ChatColor.RED + Integer.toString(this.plg.smog.SwitchSmoke(args[1], true)) + ChatColor.WHITE + " effects were revealed");
						} else {
							this.ShowHelp(player, "show");
						}

						return true;
					}

					if (args[0].equalsIgnoreCase("wand") && this.plg.CheckPerm(player, "no-smoking.config")) {
						r = 263;
						if (args.length > 1 && args[1].matches("[1-9]+[0-9]*")) {
							r = Integer.parseInt(args[1]);
						}

						this.plg.wand = r;
						player.sendMessage(this.spx + "Wand is set to material: " + ChatColor.GREEN + Integer.toString(this.plg.wand));
						return true;
					}

					if (args[0].equalsIgnoreCase("smsound") && this.plg.CheckPerm(player, "no-smoking.config")) {
						this.plg.soundsmoke = !this.plg.soundsmoke;
						player.sendMessage(this.spx + "Playing additional smoke on the sound and sfx effect place " + this.EnDis(this.plg.soundsmoke));
						this.plg.SaveCfg();
						return true;
					}

					if (args[0].equalsIgnoreCase("allsmoke") && this.plg.CheckPerm(player, "no-smoking.config")) {
						this.plg.allsmoke = !this.plg.allsmoke;
						player.sendMessage(this.spx + "Replacing all the effects with smoke " + this.EnDis(this.plg.allsmoke));
						this.plg.SaveCfg();
						return true;
					}

					int maxpage;
					int param;
					int j;
					int i;
					if (args[0].equalsIgnoreCase("list")) {
						sender.sendMessage(this.spx + "Effects list:");
						if (this.plg.smog.size() > 0) {
							int pagesize = 15;
							maxpage = (int)Math.ceil((double)(this.plg.smog.size() / pagesize));
							if (maxpage * pagesize < this.plg.smog.size()) {
								++maxpage;
							}

							param = 1;
							if (args.length == 2) {
								param = Integer.parseInt(args[1]);
							}

							if (param > maxpage) {
								param = maxpage;
							}

							dy = (param - 1) * pagesize;
							j = param * pagesize;
							if (j >= this.plg.smog.size()) {
								j = this.plg.smog.size();
							}

							if (dy > j) {
								dy = j - pagesize;
							}

							if (dy < 0) {
								dy = 0;
							}

							for(i = dy; i < j; ++i) {
								sender.sendMessage(Integer.toString(i + 1) + ". " + this.plg.smog.SPtoString(i));
							}

							sender.sendMessage(this.spx + "Page [ " + Integer.toString(param) + " / " + Integer.toString(maxpage) + " ]     (" + Integer.toString(this.plg.smog.size()) + " effects)");
						} else {
							sender.sendMessage("No effects in list");
						}

						return true;
					}

					if (args[0].equalsIgnoreCase("del") && this.plg.CheckPerm(player, "no-smoking.smoke")) {
						if (this.plg.smog.size() >= 1) {
							if (args.length > 1) {
								r = this.plg.smog.remove_id(args[1]);
								if (r > 0) {
									sender.sendMessage(this.spx + Integer.toString(r) + " effect(s) with id " + args[1] + " removed");
									this.plg.FillPlayList();
								} else {
									sender.sendMessage(this.spx + "Smoke with id " + args[1] + " was not removed. Check id.");
								}
							} else {
								this.plg.smog.remove_id(((PlayerSettings)this.plg.pcfg.get(pname)).last_id);
								sender.sendMessage(this.spx + "Last effect removed");
								this.plg.FillPlayList();
							}
						} else {
							sender.sendMessage(this.spx + "No effects left");
						}

						return true;
					}

					if (args[0].equalsIgnoreCase("rdel") && this.plg.CheckPerm(player, "no-smoking.smoke")) {
						r = 0;
						maxpage = 3;
						if (args.length == 2) {
							maxpage = Integer.parseInt(args[1]);
						}

						if (maxpage > 16) {
							maxpage = 16;
						}

						sender.sendMessage(this.spx + "Remove effects placed near you (radius " + Integer.toString(maxpage) + ")");
						Location chk = new Location(player.getLocation().getWorld(), (double)Math.round(player.getLocation().getX()), (double)Math.round(player.getLocation().getY()), (double)Math.round(player.getLocation().getZ()));

						for(dy = (int)(chk.getX() - (double)maxpage); dy < (int)chk.getX() + maxpage; ++dy) {
							for(j = (int)(chk.getY() - (double)maxpage); j < (int)chk.getY() + maxpage; ++j) {
								for(i = (int)(chk.getZ() - (double)maxpage); i < (int)chk.getZ() + maxpage; ++i) {
									Location dloc = new Location(player.getLocation().getWorld(), (double)dy, (double)j, (double)i);
									if (this.plg.smog.checkLoc(dloc)) {
										sender.sendMessage("Effects deleted: " + this.plg.smog.checkLoc2Str(dloc));
										r += this.plg.smog.remove_loc(dloc);
									}
								}
							}
						}

						if (r > 0) {
							this.plg.FillPlayList();
						}

						sender.sendMessage(this.spx + Integer.toString(r) + " effect(s) removed.");
						return true;
					}

					if (args[0].equalsIgnoreCase("near") && this.plg.CheckPerm(player, "no-smoking.smoke")) {
						r = 8;
						if (r > 16) {
							r = 16;
						}

						if (args.length == 2) {
							r = Integer.parseInt(args[1]);
						}

						sender.sendMessage(this.spx + "Effects placed near you (radius " + Integer.toString(r) + ")");
						Location chk = new Location(player.getLocation().getWorld(), (double)Math.round(player.getLocation().getX()), (double)Math.round(player.getLocation().getY()), (double)Math.round(player.getLocation().getZ()));

						for(param = (int)(chk.getX() - (double)r); param < (int)chk.getX() + r; ++param) {
							for(dy = (int)(chk.getY() - (double)r); dy < (int)chk.getY() + r; ++dy) {
								for(j = (int)(chk.getZ() - (double)r); j < (int)chk.getZ() + r; ++j) {
									Location dloc = new Location(player.getLocation().getWorld(), (double)param, (double)dy, (double)j);
									if (this.plg.smog.checkLoc(dloc)) {
										sender.sendMessage(this.plg.smog.checkLoc2Str(dloc));
									}
								}
							}
						}

						return true;
					}

					if (args[0].equalsIgnoreCase("rst") && this.plg.CheckPerm(player, "no-smoking.config")) {
						this.plg.Rst();
						player.sendMessage(this.spx + ChatColor.RED + "Effects restarted...");
						return true;
					}

					prm = false;

					for(maxpage = 0; maxpage < args.length; ++maxpage) {
						param = -1;
						String[] ln = args[maxpage].split("=");
						if (ln.length > 0) {
							if (ln.length == 2 && ln[1].matches("[1-9]+[0-9]*")) {
								param = Integer.parseInt(ln[1]);
							}

							if (ln[0].equalsIgnoreCase("eff")) {
								if (ln.length != 2) {
									((PlayerSettings)this.plg.pcfg.get(pname)).effect = 0;
									((PlayerSettings)this.plg.pcfg.get(pname)).rate = 3;
								} else {
									prm = true;
									if ((ln[1].equalsIgnoreCase("flame") || ln[1].equalsIgnoreCase("2")) && this.plg.CheckPerm(player, "no-smoking.smoke.flame")) {
										((PlayerSettings)this.plg.pcfg.get(pname)).effect = 1;
										((PlayerSettings)this.plg.pcfg.get(pname)).rate = 6;
									} else if ((ln[1].equalsIgnoreCase("signal") || ln[1].equalsIgnoreCase("3")) && this.plg.CheckPerm(player, "no-smoking.smoke.signal")) {
										((PlayerSettings)this.plg.pcfg.get(pname)).effect = 2;
										((PlayerSettings)this.plg.pcfg.get(pname)).rate = 5;
									} else if ((ln[1].equalsIgnoreCase("potion") || ln[1].equalsIgnoreCase("4")) && this.plg.CheckPerm(player, "no-smoking.smoke.potion")) {
										((PlayerSettings)this.plg.pcfg.get(pname)).effect = 3;
										((PlayerSettings)this.plg.pcfg.get(pname)).rate = 5;
										((PlayerSettings)this.plg.pcfg.get(pname)).pot = 11;
									} else if ((ln[1].equalsIgnoreCase("pearl") || ln[1].equalsIgnoreCase("5")) && this.plg.CheckPerm(player, "no-smoking.smoke.pearl")) {
										((PlayerSettings)this.plg.pcfg.get(pname)).effect = 4;
										((PlayerSettings)this.plg.pcfg.get(pname)).rate = 3;
									} else if ((ln[1].equalsIgnoreCase("eye") || ln[1].equalsIgnoreCase("6")) && this.plg.CheckPerm(player, "no-smoking.smoke.eye")) {
										((PlayerSettings)this.plg.pcfg.get(pname)).effect = 5;
										((PlayerSettings)this.plg.pcfg.get(pname)).rate = 2;
									} else if ((ln[1].equalsIgnoreCase("song") || ln[1].equalsIgnoreCase("7")) && this.plg.CheckPerm(player, "no-smoking.smoke.song")) {
										((PlayerSettings)this.plg.pcfg.get(pname)).effect = 6;
										((PlayerSettings)this.plg.pcfg.get(pname)).rate = 10;
										((PlayerSettings)this.plg.pcfg.get(pname)).song = 0;
									} else if ((ln[1].equalsIgnoreCase("sound") || ln[1].equalsIgnoreCase("8")) && this.plg.CheckPerm(player, "no-smoking.smoke.sfx")) {
										((PlayerSettings)this.plg.pcfg.get(pname)).effect = 7;
										((PlayerSettings)this.plg.pcfg.get(pname)).rate = 1;
									} else if ((ln[1].equalsIgnoreCase("light") || ln[1].equalsIgnoreCase("9")) && this.plg.CheckPerm(player, "no-smoking.smoke.light")) {
										((PlayerSettings)this.plg.pcfg.get(pname)).effect = 8;
										((PlayerSettings)this.plg.pcfg.get(pname)).rate = 10;
										((PlayerSettings)this.plg.pcfg.get(pname)).chance = 10;
										((PlayerSettings)this.plg.pcfg.get(pname)).lmode = 5;
									} else if ((ln[1].equalsIgnoreCase("explosion") || ln[1].equalsIgnoreCase("10")) && this.plg.CheckPerm(player, "no-smoking.smoke.explosion")) {
										((PlayerSettings)this.plg.pcfg.get(pname)).effect = 9;
										((PlayerSettings)this.plg.pcfg.get(pname)).rate = 10;
										((PlayerSettings)this.plg.pcfg.get(pname)).chance = 30;
									} else {
										((PlayerSettings)this.plg.pcfg.get(pname)).effect = 0;
										((PlayerSettings)this.plg.pcfg.get(pname)).rate = 3;
									}
								}
							} else if (ln[0].equalsIgnoreCase("chance")) {
								prm = true;
								if (param > 0) {
									((PlayerSettings)this.plg.pcfg.get(pname)).chance = param;
								}
							} else if (ln[0].equalsIgnoreCase("lmode")) {
								prm = true;
								if (param <= 0 || param >= 6) {
									param = 0;
									if (ln.length == 2) {
										for(j = 0; j < 6; ++j) {
											if (this.plg.lightmode[j].equalsIgnoreCase(ln[1])) {
												param = j;
												break;
											}
										}
									}
								}

								((PlayerSettings)this.plg.pcfg.get(pname)).lmode = param;
							} else if (ln[0].equalsIgnoreCase("rate")) {
								prm = true;
								if (param > 0) {
									((PlayerSettings)this.plg.pcfg.get(pname)).rate = param;
								}
							} else if (!ln[0].equalsIgnoreCase("wd") && !ln[0].equalsIgnoreCase("wind")) {
								if ((ln[0].equalsIgnoreCase("disc") || ln[0].equalsIgnoreCase("disk")) && this.plg.CheckPerm(player, "no-smoking.smoke.song")) {
									prm = true;
									if (param > 0 && param <= 12) {
										--param;
									} else if (ln.length == 2) {
										if (ln[1].equalsIgnoreCase("13")) {
											param = 0;
										} else if (ln[1].equalsIgnoreCase("cat")) {
											param = 1;
										} else if (ln[1].equalsIgnoreCase("block")) {
											param = 2;
										} else if (ln[1].equalsIgnoreCase("chirp")) {
											param = 3;
										} else if (ln[1].equalsIgnoreCase("far")) {
											param = 4;
										} else if (ln[1].equalsIgnoreCase("mall")) {
											param = 5;
										} else if (ln[1].equalsIgnoreCase("mellohi")) {
											param = 6;
										} else if (ln[1].equalsIgnoreCase("stal")) {
											param = 7;
										} else if (ln[1].equalsIgnoreCase("strad")) {
											param = 8;
										} else if (ln[1].equalsIgnoreCase("ward")) {
											param = 9;
										} else if (!ln[1].equalsIgnoreCase("11disc") && !ln[1].equalsIgnoreCase("11_disc")) {
											if (ln[1].equalsIgnoreCase("wait")) {
												param = 11;
											}
										} else {
											param = 10;
										}
									}

									if (param <= 0 || param > 11) {
										param = 0;
									}

									((PlayerSettings)this.plg.pcfg.get(pname)).song = param;
								} else if (ln[0].equalsIgnoreCase("sfx") && this.plg.CheckPerm(player, "no-smoking.smoke.sfx")) {
									prm = true;
									if (param > 0 && param <= 11) {
										--param;
									} else if (ln.length == 2) {
										if (!ln[1].equalsIgnoreCase("blaze") && !ln[1].equalsIgnoreCase("blz")) {
											if (ln[1].equalsIgnoreCase("bow")) {
												param = 1;
											} else if (!ln[1].equalsIgnoreCase("click1") && !ln[1].equalsIgnoreCase("click")) {
												if (ln[1].equalsIgnoreCase("click2")) {
													param = 3;
												} else if (ln[1].equalsIgnoreCase("door")) {
													param = 4;
												} else if (!ln[1].equalsIgnoreCase("extinguish") && !ln[1].equalsIgnoreCase("psh")) {
													if (ln[1].equalsIgnoreCase("gshoot")) {
														param = 6;
													} else if (ln[1].equalsIgnoreCase("gshriek")) {
														param = 7;
													} else if (ln[1].equalsIgnoreCase("ziron")) {
														param = 8;
													} else if (ln[1].equalsIgnoreCase("zwood")) {
														param = 9;
													} else if (ln[1].equalsIgnoreCase("zdestory")) {
														param = 10;
													}
												} else {
													param = 5;
												}
											} else {
												param = 2;
											}
										} else {
											param = 0;
										}
									}

									if (param <= 0 || param > 10) {
										param = 0;
									}

									((PlayerSettings)this.plg.pcfg.get(pname)).sfx = param;
								} else if (ln[0].equalsIgnoreCase("pot") && this.plg.CheckPerm(player, "no-smoking.smoke.potion")) {
									prm = true;
									if (!ln[1].equalsIgnoreCase("random") && !ln[1].equalsIgnoreCase("rnd")) {
										if (ln[1].equalsIgnoreCase("firework") || ln[1].equalsIgnoreCase("fw")) {
											param = 12;
										}
									} else {
										param = 11;
									}

									if (param < 1 || param > 12) {
										param = 11;
									}

									((PlayerSettings)this.plg.pcfg.get(pname)).pot = param;
								} else if (ln[0].equalsIgnoreCase("id")) {
									prm = true;
									if (ln.length == 2) {
										((PlayerSettings)this.plg.pcfg.get(pname)).id = ln[1];
									} else {
										((PlayerSettings)this.plg.pcfg.get(pname)).id = "";
									}
								}
							} else {
								prm = true;
								if (ln.length == 2) {
									((PlayerSettings)this.plg.pcfg.get(pname)).wd = this.plg.ParseDirection(ln[1]);
								} else {
									((PlayerSettings)this.plg.pcfg.get(pname)).wd = this.plg.ParseDirection("wind");
								}
							}
						}
					}

					if (prm) {
						this.PrintCurVar(player);
					} else {
						this.ShowHelp(player, "");
					}

					return true;
				}
			}
		}

		return false;
	}

	public void PrintCurVar(Player player) {
		String pname = player.getName();
		int param = ((PlayerSettings)this.plg.pcfg.get(pname)).effect;
		String ctid = ((PlayerSettings)this.plg.pcfg.get(pname)).id;
		if (!ctid.isEmpty()) {
			ctid = "  id: " + ChatColor.AQUA + ctid + ChatColor.WHITE;
		}

		String effn = ChatColor.AQUA + this.plg.Eff2Str(param) + ChatColor.WHITE + ctid;
		switch(param) {
		case 0:
			player.sendMessage(this.spx + "Current effect: " + effn + "  wind: " + ChatColor.AQUA + this.plg.Dir2Wind(((PlayerSettings)this.plg.pcfg.get(pname)).wd) + ChatColor.WHITE + "  rate: " + ChatColor.AQUA + Integer.toString(((PlayerSettings)this.plg.pcfg.get(pname)).rate));
			break;
		case 1:
			player.sendMessage(this.spx + "Current effect: " + effn + "  rate: " + ChatColor.AQUA + Integer.toString(((PlayerSettings)this.plg.pcfg.get(pname)).rate));
			break;
		case 2:
			player.sendMessage(this.spx + "Current effect: " + effn + "  rate: " + ChatColor.AQUA + Integer.toString(((PlayerSettings)this.plg.pcfg.get(pname)).rate));
			break;
		case 3:
			player.sendMessage(this.spx + "Current effect: " + effn + "  potion: " + ChatColor.AQUA + Integer.toString(((PlayerSettings)this.plg.pcfg.get(pname)).pot) + ChatColor.WHITE + "  rate: " + ChatColor.AQUA + Integer.toString(((PlayerSettings)this.plg.pcfg.get(pname)).rate));
			break;
		case 4:
			player.sendMessage(this.spx + "Current effect: " + effn + "  rate: " + ChatColor.AQUA + Integer.toString(((PlayerSettings)this.plg.pcfg.get(pname)).rate));
			break;
		case 5:
			player.sendMessage(this.spx + "Current effect: " + effn + "  rate: " + ChatColor.AQUA + Integer.toString(((PlayerSettings)this.plg.pcfg.get(pname)).rate));
			break;
		case 6:
			player.sendMessage(this.spx + "Current effect: " + effn + "  disc: " + ChatColor.AQUA + this.plg.Song2Str(((PlayerSettings)this.plg.pcfg.get(pname)).song));
			break;
		case 7:
			player.sendMessage(this.spx + "Current effect: " + effn + "  sfx: " + ChatColor.AQUA + this.plg.Sfx2Str(((PlayerSettings)this.plg.pcfg.get(pname)).sfx) + ChatColor.WHITE + "  rate: " + ChatColor.AQUA + Integer.toString(((PlayerSettings)this.plg.pcfg.get(pname)).rate));
			break;
		case 8:
			player.sendMessage(this.spx + "Current effect: " + effn);
			player.sendMessage("rate: " + ChatColor.AQUA + Integer.toString(((PlayerSettings)this.plg.pcfg.get(pname)).rate) + "  chance: " + ChatColor.AQUA + Integer.toString(((PlayerSettings)this.plg.pcfg.get(pname)).chance) + "% " + ChatColor.WHITE + "  lightmode: " + ChatColor.AQUA + ((PlayerSettings)this.plg.pcfg.get(pname)).lmode);
			player.sendMessage(ChatColor.GRAY + "Warning: if rate is not equal 10, 50, 100 it will be rounded.");
			break;
		case 9:
			player.sendMessage(this.spx + "Current effect: " + effn + ChatColor.WHITE + "  rate: " + ChatColor.AQUA + Integer.toString(((PlayerSettings)this.plg.pcfg.get(pname)).rate));
		}

	}

	public void ShowHelp(Player p, String cmd) {
		ChatColor wh = ChatColor.WHITE;
		ChatColor aq = ChatColor.AQUA;
		ChatColor gr = ChatColor.GREEN;
		p.sendMessage(ChatColor.RED + "No Smoking!:" + gr + " Help page");
		if (cmd.equalsIgnoreCase("eff")) {
			p.sendMessage(gr + "/smoke eff={effect}" + wh + " - set up current effect");
			p.sendMessage("Effects: smoke, flame, signal, potion, pearl, eye, song, sound, light");
			p.sendMessage(aq + "smoke" + wh + " - smoke effect, customizable by " + aq + "wind" + wh + " direction and " + aq + "rate");
			p.sendMessage(aq + "flame" + wh + " - mob spawner flames, customizable by " + aq + "rate");
			p.sendMessage(aq + "signal" + wh + " - ender signal, customizable by " + aq + "rate");
			p.sendMessage(aq + "potion" + wh + " - potionbreak effect, customizable by type (" + aq + "pot" + wh + ") and " + aq + "rate");
			p.sendMessage(aq + "pearl" + wh + " - ender pearl effect, customizable by " + aq + "rate");
			p.sendMessage(aq + "eye" + wh + " - ender eye effect, customizable by " + aq + "rate");
			p.sendMessage(aq + "song" + wh + " - plays song, customizable by " + aq + "disc");
			p.sendMessage(aq + "sound" + wh + " - sound effect, customizable by " + aq + "sfx");
			p.sendMessage(aq + "light" + wh + " - lightning, customizable by " + aq + "chance" + wh + " and " + aq + "rate (10,50,100)");
			p.sendMessage("This command can be combined with parameters:");
			p.sendMessage(gr + "rate, wind, disc, sfx, pot, id, chance, lmode");
			p.sendMessage("type " + gr + "/help [parameter]" + wh + "to get additional help");
		} else if (cmd.equalsIgnoreCase("rate")) {
			p.sendMessage(gr + "/smoke rate={1..10}" + wh + " - set the rate of effect. Higher value rate - less effect plays.");
			p.sendMessage(gr + "/smoke rate={10, 50, 100}" + wh + " - set the rate of lightning effect.");
			p.sendMessage("This command can be combined with parameters:");
			p.sendMessage(gr + "eff, wind, disc, sfx, pot, id, chance, lmode");
			p.sendMessage("type " + gr + "/help [parameter]" + wh + "to get additional help");
		} else if (cmd.equalsIgnoreCase("chance")) {
			p.sendMessage(gr + "/smoke chance={1..100}" + wh + " - set the chance of lightning bolt.");
			p.sendMessage("This command can be combined with parameters:");
			p.sendMessage(gr + "eff, rate, wind, disc, sfx, pot, id, lmode");
			p.sendMessage("type " + gr + "/help [parameter]" + wh + "to get additional help");
		} else if (cmd.equalsIgnoreCase("lmode")) {
			p.sendMessage(gr + "/smoke lmode={0..5}" + wh + " - sets mode of lightning occurrence:");
			p.sendMessage("0 - anytime, 1 - day, 2 - night, 3 - day-storm, 4 - night-storm, 5 - storm");
			p.sendMessage("This command can be combined with parameters:");
			p.sendMessage(gr + "eff, rate, wind, disc, sfx, pot, id, lmode");
			p.sendMessage("type " + gr + "/help [parameter]" + wh + "to get additional help");
		} else if (!cmd.equalsIgnoreCase("wind") && !cmd.equalsIgnoreCase("wd")) {
			if (!cmd.equalsIgnoreCase("disc") && !cmd.equalsIgnoreCase("disk")) {
				if (cmd.equalsIgnoreCase("pot")) {
					p.sendMessage(gr + "/smoke pot={1..10, random|rnd, firework|fw}" + wh + " - define potion color for potion break effect.");
					p.sendMessage("There are ten potions, defined by number. And you can get random potion break effect, and firework (combined from three potions)");
					p.sendMessage("This command can be combined with parameters:");
					p.sendMessage(gr + "eff, rate, wind, disc, sfx, id, chance, lmode");
					p.sendMessage("type " + gr + "/help [parameter]" + wh + "to get additional help");
				} else if (cmd.equalsIgnoreCase("sfx")) {
					p.sendMessage(gr + "/smoke sfx={sfx name}" + wh + " - define the sound effect type");
					p.sendMessage("Available effect types: ");
					p.sendMessage(aq + "blaze, bow, click1, click2, door, extinguish, gshoot, gshriek, ziron, zdoor, zdestroy");
					p.sendMessage("The effect type can also be selected by it's number. For example " + gr + "/smoke sfx=2" + wh + " is equivalent to " + gr + "/smoke sfx=bow");
					p.sendMessage("This command can be combined with parameters:");
					p.sendMessage(gr + "eff, rate, wind, disc, pot, id, chance, lmode");
					p.sendMessage("type " + gr + "/help [parameter]" + wh + "to get additional help");
				} else if (cmd.equalsIgnoreCase("id")) {
					p.sendMessage(gr + "/smoke id={id}" + wh + " - define the ID to next smoke points ");
					p.sendMessage("If ID is empty it will generated to every new spawned effect automatically");
					p.sendMessage("This command can be combined with parameters:");
					p.sendMessage(gr + "eff, rate, wind, disc, sfx, pot, chance, lmode");
					p.sendMessage("type " + gr + "/help [parameter]" + wh + "to get additional help");
				} else if (cmd.equalsIgnoreCase("cfg")) {
					p.sendMessage(gr + "/smoke cfg" + wh + " - display current configurations");
					p.sendMessage(gr + "/smoke cfg disteff=<distance> distent=<distance> ");
					p.sendMessage(gr + "distsfx=<distance> distsng=<distance> distlht=<distance>");
					p.sendMessage(gr + "tickeff=[time] tickplay=[time] tickwind=[time]");
					p.sendMessage("type " + gr + "/help [parameter]" + wh + "to get additional help");
				} else if (!cmd.equalsIgnoreCase("tickeff") && !cmd.equalsIgnoreCase("tickplay") && !cmd.equalsIgnoreCase("tickwind")) {
					if (!cmd.equalsIgnoreCase("disteff") && !cmd.equalsIgnoreCase("distent") && !cmd.equalsIgnoreCase("distsfx") && !cmd.equalsIgnoreCase("distsng") && !cmd.equalsIgnoreCase("distlht")) {
						if (cmd.equalsIgnoreCase("rst")) {
							p.sendMessage(gr + "/smoke rst" + wh + " - reseting playing effect. This command could help, when you spawning new song effect - to start it play.");
						} else if (cmd.equalsIgnoreCase("rdel")) {
							p.sendMessage(gr + "/smoke rdel [radius]" + wh + " - remove all effects placed near you (default radius 3)");
						} else if (cmd.equalsIgnoreCase("del")) {
							p.sendMessage(gr + "/smoke rdel [id] " + wh + " - remove effect-point (or remove last, if ID not entered)");
						} else if (cmd.equalsIgnoreCase("near")) {
							p.sendMessage(gr + "/smoke near [radius] " + wh + " - list all effectes placed near you (default radius 8)");
						} else if (cmd.equalsIgnoreCase("list")) {
							p.sendMessage(gr + "/smoke list [page] " + wh + " - list all smoke points");
						} else if (cmd.equalsIgnoreCase("show")) {
							p.sendMessage(gr + "/smoke show [id] " + wh + " - reveals previously hidden effects with given id");
						} else if (cmd.equalsIgnoreCase("hide")) {
							p.sendMessage(gr + "/smoke hide [id] " + wh + " - hide effects with given id");
						} else if (cmd.equalsIgnoreCase("wand")) {
							p.sendMessage(gr + "/smoke wand [material id]" + wh + " - sets the wand material (default - 263, coal)");
						} else if (cmd.equalsIgnoreCase("allsmoke")) {
							p.sendMessage(gr + "/smoke allsmoke" + wh + " - toggle playing smokes instead of all effects");
						} else if (cmd.equalsIgnoreCase("smsound")) {
							p.sendMessage(gr + "/smoke smsound" + wh + " - toggle playing smoke in additional to song and sound effect");
						} else if (cmd.equalsIgnoreCase("useperm")) {
							p.sendMessage(gr + "/smoke useperm" + wh + " - toggle permissions usage");
						} else if (cmd.equalsIgnoreCase("smoke")) {
							p.sendMessage(gr + "/smoke " + wh + " - switch mode. When smoke mode is on, use coal to place effects on any block. ");
						} else {
							p.sendMessage("Available commands:");
							p.sendMessage(gr + "/smoke" + wh + " - toggle edit (smoke) mode, when smoke mode is enabled use coal to place effects on any block.");
							p.sendMessage(gr + "/smoke help [command|parameter]" + wh + " - show help about commands or parameters");
							p.sendMessage(gr + "/smoke [commands|parameter=value]" + wh + " - execute command, or");
							p.sendMessage("set parameter value");
							p.sendMessage("Command and parameter list:");
							p.sendMessage(gr + "help, cfg, rst, list, near, del, rdel, hide, show, allsmoke, smsound");
							p.sendMessage(gr + "useperm, ticktime, windticktime, eff, rate, wind, disc, sfx, pot");
							p.sendMessage("type " + gr + "/help [command/parameter]" + wh + " to get additional help");
						}
					} else {
						p.sendMessage(gr + "/smoke cfg disteff=<distance>" + wh + " - sets the visibility distance for");
						p.sendMessage("effects (smoke, flame, ender signal, potion break)");
						p.sendMessage(gr + "/smoke cfg distent=<distance>" + wh + " - sets the visibility distance for");
						p.sendMessage("entity-effects (ender pearl, ender eye)");
						p.sendMessage(gr + "/smoke cfg distsng=<distance>" + wh + " - sets the hearing distance for");
						p.sendMessage("songs (disc melodies)");
						p.sendMessage(gr + "/smoke cfg distsfx=<distance>" + wh + " - sets the hearing distance for");
						p.sendMessage("sound effects");
						p.sendMessage(gr + "/smoke cfg distlht=<distance>" + wh + " - sets the visibility (and hearing)");
						p.sendMessage("distance for lightning effects");
					}
				} else {
					p.sendMessage("Configure frequency of loops:");
					p.sendMessage(gr + "/smoke cfg tickeff=[ticks] " + wh + " - effects playing loop");
					p.sendMessage(gr + "/smoke cfg tickplay=[ticks] " + wh + " - effects visibility check loop");
					p.sendMessage(gr + "/smoke cfg tickwind=[ticks] " + wh + " - wind changing loop");
					p.sendMessage(ChatColor.GRAY + "20 ticks is approximately equal to 1 sec");
				}
			} else {
				p.sendMessage(gr + "/smoke disc={disc name}" + wh + " - define the disc for song effect.");
				p.sendMessage("Available discs: ");
				p.sendMessage(aq + "13, cat, blocks, chirp, far, mall, mellohi, stal, strad, ward, 11disc");
				p.sendMessage("The disc can also be selected by it's number. For example " + gr + "/smoke disc=2" + wh + " is equivalent to " + gr + "/smoke disc=cat ");
				p.sendMessage("This command can be combined with parameters:");
				p.sendMessage(gr + "eff, rate, wind, sfx, pot, id, chance, lmode");
				p.sendMessage("type " + gr + "/help [parameter]" + wh + "to get additional help");
			}
		} else {
			p.sendMessage(gr + "/smoke wind={wind direction}" + wh + "(or " + gr + "/smoke wd={wind direction}" + wh + ") - define the wind direction for next smoke point. Possible wind direction: N, NW, NE, S, SE, SW, calm (up), random (rnd), all, wind ");
			p.sendMessage("This command can be combined with parameters:");
			p.sendMessage(gr + "eff, rate, disc, sfx, pot, id, chance, lmode");
			p.sendMessage("type " + gr + "/help [parameter]" + wh + "to get additional help");
		}

	}

	public String EnDis(boolean b) {
		return b ? "enabled" : "disabled";
	}
}
