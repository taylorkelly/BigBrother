package me.taylorkelly.bigbrother.datablock;

import org.bukkit.*;

public class Disconnect extends BBDataBlock {
	public Disconnect(Player player) {
		//TODO Better World support
		super(player.getName(), DISCONNECT, 0, player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ(), "");
	}
	
	public static BBDataBlock getBBDataBlock(String player, int world, int x, int y, int z, String data) {
		return new Disconnect(player, world, x, y, z, data);
	}

	private Disconnect(String player, int world, int x, int y, int z, String data) {
		super(player, DISCONNECT, world, x, y, z, data);
	}

	public void rollback(Server server) {}
	public void redo(Server server) {}

}
