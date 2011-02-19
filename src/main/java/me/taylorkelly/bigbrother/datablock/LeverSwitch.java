package me.taylorkelly.bigbrother.datablock;

import org.bukkit.Server;
import org.bukkit.block.Block;

public class LeverSwitch extends BBDataBlock {

	public LeverSwitch(String player, Block lever) {
		//TODO Better world support
		super(player, Action.LEVER_SWITCH, 0, lever.getX(), lever.getY(), lever.getZ(), 69, Byte.toString(lever.getData()));
	}

	public void rollback(Server server) {}
	public void redo(Server server) {}


	public static BBDataBlock getBBDataBlock(String player, int world, int x, int y, int z, int type, String data) {
		return new LeverSwitch(player, world, x, y, z, type, data);
	}

	private LeverSwitch(String player, int world, int x, int y, int z, int type, String data) {
		super(player, Action.LEVER_SWITCH, world, x, y, z, type, data);
	}

}
