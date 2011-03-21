package me.taylorkelly.bigbrother.datablock;

import org.bukkit.Server;
import org.bukkit.entity.Item;

public class PickupItem extends BBDataBlock {

    public PickupItem(String player, Item item, String world) {
        super(player, Action.PICKUP_ITEM, world, item.getLocation().getBlockX(), item.getLocation().getBlockY(), item.getLocation().getBlockZ(), item.getItemStack().getTypeId(), item.getItemStack().getAmount() + ";" + item.getItemStack().getData().getData() + ";" + item.getItemStack().getDurability());
    }

    public void rollback(Server server) {
    }

    public void redo(Server server) {
    }

    public static BBDataBlock getBBDataBlock(String player, String world, int x, int y, int z, int type, String data) {
        return new PickupItem(player, world, x, y, z, type, data);
    }

    private PickupItem(String player, String world, int x, int y, int z, int type, String data) {
        super(player, Action.PICKUP_ITEM, world, x, y, z, type, data);
    }
}
