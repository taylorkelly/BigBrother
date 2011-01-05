package me.taylorkelly.bigbrother.datablock;

import org.bukkit.*;

public class Command extends BBDataBlock {

	public Command(Player player, String command) {
		//TODO Better World support
		super(player.getName(), COMMAND, 0, player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ(), command);
	}
	
}
