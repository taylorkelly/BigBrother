package me.taylorkelly.bigbrother.commands;

import me.taylorkelly.bigbrother.BBPermissions;
import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.Cleanser;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CleanseCommand implements CommandExecutor {
    
    public CleanseCommand(BigBrother plugin) {
    }
    
    @Override
    public boolean onCommand(CommandSender player, Command arg1, String arg2, String[] split) {
        if(BBPermissions.cleanse((Player) player)) {
            if (Cleanser.needsCleaning()) {
                Cleanser.clean((Player) player);
            } else {
                player.sendMessage(ChatColor.RED + "No need to cleanse. Check your settings.");
            }
        }
        return true;
    }
    
}
