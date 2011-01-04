package me.taylorkelly.bigbrother.datablock;

import org.bukkit.*;

public class PlacedBlock extends BBDataBlock {
	public PlacedBlock(Player player, Block block) {
		super(player.getName(), BLOCK_PLACED, block.getX(), block.getY(), block.getZ(), block.getTypeID() + ";" + block.getData());
	}
}
