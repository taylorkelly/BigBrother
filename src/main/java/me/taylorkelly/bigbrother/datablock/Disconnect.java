package me.taylorkelly.bigbrother.datablock;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public class Disconnect extends BBDataBlock {
	public Disconnect(Player player) {
		//TODO Better World support
		super(player.getName(), Action.DISCONNECT, 0, player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ(), 0, "");
	}

	public static BBDataBlock getBBDataBlock(String player, int world, int x, int y, int z, int type, String data) {
		return new Disconnect(player, world, x, y, z, type, data);
	}

	private Disconnect(String player, int world, int x, int y, int z, int type, String data) {
		super(player, Action.DISCONNECT, world, x, y, z, type, data);
	}

	public void rollback(Server server) {}
	public void redo(Server server) {}

}
