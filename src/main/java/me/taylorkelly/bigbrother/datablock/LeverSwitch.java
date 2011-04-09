package me.taylorkelly.bigbrother.datablock;

import me.taylorkelly.bigbrother.BBPlayerInfo;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;

public class LeverSwitch extends BBDataBlock {

	public LeverSwitch(String player, Block lever, String world) {
		super(player, Action.LEVER_SWITCH, world, lever.getX(), lever.getY(), lever.getZ(), 69, Byte.toString(lever.getData()));
	}

	public void rollback(World wld) {}
	public void redo(Server server) {}


	public static BBDataBlock getBBDataBlock(BBPlayerInfo pi, String world, int x, int y, int z, int type, String data) {
		return new LeverSwitch(pi, world, x, y, z, type, data);
	}

	private LeverSwitch(BBPlayerInfo player, String world, int x, int y, int z, int type, String data) {
		super(player, Action.LEVER_SWITCH, world, x, y, z, type, data);
	}

}
