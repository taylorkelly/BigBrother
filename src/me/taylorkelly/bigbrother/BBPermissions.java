
package me.taylorkelly.bigbrother;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class BBPermissions {
    private static Permissions permissionsPlugin;
    private static boolean permissionsEnabled = false;

    public static void initialize(Server server) {
        Plugin test = server.getPluginManager().getPlugin("Permissions");
        if (test != null) {
            Logger log = Logger.getLogger("Minecraft");
            permissionsPlugin = ((Permissions)test);
            permissionsEnabled = true;
            log.log(Level.INFO, "[MYHOME] Permissions enabled.");
        } else {
            Logger log = Logger.getLogger("Minecraft");
            log.log(Level.SEVERE, "[MYHOME] Permissions isn't loaded, there are no restrictions.");
        }
    }


    private static boolean permission(Player player, String string) {
        return permissionsPlugin.Security.permission(player, string);
    }

    public static boolean isAdmin(Player player) {
        if (permissionsEnabled) {
            return permission(player, "bb.admin");
        } else {
            return player.isOp();
        }
    }
}
