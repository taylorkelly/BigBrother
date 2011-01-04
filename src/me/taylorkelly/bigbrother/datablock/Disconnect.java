package me.taylorkelly.bigbrother.datablock;

import org.bukkit.*;

public class Disconnect extends BBDataBlock {
	public Disconnect(Player player) {
		super(player.getName(), DISCONNECT, player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ(), "");
	}
}
