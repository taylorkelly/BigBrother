package me.taylorkelly.bigbrother.datablock;

import org.bukkit.*;

public class Teleport extends BBDataBlock {
	public Teleport(Player player, Location to) {
		super(player.getName(), TELEPORT, to.getBlockX(), to.getBlockY(), to.getBlockZ(), "");
	}
}
