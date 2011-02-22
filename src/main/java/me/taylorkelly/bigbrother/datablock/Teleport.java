package me.taylorkelly.bigbrother.datablock;

import org.bukkit.Location;
import org.bukkit.Server;

public class Teleport extends BBDataBlock {
	public Teleport(String player, Location to) {
		super(player, Action.TELEPORT, to.getWorld().getName(), to.getBlockX(), to.getBlockY(), to.getBlockZ(), 0, "");
	}

	public static BBDataBlock getBBDataBlock(String player, String world, int x, int y, int z, int type, String data) {
		return new Teleport(player, world, x, y, z, type, data);
	}

	private Teleport(String player, String world, int x, int y, int z, int type, String data) {
		super(player, Action.TELEPORT, world, x, y, z, type, data);
	}

	public void rollback(Server server) {}
	public void redo(Server server) {}
}
