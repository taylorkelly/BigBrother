package me.taylorkelly.bigbrother.datablock;

import org.bukkit.*;

public class Login extends BBDataBlock {
	public Login(Player player) {
		//TODO Better World support
		super(player.getName(), LOGIN, 0, player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ(), "");
	}
	
	public static BBDataBlock getBBDataBlock(String player, int world, int x, int y, int z, String data) {
		return new Login(player, world, x, y, z, data);
	}

	private Login(String player, int world, int x, int y, int z, String data) {
		super(player, LOGIN, world, x, y, z, data);
	}

	public void rollback(Server server) {}
}
