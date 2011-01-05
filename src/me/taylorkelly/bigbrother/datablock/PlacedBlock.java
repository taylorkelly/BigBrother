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
	
	public void redo(Server server) {
		String[] datas = data.split(";");
		int type = Integer.parseInt(datas[0]);
		byte data = Byte.parseByte(datas[1]);
		server.getWorlds()[world].getBlockAt(x, y, z).setTypeID(type);
		server.getWorlds()[world].getBlockAt(x, y, z).setData(data);
	}
	
	public static BBDataBlock getBBDataBlock(String player, int world, int x, int y, int z, String data) {
		return new PlacedBlock(player, world, x, y, z, data);
	}

}
