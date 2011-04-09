package me.taylorkelly.bigbrother.datablock;

import me.taylorkelly.bigbrother.BBPlayerInfo;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;

public class ChestOpen extends BBDataBlock {

    public ChestOpen(String player, Block block, String world) {
        super(player, Action.OPEN_CHEST, world, block.getX(), block.getY(), block.getZ(), 54, "");
    }

    private ChestOpen(BBPlayerInfo player, String world, int x, int y, int z, int type, String data) {
        super(player, Action.OPEN_CHEST, world, x, y, z, type, data);
    }

    public void rollback(World wld) {
        
    }

    public void redo(Server server) {
    }

    public static BBDataBlock getBBDataBlock(BBPlayerInfo pi, String world, int x, int y, int z, int type, String data) {
        return new ChestOpen(pi, world, x, y, z, type, data);
    }

}
