package me.taylorkelly.bigbrother.commands;

import java.util.List;

import me.taylorkelly.bigbrother.BBPermissions;
import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.finder.Finder;
import me.taylorkelly.util.Numbers;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HereCommand implements CommandExecutor {
    
    private BigBrother plugin;
    
    public HereCommand(BigBrother plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender send, Command arg1, String arg2, String[] split) {
        Player player = (Player) send;
        if (BBPermissions.info(player)) {
            if (split.length == 1) {
                Finder finder = new Finder(player.getLocation(), plugin.getServer().getWorlds(), plugin.worldManager, plugin);
                finder.addReciever(player);
                finder.find();
            } else if (Numbers.isNumber(split[1]) && split.length == 2) {
                Finder finder = new Finder(player.getLocation(), plugin.getServer().getWorlds(), plugin.worldManager, plugin);
                finder.setRadius(Double.parseDouble(split[1]));
                finder.addReciever(player);
                finder.find();
            } else if (split.length == 2) {
                Finder finder = new Finder(player.getLocation(), plugin.getServer().getWorlds(), plugin.worldManager, plugin);
                finder.addReciever(player);
                List<Player> targets = plugin.getServer().matchPlayer(split[1]);
                Player findee = null;
                if (targets.size() == 1) {
                    findee = targets.get(0);
                }
                finder.find((findee == null) ? split[1] : findee.getName());
            } else if (Numbers.isNumber(split[2]) && split.length == 3) {
                Finder finder = new Finder(player.getLocation(), plugin.getServer().getWorlds(), plugin.worldManager, plugin);
                finder.setRadius(Double.parseDouble(split[2]));
                finder.addReciever(player);
                List<Player> targets = plugin.getServer().matchPlayer(split[1]);
                Player findee = null;
                if (targets.size() == 1) {
                    findee = targets.get(0);
                }
                finder.find((findee == null) ? split[1] : findee.getName());
            } else {
                player.sendMessage(BigBrother.premessage + "usage is " + ChatColor.RED + "/bb here");
                player.sendMessage("or " + ChatColor.RED + "/bb here <radius>");
                player.sendMessage("or " + ChatColor.RED + "/bb here <name>");
                player.sendMessage("or " + ChatColor.RED + "/bb here <name> <radius>");
            }
        }
        return true;
    }
    
}
