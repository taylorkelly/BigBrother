
package me.taylorkelly.bigbrother;

import me.taylorkelly.bigbrother.datablock.LavaFlow;
import org.bukkit.block.Block;

/**
 * This class is used to track the placement of lava so that flows can be
 * properly associated with a specific player
 */
public class LavaFlowLogger {

    //TODO: Make it actually work...
    public static LavaFlow getFlow(Block blockFrom, Block blockTo) {
        return new LavaFlow("Environment", blockFrom.getWorld().getName(), blockTo.getX(), blockTo.getY(), blockTo.getZ(), blockFrom.getTypeId(), (byte)0);
    }

    public void logLava(Block sourceBlock) {

    }

}
