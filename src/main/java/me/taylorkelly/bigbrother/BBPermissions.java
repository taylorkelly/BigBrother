
package me.taylorkelly.bigbrother;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nijikokun.bukkit.Permissions.Permissions;

public class BBPermissions {
    private static boolean permissionsEnabled = false;

    public static void initialize(Server server) {
        Plugin test = server.getPluginManager().getPlugin("Permissions");
        if (test != null) {
            permissionsEnabled = true;
            BigBrother.info("Permissions enabled.",null);
        } else {
            BigBrother.severe("Permissions isn't loaded, there are no restrictions.",null);
        }
    }


    private static boolean permission(Player player, String string) {
        return Permissions.Security.permission(player, string);
    }

    public static boolean info(Player player) {
        if (permissionsEnabled) {
            return permission(player, "bb.admin.info");
        } else {
            return player.isOp();
        }
    }
    
    public static boolean rollback(Player player) {
        if (permissionsEnabled) {
            return permission(player, "bb.admin.rollback");
        } else {
            return player.isOp();
        }
    }
    
    public static boolean watch(Player player) {
        if (permissionsEnabled) {
            return permission(player, "bb.admin.watch");
        } else {
            return player.isOp();
        }
    }

    public static boolean cleanse(Player player) {
        if (permissionsEnabled) {
            return permission(player, "bb.admin.cleanse");
        } else {
            return player.isOp();
        }
    }
}
