package io.github.densyakun.bukkit.sao;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
public class Main extends JavaPlugin implements Listener {
	public static final String param_is_not_enough = ChatColor.GOLD + "パラメータが足りません";
	public static final String param_wrong_cmd = ChatColor.GOLD + "パラメータが間違っています";
	public static final String cmd_player_only = ChatColor.GOLD + "このコマンドはプレイヤーのみ実行できます";
	public static Main main;
	UUID owneruuid;
	int deathbantime = 60;
	Map<UUID, Long> deathban = new HashMap<UUID, Long>();
	@Override
	public void onLoad() {
		main = this;
	}
	@Override
	public void onEnable() {
		PlayerManager.init();
		load();
		getServer().getPluginManager().registerEvents(this, this);
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("sao")) {
			if (args.length == 0) {
				sender.sendMessage(param_is_not_enough);
			} else if (args[0].equalsIgnoreCase("admin")) {
				if (sender.isOp()) {
					if (args.length == 1) {
						sender.sendMessage(param_is_not_enough);
					} else if (args[1].equalsIgnoreCase("load")) {
						load();
					} else if (args[1].equalsIgnoreCase("save")) {
						save();
					} else if (args[1].equalsIgnoreCase("white")) {
						PlayerManager.setWhiteMode(PlayerManager.whitemode);
					} else if (args[1].equalsIgnoreCase("rank")) {
						if (args.length == 2) {
							sender.sendMessage(param_is_not_enough);
						} else if (args[2].equalsIgnoreCase("set")) {
							if (args.length == 3) {
								sender.sendMessage(param_is_not_enough);
							} else {
								Player player = null;
								if (args.length == 4) {
									if (sender instanceof Player) {
										player = (Player) sender;
									} else {
										sender.sendMessage(ChatColor.GREEN + "プレイヤーを指定して下さい");
									}
								} else {
									player = getServer().getPlayer(args[4]);
								}
								if (player != null) {
									try {
										PlayerRank rank = PlayerRank.valueOf(args[3]);
										PlayerData pdata = PlayerManager.getPlayerData(player.getUniqueId());
										pdata.setRank(rank);
										PlayerManager.setPlayerData(pdata);
										PlayerManager.namereload(player);
										sender.sendMessage(ChatColor.AQUA + "Done");
									} catch (IllegalArgumentException e) {
										String a = "";
										PlayerRank[] b = PlayerRank.values();
										for (int c = 0; c < b.length; c++) {
											if (c != 0) {
												a += ", ";
											}
											a += b[c];
										}
										sender.sendMessage(ChatColor.GOLD + a);
									}
								}
							}
						} else if (args[2].equalsIgnoreCase("get")) {
							Player player = null;
							if (args.length == 3) {
								if (sender instanceof Player) {
									player = (Player) sender;
								} else {
									sender.sendMessage(ChatColor.GREEN + "プレイヤーを指定して下さい");
								}
							} else {
								player = getServer().getPlayer(args[3]);
							}
							if (player != null) {
								PlayerRank rank = PlayerManager.getPlayerData(player.getUniqueId()).getRank();
								sender.sendMessage(rank.getChatColor() + rank.name());
							}
						} else {
							sender.sendMessage(param_wrong_cmd);
						}
					} else {
						sender.sendMessage(param_wrong_cmd);
					}
				} else {
					sender.sendMessage(param_wrong_cmd);
				}
			} else {
				sender.sendMessage(param_wrong_cmd);
			}
		} else if (label.equalsIgnoreCase("menu")) {
		} else if (label.equalsIgnoreCase("nick")) {
			if (sender instanceof Player) {
				PlayerData playerdata = PlayerManager.getPlayerData(((HumanEntity) sender).getUniqueId());
				if (args.length == 0) {
					playerdata.nick = null;
					PlayerManager.save();
					PlayerManager.namereload((Player) sender);
					sender.sendMessage(ChatColor.AQUA + "ニックネームを初期化しました");
					sender.sendMessage(ChatColor.GOLD + "ニックネームを設定するには、/nick (name) を実行して下さい");
				} else {
					if (sender instanceof Player) {
						playerdata.nick = args[0];
						PlayerManager.save();
						PlayerManager.namereload((Player) sender);
						sender.sendMessage(ChatColor.AQUA + "ニックネームを変更しました");
					} else {
						sender.sendMessage(cmd_player_only);
					}
				}
			}
		}
		return true;
	}
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (command.getName().equalsIgnoreCase("sao")) {
			if (args.length != 0 && args[0].equalsIgnoreCase("admin")) {
				if (args.length == 1) {
					List<String> a = new ArrayList<String>();
					a.add("load");
					a.add("save");
					a.add("rank");
					return a;
				} else if (args[1].equalsIgnoreCase("rank")) {
					if (args.length == 2) {
					} else if (args[2].equalsIgnoreCase("set")) {
						if (args.length == 3) {
							List<String> a = new ArrayList<String>();
							PlayerRank[] b = PlayerRank.values();
							for (int c = 0; c < b.length; c++) {
								a.add(b[c].name());
							}
							return a;
						}
					}
				}
			}
		}
		return null;
	}
	@Override
	public void onDisable() {
		Player[] players = getServer().getOnlinePlayers().toArray(new Player[0]);
		for (int a = 0; a < players.length; a++) {
			players[a].kickPlayer("再起動またはサーバー停止のため自動キックされました");
		}
		save();
	}
	void load() {
		saveDefaultConfig();
		String uuidstr = getConfig().getString("owneruuid", null);
		if (uuidstr != null) {
			owneruuid = UUID.fromString(uuidstr);
		}
		deathbantime = getConfig().getInt("deathbantime", 60);
		PlayerManager.load();
	}
	void save() {
		PlayerManager.save();
	}
	boolean isDeathBAN(UUID uuid) {
		Long time = deathban.get(uuid);
		if (time != null) {
			return new Date().getTime() - time < deathbantime * 1000;
		}
		return false;
	}
	@EventHandler
	public void EntityDamage(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			e.setCancelled(true);
		}
	}
	@EventHandler
	public void PlayerJoin(PlayerJoinEvent e) {
		//e.setJoinMessage(null);
		if (isDeathBAN(e.getPlayer().getUniqueId())) {
			e.getPlayer().kickPlayer("死亡後、" + deathbantime + "秒間はサーバーに入ることが出来ません。");
		} else {
			PlayerManager.namereload(e.getPlayer());
			if (!PlayerManager.getPlayerData(e.getPlayer().getUniqueId()).getRank().isAdmin() && PlayerManager.whitemode) {
				e.getPlayer().kickPlayer("ホワイトモード(管理者専用モード)になったためキックされました。復旧するまでしばらくお待ち下さい");
			} else {
				new LoginManager(e.getPlayer());
			}
		}
	}
	@EventHandler
	public void PlayerQuit(PlayerQuitEvent e) {
		LoginManager.removeLogin(e.getPlayer());
	}
	@EventHandler
	public void PlayerDeath(PlayerDeathEvent e) {
		e.getEntity().kickPlayer(deathbantime + "秒間はサーバーに入ることが出来ません。");
	}
	/*@EventHandler
	public void a(AsyncPlayerChatEvent e) {
		LoginManager login = LoginManager.getLogin(e.getPlayer());
		if (login != null) {
			e.setCancelled(true);
		}
	}*/
}
