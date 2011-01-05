package me.taylorkelly.bigbrother.datablock;

import org.bukkit.*;

public class Command extends BBDataBlock {

	public Command(Player player, String command) {
		//TODO Better World support
		super(player.getName(), COMMAND, 0, player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ(), command);
	}

	public void rollback(Server server) {}
	
	public static BBDataBlock getBBDataBlock(String player, int world, int x, int y, int z, String data) {
		return new Command(player, world, x, y, z, data);
	}

	private Command(String player, int world, int x, int y, int z, String data) {
		super(player, COMMAND, world, x, y, z, data);
	}
	
}
