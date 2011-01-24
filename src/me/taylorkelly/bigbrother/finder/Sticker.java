package me.taylorkelly.bigbrother.finder;

import java.util.ArrayList;
import java.util.HashMap;

import me.taylorkelly.bigbrother.BigBrother;

import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Sticker {
    private Server server;
    private HashMap<String, StickMode> playerModes;
    private ArrayList<Class<? extends StickMode>> modes;
    
    public Sticker(Server server) {
        this.server = server;
        playerModes = new HashMap<String, StickMode>();
        modes = new ArrayList<Class<? extends StickMode>>();
        modes.add(HistoryStick.class);
    }

    public void setMode(Player player, int i) {
        if(i == 0) {
            player.sendMessage(BigBrother.premessage + "Turning off SuperStick");
            playerModes.remove(player.getName());
            return;
        }
        i--;
        if(i < 0 || i >= modes.size()) {
            player.sendMessage(BigBrother.premessage + (i+1) + " is out of SuperStick range. Setting to 1");
            i = 0;
        }
        try {
            playerModes.put(player.getName(), modes.get(i).newInstance());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public String descMode(Player player) {
        if(playerModes.containsKey(player.getName())) {
            return playerModes.get(player.getName()).getDescription();
        } else {
            return null;
        }
    }

    public boolean hasStick(Player player) {
        return playerModes.containsKey(player.getName());
    }

    public void blockInfo(Player player, Block block) {
        if(playerModes.containsKey(player.getName())) {
            StickMode mode = playerModes.get(player.getName());
            ArrayList<String> info = mode.getInfoOnBlock(block);
            for(String msg : info) {
                player.sendMessage(msg);
            }
            
        }
    }

}
