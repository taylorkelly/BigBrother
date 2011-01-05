package me.taylorkelly.bigbrother.datablock;
import org.bukkit.*;

public class BrokenBlock extends BBDataBlock {
	public BrokenBlock(Player player, Block block) {
		//TODO Better World support
		super(player.getName(), BLOCK_BROKEN, 0, block.getX(), block.getY(), block.getZ(), block.getTypeID(), block.getData() + "");
		//TODO BBReporter.torchCheck(player, block);
		//TODO sign check
		//TODO chest check
	}
	
	public void send() {
		//TODO send bystanders
		super.send();
	}

	public void rollback(Server server) {
		// TODO Chunk loading stuffs
		// if (!world.isChunkLoaded(world.getChunkAt(destination.getBlockX(), destination.getBlockZ())))
		// 		world.loadChunk(world.getChunkAt(destination.getBlockX(), destination.getBlockZ()));

		byte blockData = Byte.parseByte(data);
		server.getWorlds()[world].getBlockAt(x, y, z).setTypeID(type);
		server.getWorlds()[world].getBlockAt(x, y, z).setData(blockData);
	}
	
	public void redo(Server server) {
		// TODO Chunk loading stuffs
		// if (!world.isChunkLoaded(world.getChunkAt(destination.getBlockX(), destination.getBlockZ())))
		// 		world.loadChunk(world.getChunkAt(destination.getBlockX(), destination.getBlockZ()));

		server.getWorlds()[world].getBlockAt(x, y, z).setTypeID(0);
	}
	
	public static BBDataBlock getBBDataBlock(String player, int world, int x, int y, int z, int type, String data) {
		return new BrokenBlock(player, world, x, y, z, type, data);
	}
	
	private BrokenBlock(String player, int world, int x, int y, int z, int type, String data) {
		super(player, BLOCK_BROKEN, world, x, y, z, type, data);
	}

}
