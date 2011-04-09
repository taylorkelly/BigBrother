package me.taylorkelly.bigbrother.datablock;

import me.taylorkelly.bigbrother.BBPlayerInfo;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Command extends BBDataBlock {

	public Command(Player player, String command, String world) {
		super(player.getName(), Action.COMMAND, world, player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ(), 0, command);
	}

	public void rollback(World wld) {}
	public void redo(Server server) {}


	public static BBDataBlock getBBDataBlock(BBPlayerInfo pi, String world, int x, int y, int z, int type, String data) {
		return new Command(pi, world, x, y, z, type, data);
	}

	private Command(BBPlayerInfo player, String world, int x, int y, int z, int type, String data) {
		super(player, Action.COMMAND, world, x, y, z, type, data);
	}

}
