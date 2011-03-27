package me.taylorkelly.bigbrother.commands;

import me.taylorkelly.bigbrother.BigBrother;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class VersionCommand implements CommandExecutor {
    
    public VersionCommand(BigBrother plugin) {
    }

    @Override
    public boolean onCommand(CommandSender player, Command arg1, String arg2, String[] arg3) {
        player.sendMessage("You're running: " + ChatColor.AQUA.toString() + BigBrother.name + " " + BigBrother.version);
        return true;
    }
    
}
