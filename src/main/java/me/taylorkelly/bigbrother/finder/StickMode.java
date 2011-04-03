package me.taylorkelly.bigbrother.finder;

import java.util.ArrayList;
import me.taylorkelly.bigbrother.WorldManager;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
/**
 * The super class for any types of SuperSticks.
 */
public abstract class StickMode {

    /**
     * Returns a List of text describing the info on (or around) a particular
     * block
     * @param block The block to get info on
     * @param manager The WorldManager (to get index)
     * @return The ArrayList of descriptions
     */
    public abstract ArrayList<String> getInfoOnBlock(Block block, WorldManager manager, boolean leftclick);

    /**
     * Returns the description of the stick... (polymorphism!)
     * @return Description
     */
    public abstract String getDescription();

    /**
     * What happens when the stick should be initialized on a particular player
     * @param player The player to be initialized on
     */
    public abstract void initialize(Player player);

    /**
     * What happens when the stick should be disabled on a particular player
     * @param player The player to be disabled on
     */
    public abstract void disable(Player player);

    /**
     * Returns whether the stick is being used based on the ItemStack it's
     * being used with
     * @param itemStack The itemStack that the stick is being used with
     */
    public abstract boolean usesStick(ItemStack itemStack);

    /**
     * What should happen after the player uses the stick.
     * Useful for giving back items or passing information
     * @param player The player to update
     */
    public abstract void update(Player player);

    /**
     * If this stick is based on right-clicking
     * @return true if so, false if not
     */
    public abstract boolean rightClickStick();

    /**
     * Do we allow left-clicking?
     * @return true if yes.
     */
    public abstract boolean leftClickStick();
}
