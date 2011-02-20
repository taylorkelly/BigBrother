package me.taylorkelly.bigbrother.datablock;

import me.taylorkelly.bigbrother.BBSettings;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public class Login extends BBDataBlock {
	public Login(Player player, int world) {
        super(player.getName(), Action.LOGIN, world, player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ(), 0,
                BBSettings.ipPlayer ? player.getAddress().getAddress().toString().substring(1) : "");
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
