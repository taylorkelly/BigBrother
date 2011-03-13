package me.taylorkelly.bigbrother;

import me.taylorkelly.help.Help;
import org.bukkit.plugin.Plugin;

public class BBHelp {

    public static void initialize(Plugin plugin) {
        Plugin test = plugin.getServer().getPluginManager().getPlugin("Help");
        if (test != null) {
            Help helpPlugin = ((Help) test);
            String[] permissions = new String[]{"bb.admin.watch", "bb.admin.info", "bb.admin.rollback", "bb.admin.cleanse"};
            helpPlugin.registerCommand("bb help", "Help for all BigBrother commands", plugin, permissions);
            helpPlugin.registerCommand("bb watch [player]", "Toggle the watch on [player]", plugin, permissions[0]);
            helpPlugin.registerCommand("bb watched", "Displays the list of watched players", plugin, permissions[1]);
            helpPlugin.registerCommand("bb unwatched", "Displays the list of unwatched players", plugin, permissions[1]);
            helpPlugin.registerCommand("bb stick (#)", "Tools to examine block history", plugin, true, permissions[1]);
            helpPlugin.registerCommand("bb here", "An overview of the block history around you", plugin, true, permissions[1]);
            helpPlugin.registerCommand("bb here [#]", "An overview of [#] blocks around you", plugin, permissions[1]);
            helpPlugin.registerCommand("bb here [player]", "Displays [player]'s changes around you", plugin, permissions[1]);
            helpPlugin.registerCommand("bb here [player] [#]", "Displays [player]'s changes within [#] blocks", plugin, permissions[1]);
            helpPlugin.registerCommand("bb find [x] [y] [z]", "Displays changes around [x] [y] [z]", plugin, permissions[1]);
            helpPlugin.registerCommand("bb find [x] [y] [z] [player]", "Displays [player]'s changes around [x] [y] [z]", plugin, permissions[1]);
            helpPlugin.registerCommand("bb rollback (players) (t) (r) (id)", "Perform a rollback with given arguments", plugin, permissions[2]);
            helpPlugin.registerCommand("bb undo", "Undoes the most recent rollback", plugin, permissions[2]);
            helpPlugin.registerCommand("bb cleanse", "Cleanse the database", plugin, permissions[3]);
            helpPlugin.registerCommand("bb log", "Gives you a log for inspecting non-solid blocks", plugin, permissions[1]);
            BBLogging.info("'Help' support enabled");
        } else {
            BBLogging.warning("'Help' isn't detected. No /help support");
        }
    }
}
