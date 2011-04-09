package me.taylorkelly.bigbrother.datablock;

import me.taylorkelly.bigbrother.BBPlayerInfo;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;

public class Disconnect extends BBDataBlock {
	public Disconnect(String player, Location location, String world) {
		super(player, Action.DISCONNECT, world, location.getBlockX(), location.getBlockY(), location.getBlockZ(), 0, "");
	}

	public static BBDataBlock getBBDataBlock(BBPlayerInfo pi, String world, int x, int y, int z, int type, String data) {
		return new Disconnect(pi, world, x, y, z, type, data);
	}

	private Disconnect(BBPlayerInfo player, String world, int x, int y, int z, int type, String data) {
		super(player, Action.DISCONNECT, world, x, y, z, type, data);
	}

	public void rollback(World wld) {}
	public void redo(Server server) {}

}
