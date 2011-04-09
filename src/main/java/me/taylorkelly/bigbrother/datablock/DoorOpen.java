package me.taylorkelly.bigbrother.datablock;

import me.taylorkelly.bigbrother.BBPlayerInfo;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;

public class DoorOpen extends BBDataBlock {

    public DoorOpen(String player, Block door, String world) {
        super(player, Action.DOOR_OPEN, world, door.getX(), door.getY(), door.getZ(), 324, door.getData() + "");
    }

    public void rollback(World wld) {
    }

    public void redo(Server server) {
    }

    public static BBDataBlock getBBDataBlock(BBPlayerInfo pi, String world, int x, int y, int z, int type, String data) {
        return new DoorOpen(pi, world, x, y, z, type, data);
    }

    private DoorOpen(BBPlayerInfo player, String world, int x, int y, int z, int type, String data) {
        super(player, Action.DOOR_OPEN, world, x, y, z, type, data);
    }
}
