package me.taylorkelly.bigbrother.datablock;

import java.util.logging.Level;
import java.util.logging.Logger;

import me.taylorkelly.bigbrother.BigBrother;

import org.bukkit.*;
import org.bukkit.block.Sign;

public class DestroySignText extends BBDataBlock {
    public DestroySignText(Player player, Sign sign) {
        // TODO Better World support
        super(player.getName(), DESTROY_SIGN_TEXT, 0, sign.getX(), sign.getY(), sign.getZ(), 323, getText(sign));
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
        super(player, DESTROY_SIGN_TEXT, world, x, y, z, type, data);
    }

    public void rollback(Server server) {
        // TODO Chunk loading stuffs
        // if (!world.isChunkLoaded(world.getChunkAt(destination.getBlockX(),
        // destination.getBlockZ())))
        // world.loadChunk(world.getChunkAt(destination.getBlockX(),
        // destination.getBlockZ()));

        String[] lines = data.split("\u0060");

        
        Block block = server.getWorlds()[world].getBlockAt(x, y, z);
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
        // TODO Chunk loading stuffs
        // if (!world.isChunkLoaded(world.getChunkAt(destination.getBlockX(),
        // destination.getBlockZ())))
        // world.loadChunk(world.getChunkAt(destination.getBlockX(),
        // destination.getBlockZ()));

        // funky stuff.
    }
}
