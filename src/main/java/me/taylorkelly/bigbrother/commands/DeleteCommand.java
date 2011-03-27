package me.taylorkelly.bigbrother.commands;

import me.taylorkelly.bigbrother.BBPermissions;
import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.rollback.RollbackConfirmation;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeleteCommand implements CommandExecutor {
    
    public DeleteCommand(BigBrother plugin) {
    }
    
    @Override
    public boolean onCommand(CommandSender player, Command arg1, String arg2, String[] split) {
        if(BBPermissions.rollback((Player) player)) {
            if (split.length == 1) {
                if (RollbackConfirmation.hasRI((Player) player)) {
                    RollbackConfirmation.deleteRI((Player) player);
                    player.sendMessage(BigBrother.premessage + "You have deleted your rollback.");
                } else {
                    player.sendMessage(BigBrother.premessage + "You have no rollback to delete.");
                }
            } else {
                player.sendMessage(BigBrother.premessage + "usage is " + ChatColor.RED + "/bb delete");
            }
            // Undo rollback.
        }
        return true;
    }
    
}
