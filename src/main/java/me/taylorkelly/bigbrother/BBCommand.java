package me.taylorkelly.bigbrother;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class BBCommand implements CommandExecutor {
    
    private HashMap<String, CommandExecutor> executors = new HashMap<String, CommandExecutor>();
    
    public BBCommand(BigBrother plugin) {
    }
    
    public void registerExecutor(String subcmd, CommandExecutor cmd) {
        executors.put(subcmd.toLowerCase(), cmd);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        String commandName = command.getName().toLowerCase();
        
        if (sender instanceof Player) {
            if (commandName.equals("bb")) {
                if (args.length == 0)
                    return false;
                
                String subcommandName = args[0].toLowerCase();
                
                if (!executors.containsKey(subcommandName))
                    return false;
                
                return executors.get(subcommandName).onCommand(sender, command, commandLabel, args);
            }
        } else if (sender instanceof ConsoleCommandSender) {
            if (commandName.equals("bb")) {
                ConsoleCommandSender console = (ConsoleCommandSender) sender;
                if (args.length == 0) {
                    return false;
                } else if (args[0].equalsIgnoreCase("version")) {
                    console.sendMessage("You're running: " + ChatColor.AQUA.toString() + BigBrother.name + " " + BigBrother.version);
                } else if (args[0].equalsIgnoreCase("update")) {
                    Updatr.updateAvailable(console);
                }
                return true;
            }
            return false;
        }
        return false;
    }
    
}
