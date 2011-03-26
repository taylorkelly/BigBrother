package me.taylorkelly.bigbrother.commands;

import me.taylorkelly.bigbrother.BBPermissions;
import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.rollback.Rollback;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UndoCommand implements CommandExecutor {
    
    private BigBrother plugin;
    
    public UndoCommand(BigBrother plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender player, Command arg1, String arg2, String[] split) {
        if (BBPermissions.rollback((Player) player)) {
            if (split.length == 1) {
                if (Rollback.canUndo()) {
                    int size = Rollback.undoSize();
                    player.sendMessage(BigBrother.premessage + "Undo-ing last rollback of " + size + " blocks");
                    Rollback.undo(plugin.getServer(), (Player) player);
                    player.sendMessage(BigBrother.premessage + "Undo successful");
                } else {
                    player.sendMessage(BigBrother.premessage + "No rollback to undo");
                }
            } else {
                player.sendMessage(BigBrother.premessage + "Usage is " + ChatColor.RED + "/bb undo");
            }
        }
        return true;
    }
    
}
