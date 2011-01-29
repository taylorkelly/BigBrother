package me.taylorkelly.bigbrother.finder;

import java.util.ArrayList;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public abstract class StickMode {
    
    public abstract ArrayList<String> getInfoOnBlock(Block block);
    
    public abstract String getDescription();
    
    public abstract void initialize(Player player);

    public abstract void disable(Player player);

    public abstract boolean usesStick();

    public abstract void update(Player player);

}
