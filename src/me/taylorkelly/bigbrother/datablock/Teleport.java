package me.taylorkelly.bigbrother.datablock;

import org.bukkit.*;

public class Teleport extends BBDataBlock {
	public Teleport(Player player, Location to) {
		//TODO Better World support
		super(player.getName(), TELEPORT, 0, to.getBlockX(), to.getBlockY(), to.getBlockZ(), "");
	}
}
