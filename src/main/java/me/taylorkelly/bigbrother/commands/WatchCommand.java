package me.taylorkelly.bigbrother.commands;

import java.util.List;

import me.taylorkelly.bigbrother.BBPermissions;
import me.taylorkelly.bigbrother.BigBrother;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WatchCommand implements CommandExecutor {
    private BigBrother plugin;
    public WatchCommand(BigBrother bigBrother) {
        plugin=bigBrother;
    }

    @Override
    public boolean onCommand(CommandSender player, Command arg1, String arg2, String[] split) {
        if(BBPermissions.watch((Player) player)) {
            if (split.length == 2) {
                List<Player> targets = plugin.getServer().matchPlayer(split[1]);
                Player watchee = null;
                if (targets.size() == 1) {
                    watchee = targets.get(0);
                }
                String playerName = (watchee == null) ? split[1] : watchee.getName();

                if (plugin.toggleWatch(playerName)) {
                    String status = (watchee == null) ? " (offline)" : " (online)";
                    player.sendMessage(BigBrother.premessage + "Now watching " + playerName + status);
                } else {
                    String status = (watchee == null) ? " (offline)" : " (online)";
                    player.sendMessage(BigBrother.premessage + "No longer watching " + playerName + status);
                }
            } else {
                player.sendMessage(BigBrother.premessage + "usage is " + ChatColor.RED + "/bb watch <player>");
            }
        }
        return true;
    }
    
}
