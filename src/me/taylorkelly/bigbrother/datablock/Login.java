package me.taylorkelly.bigbrother.datablock;

import org.bukkit.*;

public class Login extends BBDataBlock {
	public Login(Player player) {
		//TODO Better World support
		super(player.getName(), LOGIN, 0, player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ(), "");
	}
}
