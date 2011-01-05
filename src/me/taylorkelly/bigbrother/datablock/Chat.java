package me.taylorkelly.bigbrother.datablock;

import org.bukkit.*;

public class Chat extends BBDataBlock {
	public Chat(Player player, String message) {
		//TODO Better World support
		super(player.getName(), CHAT, 0, player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ(), message);
	}
	
	public void rollback(Server server) {}
	
	public static BBDataBlock getBBDataBlock(String player, int world, int x, int y, int z, String data) {
		return new Chat(player, world, x, y, z, data);
	}

	private Chat(String player, int world, int x, int y, int z, String data) {
		super(player, CHAT, world, x, y, z, data);
	}
}
