package me.taylorkelly.bigbrother.datablock;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public class Command extends BBDataBlock {

	public Command(Player player, String command, String world) {
		super(player.getName(), Action.COMMAND, world, player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ(), 0, command);
	}

	public void rollback(Server server) {}
	public void redo(Server server) {}


	public static BBDataBlock getBBDataBlock(String player, String world, int x, int y, int z, int type, String data) {
		return new Command(player, world, x, y, z, type, data);
	}

	private Command(String player, String world, int x, int y, int z, int type, String data) {
		super(player, Action.COMMAND, world, x, y, z, type, data);
	}

}
