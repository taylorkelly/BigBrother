package me.taylorkelly.bigbrother.finder;

import java.util.ArrayList;
import org.bukkit.block.Block;

public abstract class StickMode {
    
    public abstract ArrayList<String> getInfoOnBlock(Block block);
    
    public abstract String getDescription();

}
