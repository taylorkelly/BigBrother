package me.taylorkelly.bigbrother.datablock;

import org.bukkit.Block;
import org.bukkit.Server;

public class DoorOpen extends BBDataBlock {

	public DoorOpen(String player, Block door) {
		//TODO Better world support
		super(player, DOOR_OPEN, 0, door.getX(), door.getY(), door.getZ(), door.getData() + "");
	}

	
	public void rollback(Server server) {}
	
	public static BBDataBlock getBBDataBlock(String player, int world, int x, int y, int z, String data) {
		return new DoorOpen(player, world, x, y, z, data);
	}

	private DoorOpen(String player, int world, int x, int y, int z, String data) {
		super(player, DOOR_OPEN, world, x, y, z, data);
	}

}
