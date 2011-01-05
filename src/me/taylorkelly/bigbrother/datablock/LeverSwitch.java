package me.taylorkelly.bigbrother.datablock;

import org.bukkit.Block;
import org.bukkit.Server;

public class LeverSwitch extends BBDataBlock {

	public LeverSwitch(String player, Block lever) {
		//TODO Better world support
		super(player, LEVER_SWITCH, 0, lever.getX(), lever.getY(), lever.getZ(), lever.getData() + "");
	}

	public void rollback(Server server) {}
	public void redo(Server server) {}

	
	public static BBDataBlock getBBDataBlock(String player, int world, int x, int y, int z, String data) {
		return new LeverSwitch(player, world, x, y, z, data);
	}

	private LeverSwitch(String player, int world, int x, int y, int z, String data) {
		super(player, LEVER_SWITCH, world, x, y, z, data);
	}

}
