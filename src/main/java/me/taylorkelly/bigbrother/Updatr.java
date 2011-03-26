package me.taylorkelly.bigbrother;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class Updatr {

    public static void updateAvailable(Player player) {
        URLReader reader = new URLReader();
        if (!reader.versionIsUpToDate(BigBrother.version)) {
            player.sendMessage(ChatColor.RED.toString() + BigBrother.name + " " + BigBrother.version + " has an update to " + reader.getCurrVersion());
        } else {
            player.sendMessage(ChatColor.AQUA.toString() + BigBrother.name + " " + BigBrother.version + " is up to date!");
        }
    }

    static void updateAvailable(ConsoleCommandSender console) {
        URLReader reader = new URLReader();
        if (!reader.versionIsUpToDate(BigBrother.version)) {
            console.sendMessage(ChatColor.RED.toString() + BigBrother.name + " " + BigBrother.version + " has an update to " + reader.getCurrVersion());
        } else {
            console.sendMessage(ChatColor.AQUA.toString() + BigBrother.name + " " + BigBrother.version + " is up to date!");
        }
    }
}
