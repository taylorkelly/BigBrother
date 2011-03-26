package me.taylorkelly.bigbrother.commands;

import me.taylorkelly.bigbrother.BBPermissions;
import me.taylorkelly.bigbrother.BigBrother;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DoneCommand implements CommandExecutor {
    
    private BigBrother plugin;
    
    public DoneCommand(BigBrother plugin) {
        this.plugin=plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender send, Command arg1, String arg2, String[] arg3) {

        Player player=(Player) send;
        if(BBPermissions.info(player)) {
                plugin.sticker.setMode(player, 0);
                plugin.reportStickMode(player, 0);
        }
        return true;
    }
    
}
