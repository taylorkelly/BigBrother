package me.taylorkelly.bigbrother.commands;

import me.taylorkelly.bigbrother.BigBrother;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpCommand implements CommandExecutor {
    
    public HelpCommand(BigBrother plugin) {
    }
    
    @Override
    public boolean onCommand(CommandSender player, Command arg1, String arg2, String[] arg3) {
        // TODO: Modular help system
        player.sendMessage(BigBrother.premessage + "BigBrother version " + BigBrother.version + " help");
        player.sendMessage(BigBrother.premessage + " " + ChatColor.RED + "/bb watch (name)" + ChatColor.WHITE + " - Toggles the whether BB is watching (name)");
        player.sendMessage(BigBrother.premessage + " " + ChatColor.RED + "/bb watched" + ChatColor.WHITE + " - Shows which users are being watched by BB");
        player.sendMessage(BigBrother.premessage + " " + ChatColor.RED + "/bb unwatched" + ChatColor.WHITE + " - Shows which users that are logged in are unwatched");
        player.sendMessage(BigBrother.premessage + " " + ChatColor.RED + "/bb stick (0|1|2)" + ChatColor.WHITE + " - Gives you a stick (1), a log you can place (2), or disables either (0).");
        player.sendMessage(BigBrother.premessage + " " + ChatColor.RED + "/bb here" + ChatColor.WHITE + " - See changes that took place in the area you are standing in.");
        player.sendMessage(BigBrother.premessage + " " + ChatColor.RED + "/bb find x y z" + ChatColor.WHITE + " - Get the history of an area at a specific coordinate");
        player.sendMessage(BigBrother.premessage + " " + ChatColor.RED + "/bb rollback (name1) [name2] [options]" + ChatColor.WHITE + " - A command you should study in length via our helpful online wiki.");
        player.sendMessage(BigBrother.premessage + " " + ChatColor.RED + "/bb undo" + ChatColor.WHITE + " - Great for fixing bad rollbacks. It's like it never happened!");
        player.sendMessage(BigBrother.premessage + " " + ChatColor.RED + "/bb confirm" + ChatColor.WHITE + " - Confirms your rollback (if applicable).");
        player.sendMessage(BigBrother.premessage + " " + ChatColor.RED + "/bb delete" + ChatColor.WHITE + " - Delete your rollback (if applicable).");
        return false;
    }
    
}
