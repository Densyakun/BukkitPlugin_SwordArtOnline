package io.github.densyakun.bukkit.sao;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
public class PlayerManager {
	static File pdatafile;
	static ArrayList<PlayerData> pdata = new ArrayList<PlayerData>();
	static boolean whitemode = false;
	static void init() {
		pdatafile = new File(Main.main.getDataFolder(), "pdata.dat");
	}
	@SuppressWarnings("unchecked")
	static void load() {
		try {
			if (pdatafile.exists()) {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(pdatafile));
				pdata = (ArrayList<PlayerData>) ois.readObject();
				ois.close();
			} else {
				pdatafile.createNewFile();
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	static void save() {
		try {
			pdatafile.createNewFile();
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(pdatafile));
			oos.writeObject(pdata);
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	static void setPlayerData(PlayerData playerdata) {
		for (int a = 0; a < pdata.size(); a++) {
			if (pdata.get(a).uuid.equals(playerdata.uuid)) {
				pdata.set(a, playerdata);
				return;
			}
		}
		pdata.add(playerdata);
	}
	static PlayerData getPlayerData(UUID uuid) {
		for (int a = 0; a < pdata.size(); a++) {
			PlayerData b = pdata.get(a);
			if (b.uuid.equals(uuid)) {
				return b;
			}
		}
		PlayerData data = new PlayerData(uuid);
		pdata.add(data);
		return data;
	}
	static void namereload(Player player) {
		PlayerData data = getPlayerData(player.getUniqueId());
		String name = new String();
		switch (data.getRank()) {
		case Owner:
			name += data.getRank().getChatColor() + "[O]" + ChatColor.WHITE;
			break;
		case Admin:
			name += data.getRank().getChatColor() + "[A]" + ChatColor.WHITE;
			break;
		default:
			player.setGameMode(GameMode.SURVIVAL);
			break;
		}
		player.setOp(data.getRank().isAdmin());
		if (data.getRank().isAdmin()) {
			name += ChatColor.BOLD;
		}
		String nick = data.nick;
		if (nick == null) {
			name += player.getName() + ChatColor.RESET;
		} else {
			name += nick + ChatColor.RESET;
		}
		String tabname = name.substring(0, name.length() <= 16 ? name.length() : 16);
		player.setDisplayName(name);
		player.setPlayerListName(tabname.charAt(tabname.length() - 1) == '§' ? tabname.substring(0, tabname.length() - 1) : tabname);
		if (!getPlayerData(player.getUniqueId()).getRank().isAdmin()) {
			Iterator<? extends Player> a = Bukkit.getServer().getOnlinePlayers().iterator();
			while (a.hasNext()) {
				Player c = a.next();
				PlayerData d = getPlayerData(c.getUniqueId());
				if (d.hide) {
					player.hidePlayer(c);
				} else {
					player.showPlayer(c);
				}
			}
		}
	}
	static void setWhiteMode(boolean whitemode) {
		if (PlayerManager.whitemode = whitemode) {
			Main.main.getLogger().info("ホワイトモードが有効");
		} else {
			Main.main.getLogger().info("ホワイトモードが無効");
		}
		Iterator<? extends Player> players = Bukkit.getServer().getOnlinePlayers().iterator();
		while (players.hasNext()) {
			Player a = players.next();
			if (getPlayerData(a.getUniqueId()).getRank().isAdmin()) {
				if (whitemode) {
					a.sendMessage(ChatColor.AQUA + "ホワイトモードが有効");
				} else {
					a.sendMessage(ChatColor.RED + "ホワイトモードが無効");
				}
			} else if (whitemode) {
				a.kickPlayer("ホワイトモード(管理者専用モード)になったためキックされました。復旧するまでしばらくお待ち下さい");
			}
		}
	}
}
