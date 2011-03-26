package me.taylorkelly.bigbrother.commands;

import me.taylorkelly.bigbrother.BBPermissions;
import me.taylorkelly.bigbrother.BigBrother;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnwatchedCommand implements CommandExecutor {
    
    private BigBrother plugin;
    
    public UnwatchedCommand(BigBrother plugin) {
        this.plugin=plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender player, Command arg1, String arg2, String[] split) {
        if(BBPermissions.info((Player) player)) {
            String unwatchedPlayers = plugin.getUnwatchedPlayers();
            if (unwatchedPlayers.equals("")) {
                player.sendMessage(BigBrother.premessage + "Everyone on is being watched.");
            } else {
                player.sendMessage(BigBrother.premessage + "Currently not watching:");
                player.sendMessage(unwatchedPlayers);
            }
        }
        return true;
    }
    
}
