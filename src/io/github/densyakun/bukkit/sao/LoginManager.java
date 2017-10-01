package io.github.densyakun.bukkit.sao;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
public class LoginManager implements Runnable {
	static List<LoginManager> z = new ArrayList<LoginManager>();
	Player player;
	LoginManager(Player player) {
		this.player = player;
		z.add(this);
		new Thread(this).start();
	}
	@Override
	public void run() {
		player.sendMessage(ChatColor.GRAY + "Link Start!");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		player.sendMessage(ChatColor.GREEN + "Touch " + ChatColor.BOLD.toString() + "OK");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		player.sendMessage(ChatColor.GREEN + "sight " + ChatColor.BOLD.toString() + "OK");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		player.sendMessage(ChatColor.GREEN + "Hearing " + ChatColor.BOLD.toString() + "OK");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		player.sendMessage(ChatColor.GREEN + "Taste " + ChatColor.BOLD.toString() + "OK");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		player.sendMessage(ChatColor.GREEN + "Smell " + ChatColor.BOLD.toString() + "OK");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		titlemotd(player.getName(), "Welcome to Sword Art Online !", "aqua");
	}
	static LoginManager getLogin(Player player) {
		for (int b = 0; b < z.size(); b++) {
			LoginManager c = z.get(b);
			if (c.player.equals(player)) {
				return c;
			}
		}
		return null;
	}
	static void removeLogin(Player player) {
		for (int b = 0; b < z.size(); b++) {
			if (z.get(b).player.equals(player)) {
				z.remove(b);
			}
		}
	}
	static void titlemotd(String target, String text, String color) {
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title " + target + " title [{\"text\":\"" + text + "\",\"color\":\"" + color + "\"}]");
	}
}
