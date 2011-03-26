package me.taylorkelly.bigbrother.commands;

import me.taylorkelly.bigbrother.BBPermissions;
import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.util.Numbers;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StickCommand implements CommandExecutor {
    
    private BigBrother plugin;
    
    public StickCommand(BigBrother plugin) {
        this.plugin=plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] split) {
        Player player=(Player) arg0;
        if(BBPermissions.info(player)) {
            if (split.length == 1) {
                plugin.sticker.setMode(player, 1);
                plugin.reportStickMode(player, 1);
            } else if (split.length == 2 && Numbers.isInteger(split[1])) {
                plugin.sticker.setMode(player, Integer.parseInt(split[1]));
                plugin.reportStickMode(player, Integer.parseInt(split[1]));
            } else {
                player.sendMessage(BigBrother.premessage + "Usage is " + ChatColor.RED + "/bb stick [#]");
            }
        }
        return true;
    }
    
}
