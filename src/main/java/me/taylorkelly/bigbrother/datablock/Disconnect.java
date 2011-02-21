package me.taylorkelly.bigbrother.datablock;

import org.bukkit.Location;
import org.bukkit.Server;

public class Disconnect extends BBDataBlock {
	public Disconnect(String player, Location location, String world) {
		super(player, Action.DISCONNECT, world, location.getBlockX(), location.getBlockY(), location.getBlockZ(), 0, "");
	}

	public static BBDataBlock getBBDataBlock(String player, String world, int x, int y, int z, int type, String data) {
		return new Disconnect(player, world, x, y, z, type, data);
	}

	private Disconnect(String player, String world, int x, int y, int z, int type, String data) {
		super(player, Action.DISCONNECT, world, x, y, z, type, data);
	}

	public void rollback(Server server) {}
	public void redo(Server server) {}

}
