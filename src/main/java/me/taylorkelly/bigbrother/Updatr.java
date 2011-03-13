package me.taylorkelly.bigbrother;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

class Updatr {

    public static void updateAvailable(Player player) {
        URLReader reader = new URLReader();
        if (!reader.versionIsUpToDate(BigBrother.version)) {
            player.sendMessage(ChatColor.RED.toString() + BigBrother.name + " " + BigBrother.version + " has an update to " + reader.getCurrVersion());
        } else {
            player.sendMessage(ChatColor.AQUA.toString() + BigBrother.name + " " + BigBrother.version + " is up to date!");
        }
    }
}
