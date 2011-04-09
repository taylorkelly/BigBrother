package me.taylorkelly.bigbrother.datablock;

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBPlayerInfo;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class DestroySignText extends BBDataBlock {

    public DestroySignText(String name, Sign sign, String world) {
        super(name, Action.DESTROY_SIGN_TEXT, world, sign.getX(), sign.getY(), sign.getZ(), 323, getText(sign));
    }

    private static String getText(Sign sign) {
        StringBuilder message = new StringBuilder();
        String[] lines = sign.getLines();
        for (int i = 0; i < lines.length; i++) {
            message.append(lines[i]);
            if (i < lines.length - 1) {
                message.append("\u0060");
            }
        }
        return message.toString();
    }

    public static BBDataBlock getBBDataBlock(BBPlayerInfo pi, String world, int x, int y, int z, int type, String data) {
        return new DestroySignText(pi, world, x, y, z, type, data);
    }

    private DestroySignText(BBPlayerInfo player, String world, int x, int y, int z, int type, String data) {
        super(player, Action.DESTROY_SIGN_TEXT, world, x, y, z, type, data);
    }

    public DestroySignText(BBPlayerInfo player, Sign sign, String world) {
        super(player, Action.DESTROY_SIGN_TEXT, world, sign.getX(), sign.getY(), sign.getZ(), 323, getText(sign));
    }

    @Override
    public void rollback(World wld) {
        World currWorld = wld;//server.getWorld(world);
        if (!currWorld.isChunkLoaded(x >> 4, z >> 4)) {
            currWorld.loadChunk(x >> 4, z >> 4);
        }

        String[] lines = data.split("\u0060");


        Block block = currWorld.getBlockAt(x, y, z);
        if (block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            for (int i = 0; i < lines.length; i++) {
                sign.setLine(i, lines[i]);
            }
        } else {
            BBLogging.warning("Error when restoring sign");
        }
    }

    @Override
    public void redo(Server server) {
        World currWorld = server.getWorld(world);
        if (!currWorld.isChunkLoaded(x >> 4, z >> 4)) {
            currWorld.loadChunk(x >> 4, z >> 4);
        }

        Block block = currWorld.getBlockAt(x, y, z);
        if (block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            for (int i = 0; i < sign.getLines().length; i++) {
                sign.setLine(i, "");
            }
        } else {
            BBLogging.warning("Error when restoring sign");
        }
    }
}
