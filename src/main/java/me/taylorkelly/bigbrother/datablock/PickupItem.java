package me.taylorkelly.bigbrother.datablock;

import me.taylorkelly.bigbrother.BBPlayerInfo;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Item;

public class PickupItem extends BBDataBlock {

    public PickupItem(String player, Item item, String world) {
        super(player, Action.PICKUP_ITEM, world, item.getLocation().getBlockX(), item.getLocation().getBlockY(), item.getLocation().getBlockZ(), item.getItemStack().getTypeId(), item.getItemStack().getAmount() + ";" + item.getItemStack().getData().getData() + ";" + item.getItemStack().getDurability());
    }

    public void rollback(World wld) {
    }

    public void redo(Server server) {
    }

    public static BBDataBlock getBBDataBlock(BBPlayerInfo pi, String world, int x, int y, int z, int type, String data) {
        return new PickupItem(pi, world, x, y, z, type, data);
    }

    private PickupItem(BBPlayerInfo player, String world, int x, int y, int z, int type, String data) {
        super(player, Action.PICKUP_ITEM, world, x, y, z, type, data);
    }
}
