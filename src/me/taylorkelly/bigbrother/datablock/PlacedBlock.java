package me.taylorkelly.bigbrother.datablock;

import org.bukkit.*;

public class PlacedBlock extends BBDataBlock {
	public PlacedBlock(Player player, Block block) {
		//TODO Better World support
		super(player.getName(), BLOCK_PLACED, 0, block.getX(), block.getY(), block.getZ(), block.getTypeID() + ";" + block.getData());
	}
}
