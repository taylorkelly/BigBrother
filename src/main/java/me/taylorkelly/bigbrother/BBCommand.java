package me.taylorkelly.bigbrother;

import java.util.HashMap;
import java.util.List;

import me.taylorkelly.bigbrother.finder.Finder;
import me.taylorkelly.bigbrother.rollback.Rollback;
import me.taylorkelly.bigbrother.rollback.RollbackConfirmation;
import me.taylorkelly.bigbrother.rollback.RollbackInterpreter;
import me.taylorkelly.util.Numbers;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class BBCommand implements CommandExecutor {
    
    private BigBrother plugin;
    private HashMap<String, CommandExecutor> executors = new HashMap<String, CommandExecutor>();
    
    public BBCommand(BigBrother plugin) {
        this.plugin = plugin;
    }
    
    public void registerExecutor(String subcmd, CommandExecutor cmd) {
        executors.put(subcmd.toLowerCase(), cmd);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        String[] split = args;
        String commandName = command.getName().toLowerCase();
        String subcommandName = args[0].toLowerCase();
        
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (commandName.equals("bb")) {
                if (args.length == 0)
                    return false;
                
                if (!executors.containsKey(subcommandName))
                    return false;
                
                executors.get(subcommandName).onCommand(sender, command, commandLabel, args);
            }
        } else if (sender instanceof ConsoleCommandSender) {
            if (commandName.equals("bb")) {
                ConsoleCommandSender console = (ConsoleCommandSender) sender;
                if (split.length == 0) {
                    return false;
                } else if (split[0].equalsIgnoreCase("version")) {
                    console.sendMessage("You're running: " + ChatColor.AQUA.toString() + plugin.name + " " + plugin.version);
                } else if (split[0].equalsIgnoreCase("update")) {
                    Updatr.updateAvailable(console);
                }
                return true;
            }
            return false;
        }
        return false;
    }
    
}
