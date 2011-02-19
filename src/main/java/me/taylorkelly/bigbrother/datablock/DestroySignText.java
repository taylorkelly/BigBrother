package me.taylorkelly.bigbrother.datablock;

import java.util.logging.Level;

import me.taylorkelly.bigbrother.BigBrother;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;

public class DestroySignText extends BBDataBlock {
    public DestroySignText(Player player, Sign sign) {
        this(player.getName(), sign);
    }

    public DestroySignText(String name, Sign sign) {
        // TODO Better World support
        super(name, Action.DESTROY_SIGN_TEXT, 0, sign.getX(), sign.getY(), sign.getZ(), 323, getText(sign));
    }

    private static String getText(Sign sign) {
        StringBuilder message = new StringBuilder();
        String[] lines = sign.getLines();
        for (int i = 0; i < lines.length; i++) {
            message.append(lines[i]);
            if(i < lines.length - 1) message.append("\u0060");
        }
        return message.toString();
    }

    public static BBDataBlock getBBDataBlock(String player, int world, int x, int y, int z, int type, String data) {
        return new DestroySignText(player, world, x, y, z, type, data);
    }

    private DestroySignText(String player, int world, int x, int y, int z, int type, String data) {
        super(player, Action.DESTROY_SIGN_TEXT, world, x, y, z, type, data);
    }

    public void rollback(Server server) {
        World worldy = server.getWorlds().get(world);
        if(!((CraftWorld)worldy).getHandle().A.a(x >> 4, z >> 4)) {
            ((CraftWorld)worldy).getHandle().A.d(x >> 4, z >> 4);
        }

        String[] lines = data.split("\u0060");


        Block block = worldy.getBlockAt(x, y, z);
        if (block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            for (int i = 0; i < lines.length; i++) {
                sign.setLine(i, lines[i]);
            }
        } else {
            BigBrother.log.log(Level.WARNING, "[BBROTHER]: Error when restoring sign");
        }
    }

    public void redo(Server server) {
        World worldy = server.getWorlds().get(world);
        if(!((CraftWorld)worldy).getHandle().A.a(x >> 4, z >> 4)) {
            ((CraftWorld)worldy).getHandle().A.d(x >> 4, z >> 4);
        }

        Block block = worldy.getBlockAt(x, y, z);
        if (block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            for (int i = 0; i < sign.getLines().length; i++) {
                sign.setLine(i, "");
            }
        } else {
            BigBrother.log.log(Level.WARNING, "[BBROTHER]: Error when restoring sign");
        }
     }
}
