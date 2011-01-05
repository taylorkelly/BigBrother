package me.taylorkelly.bigbrother.datablock;

import org.bukkit.*;

public class PlacedBlock extends BBDataBlock {
	public PlacedBlock(Player player, Block block) {
		//TODO Better World support
		super(player.getName(), BLOCK_PLACED, 0, block.getX(), block.getY(), block.getZ(), block.getTypeID() + ";" + block.getData());
	}
	
	private PlacedBlock(String player, int world, int x, int y, int z, String data) {
		super(player, BLOCK_PLACED, world, x, y, z, data);
	}

	public void rollback(Server server) {
		server.getWorlds()[world].getBlockAt(x, y, z).setTypeID(0);
	}
	
	public static BBDataBlock getBBDataBlock(String player, int world, int x, int y, int z, String data) {
		return new PlacedBlock(player, world, x, y, z, data);
	}

}
