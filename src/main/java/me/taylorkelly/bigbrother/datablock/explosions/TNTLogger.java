package me.taylorkelly.bigbrother.datablock.explosions;

import java.util.HashMap;
import java.util.List;
import me.taylorkelly.bigbrother.datablock.BBDataBlock;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class TNTLogger {

    private static HashMap<Location, String> tntMap = new HashMap<Location, String>();

    public static void log(String player, Block block) {
        tntMap.put(block.getLocation(), player);
    }

    public static void createTNTDataBlock(List<Block> blockList, Location location) {
        System.out.println("Searching for... " + location.toString());
        String player = BBDataBlock.ENVIRONMENT;
        if(tntMap.containsKey(location)) {
            System.out.println("found from" + player);
            player = tntMap.get(location);
        } else {
            System.out.println("not found");
        }
        for (Block block : blockList) {
            BBDataBlock dataBlock = new TNTExplosion(player, block, location.getWorld().getName());
            dataBlock.send();
            if (block.getType() == Material.TNT) {
                TNTLogger.log(player, block);
            }
        }
    }
}
