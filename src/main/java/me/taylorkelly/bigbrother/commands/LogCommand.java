package me.taylorkelly.bigbrother.commands;

import me.taylorkelly.bigbrother.BBPermissions;
import me.taylorkelly.bigbrother.BigBrother;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LogCommand implements CommandExecutor {
    
    private BigBrother plugin;
    
    public LogCommand(BigBrother plugin) {
        this.plugin=plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender send, Command arg1, String arg2, String[] split) {
        Player player=(Player) send;
        if(BBPermissions.info(player)) {
            if (split.length == 1) {
                plugin.sticker.setMode(player, 2);
                plugin.reportStickMode(player, 2);
            } else {
                player.sendMessage(BigBrother.premessage + "Usage is " + ChatColor.RED + "/bb log");
            }
        }
        return true;
    }
    
}
