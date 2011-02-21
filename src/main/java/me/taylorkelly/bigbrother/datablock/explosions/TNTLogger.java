package me.taylorkelly.bigbrother.datablock.explosions;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class TNTLogger {

    public static void log(Player player, Block block) {
        // TODO Auto-generated method stub
    }

    public static void createTNTDataBlock(Location location, List<Block> blockList) {
        // TODO Auto-generated method stub
    }

    public static void createTNTDataBlock(List<Block> blockList, String world) {
        TNTExplosion.create(blockList, world);
    }

}
