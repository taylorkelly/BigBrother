package me.taylorkelly.bigbrother.datablock;

import org.bukkit.*;

public class Disconnect extends BBDataBlock {
	public Disconnect(Player player) {
		//TODO Better World support
		super(player.getName(), DISCONNECT, 0, player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ(), "");
	}
}
