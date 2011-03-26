package me.taylorkelly.bigbrother.commands;

import me.taylorkelly.bigbrother.BBPermissions;
import me.taylorkelly.bigbrother.BigBrother;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WatchedCommand implements CommandExecutor {
    private BigBrother plugin;
    public WatchedCommand(BigBrother bigBrother) {
        plugin=bigBrother;
    }

    @Override
    public boolean onCommand(CommandSender player, Command arg1, String arg2, String[] split) {
        if(BBPermissions.info((Player) player)) {
            String watchedPlayers = plugin.getWatchedPlayers();
            if (watchedPlayers.equals("")) {
                player.sendMessage(BigBrother.premessage + "Not watching anyone.");
            } else {
                player.sendMessage(BigBrother.premessage + "Now watching:");
                player.sendMessage(watchedPlayers);
            }
        }
        return true;
    }
    
}
