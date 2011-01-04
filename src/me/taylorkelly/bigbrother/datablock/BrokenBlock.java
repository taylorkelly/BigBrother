package me.taylorkelly.bigbrother.datablock;
import org.bukkit.Block;

public class BrokenBlock extends BBDataBlock {
	public BrokenBlock(String player, Block block) {
		super(player, BLOCK_BROKEN, block.getX(), block.getY(), block.getZ(), block.getTypeID() + ";" + block.getData());
	}
}
