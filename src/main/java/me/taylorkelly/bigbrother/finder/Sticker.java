package me.taylorkelly.bigbrother.finder;

import java.util.ArrayList;
import java.util.HashMap;

import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.WorldManager;

import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Used to activate and control different SuperSticks
 * @author taylor
 */
public class Sticker {
    //private Server server;

    private HashMap<String, StickMode> playerModes;
    private ArrayList<Class<? extends StickMode>> modes;
    private WorldManager manager;

    /**
     * Creates the Stick Controller
     * @param server The server
     * @param manager The world manager (for passing to sticks)
     */
    public Sticker(Server server, WorldManager manager) {
        this.manager = manager;
        //this.server = server;
        playerModes = new HashMap<String, StickMode>();
        modes = new ArrayList<Class<? extends StickMode>>();

        // Add any new SuperSticks here
        modes.add(HistoryStick.class); // SS 1
        modes.add(HistoryLog.class); // SS 2
    }

    /**
     * Changes the SuperStick of the player. Handles out-of-range indexes and
     * 0-case (to turn off the SuperStick)
     * @param player The player to change their SuperStick mode
     * @param i The index of the SuperStick to change to
     */
    public void setMode(Player player, int i) {
        if (i == 0 && playerModes.containsKey(player.getName())) {
            player.sendMessage(BigBrother.premessage + "Turning off SuperStick");
            StickMode mode = playerModes.remove(player.getName());
            mode.disable(player);
            return;
        }
        i--;
        if (i < 0 || i >= modes.size()) {
            player.sendMessage(BigBrother.premessage + (i + 1) + " is out of SuperStick range. Setting to 1");
            i = 0;
        }
        try {
            if (playerModes.containsKey(player.getName())) {
                playerModes.get(player.getName()).disable(player);
            }
            playerModes.put(player.getName(), modes.get(i).newInstance());
            playerModes.get(player.getName()).initialize(player);
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        }
    }

    /**
     * Returns the description of the Stick that the player is holding
     * @param player The player to get their stick info
     * @return the description, or null if the player has no stick
     */
    public String descMode(Player player) {
        if (playerModes.containsKey(player.getName())) {
            return playerModes.get(player.getName()).getDescription();
        } else {
            return null;
        }
    }

    /**
     * Determines if a player is using their stick based on the item stack
     * they're associated with. This association is typically what item stack
     * they have in their hand
     * @param player The player to check
     * @param itemStack The item stack they're interacting with.
     * @return true if they're using their stick. false if not
     */
    public boolean hasStick(Player player, ItemStack itemStack) {
        if (playerModes.containsKey(player.getName())) {
            return playerModes.get(player.getName()).usesStick(itemStack);
        }
        return false;
    }

    /**
     * Sends info on a block to a specific player based on their stick
     * @param player The player to send info to and to use their stick
     * @param block The block to get info about
     * @param leftclick 
     */
    private void blockInfo(Player player, Block block, boolean leftclick) {
        if (playerModes.containsKey(player.getName())) {
            StickMode mode = playerModes.get(player.getName());
            ArrayList<String> info = mode.getInfoOnBlock(block, manager, leftclick);
            for (String msg : info) {
                player.sendMessage(msg);
            }
        }
    }

    /**
     * Occurs when a player uses their stick. Gets info and applies updates
     * @param player The player to have their stick used.... >.>
     * @param block The block that the stick is interacting with
     */
    public void stick(Player player, Block block, boolean leftclick) {
        blockInfo(player, block, leftclick);
        if (playerModes.containsKey(player.getName())) {
            StickMode mode = playerModes.get(player.getName());
            mode.update(player);
        }
    }

    /**
     * Returns if the player is holding a stick that uses right clicks
     * @param player The player to get info about
     * @return Whether they are holding a right click stick
     */
    public boolean rightClickStick(Player player) {
        if (playerModes.containsKey(player.getName())) {
            StickMode mode = playerModes.get(player.getName());
            return mode.rightClickStick();
        }
        return false;
    }

    public boolean leftClickStick(Player player) {
        if (playerModes.containsKey(player.getName())) {
            StickMode mode = playerModes.get(player.getName());
            return mode.leftClickStick();
        }
        return false;
    }
}
