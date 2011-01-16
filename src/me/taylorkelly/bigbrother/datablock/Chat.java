package me.taylorkelly.bigbrother.datablock;

import org.bukkit.*;
import org.bukkit.entity.Player;

public class Chat extends BBDataBlock {
	public Chat(Player player, String message) {
		//TODO Better World support
		super(player.getName(), CHAT, 0, player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ(), 0, message);
	}
	
	public void rollback(Server server) {}
	public void redo(Server server) {}
	
	public static BBDataBlock getBBDataBlock(String player, int world, int x, int y, int z, int type, String data) {
		return new Chat(player, world, x, y, z, type, data);
	}

	private Chat(String player, int world, int x, int y, int z, int type, String data) {
		super(player, CHAT, world, x, y, z, type, data);
	}
}
