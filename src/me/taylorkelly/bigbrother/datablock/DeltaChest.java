package me.taylorkelly.bigbrother.datablock;

import org.bukkit.*;

public class DeltaChest extends BBDataBlock{

	public DeltaChest(Player player, InventoryChangeEvent chestEvent, Chest chest) {
		//TODO Better World support
		super(player.getName(), DELTA_CHEST, 0, chest.getX(), chest.getY(), chest.getZ(), processDeltaChest(chestEvent));
	}
	
	private static String processDeltaChest(InventoryChangeEvent chestEvent) {
		//TODO this.
		return null;
	}

}
