package me.taylorkelly.bigbrother.datablock;
import org.bukkit.*;

public class BrokenBlock extends BBDataBlock {
	public BrokenBlock(Player player, Block block) {
		//TODO Better World support
		super(player.getName(), BLOCK_BROKEN, 0, block.getX(), block.getY(), block.getZ(), block.getTypeID() + ";" + block.getData());
		//TODO BBReporter.torchCheck(player, block);
		//TODO sign check
		//TODO chest check
	}
	
	private BrokenBlock(String player, int world, int x, int y, int z, String data) {
		super(player, BLOCK_BROKEN, world, x, y, z, data);
	}
	
	public void send() {
		//TODO send bystanders
		super.send();
	}

	public void rollback(Server server) {
		String[] datas = data.split(";");
		int type = Integer.parseInt(datas[0]);
		byte data = Byte.parseByte(datas[1]);
		server.getWorlds()[world].getBlockAt(x, y, z).setTypeID(type);
		server.getWorlds()[world].getBlockAt(x, y, z).setData(data);
	}
	
	public void redo(Server server) {
		server.getWorlds()[world].getBlockAt(x, y, z).setTypeID(0);
	}
	
	public static BBDataBlock getBBDataBlock(String player, int world, int x, int y, int z, String data) {
		return new BrokenBlock(player, world, x, y, z, data);
	}

}
