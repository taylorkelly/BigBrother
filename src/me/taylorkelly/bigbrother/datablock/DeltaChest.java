package me.taylorkelly.bigbrother.datablock;

import org.bukkit.*;

public class DeltaChest extends BBDataBlock{

	public DeltaChest(Player player, InventoryChangeEvent chestEvent, Chest chest) {
		super(player.getName(), DELTA_CHEST, chest.getX(), chest.getY(), chest.getZ(), processDeltaChest(chestEvent));
	}
	
	private static String processDeltaChest(InventoryChangeEvent chestEvent) {
		//TODO this.
		return null;
	}

}
