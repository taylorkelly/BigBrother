package me.taylorkelly.bigbrother.datablock;

import org.bukkit.*;

public class Login extends BBDataBlock {
	public Login(Player player) {
		super(player.getName(), LOGIN, player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ(), "");
	}
}
