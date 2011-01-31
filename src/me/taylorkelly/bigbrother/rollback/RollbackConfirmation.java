package me.taylorkelly.bigbrother.rollback;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class RollbackConfirmation {
    private static HashMap<String, RollbackInterpreter> confirmees = new HashMap<String, RollbackInterpreter>();
    
    
    public static boolean hasRI(Player player) {
        if(confirmees.containsKey(player.getName())) {
            return true;
        } else {
            return false;
        }
    }
    
    public static RollbackInterpreter getRI(Player player) {
        return confirmees.remove(player.getName());
    }
    
    public static void deleteRI(Player player) {
        confirmees.remove(player.getName());
    }

    public static void setRI(Player player, RollbackInterpreter interpreter) {
        confirmees.put(player.getName(), interpreter);
    }
    
}
