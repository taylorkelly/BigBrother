package me.taylorkelly.bigbrother.datablock;

import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.tablemgrs.BBUsersTable;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Login extends BBDataBlock {
	public Login(Player player, String world) {
        super(BBUsersTable.getInstance().getUserByName(player.getName()), Action.LOGIN, world, player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ(), 0,
                BBSettings.ipPlayer ? player.getAddress().getAddress().toString().substring(1) : "");
	}

	public static BBDataBlock getBBDataBlock(BBPlayerInfo pi, String world, int x, int y, int z, int type, String data) {
		return new Login(pi, world, x, y, z, type, data);
	}

	private Login(BBPlayerInfo player, String world, int x, int y, int z, int type,  String data) {
		super(player, Action.LOGIN, world, x, y, z, type, data);
	}

	public void rollback(World wld) {}
	public void redo(Server server) {}

}
