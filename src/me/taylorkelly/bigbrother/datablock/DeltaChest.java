package me.taylorkelly.bigbrother.datablock;

import org.bukkit.*;

public class DeltaChest extends BBDataBlock {

	public DeltaChest(Player player, InventoryChangeEvent chestEvent, Chest chest) {
		// TODO Better World support
		super(player.getName(), DELTA_CHEST, 0, chest.getX(), chest.getY(), chest.getZ(), processDeltaChest(chestEvent));
	}

	private DeltaChest(String player, int world, int x, int y, int z, String data) {
		super(player, DELTA_CHEST, world, x, y, z, data);
	}

	private static String processDeltaChest(InventoryChangeEvent chestEvent) {
		// TODO this.
		return null;
	}

	@Override
	public void rollback(Server server) {
		// TODO this.
	}

	public static BBDataBlock getBBDataBlock(String player, int world, int x, int y, int z, String data) {
		return new DeltaChest(player, world, x, y, z, data);
	}

}
