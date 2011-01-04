package me.taylorkelly.bigbrother.datablock;

import org.bukkit.*;

public class Chat extends BBDataBlock {
	public Chat(Player player, String message) {
		super(player.getName(), COMMAND, player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ(), message);
	}
}
