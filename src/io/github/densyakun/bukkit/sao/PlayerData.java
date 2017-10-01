package io.github.densyakun.bukkit.sao;
import java.io.Serializable;
import java.util.UUID;
public final class PlayerData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static int max_username = 8;
	public static int max_password = 16;
	UUID uuid;
	private PlayerRank rank;
	boolean hide = false;
	String nick;
	//public Language language = Language.japanese;
	public PlayerData(UUID uuid) {
		this.uuid = uuid;
		rank = PlayerRank.getDefault();
	}
	public PlayerRank getRank() {
		if (uuid.equals(Main.main.owneruuid)) {
			return PlayerRank.Owner;
		}
		return rank;
	}
	public PlayerRank getInternalRank() {
		return rank;
	}
	public void setRank(PlayerRank rank) {
		if (!uuid.equals(Main.main.owneruuid)) {
			this.rank = rank;
			PlayerManager.save();
		}
	}
}
