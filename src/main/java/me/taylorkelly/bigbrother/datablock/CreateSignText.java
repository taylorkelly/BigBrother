package me.taylorkelly.bigbrother.datablock;

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBPlayerInfo;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class CreateSignText extends BBDataBlock {

    public CreateSignText(String player, Sign sign, String world) {
        super(player, Action.CREATE_SIGN_TEXT, world, sign.getX(), sign.getY(), sign.getZ(), 323, getText(sign));
    }

    public CreateSignText(String player, String[] lines, Block block) {
        super(player, Action.CREATE_SIGN_TEXT, block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), 323, getText(lines));
    }

    private static String getText(Sign sign) {
        String[] lines = sign.getLines();
        return getText(lines);
    }

    private static String getText(String[] lines) {
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            message.append(lines[i]);
            if (i < lines.length - 1) {
                message.append("\u0060");
            }
        }
        return message.toString();
    }

    public static BBDataBlock getBBDataBlock(BBPlayerInfo pi, String world, int x, int y, int z, int type, String data) {
        return new CreateSignText(pi, world, x, y, z, type, data);
    }

    private CreateSignText(BBPlayerInfo player, String world, int x, int y, int z, int type, String data) {
        super(player, Action.CREATE_SIGN_TEXT, world, x, y, z, type, data);
    }


    @Override
    public void redo(Server server) {
        World currWorld = server.getWorld(world);
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
    public void rollback(World wld) {
        World currWorld = wld;//server.getWorld(world);
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
