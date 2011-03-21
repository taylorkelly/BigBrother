package me.taylorkelly.bigbrother;

import java.util.HashMap;
import me.taylorkelly.bigbrother.datablock.BBDataBlock;
import me.taylorkelly.bigbrother.datablock.LavaFlow;
import org.bukkit.Location;
import org.bukkit.block.Block;

/**
 * This class is used to track the placement of lava so that flows can be
 * properly associated with a specific player
 */
public class LavaFlowLogger {
    public static double THRESHOLD = 3.0;
    private static HashMap<Location, String> lavaMap = new HashMap<Location, String>();

    public static LavaFlow getFlow(Block blockFrom, Block blockTo) {
        String player = BBDataBlock.ENVIRONMENT;
        Location bestLocation = null;
        double bestDistance = THRESHOLD;
        for (Location loc : lavaMap.keySet()) {
            double distance = distance(loc, blockFrom.getLocation());
            if (distance < bestDistance) {
                bestLocation = loc;
                bestDistance = distance;
                break;
            }
        }
        if (bestLocation != null) {
            player = lavaMap.get(bestLocation);
            log(blockTo.getLocation(), player);
        }
        return new LavaFlow(player, blockFrom.getWorld().getName(), blockTo.getX(), blockTo.getY(), blockTo.getZ(), blockFrom.getTypeId(), (byte) 0);
    }

    public static void log(Block sourceBlock, String player) {
        lavaMap.put(sourceBlock.getLocation(), player);
    }

    public static void log(Location location, String name) {
        lavaMap.put(location, name);
    }

    public static double distance(Location from, Location to) {
        if (!from.getWorld().getName().equals(to.getWorld().getName())) {
            return Double.MAX_VALUE;
        } else {
            return Math.sqrt(Math.pow(from.getX() - to.getX(), 2) + Math.pow(from.getY() - to.getY(), 2) + Math.pow(from.getZ() - to.getZ(), 2));
        }
    }
}
