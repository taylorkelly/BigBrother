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

public class RollbackCommand implements CommandExecutor {
    
    private BigBrother plugin;
    
    public RollbackCommand(BigBrother plugin) {
        this.plugin=plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender player, Command arg1, String arg2, String[] split) {
        if(BBPermissions.rollback((Player) player)) {
            if (split.length > 1) {
                RollbackInterpreter interpreter = new RollbackInterpreter((Player) player, split, plugin.getServer(), plugin.worldManager, plugin);
                Boolean passed = interpreter.interpret();
                if (passed != null) {
                    if (passed) {
                        interpreter.send();
                    } else {
                        player.sendMessage(BigBrother.premessage + ChatColor.RED + "Warning: " + ChatColor.WHITE + "You are rolling back without a time or radius argument.");
                        player.sendMessage("Use " + ChatColor.RED + "/bb confirm" + ChatColor.WHITE + " to confirm the rollback.");
                        player.sendMessage("Use " + ChatColor.RED + "/bb delete" + ChatColor.WHITE + " to delete it.");
                        RollbackConfirmation.setRI((Player) player, interpreter);
                    }
                }
            } else {
                player.sendMessage(BigBrother.premessage + "Usage is: " + ChatColor.RED + "/bb rollback name1 [name2] [options]");
                player.sendMessage(BigBrother.premessage + "Please read the full command documentation at https://github.com/tkelly910/BigBrother/wiki/Commands");
            }
        }
        return true;
    }
    
}
