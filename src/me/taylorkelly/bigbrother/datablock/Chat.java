package me.taylorkelly.bigbrother.datablock;

import org.bukkit.*;

public class Chat extends BBDataBlock {
	public Chat(Player player, String message) {
		//TODO Better World support
		super(player.getName(), COMMAND, 0, player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ(), message);
	}
}
