package me.taylorkelly.bigbrother.datablock;

import me.taylorkelly.bigbrother.BBPlayerInfo;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Chat extends BBDataBlock {
	public Chat(Player player, String message, String world) {
		super(player.getName(), Action.CHAT, world, player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ(), 0, message);
	}

	public void rollback(World wld) {}
	public void redo(Server server) {}

	public static BBDataBlock getBBDataBlock(BBPlayerInfo pi, String world, int x, int y, int z, int type, String data) {
		return new Chat(pi, world, x, y, z, type, data);
	}

	private Chat(BBPlayerInfo player, String world, int x, int y, int z, int type, String data) {
		super(player, Action.CHAT, world, x, y, z, type, data);
	}
}
