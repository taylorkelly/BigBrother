package me.taylorkelly.bigbrother.datablock;

import org.bukkit.*;

public class Command extends BBDataBlock {

	public Command(Player player, String command) {
		super(player.getName(), COMMAND, player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ(), command);
	}
	
}
