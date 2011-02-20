package me.taylorkelly.bigbrother.datablock;

import org.bukkit.Server;
import org.bukkit.block.Block;

public class DoorOpen extends BBDataBlock {

	public DoorOpen(String player, Block door, int world) {
		super(player, Action.DOOR_OPEN, world, door.getX(), door.getY(), door.getZ(), 324, door.getData() + "");
	}


	public void rollback(Server server) {}
	public void redo(Server server) {}


	public static BBDataBlock getBBDataBlock(String player, int world, int x, int y, int z, int type, String data) {
		return new DoorOpen(player, world, x, y, z, type, data);
	}

	private DoorOpen(String player, int world, int x, int y, int z, int type, String data) {
		super(player, Action.DOOR_OPEN, world, x, y, z, type, data);
	}

}
