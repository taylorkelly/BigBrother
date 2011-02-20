package me.taylorkelly.bigbrother.finder;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class StickMode {

    public abstract ArrayList<String> getInfoOnBlock(Block block, List<World> worlds);

    public abstract String getDescription();

    public abstract void initialize(Player player);

    public abstract void disable(Player player);

    public abstract boolean usesStick(ItemStack itemStack);

    public abstract void update(Player player);

    public abstract boolean rightClickStick();

}
