package me.taylorkelly.bigbrother.datablock;

import me.taylorkelly.bigbrother.BBPlayerInfo;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;

public class Teleport extends BBDataBlock {
	public Teleport(String player, Location to) {
		super(player, Action.TELEPORT, to.getWorld().getName(), to.getBlockX(), to.getBlockY(), to.getBlockZ(), 0, "");
	}

	public static BBDataBlock getBBDataBlock(BBPlayerInfo pi, String world, int x, int y, int z, int type, String data) {
		return new Teleport(pi, world, x, y, z, type, data);
	}

	private Teleport(BBPlayerInfo player, String world, int x, int y, int z, int type, String data) {
		super(player, Action.TELEPORT, world, x, y, z, type, data);
	}

	public void rollback(World wld) {}
	public void redo(Server server) {}
}
