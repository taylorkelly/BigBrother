package me.taylorkelly.bigbrother.datablock;

import org.bukkit.*;

public class Teleport extends BBDataBlock {
	public Teleport(Player player, Location to) {
		//TODO Better World support
		super(player.getName(), TELEPORT, 0, to.getBlockX(), to.getBlockY(), to.getBlockZ(), "");
	}
	
	public static BBDataBlock getBBDataBlock(String player, int world, int x, int y, int z, String data) {
		return new Teleport(player, world, x, y, z, data);
	}

	private Teleport(String player, int world, int x, int y, int z, String data) {
		super(player, TELEPORT, world, x, y, z, data);
	}

	public void rollback(Server server) {}
}
