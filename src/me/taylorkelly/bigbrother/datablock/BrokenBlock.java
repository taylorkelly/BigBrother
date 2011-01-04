package me.taylorkelly.bigbrother.datablock;
import org.bukkit.*;

public class BrokenBlock extends BBDataBlock {
	public BrokenBlock(Player player, Block block) {
		super(player.getName(), BLOCK_BROKEN, block.getX(), block.getY(), block.getZ(), block.getTypeID() + ";" + block.getData());
		//TODO BBReporter.torchCheck(player, block);
		//TODO sign check
		//TODO chest check
	}
	
	public void send() {
		super.send();
	}
}
