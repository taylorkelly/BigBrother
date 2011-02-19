package me.taylorkelly.bigbrother.datablock;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public class Login extends BBDataBlock {
	public Login(Player player) {
		//TODO Better World support
		super(player.getName(),Action.LOGIN, 0, player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ(), 0, "");
	}

	public static BBDataBlock getBBDataBlock(String player, int world, int x, int y, int z, int type, String data) {
		return new Login(player, world, x, y, z, type, data);
	}

	private Login(String player, int world, int x, int y, int z, int type,  String data) {
		super(player, Action.LOGIN, world, x, y, z, type, data);
	}

	public void rollback(Server server) {}
	public void redo(Server server) {}

}
