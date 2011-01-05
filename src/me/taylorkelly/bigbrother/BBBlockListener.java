package me.taylorkelly.bigbrother;

import me.taylorkelly.bigbrother.datablock.*;
import org.bukkit.event.block.*;
import org.bukkit.*;

public class BBBlockListener extends BlockListener {
	private BigBrother plugin;
	
	public BBBlockListener(BigBrother plugin) {
		this.plugin = plugin;
	}
	
	public void onBlockBroken(BlockBrokenEvent event) {
		Player player = event.getPlayer();
		if (BBSettings.blockBreak && plugin.watching(player)) {
			Block block = event.getBlock();
			BrokenBlock dataBlock = new BrokenBlock(player, block);
			dataBlock.send();
		}
	}

	public void onBlockPlaced(BlockPlacedEvent event) {
		Player player = event.getPlayer();
		if (BBSettings.blockPlace && plugin.watching(player)) {
			Block block = event.getBlock();
			PlacedBlock dataBlock = new PlacedBlock(player, block);
			dataBlock.send();
		}
	}
}
