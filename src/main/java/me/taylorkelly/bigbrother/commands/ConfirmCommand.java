package me.taylorkelly.bigbrother.commands;

import me.taylorkelly.bigbrother.BBPermissions;
import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.rollback.RollbackConfirmation;
import me.taylorkelly.bigbrother.rollback.RollbackInterpreter;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ConfirmCommand implements CommandExecutor {
    
    public ConfirmCommand(BigBrother plugin) {
    }
    
    @Override
    public boolean onCommand(CommandSender player, Command arg1, String arg2, String[] split) {
        if (BBPermissions.rollback((Player) player)) {
            if (split.length == 1) {
                if (RollbackConfirmation.hasRI((Player) player)) {
                    RollbackInterpreter interpret = RollbackConfirmation.getRI((Player) player);
                    interpret.send();
                } else {
                    player.sendMessage(BigBrother.premessage + "You have no rollback to confirm.");
                }
            } else {
                player.sendMessage(BigBrother.premessage + "usage is " + ChatColor.RED + "/bb confirm");
            }
            
        }
        return true;
    }
    
}
